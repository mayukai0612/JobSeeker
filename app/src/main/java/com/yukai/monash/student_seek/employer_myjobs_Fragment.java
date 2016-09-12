package com.yukai.monash.student_seek;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by yukaima on 21/05/16.
 */
public class employer_myjobs_Fragment extends Fragment{

    private ArrayList<activejobs_model> activejobs_modelArrayList;
    private ListView activejobsLV;
    private Activejobs_adapter Activejobs_adapter;
    public Button btn_switch;

    private SharedPreferenceHelper sharedPreferenceHelper;
    private Gson gson;
    public static employer_myjobs_Fragment newInstance() {
        employer_myjobs_Fragment employerjobsFragment = new employer_myjobs_Fragment();
        return employerjobsFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hire_myjobs, container, false);

        btn_switch = (Button)view.findViewById(R.id.btn_show);

        activejobsLV = (ListView) view.findViewById(R.id.listview_activejobs);
        activejobs_modelArrayList = new ArrayList<activejobs_model>();
        sharedPreferenceHelper = new SharedPreferenceHelper(getContext(),"Login Credentials");


        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        String employerid = sharedPreferenceHelper.loadPreferences("userid");
        Log.d("employerid",employerid);
        params.add("employerid", employerid);
        client.post(getContext(), "http://173.255.245.239/jobs/get_job_desc.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                try {
                    JsonReader reader = new JsonReader(new StringReader(response));
                    reader.setLenient(true);

                    activejobs_modelArrayList = new Gson().fromJson(reader, new TypeToken<List<activejobs_model>>() {
                    }.getType());
                    Activejobs_adapter = new Activejobs_adapter(getContext(), activejobs_modelArrayList);
                    activejobsLV.setAdapter(Activejobs_adapter);
                    activejobsLV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(

                    ) {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                            builder.setTitle("Remove this job?");
                            builder.setMessage("Are you sure you wish to remove this job?");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            // Remove job from Database
                                            activejobs_model job = activejobs_modelArrayList.remove(position);
                                            String jobid = job.getJobid();
                                            deleteJobFromDataBase(jobid);
                                            // Update ListView
                                            Activejobs_adapter.notifyDataSetChanged();
                                            Toast.makeText(getActivity(),
                                                    "This job has been removed.",
                                                    Toast.LENGTH_SHORT)
                                                    .show();
                                        }
                                    }
                            );
                            builder.setNegativeButton("Exit",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            // Close the dialog
                                            dialogInterface.cancel();
                                        }
                                    }
                            );
                            // Create and show dialog
                            builder.create().show();
                            return false;
                        }
                    });
                } catch (NullPointerException e) {
                }catch (JsonSyntaxException e){}

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });





        //change to employer_applicants fragment
        btn_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getFragmentManager().beginTransaction()
                        .addToBackStack(null) //adding to back stack in order to use back button of phone to navigate
                        .replace(R.id.fragmentContainer, new employer_applicantsFragment())
                        .commit();
            }
        });


        return view;
    }

    public void deleteJobFromDataBase(String jobid)
    {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("jobid", jobid);
        client.post(getContext(), "http://173.255.245.239/jobs/employers_delete_jobs.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }
}

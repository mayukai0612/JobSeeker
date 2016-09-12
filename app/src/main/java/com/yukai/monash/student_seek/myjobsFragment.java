package com.yukai.monash.student_seek;


import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
 * Created by yukaima on 8/05/16.
 */
public class myjobsFragment extends Fragment {

    private ListView myjobsListview;
    private SharedPreferenceHelper sharedPreferenceHelper;
    private ArrayList<JobModel> seeker_jobs_arraylist;
    private Seeker_jobs_adapter seeker_jobs_adapter;
    public myjobsFragment() {
    }

    public static myjobsFragment newInstance() {
        myjobsFragment myjobsFragment = new myjobsFragment();
        return myjobsFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_seeker_myjobs, container, false);
        myjobsListview = (ListView)view.findViewById(R.id.listview_seeker_myjobs);

        seeker_jobs_arraylist = new ArrayList<JobModel>();
        sharedPreferenceHelper = new SharedPreferenceHelper(getContext(),"Login Credentials");


        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        String userid = sharedPreferenceHelper.loadPreferences("userid");
        params.add("userid", userid);


        client.post(getContext(), "http://173.255.245.239/jobs/get_seeker_jobs.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);

                JsonReader reader = new JsonReader(new StringReader(response));
                reader.setLenient(true);
                try {
                    seeker_jobs_arraylist = new Gson().fromJson(reader, new TypeToken<List<JobModel>>() {
                    }.getType());

                    seeker_jobs_adapter = new Seeker_jobs_adapter(getContext(), seeker_jobs_arraylist);
                    myjobsListview.setAdapter(seeker_jobs_adapter);
                    myjobsListview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(

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
                                            JobModel job = seeker_jobs_arraylist.remove(position);
                                            String jobid = job.getJobid();
                                            deleteJobFromDataBase(jobid);
                                            // Update ListView
                                            seeker_jobs_adapter.notifyDataSetChanged();
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
                } catch (JsonSyntaxException e) {
                }
                ;


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

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

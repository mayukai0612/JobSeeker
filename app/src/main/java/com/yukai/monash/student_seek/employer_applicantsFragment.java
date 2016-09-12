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
public class employer_applicantsFragment extends Fragment {
    private Button btn_back;
    private ListView applicantsLV;
    private ArrayList<Applicants_model> applicantssArrayList;
    private SharedPreferenceHelper sharedPreferenceHelper;
    private Applicants_adapter applicants_adapter;

    public static employer_applicantsFragment newInstance() {
        employer_applicantsFragment employer_applicantsFragment = new employer_applicantsFragment();
        return employer_applicantsFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_applicants, container, false);

        applicantsLV =(ListView)view.findViewById(R.id.listview_applicants);
        applicantssArrayList = new ArrayList<Applicants_model>();
        sharedPreferenceHelper = new SharedPreferenceHelper(getContext(),"Login Credentials");


        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        String employerid = sharedPreferenceHelper.loadPreferences("userid");
        params.add("employerid", employerid);
        Log.d("empid",employerid);
        client.post(getContext(), "http://173.255.245.239/jobs/get_applicants.php",params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                try {
                    JsonReader reader = new JsonReader(new StringReader(response));
                    reader.setLenient(true);

                    applicantssArrayList = new Gson().fromJson(reader, new TypeToken<List<Applicants_model>>() {
                    }.getType());
                    applicants_adapter = new Applicants_adapter(getContext(), applicantssArrayList);
                    applicantsLV.setAdapter(applicants_adapter);
                    applicantsLV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(

                    ) {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                            builder.setTitle("Remove this applicant?");
                            builder.setMessage("Are you sure you wish to remove this applicant?");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            // Remove job from Database
                                            Applicants_model applicants_model = applicantssArrayList.remove(position);
                                            String userid = applicants_model.getUserid();
                                            deleteApplicantsFromDataBase(userid);
                                            // Update ListView
                                            applicants_adapter.notifyDataSetChanged();
                                            Toast.makeText(getActivity(),
                                                    "This applicant has been removed.",
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
                }catch (JsonSyntaxException e){}
                catch (NullPointerException e){}

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });


        btn_back = (Button)view.findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

        return view;
    }

    public void deleteApplicantsFromDataBase(String userid)
    {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        sharedPreferenceHelper = new SharedPreferenceHelper(getContext(),"Login Credentials");
        String employerid = sharedPreferenceHelper.loadPreferences("userid");
        params.add("userid", userid);
        params.add("empid", employerid);
        client.post(getContext(), "http://173.255.245.239/jobs/employer_delete_applicants.php", params, new AsyncHttpResponseHandler() {
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

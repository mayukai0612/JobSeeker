package com.yukai.monash.student_seek;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by yukaima on 22/05/16.
 */
public class postjob_fragment extends Fragment {

    private Button postjobs;
    private Button clear;
    private EditText et_jobdesc;

    private SharedPreferenceHelper sharedPreferenceHelper;

    public static postjob_fragment newInstance() {
        postjob_fragment postjob_fragment = new postjob_fragment();
        return postjob_fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_postjob, container, false);

        postjobs =(Button) view.findViewById(R.id.btn_save_postjob);
        clear = (Button)view.findViewById(R.id.btn_clear_postjob);
        et_jobdesc = (EditText)view.findViewById(R.id.et_jobdesc);
        sharedPreferenceHelper = new SharedPreferenceHelper(getContext(),"Login Credentials");
        //save posted jobs to database
        postjobs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCompanyInfo();

            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_jobdesc.setText("" );
            }
        });




        return view;
    }

    public void postJob()
    {
        //check if user has entered job descriptions
        String desc =et_jobdesc.getText().toString().trim();
        if (desc.matches(""))
        {
            Toast.makeText(getContext(), "Please enter job description!", Toast.LENGTH_LONG).show();

        }else {

            String userid = sharedPreferenceHelper.loadPreferences("userid");
            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            params.add("userid", userid);
            params.add("jobdesc", et_jobdesc.getText().toString());
            client.post(getContext(), "http://173.255.245.239/jobs/postjobs.php", params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    String response = new String(responseBody);
                    Toast.makeText(getContext(), response, Toast.LENGTH_LONG).show();

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(getContext(), "Post failed, Please retry later.", Toast.LENGTH_LONG).show();

                }
            });
        }
    }

    public void checkCompanyInfo()
    {
         Boolean isComanyInfo;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        sharedPreferenceHelper = new SharedPreferenceHelper(getActivity(),"Login Credentials");
        params.add("userid", sharedPreferenceHelper.loadPreferences("userid"));
        client.post(getActivity(), "http://173.255.245.239/jobs/get_company_info.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                JSONObject object = null;
                try {
                    object = new JSONObject(response);

                    if (object.has("company") && object.has("aboutus"))
                        postJob();
                    else {
                        Toast.makeText(getContext(), "Please complete company profile first!", Toast.LENGTH_LONG).show();
                        Intent i = new Intent(getActivity(), hire_editprofile_activity.class);
                        startActivity(i);
                        getActivity().finish();
                    }

                } catch (JSONException e) {

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }
}

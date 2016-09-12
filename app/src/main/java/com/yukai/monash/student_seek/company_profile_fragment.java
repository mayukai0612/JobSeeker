package com.yukai.monash.student_seek;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by yukaima on 22/05/16.
 */
public class company_profile_fragment extends Fragment {
    private Button edit_profile;
    private TextView tv_profile_company;
    private TextView aboutus;
    private TextView employer_name;
    private ImageView company_cover;
    private TextView logout;
    private com.yukai.monash.student_seek.RoundImageView employer_icon;

    private SharedPreferenceHelper sharedPreferenceHelper;
    private static final String UserIconUrlPrefix = "http://173.255.245.239/jobs/image/";


    public static company_profile_fragment newInstance() {
        company_profile_fragment company_profile_fragment = new company_profile_fragment();
        return company_profile_fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_company_profile, container, false);

        //initialization
        edit_profile = (Button)view.findViewById(R.id.btn_company_editprofile);
        tv_profile_company = (TextView)view.findViewById(R.id.tv_profile_company);
        aboutus = (TextView)view.findViewById(R.id.tv_companyprofile_aboutus);
        employer_name = (TextView)view.findViewById(R.id.companyprofile_name);
        employer_icon = (com.yukai.monash.student_seek.RoundImageView)view.findViewById(R.id.companyprofile_icon);
        company_cover = (ImageView)view.findViewById(R.id.companyprofile_profile_cover);
        logout =(TextView)view.findViewById(R.id.employer_logout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferenceHelper.deletePreferences("userid");
                sharedPreferenceHelper.deletePreferences("userType");
                sharedPreferenceHelper.deletePreferences("linkedinPicUrl");
                sharedPreferenceHelper.deletePreferences("status");
                Intent intent = new Intent(getActivity(),First_view_Activity.class);
                startActivity(intent);
            }
        });
        //change to company edit profile
        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),hire_editprofile_activity.class);
                startActivity(i);
            }
        });

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

                    String companyname = object.getString("company");
                    String SAboutus = object.getString("aboutus");
                    String name = object.getString("firstname") + " " + object.getString("lastname");
                    String icon_filename = object.getString("icon_filename");
                    String icon_url = UserIconUrlPrefix + icon_filename + ".png";

                    Log.d("test", "test");
                    Log.d("object", object.toString());
                    Log.d("pic url", sharedPreferenceHelper.loadPreferences("linkedinPicUrl"));

                    if (icon_filename == "null") {
                        Picasso.with(getActivity()).load(sharedPreferenceHelper.loadPreferences("linkedinPicUrl"))
                                .resize(70, 70)
                                .centerCrop()
                                .into(employer_icon);
                    } else {
                        Picasso.with(getActivity()).load(icon_url)
                                .resize(70, 70)
                                .centerCrop()
                                .into(employer_icon);

                    }
                    tv_profile_company.setText(companyname);
                    aboutus.setText(SAboutus);
                    employer_name.setText(name);

                } catch (JSONException e) {
                    Log.d("exception", e.toString());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });


        loadCompanyCover();
        return view;
    }

    public void loadCompanyCover()
    {
        Log.d("test","loadcover");
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        sharedPreferenceHelper = new SharedPreferenceHelper(getActivity(),"Login Credentials");
        String userid = sharedPreferenceHelper.loadPreferences("userid");
        params.add("userid", userid);
        client.post(getContext(), "http://173.255.245.239/jobs/get_cover_filename.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(response);
                    String iconpath = object.getString("filename");
                    String icon_url = UserIconUrlPrefix + iconpath + ".png";




                    Log.d("iconpath",icon_url);
                    if(iconpath == "null")
                    {
                        try {
                            Picasso.with(getActivity()).load(sharedPreferenceHelper.loadPreferences("linkedinPicUrl"))
                                    .resize(180, 70)
                                    .centerCrop()
                                    .into(company_cover);
                        }catch (IllegalArgumentException e){}
                    }
                    else
                    {
                        Picasso.with(getActivity()).load(icon_url)
                                .resize(180, 70)
                                .centerCrop()
                                .into(company_cover);

                    }


                  //  progress.dismiss();
                } catch (JSONException e) {
                    Log.d("exception",e.toString());
                }

            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });


    }
}


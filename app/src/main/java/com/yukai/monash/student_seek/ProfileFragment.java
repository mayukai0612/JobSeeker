package com.yukai.monash.student_seek;

import android.app.Fragment;
import android.app.ProgressDialog;
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
 * Created by yukaima on 8/05/16.
 */
public class ProfileFragment extends Fragment {
    private Button editprofile;
    private TextView fn;
    private TextView ln;
    private TextView aboutme;
    private TextView languages;
    private TextView education;
    private TextView logout;
    private SharedPreferenceHelper sharedPreferenceHelper;
    private ImageView seekericon;
    private ProgressDialog progress;
    private String linkedinPicUrl;
    private static final String UserIconUrlPrefix = "http://173.255.245.239/jobs/image/";

    public ProfileFragment() {
    }

    public static ProfileFragment newInstance() {
        ProfileFragment stuProfileFragment = new ProfileFragment();
        return stuProfileFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_seeker_profile, container, false);
        fn = (TextView) view.findViewById(R.id.tv_profile_fn);
        ln = (TextView) view.findViewById(R.id.tv_profile_ln);
        aboutme = (TextView) view.findViewById(R.id.tv_profile_aboutme);
        languages = (TextView) view.findViewById(R.id.tv_show_languages);
        education = (TextView) view.findViewById(R.id.tv__profile_edu);
        logout = (TextView)view.findViewById(R.id.logout);
        seekericon = (ImageView) view.findViewById(R.id.image_view_seeker_icon);
        sharedPreferenceHelper = new SharedPreferenceHelper(getContext(), "Login Credentials");

        editprofile = (Button) view.findViewById(R.id.btn_edit);
        editprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), jobseeker_editprofile_Activity.class);
                startActivity(i);
            }
        });

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

        // load user info and set it on view
        setProfileView();
        return view;
    }


    public void setProfileView()
    {
        progress= new ProgressDialog(getActivity());
        progress.setMessage("Retrieve data...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        sharedPreferenceHelper = new SharedPreferenceHelper(getContext(), "Login Credentials");
        String userid = sharedPreferenceHelper.loadPreferences("userid");
        params.add("userid", userid);
        client.post(getContext(), "http://173.255.245.239/jobs/get_profile.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(response);
                    String slanguagues = object.getString("languages");
                    String SAboutme = object.getString("aboutme");
                    String sln = object.getString("lastname");
                    String sfn = object.getString("firstname");
                    String seducation = object.getString("education");
                    String iconpath = object.getString("filename");
                    String icon_url = UserIconUrlPrefix + iconpath + ".png";


                    fn.setText(sfn);
                    ln.setText(sln);
                    aboutme.setText(SAboutme);
                    languages.setText(slanguagues);
                    aboutme.setText(SAboutme);
                    education.setText(seducation);

                    Log.d("iconpath", iconpath);
                    sharedPreferenceHelper = new SharedPreferenceHelper(getContext(), "Login Credentials");

                    Log.d("path", sharedPreferenceHelper.loadPreferences("linkedinPicUrl"));
                    if(iconpath == "null" )
                    {
                     //   if(sharedPreferenceHelper.loadPreferences("linkedinPicUrl") != "null") {
                        try{
                            Picasso.with(getActivity()).load(sharedPreferenceHelper.loadPreferences("linkedinPicUrl"))
                                    .resize(180, 70)
                                    .centerCrop()
                                    .into(seekericon);
                        }catch ( IllegalArgumentException e){}
                    }
                    else
                    {
                        Picasso.with(getActivity()).load(icon_url)
                                .resize(180, 70)
                                .centerCrop()
                                .into(seekericon);

                    }


                    progress.dismiss();
                } catch (JSONException e) {
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });

    }





}

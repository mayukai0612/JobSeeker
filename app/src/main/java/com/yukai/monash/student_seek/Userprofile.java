package com.yukai.monash.student_seek;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.linkedin.platform.APIHelper;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

/**
 * Created by yukaima on 8/06/16.
 */
public class Userprofile extends AppCompatActivity

    {

        private TextView username;
        private TextView email;
        private ImageView usericon;
        private static final String host = "api.linkedin.com";
        private static final String topCardUrl = "https://" + host + "/v1/people/~:" +
                "(email-address,date-of-birth,num-recommenders,first-name,last-name,formatted-name,phone-numbers,public-profile-url,picture-url,picture-urls::(original))";
        ProgressDialog progress;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_userprofile);

            username = (TextView)findViewById(R.id.userprofile_name);
            usericon = (ImageView)findViewById(R.id.userprofile_imageview);
            email = (TextView)findViewById(R.id.userprofile_email);


            progress= new ProgressDialog(this);
            progress.setMessage("Retrieve data...");
            progress.setCanceledOnTouchOutside(false);
            progress.show();
            getUserData();

        }
        public void getUserData(){
            APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
            apiHelper.getRequest(Userprofile.this, topCardUrl, new ApiListener() {
                @Override
                public void onApiSuccess(ApiResponse result) {
                    try {
                        setUserProfile(result.getResponseDataAsJson());
                        progress.dismiss();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onApiError(LIApiError error) {
                    // ((TextView) findViewById(R.id.error)).setText(error.toString());

                }
            });
        }

        public  void  setUserProfile(JSONObject response){
            try {
                Log.d("test", response.toString());
                if(response.has("emailAddress"))
                {
                    email.setText(response.get("emailAddress").toString());
                }else
                {
                    email.setText("");
                }
                if(response.has("firstName") && response.has("lastName")&&response.has("numRecommenders"))
                {
                    username.setText(response.get("firstName").toString() + " " + response.get("lastName").toString()+response.get("numRecommenders").toString());
                }else
                {
                    username.setText("null");
                }
                if (response.has("pictureUrl"))
                {
                    Picasso.with(this).load(response.getString("pictureUrl"))
                            .into(usericon);
                }else {
                    return;
                }

            } catch (Exception e){
                e.printStackTrace();
            }
        }
}



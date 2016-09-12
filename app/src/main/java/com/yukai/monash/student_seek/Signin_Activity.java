package com.yukai.monash.student_seek;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.linkedin.platform.APIHelper;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import cz.msebera.android.httpclient.Header;

/**
 * Created by yukaima on 18/05/16.
 */
public class Signin_Activity extends AppCompatActivity {
    private Button signin;
    private EditText email;
    private EditText password;
    private Button linkedin;
    private ProgressDialog progress;
    private String linkedinEmail;
    private String linkedinFn;
    private String linkedinLn;
    private String linkedinPicUrl;
    private String linkedinPublicProfileUrl;

    private SharedPreferenceHelper sharedPreferenceHelper;

    private static final String TAG = Signin_Activity.class.getSimpleName();
    private static final String host = "api.linkedin.com";
    private static final String topCardUrl = "https://" + host + "/v1/people/~:" +
            "(email-address,formatted-name,first-name,last-name,phone-numbers,public-profile-url,picture-url,picture-urls::(original))";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        generateHashkey();

        signin = (Button)findViewById(R.id.btn_seek_signin);
        email = (EditText)findViewById(R.id.et_loginemail);
        password = (EditText)findViewById(R.id.et_loginpw);
        linkedin = (Button)findViewById(R.id.btn_linkedin);

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String input_email = email.getText().toString();
                String input_pwd = password.getText().toString();

                if (input_email == "" || input_pwd == "") {
                    Toast.makeText(Signin_Activity.this, "Do not leave blank.", Toast.LENGTH_LONG).show();

                } else {
                    AsyncHttpClient client = new AsyncHttpClient();
                    RequestParams params = new RequestParams();
                    params.add("email", input_email);
                    params.add("password", input_pwd);
                    client.post("http://173.255.245.239/jobs/user_login.php", params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            String response = new String(responseBody);
                            JSONObject object = null;
                            try {
                                object = new JSONObject(response);
                                String success = object.getString("success");

                                if(success.equals("0"))
                                {
                                    Toast.makeText(Signin_Activity.this,"Invalid email or password.",Toast.LENGTH_LONG).show();

                                }
                                else if(success.equals("1"))
                                {
                                    // save userid to sharedPreferences
                                    sharedPreferenceHelper = new SharedPreferenceHelper(Signin_Activity.this,"Login Credentials");
                                    String userid = object.getString("userid");
                                    sharedPreferenceHelper.savePreferences("userid",userid);
                                    sharedPreferenceHelper.savePreferences("status","login");

                                    //change to homepage
                                    Toast.makeText(Signin_Activity.this,"Log in .",Toast.LENGTH_LONG).show();
                                    if(sharedPreferenceHelper.loadPreferences("userType") == "jobseeker") {
                                        Intent intent = new Intent(Signin_Activity.this, JobSeekerHomePage_Activity.class);
                                        startActivity(intent);
                                    }else{
                                        Intent intent = new Intent(Signin_Activity.this, EmployerHomePage_Activity.class);
                                        startActivity(intent);
                                    }
                                    Signin_Activity.this.finish();
                                }


                            }catch (JSONException e){}
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                        }
                    });

                }
            }
        });

        linkedin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login_linkedin();

            }
        });
    }

    public void login_linkedin(){
        LISessionManager.getInstance(getApplicationContext()).init(this, buildScope(), new AuthListener() {
            @Override
            public void onAuthSuccess() {

                // Toast.makeText(getApplicationContext(), "success" + LISessionManager.getInstance(getApplicationContext()).getSession().getAccessToken().toString(), Toast.LENGTH_LONG).show();

            }

            @Override
            public void onAuthError(LIAuthError error) {

                Toast.makeText(getApplicationContext(), "failed " + error.toString(),
                        Toast.LENGTH_LONG).show();
            }
        }, true);
    }

    // Build the list of member permissions our LinkedIn session requires
    private static Scope buildScope() {
        return Scope.build(Scope.R_BASICPROFILE, Scope.W_SHARE,Scope.R_EMAILADDRESS);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LISessionManager.getInstance(getApplicationContext()).onActivityResult(this,
                requestCode, resultCode, data);

        progress= new ProgressDialog(this);
        progress.setMessage("Retrieve data...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        getUserData();

//        Intent intent = new Intent(Signin_Activity.this,Userprofile.class);
//        startActivity(intent);
    }

    // generate the hash key
    public void generateHashkey(){
        try {
            PackageInfo info = this.getPackageManager().getPackageInfo(
                    this.getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());

                Log.d("Hask key", Base64.encodeToString(md.digest(), Base64.NO_WRAP));
                Log.d("package",info.packageName);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(TAG, e.getMessage(), e);
        } catch (NoSuchAlgorithmException e) {
            Log.d(TAG, e.getMessage(), e);
        }
    }

    public void getUserData(){
        APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
        apiHelper.getRequest(Signin_Activity.this, topCardUrl, new ApiListener() {
            @Override
            public void onApiSuccess(ApiResponse result) {
                try {
                    //get user profile from linkedin
                    setUserProfile(result.getResponseDataAsJson());
                    progress.dismiss();
                    signUpwithLinkedin();

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
            linkedinEmail =  response.get("emailAddress").toString();
            }else
            {
                linkedinEmail = "";
            }
            if(response.has("firstName"))
            {
               linkedinFn =  response.get("firstName").toString();
            }else
            {
                linkedinFn = "";
            }
            if(response.has("lastName"))
            {
                linkedinLn =  response.get("lastName").toString();
            }else
            {
                linkedinLn = "";
            }
            if (response.has("pictureUrl"))
            {
                linkedinPicUrl = response.getString("pictureUrl");
//                Picasso.with(this).load(response.getString("pictureUrl"))
//                        .into(usericon);
            }else {
                linkedinPicUrl = "";
            }
            if (response.has("publicProfileUrl"))
            {
                linkedinPublicProfileUrl = response.getString("publicProfileUrl");

            }else {
                linkedinPublicProfileUrl = "";
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }


    //sign up with linkedin, register with the acquired user_info
    public void signUpwithLinkedin()
    {
            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params  = new RequestParams();
            Log.d("params",linkedinEmail+linkedinLn+linkedinFn+linkedinPicUrl+linkedinPublicProfileUrl);
        params.add("firstname", linkedinFn);
            params.add("lastname",linkedinLn);
            params.add("email",linkedinEmail);
            params.add("linkedinUrl",linkedinPublicProfileUrl);
            params.add("iconUrl",linkedinPicUrl);
            sharedPreferenceHelper = new SharedPreferenceHelper(Signin_Activity.this,"Login Credentials");
            sharedPreferenceHelper.savePreferences("linkedinPicUrl",linkedinPicUrl);

        client.post("http://173.255.245.239/jobs/linkedIn_login.php", params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    String response = new String(responseBody);
                    JSONObject object = null;
                    try {
                        object = new JSONObject(response);
                        String status = object.getString("status");
                        Log.d("status",status);
                        if(status.equals("exists"))
                        {
                            Toast.makeText(Signin_Activity.this,"You have logged in with this account.",Toast.LENGTH_LONG).show();

                            if(sharedPreferenceHelper.loadPreferences("userType") == "jobseeker") {
                                Intent intent = new Intent(Signin_Activity.this,JobSeekerHomePage_Activity.class);
                                intent.putExtra("email",linkedinEmail);
                                SharedPreferenceHelper sharedPreferenceHelper = new SharedPreferenceHelper(Signin_Activity.this,"Login Credentials");
                                sharedPreferenceHelper.savePreferences("status", "login");
                                startActivity(intent);
                                Signin_Activity.this.finish();
                            }else{
                                Intent intent = new Intent(Signin_Activity.this,EmployerHomePage_Activity.class);
                                intent.putExtra("email",linkedinEmail);
                                Log.d("usertype","hire");
                                SharedPreferenceHelper sharedPreferenceHelper = new SharedPreferenceHelper(Signin_Activity.this,"Login Credentials");
                                sharedPreferenceHelper.savePreferences("status", "login");
                                startActivity(intent);
                                Signin_Activity.this.finish();
                            }

                        }else if(status.equals("error"))
                        {
                            Toast.makeText(Signin_Activity.this,"Error,please retry.",Toast.LENGTH_LONG).show();

                        }
                        else if(status.equals("success"))
                        {
                            Toast.makeText(Signin_Activity.this,"You have logged in with this account.",Toast.LENGTH_LONG).show();

                            if(sharedPreferenceHelper.loadPreferences("userType") == "jobseeker") {
                                Intent intent = new Intent(Signin_Activity.this,JobSeekerHomePage_Activity.class);
                                intent.putExtra("email",linkedinEmail);
                                SharedPreferenceHelper sharedPreferenceHelper = new SharedPreferenceHelper(Signin_Activity.this,"Login Credentials");
                                sharedPreferenceHelper.savePreferences("status", "login");
                                startActivity(intent);
                                Signin_Activity.this.finish();
                            }else{
                                Intent intent = new Intent(Signin_Activity.this,EmployerHomePage_Activity.class);
                                intent.putExtra("email",linkedinEmail);
                                SharedPreferenceHelper sharedPreferenceHelper = new SharedPreferenceHelper(Signin_Activity.this,"Login Credentials");
                                sharedPreferenceHelper.savePreferences("status", "login");
                                startActivity(intent);
                                Signin_Activity.this.finish();
                            }
                        }


                    }catch (JSONException e){}
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.d("response",error.toString());
                }
            });

        }


    }

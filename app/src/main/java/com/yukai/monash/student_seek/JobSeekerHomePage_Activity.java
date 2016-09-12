package com.yukai.monash.student_seek;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarFragment;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class JobSeekerHomePage_Activity extends AppCompatActivity {
    private CoordinatorLayout coordinatorLayout;
    private  String userid;
    private String useremail;
    private BottomBar bottomBar;
    private Bundle savedInstanceState;

    private SharedPreferenceHelper sharedPreferenceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottom_navigate_homepage);

        this.savedInstanceState = savedInstanceState;

        //instantiate sharedPreferenceHelper
        sharedPreferenceHelper = new SharedPreferenceHelper(this,"Login Credentials");

        //if user has logged in, check if userid has been saved in sharedPreferecne
        if(sharedPreferenceHelper.loadPreferences("status") == "login")
        {
            //get userid from server if it does not exsit in sp
            if (!checkUseridInSharedPreferences("userid"))
                getUseridFromServer();
            coordinatorLayout = (CoordinatorLayout) findViewById(R.id.three_buttons_activity);
            //attach bottombar
            bottomBar = BottomBar.attach(this, savedInstanceState);
            bottomBar.setActiveTabColor("#a6ced3");
            bottomBar.useDarkTheme(true);

            bottomBar.setFragmentItems(getFragmentManager(), R.id.fragmentContainer,
                    new BottomBarFragment(searchFragment.newInstance(), R.drawable.ic_search, "Search"),
                    new BottomBarFragment(myjobsFragment.newInstance(), R.drawable.ic_work, "My jobs"),
                    new BottomBarFragment(ProfileFragment.newInstance(), R.drawable.ic_account_box, "Profile")
            );
        }
        else
        {
            coordinatorLayout = (CoordinatorLayout) findViewById(R.id.three_buttons_activity);
            //attach bottombar
            bottomBar = BottomBar.attach(this, savedInstanceState);
            bottomBar.setActiveTabColor("#a6ced3");
            bottomBar.useDarkTheme(true);

            //relate fragment to bottombar
            userid  = sharedPreferenceHelper.loadPreferences("userid");
                 bottomBar.setFragmentItems(getFragmentManager(), R.id.fragmentContainer,
                    new BottomBarFragment(searchFragment.newInstance(), R.drawable.ic_search, "Search"),
                     new BottomBarFragment(Unsignin_fragment.newInstance("My Jobs","Track the status of your applications here."), R.drawable.ic_work, "My jobs"),
                    new BottomBarFragment(Unsignin_fragment.newInstance("Profile","Create your profile in seconds and start applying"), R.drawable.ic_account_box, "Profile")

            );
        }


//        // Set the color for the active tab. Ignored on mobile when there are more than three tabs.
//        bottomBar.setActiveTabColor("#C2185B");

        // Use the dark theme. Ignored on mobile when there are more than three tabs.
        //bottomBar.useDarkTheme(true);

        // Use custom text appearance in tab titles.
        //bottomBar.setTextAppearance(R.style.MyTextAppearance);

        // Use custom typeface that's located at the "/src/main/assets" directory. If using with
        // custom text appearance, set the text appearance first.
        //bottomBar.setTypeFace("MyFont.ttf");

        //show profile fragment when cancling from edit_profile activity
//        Intent i = getIntent();
//        String fragmentTag =  i.getStringExtra("FragmentTag");
//        if(fragmentTag == "profile")
//        {
//            //mViewPager.setCurrentItem(3);
//        }

   }


//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//
//        // Necessary to restore the BottomBar's state, otherwise we would
//        // lose the current tab on orientation change.
//        bottomBar.onSaveInstanceState(outState);
//    }


    public boolean checkUseridInSharedPreferences(String key) {
        String tmp = sharedPreferenceHelper.loadPreferences(key);
        if (tmp != "")
        {
            userid = tmp;
            return true;
        }
        else
        {
            return false;
        }
    }

    public void getUseridFromServer()
    {
        Intent i = getIntent();
        String useremail = i.getStringExtra("email");
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("email", useremail);
        client.post(this, "http://173.255.245.239/jobs/get_userid.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                JSONObject object = null;
                try {
                    //get userid and save it to sp
                    object = new JSONObject(response);
                    userid = object.getString("userid");
                    Log.d("userid", userid);//test
                    sharedPreferenceHelper.savePreferences("userid",userid);
                   // sharedPreferenceHelper.savePreferences("status","login");
                } catch (JSONException e) {
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }


}
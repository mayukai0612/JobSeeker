package com.yukai.monash.student_seek;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class First_view_Activity extends AppCompatActivity{

    private SharedPreferenceHelper sharedPreferenceHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firstview);
        sharedPreferenceHelper = new SharedPreferenceHelper(this,"Login Credentials");
        sharedPreferenceHelper.savePreferences("status","logoff");
        sharedPreferenceHelper.deletePreferences("userid");
        sharedPreferenceHelper.deletePreferences("userType");


    }

    //navigate to job seeker homepage
    public void navigate_jobseeker(View view)
    {
        Intent i = new Intent(First_view_Activity.this,JobSeekerHomePage_Activity.class);
        sharedPreferenceHelper.savePreferences("userType","jobseeker");
        startActivity(i);
    }

    //navigate to employer homepage
    public void navigate_employer(View view)
    {
        Intent i = new Intent(First_view_Activity.this,EmployerHomePage_Activity.class);
        sharedPreferenceHelper.savePreferences("userType","employer");
        startActivity(i);
    }

}

package com.yukai.monash.student_seek;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * Created by yukaima on 8/05/16.
 */
public class Unsignin_fragment extends Fragment{

    private Button createProfile;
    private Button signin;
    private TextView tv_title;
    private TextView tv_hint;

    private String userid;

    public static Unsignin_fragment newInstance(String title,String hint) {
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("hint", hint);

        Unsignin_fragment unsignin_fragment = new Unsignin_fragment();
        unsignin_fragment.setArguments(args);
        return unsignin_fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_unsignin, container, false);

        //get textview
        tv_title = (TextView)view.findViewById(R.id.tv_unlogin_title);
        tv_hint = (TextView)view.findViewById(R.id.tv_unlogin_hint);

        //set text
        tv_title.setText(getArguments().getString("title"));
        tv_hint.setText(getArguments().getString("hint"));

        //get button
        createProfile = (Button)view.findViewById(R.id.btn_createProfile);
        signin = (Button)view.findViewById(R.id.btn_sign_up);

        //navigate to createprofile
        createProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),createprofile_Activity.class);
                startActivity(i);
            }
        });

        //navigate to signin
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),Signin_Activity.class);
                startActivity(i);
            }
        });

        return  view;
    }
}
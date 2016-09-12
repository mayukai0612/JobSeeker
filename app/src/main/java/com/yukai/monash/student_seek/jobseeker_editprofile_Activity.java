package com.yukai.monash.student_seek;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import cz.msebera.android.httpclient.Header;

/**
 * Created by yukaima on 20/05/16.
 */
public class jobseeker_editprofile_Activity extends AppCompatActivity implements View.OnClickListener {

    private static int CAMERA_REQUEST_CODE = 1;
    private static int GALLERY_REQUEST_CODE = 2;
    private static final String UserIconUrlPrefix = "http://173.255.245.239/jobs/image/";

    private Button btn_cancle;
    private Button btn_save;
    private LinearLayout line_userphoto;
    private com.yukai.monash.student_seek.RoundImageView roundImageView;
    private EditText aboutme;
    private EditText languages;
    private EditText Education;
    private TextView tv_fn;
    private TextView tv_ln;


    private View inflate;
    private TextView choosePhoto;
    private TextView takePhoto;
    private Dialog dialog;
    private com.yukai.monash.student_seek.RoundImageView seeker_icon;

    private String firstname;
    private String lastname;
    private String userid;
    private SharedPreferenceHelper sharedPreferenceHelper;
    private ProgressDialog progress;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_jobseeker_editprofile);


        //instantiate variables
        btn_cancle = (Button)findViewById(R.id.btn_cancle);
        btn_save =(Button)findViewById(R.id.btn_save);
        line_userphoto = (LinearLayout)findViewById(R.id.line_userphoto);
        roundImageView = (com.yukai.monash.student_seek.RoundImageView) findViewById(R.id.user_icon);
        aboutme = (EditText)findViewById(R.id.et_aboutme);
        languages = (EditText)findViewById(R.id.et_language);
        Education = (EditText)findViewById(R.id.et_education);
        tv_fn = (EditText)findViewById(R.id.tv_seeker_fn);
        tv_ln = (EditText)findViewById(R.id.tv_seeker_ln);
        seeker_icon = (com.yukai.monash.student_seek.RoundImageView)findViewById(R.id.user_icon);

//        Picasso.with(this).load("http://173.255.245.239/stu_seek/Image/20160524052034.png")
//                .resize(70,70)
//                .centerCrop()
//                .into(seeker_icon);

        //navigate to profile fragment
        btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(jobseeker_editprofile_Activity.this, JobSeekerHomePage_Activity.class);
                i.putExtra("FragmentTag", "profile");
                startActivity(i);
            }
        });

        //setting btn_save
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //save the data to database and navigate to profile fragment
                saveUserBackground();
            }
        });

        //call camera and gallery
        line_userphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(jobseeker_editprofile_Activity.this, R.style.ActionSheetDialogStyle);

                inflate = LayoutInflater.from(jobseeker_editprofile_Activity.this).inflate(R.layout.dialog_layout, null);

                choosePhoto = (TextView) inflate.findViewById(R.id.choosePhoto);
                takePhoto = (TextView) inflate.findViewById(R.id.takePhoto);
                choosePhoto.setOnClickListener(jobseeker_editprofile_Activity.this);
                takePhoto.setOnClickListener(jobseeker_editprofile_Activity.this);
                dialog.setContentView(inflate);
                Window dialogWindow = dialog.getWindow();
                dialogWindow.setGravity(Gravity.BOTTOM);
                WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                lp.y = 20;
                dialogWindow.setAttributes(lp);
                dialog.show();


            }
        });


        createNewRowsForNewUser();

        setUserInfo();

    }

    //create new rows if userid does not exist in USER_ICON & USER_BACKGROUND tables in database
    public void createNewRowsForNewUser()
    {
        progress= new ProgressDialog(this);
        progress.setMessage("Retrieve data...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();

        sharedPreferenceHelper = new SharedPreferenceHelper(this,"Login Credentials");
        userid = sharedPreferenceHelper.loadPreferences("userid");
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params =  new RequestParams();
        params.add("userid", userid);
        client.post("http://173.255.245.239/jobs/new_user_table.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    public void setUserInfo()
    {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        sharedPreferenceHelper = new SharedPreferenceHelper(this,"Login Credentials");
        userid = sharedPreferenceHelper.loadPreferences("userid");
        params.add("userid",userid);
        client.post(jobseeker_editprofile_Activity.this, "http://173.255.245.239/jobs/get_userinfo.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                JSONObject object = null;
                try {
                    object = new JSONObject(response);
                    Log.d("response",object.toString());
                    firstname = object.getString("firstname");
                    lastname = object.getString("lastname");
                    String slanguagues = object.getString("languages");
                    String SAboutme = object.getString("aboutme");
                    String seducation = object.getString("education");
                    String icon_filename = object.getString("icon_filename");
                    String icon_url = UserIconUrlPrefix + icon_filename + ".png";

                    tv_fn.setText(firstname);
                    tv_ln.setText(lastname);
                    aboutme.setText(SAboutme);
                    languages.setText(slanguagues);
                    Education.setText(seducation);

                    if(icon_filename == "null")
                    {
                        Picasso.with(jobseeker_editprofile_Activity.this).load(sharedPreferenceHelper.loadPreferences("linkedinPicUrl"))
                                .resize(70, 70)
                                .centerCrop()
                                .into(seeker_icon);
                    }
                    else
                    {
                        Picasso.with(jobseeker_editprofile_Activity.this).load(icon_url)
                                .resize(70, 70)
                                .centerCrop()
                                .into(seeker_icon);

                    }
                } catch (JSONException e) {

                   // Toast.makeText(jobseeker_editprofile_Activity.this, "wrong", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
        progress.dismiss();

    }

    @Override
    protected void onActivityResult(int requestcode, int resultcode, Intent data)
    {
        if(requestcode== CAMERA_REQUEST_CODE)
        {
            if(data == null)
            {
                return;
            }
            else
            {
                Bundle extras = data.getExtras();
                if(extras != null)
                {
                    Bitmap bm = extras.getParcelable("data");
                    roundImageView.setImageBitmap(bm);
                    sendImage(bm);

                }

            }
        }
        else if(requestcode == GALLERY_REQUEST_CODE)
        {
            if(data == null)
            {
                return;
            }
            Uri uri;
            uri = data.getData();
            Uri fileuri = convertUri(uri);
            Bitmap bitmap = null;
            InputStream is = null;
            try {
                is = getContentResolver().openInputStream(uri);
                 bitmap = BitmapFactory.decodeStream(is);
                is.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            roundImageView.setImageBitmap(bitmap);
            sendImage(bitmap);
        }

    }



    //save image to SD and create file uri
    private  Uri saveBitmap(Bitmap bm)
    {
        //set a file path
        File tmpDir =  new File(Environment.getExternalStorageDirectory()+ "/com.monash.yukai");
        if(!tmpDir.exists())
        {
            //if file path does not exsit, create one
            tmpDir.mkdir();
        }

        File img = new File (tmpDir.getAbsolutePath() + "usericon.png");
        try
        {
            //write bitmap to specified file
            FileOutputStream fos  = new FileOutputStream(img);
            bm.compress(Bitmap.CompressFormat.PNG,85,fos);
            fos.flush();
            fos.close();
            return Uri.fromFile(img);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return  null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    //convert content Uri to file Uri
    private Uri convertUri(Uri uri)
    {
        InputStream is = null;
        try
        {
            is  = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            is.close();
            saveBitmap(bitmap); //save bitmap to sd and convert content uri to file uri
        }catch (FileNotFoundException e)
        {e.printStackTrace();
        return null;}
        catch (IOException e){e.printStackTrace();}
        return null;
    }

    //send image to specific server
    public void sendImage(Bitmap bm)
    {
        //convert bitmap type to byte[] type
        ByteArrayOutputStream stream  = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG,60,stream);
        byte[] bytes = stream.toByteArray();
        //encode to string with Base64
        String img  = new String(Base64.encodeToString(bytes,Base64.DEFAULT));

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params =  new RequestParams();
        params.add("img",img);
        params.add("userid",userid);
        client.post("http://173.255.245.239/jobs/image/ImageUpload.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                Toast.makeText(jobseeker_editprofile_Activity.this, response, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(jobseeker_editprofile_Activity.this, "upload fail", Toast.LENGTH_LONG).show();

            }
        });

    }

        // set onclick listener for pop up dialog
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.takePhoto:
                    // create Intent to take a picture and return control to the calling application
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, CAMERA_REQUEST_CODE);
                    break;

                case R.id.choosePhoto:
                    //
                    Intent intentOfChoosePhoto = new Intent(Intent.ACTION_GET_CONTENT);
                    intentOfChoosePhoto.setType("image/*");
                    startActivityForResult(intentOfChoosePhoto,GALLERY_REQUEST_CODE);

                    break;
            }
            dialog.dismiss();
        }

    public void saveUserBackground()
    {
        userid = sharedPreferenceHelper.loadPreferences("userid");
        String firstname = tv_fn.getText().toString();
        String lastname = tv_ln.getText().toString();
        String sAboutme = aboutme.getText().toString();
        String sLanguages = languages.getText().toString();
        String sEducation = Education.getText().toString();

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("firstname",firstname);
        params.add("lastname",lastname);
        params.add("userid",userid);
        params.add("aboutme",sAboutme);
        params.add("languages",sLanguages);
        params.add("education",sEducation);
        client.post(this, "http://173.255.245.239/jobs/user_background.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Toast.makeText(jobseeker_editprofile_Activity.this, "update", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });

    }




}

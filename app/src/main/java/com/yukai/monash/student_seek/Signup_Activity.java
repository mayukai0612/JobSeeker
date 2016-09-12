package com.yukai.monash.student_seek;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

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
 * Created by yukaima on 18/05/16.
 */
public class Signup_Activity extends AppCompatActivity implements View.OnClickListener{

    private Button btn_signup;
    private EditText firstname;
    private EditText lastname;
    private EditText email;
    private EditText password;
    private com.yukai.monash.student_seek.RoundImageView addPhoto;


    private String fn;
    private String ln;
    private String sEmail;
    private String pwd;

    private View inflate;
    private TextView choosePhoto;
    private TextView takePhoto;
    private Dialog dialog;


    private static int CAMERA_REQUEST_CODE = 1;
    private static int GALLERY_REQUEST_CODE = 2;
    private static int CROP_REQUEST_CODE = 3;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        btn_signup = (Button)findViewById(R.id.btn_signup_finish);
        firstname = (EditText)findViewById(R.id.et_signupfn);
        lastname = (EditText)findViewById(R.id.et_signupln);
        email = (EditText)findViewById(R.id.et_signupemail);
        password = (EditText)findViewById(R.id.et_signuppwd);
        addPhoto = (com.yukai.monash.student_seek.RoundImageView)findViewById(R.id.addphoto);

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fn = firstname.getText().toString();
                ln = lastname.getText().toString();
                sEmail = email.getText().toString();
                pwd = password.getText().toString();


                if(fn.equals("") || ln.equals("") || sEmail.equals("") || pwd.equals(""))
                {
                    Toast.makeText(Signup_Activity.this,"Do not leave blank.",Toast.LENGTH_LONG).show();
                }
                else
                {
                    AsyncHttpClient client = new AsyncHttpClient();
                    RequestParams params  = new RequestParams();
                    params.add("firstname",fn);
                    params.add("lastname",ln);
                    params.add("email",sEmail);
                    params.add("password",pwd);
                    client.post("http://173.255.245.239/jobs/user_signup.php", params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            String response = new String(responseBody);
                            JSONObject object = null;
                            try {
                                object = new JSONObject(response);
                                String status = object.getString("status");

                                if(status.equals("exists"))
                                {
                                    Toast.makeText(Signup_Activity.this,"User exists,please change.",Toast.LENGTH_LONG).show();

                                }else if(status.equals("error"))
                                {
                                    Toast.makeText(Signup_Activity.this,"Error,please retry.",Toast.LENGTH_LONG).show();

                                }
                                else if(status.equals("success"))
                                {
                                    Toast.makeText(Signup_Activity.this,"Register successfully.",Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(Signup_Activity.this,JobSeekerHomePage_Activity.class);
                                    intent.putExtra("email",sEmail);
                                    SharedPreferenceHelper sharedPreferenceHelper = new SharedPreferenceHelper(Signup_Activity.this,"Login Credentials");
                                    sharedPreferenceHelper.savePreferences("status", "login");
                                    startActivity(intent);
                                    Signup_Activity.this.finish();
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

        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPhoto();
            }
        });



    }

    public void addPhoto()
    {
        dialog = new Dialog(Signup_Activity.this,R.style.ActionSheetDialogStyle);

        inflate = LayoutInflater.from(Signup_Activity.this).inflate(R.layout.dialog_layout, null);

        choosePhoto = (TextView) inflate.findViewById(R.id.choosePhoto);
        takePhoto = (TextView) inflate.findViewById(R.id.takePhoto);
        choosePhoto.setOnClickListener(Signup_Activity.this);
        takePhoto.setOnClickListener(Signup_Activity.this);
        dialog.setContentView(inflate);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity( Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.y = 20;
        dialogWindow.setAttributes(lp);
        dialog.show();
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
//                    Uri uri = saveBitmap(bm);
//                    Bitmap bitmap = null;
//                    try {
//                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                    addPhoto.setImageBitmap(bm);
                    sendImage(bm);

//                    startImageZoom(uri);
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
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            addPhoto.setImageBitmap(bitmap);
            sendImage(bitmap);
            //  startImageZoom(fileuri);
        }
//        else if (requestcode == CROP_REQUEST_CODE)
//        {
//            // data from crop
//              if (data == null)
//              {
//                return;
//             }
//            Bitmap bitmap = data.getParcelableExtra("data");
//            roundImageView.setImageBitmap(bitmap);
//
//        }
    }

    private  Uri saveBitmap(Bitmap bm)
    {
        File tmpDir =  new File(Environment.getExternalStorageDirectory()+ "/com.monash.yukai");
        if(!tmpDir.exists())
        {
            tmpDir.mkdir();
        }

        File img = new File (tmpDir.getAbsolutePath() + "usericon.png");
        try
        {
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
            saveBitmap(bitmap);
        }catch (FileNotFoundException e)
        {e.printStackTrace();
            return null;}
        catch (IOException e){e.printStackTrace();}
        return null;
    }

    //send image to specific server
    public void sendImage(Bitmap bm)
    {
        //convert bitmap to string type with Base64
        ByteArrayOutputStream stream  = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 60, stream);
        byte[] bytes = stream.toByteArray();
        String img  = new String(Base64.encodeToString(bytes, Base64.DEFAULT));

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params =  new RequestParams();
        params.add("img",img);
        params.add("userid", "1");
        client.post("http://173.255.245.239/stu_seek/Image/ImageUpload.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Toast.makeText(Signup_Activity.this, "upload success", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(Signup_Activity.this, "upload fail", Toast.LENGTH_LONG).show();

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



}

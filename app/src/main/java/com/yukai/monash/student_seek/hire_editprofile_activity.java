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
 * Created by yukaima on 22/05/16.
 */
public class hire_editprofile_activity extends AppCompatActivity implements View.OnClickListener{


    private static int CAMERA_REQUEST_CODE = 1;
    private static int GALLERY_REQUEST_CODE = 2;
    private static final String UserIconUrlPrefix = "http://173.255.245.239/jobs/image/";

    private LinearLayout photo_linerlayout;
    private Button btn_hire_save;
    private Button btn_hire_cancel;
    private EditText company;
    private TextView aboutus;
    private TextView firstname;
    private TextView lastname;
    private TextView about;
    private String userid;
    private com.yukai.monash.student_seek.RoundImageView employer_icon;




    private View inflate;
    private TextView choosePhoto;
    private TextView takePhoto;
    private Dialog dialog;

    private SharedPreferenceHelper sharedPreferenceHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hire_editprofile);

        photo_linerlayout = (LinearLayout)findViewById(R.id.line_photos);
        btn_hire_save = (Button)findViewById(R.id.btn_hire_save);
        btn_hire_cancel = (Button)findViewById(R.id.btn_hire_cancle);
        firstname = (TextView)findViewById(R.id.tv_hire_fn);
        lastname = (TextView)findViewById(R.id.tv_hire_ln);
        company = (EditText)findViewById(R.id.tv_seeker_company);
        aboutus = (EditText)findViewById(R.id.et_hire_aboutus);
        about = (TextView)findViewById(R.id.about_licences);
        employer_icon = (com.yukai.monash.student_seek.RoundImageView)findViewById(R.id.employer_icon);
        sharedPreferenceHelper = new SharedPreferenceHelper(this,"Login Credentials");

        //get company info and set view
        getNSetCompanyInfo();

        //about screen
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(hire_editprofile_activity.this,About_activity.class);
                startActivity(i);
            }
        });
            //change to coverphoto activity
            photo_linerlayout.setOnClickListener(new View.OnClickListener()

            {
                @Override
                public void onClick (View v){
                Intent i = new Intent(hire_editprofile_activity.this, coverphoto_activity.class);
                startActivity(i);
            }
            }

            );

        btn_hire_save.setOnClickListener(new View.OnClickListener()
                    {
                                             @Override
                                             public void onClick(View v) {
                                                 //check if user has entered compamny info
                                                 String companyName = company.getText().toString().trim();
                                                 String companyAboutus = aboutus.getText().toString().trim();
                                                 if(companyName.matches("") || companyAboutus.matches(""))
                                                 {
                                                     Toast.makeText(hire_editprofile_activity.this, "Please complete company info!", Toast.LENGTH_LONG).show();

                                                 }
                                                 else{
                                                 AsyncHttpClient client = new AsyncHttpClient();
                                                 RequestParams params = new RequestParams();
                                                 params.add("userid", sharedPreferenceHelper.loadPreferences("userid"));
                                                 params.add("company", company.getText().toString());
                                                 params.add("aboutus", aboutus.getText().toString());
                                                 client.post(hire_editprofile_activity.this, "http://173.255.245.239/jobs/save_company_info.php", params, new AsyncHttpResponseHandler() {
                                                     @Override
                                                     public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                         Toast.makeText(hire_editprofile_activity.this, "success", Toast.LENGTH_LONG).show();

                                                     }


                                                     @Override
                                                     public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                                                     }
                                                 });

                                                 Intent i = new Intent(hire_editprofile_activity.this, EmployerHomePage_Activity.class);
                                                 startActivity(i);
            }}
            }

            );
        btn_hire_cancel.setOnClickListener(new View.OnClickListener()

                                           {
                                               @Override
                                               public void onClick(View v) {
                                                   String companyName = company.getText().toString().trim();
                                                   String companyAboutus = aboutus.getText().toString().trim();
                                                   if(companyName.matches("") || companyAboutus.matches(""))
                                                   {
                                                       Toast.makeText(hire_editprofile_activity.this, "Please complete company info!", Toast.LENGTH_LONG).show();

                                                   }else{
                                                   Intent i = new Intent(hire_editprofile_activity.this, EmployerHomePage_Activity.class);
                                                   startActivity(i);
            }                                          }
            }

            );

        employer_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(hire_editprofile_activity.this,R.style.ActionSheetDialogStyle);

                inflate = LayoutInflater.from(hire_editprofile_activity.this).inflate(R.layout.dialog_layout, null);

                choosePhoto = (TextView) inflate.findViewById(R.id.choosePhoto);
                takePhoto = (TextView) inflate.findViewById(R.id.takePhoto);
                choosePhoto.setOnClickListener(hire_editprofile_activity.this);
                takePhoto.setOnClickListener(hire_editprofile_activity.this);
                dialog.setContentView(inflate);
                Window dialogWindow = dialog.getWindow();
                dialogWindow.setGravity( Gravity.BOTTOM);
                WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                lp.y = 20;
                dialogWindow.setAttributes(lp);
                dialog.show();
            }
        });


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
                    employer_icon.setImageBitmap(bm);
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

            employer_icon.setImageBitmap(bitmap);
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
        params.add("img", img);
        params.add("userid", userid);
        client.post("http://173.255.245.239/jobs/image/ImageUpload.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d("userid",userid);
                Log.d("response",responseBody.toString());
                String response = new String(responseBody);
                Toast.makeText(hire_editprofile_activity.this, response, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(hire_editprofile_activity.this, "upload fail", Toast.LENGTH_LONG).show();

            }
        });

    }

    public void getNSetCompanyInfo()
    {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        sharedPreferenceHelper = new SharedPreferenceHelper(this,"Login Credentials");
        userid = sharedPreferenceHelper.loadPreferences("userid");
        params.add("userid", sharedPreferenceHelper.loadPreferences("userid"));
        client.post(this, "http://173.255.245.239/jobs/get_company_info.php", params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String response = new String(responseBody);
                        JSONObject object = null;
                        try {
                            object = new JSONObject(response);
                            String companyname = object.getString("company");
                            String SAboutus = object.getString("aboutus");
                            String fn = object.getString("firstname");
                            String ln = object.getString("lastname");
                            String icon_filename = object.getString("icon_filename");
                            String icon_url = UserIconUrlPrefix + icon_filename + ".png";


                            Picasso.with(hire_editprofile_activity.this).load(icon_url)
                                    .resize(70,70)
                                    .centerCrop()
                                    .into(employer_icon);
                            company.setText(companyname);
                            aboutus.setText(SAboutus);
                            firstname.setText(fn);
                            lastname.setText(ln);
                        } catch (JSONException e) {
                   //   Toast.makeText(hire_editprofile_activity.this, "wrong", Toast.LENGTH_LONG).show();

                        }
                    }
                    @Override
                    public void onFailure ( int statusCode, Header[] headers,
                                            byte[] responseBody, Throwable error){

                    }
                }

        );
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

package com.yukai.monash.student_seek;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by yukaima on 10/05/16.
 */
public class Applicants_adapter extends BaseAdapter {

    private Context context;
    private List<Applicants_model> applicantssArrayList;
    private static final String UserIconUrlPrefix = "http://173.255.245.239/jobs/image/";

    public Applicants_adapter(Context context, List<Applicants_model> applicantssArrayList) {
        this.context = context;
        this.applicantssArrayList = applicantssArrayList ;
    }

    //set a viewholder
    public static class ViewHolder
    {

        TextView fn;
        TextView edu;
        ImageView applicantsphoto;
    }

    @Override
    public int getCount() {
        return applicantssArrayList.size();
    }

    @Override
    public Applicants_model getItem(int i)
    {
        return applicantssArrayList.get(i);
    }

    @Override
    public long getItemId(int i)
    {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
         final ViewHolder vh;
// Check if view has been created for the row. If not, lets inflate it
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); // Reference list item layout here
            view = inflater.inflate(R.layout.list_applicants_items, null);
            //Setup ViewHolder and attach to view
            vh = new ViewHolder();
            vh.fn =(TextView)view.findViewById(R.id.tv_applicants_fn);
            vh.edu = (TextView) view.findViewById(R.id.tv_applicants_edu);
            vh.applicantsphoto = (ImageView)view.findViewById(R.id.image_applicants_job_view);
            view.setTag(vh);
        } else {
// View has already been created, fetch our ViewHolder
            vh = (ViewHolder) view.getTag();
        }
// Assign values to the TextViews using the object we created
        vh.fn.setText(applicantssArrayList.get(i).getFirstname());
        vh.edu.setText(applicantssArrayList.get(i).getEmail());



        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("userid", applicantssArrayList.get(i).getUserid());
        client.post("http://173.255.245.239/jobs/get_user_iconfilename.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                JSONObject object = null;
                try{
                    object = new JSONObject(response);
                    String filename  = object.getString("icon_filename");
                    String path = UserIconUrlPrefix + filename + ".png";
                    Picasso.with(context).load(path).into(vh.applicantsphoto);

                }
                catch (JSONException e){}
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });

// Return the completed View of the row being processed
        return view; }
}


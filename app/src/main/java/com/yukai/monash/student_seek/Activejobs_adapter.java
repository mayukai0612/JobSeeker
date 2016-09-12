package com.yukai.monash.student_seek;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by yukaima on 10/05/16.
 */
public class Activejobs_adapter extends BaseAdapter {

    private Context context;
    private List<activejobs_model> activeJobsArrayList;
    private static final String UserIconUrlPrefix = "http://173.255.245.239/jobs/image/";


    public Activejobs_adapter(Context context, List<activejobs_model> activeJobsArrayList) {
        this.context = context;
        this.activeJobsArrayList = activeJobsArrayList ;
    }

    //set a viewholder
    public static class ViewHolder
    {

        TextView jobDesc;
        ImageView activejobsCoverPhoto;
    }

    @Override
    public int getCount() {
        return activeJobsArrayList.size();
    }

    @Override
    public activejobs_model getItem(int i)
    {
        return activeJobsArrayList.get(i);
    }

    @Override
    public long getItemId(int i)
    {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder vh;
// Check if view has been created for the row. If not, lets inflate it
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); // Reference list item layout here
                view = inflater.inflate(R.layout.list_employer_jobs_items, null);
 //Setup ViewHolder and attach to view
            vh = new ViewHolder();
            vh.jobDesc =(TextView)view.findViewById(R.id.list_job_desc);
            vh.activejobsCoverPhoto = (ImageView) view.findViewById(R.id.list_hire_jobs_image);

              view.setTag(vh);
        } else {
// View has already been created, fetch our ViewHolder
            vh = (ViewHolder) view.getTag();
        }
// Assign values to the TextViews using the object we created
           vh.jobDesc.setText(activeJobsArrayList.get(i).getJobdesc());
        String url = UserIconUrlPrefix + activeJobsArrayList.get(i).getCompanyPicFile() +".png";
        Picasso.with(context).load(url)
                .resize(70,70)
                .centerCrop()
                .into(vh.activejobsCoverPhoto);
        return view; }
}


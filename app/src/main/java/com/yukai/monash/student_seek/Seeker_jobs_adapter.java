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
public class Seeker_jobs_adapter extends BaseAdapter {

    private Context context;
    private List<JobModel> seekerJobArraylist;
    private static final String UserIconUrlPrefix = "http://173.255.245.239/jobs/image/";


    public Seeker_jobs_adapter(Context context, List<JobModel> seekerJobArraylist) {
        this.context = context;
        this.seekerJobArraylist = seekerJobArraylist;
    }

    //set a viewholder
    public static class ViewHolder
    {

        TextView jobDesc;
        TextView company;
        ImageView jobsCoverPhoto;
    }

    @Override
    public int getCount() {
        return seekerJobArraylist.size();
    }

    @Override
    public JobModel getItem(int i)
    {
        return seekerJobArraylist.get(i);
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
            view = inflater.inflate(R.layout.list_user_jobs_items, null);
            //Setup ViewHolder and attach to view
            vh = new ViewHolder();
            vh.jobDesc =(TextView)view.findViewById(R.id.tv_seeker_desc);
            vh.company =(TextView)view.findViewById(R.id.tv_seeker_company);
            vh.jobsCoverPhoto = (ImageView)view.findViewById(R.id.image_applicants);

            view.setTag(vh);
        } else {
// View has already been created, fetch our ViewHolder
            vh = (ViewHolder) view.getTag();
        }
// Assign values to the TextViews using the object we created
        vh.jobDesc.setText(seekerJobArraylist.get(i).getJobdesc());
        vh.company.setText(seekerJobArraylist.get(i).getCompany());
        String url = UserIconUrlPrefix + seekerJobArraylist.get(i).getCompanyPicFile() +".png";
        Picasso.with(context).load(url)
                .resize(70,70)
                .centerCrop()
                .into(vh.jobsCoverPhoto);
        return view; }
}


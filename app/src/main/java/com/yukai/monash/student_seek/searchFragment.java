package com.yukai.monash.student_seek;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;
import com.dexafree.materialList.card.OnActionClickListener;
import com.dexafree.materialList.card.action.TextViewAction;
import com.dexafree.materialList.listeners.RecyclerItemClickListener;
import com.dexafree.materialList.view.MaterialListView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

/**
 * Created by yukaima on 8/05/16.
 */
public class searchFragment extends Fragment {

    private SearchView searchView;


    private Context mContext;
    private MaterialListView mListView;

    private  ArrayList<JobModel> jobsArrayList;
    private SharedPreferenceHelper sharedPreferenceHelper;

    private static final String UserIconUrlPrefix = "http://173.255.245.239/jobs/image/";


    public static searchFragment newInstance() {
        searchFragment sampleFragment = new searchFragment();
        return sampleFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);


        searchView = (SearchView) view.findViewById(R.id.searchView);
        mListView = (MaterialListView) view.findViewById(R.id.material_listview);
        sharedPreferenceHelper = new SharedPreferenceHelper(getContext(),"Login Credentials");

        //set searchview to show search box
      //  searchView.setIconifiedByDefault(true);
        //set searchview submitButton
        searchView.setSubmitButtonEnabled(true);
        //set searchview background
        searchView.setBackgroundColor(getResources().getColor(R.color.searchview_bg));

        //set hint on searchview
        searchView.setQueryHint("Search(e.g.sales,hostess)");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchJobs(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });



        //set card views
        mContext = getActivity();
        mListView.setItemAnimator(new SlideInLeftAnimator());
        mListView.getItemAnimator().setAddDuration(300);
        mListView.getItemAnimator().setRemoveDuration(300);

        //set empty view in card views
        final ImageView emptyView = (ImageView) view.findViewById(R.id.imageView);
        emptyView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        mListView.setEmptyView(emptyView);
        Picasso.with(getActivity())
                .load("https://www.skyverge.com/wp-content/uploads/2012/05/github-logo.png")
                .resize(100, 100)
                .centerInside()
                .into(emptyView);

        // Add the ItemTouchListener
        mListView.addOnItemTouchListener(new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull Card card, int position) {
                int index = Integer.parseInt("" + card.getTag());
                String employerid = jobsArrayList.get(index).getEmployerid();
                String jobid = jobsArrayList.get(index).getJobid();

                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams params = new RequestParams();
                String userid = sharedPreferenceHelper.loadPreferences("userid");
                params.add("emp_id", employerid);
                params.add("jobid", jobid);
                params.add("userid", userid);

                client.post(getContext(), "http://173.255.245.239/jobs/apply_job.php", params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String response = new String(responseBody);
                        JSONObject object = null;
                        try {
                            object = new JSONObject(response);
                            String success = object.getString("message");

                            if (success.equals("0")) {
                                Toast.makeText(getContext(), "Error,Please try later.", Toast.LENGTH_LONG).show();

                            } else if (success.equals("1")) {
                                Toast.makeText(getContext(), "Apply successfully.", Toast.LENGTH_LONG).show();
                            } else if (success.equals("2")) {
                                Toast.makeText(getContext(), "You have applied this job already.", Toast.LENGTH_LONG).show();
                            }


                        } catch (JSONException e) {
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                    }
                });
            }

            @Override
            public void onItemLongClick(@NonNull Card card, int position) {
                Log.d("LONG_CLICK", "" + card.getTag());
            }
        });

        getJobsArrayList();


        return view;
    }
    public void searchJobs(String query)
    {
        jobsArrayList = new ArrayList<JobModel>();
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        Log.d("query",query);
        params.add("keyword",query);
        client.post(getContext(), "http://173.255.245.239/jobs/get_jobs_info.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);


                JsonReader reader = new JsonReader(new StringReader(response));
                //reader.setLenient(true);
                if(responseBody.length > 0) {
                    jobsArrayList = new Gson().fromJson(reader, new TypeToken<List<JobModel>>() {
                    }.getType());


                    mListView.getAdapter().clearAll();
                    fillArray(jobsArrayList);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }


    private void fillArray(ArrayList<JobModel> jobsArrayListParam) {
        List<Card> cards = new ArrayList<>();
        try {
            for (int i = 0; i < jobsArrayListParam.size(); i++) {
                String desc = jobsArrayListParam.get(i).getJobdesc();
                String company = jobsArrayListParam.get(i).getCompany();
                String companyFileName = jobsArrayListParam.get(i).getCompanyPicFile();
                String picUrl = UserIconUrlPrefix + companyFileName + ".png";
                cards.add(getRandomCard(i, desc, company, picUrl));
            }
        }catch (NullPointerException e){}

        mListView.getAdapter().addAll(cards);
    }

    private Card getRandomCard(final int position,String company,String desciptionParam,String picUrl) {
       // String title = "Job number " + (position + 1);
        String description = desciptionParam;
        final CardProvider provider = new Card.Builder(getActivity())
                .setTag(""+position)
                .setDismissible()
                .withProvider(new CardProvider())
                .setLayout(R.layout.material_image_with_buttons_card)
               // .setTitle(title)
                .setDescription(company)
                .setDrawable(picUrl)
                .addAction(R.id.left_text_button, new TextViewAction(getActivity())
                        .setText(description)
                        .setTextResourceColor(R.color.black_button))
                .addAction(R.id.right_text_button, new TextViewAction(getActivity())
                        .setText("Apply")
                        .setTextResourceColor(R.color.accent_material_dark)
                        .setListener(new OnActionClickListener() {
                            @Override
                            public void onActionClicked(View view, Card card) {
                                //  Toast.makeText(mContext, position, Toast.LENGTH_SHORT).show();
                            }
                        }));


        if (position % 2 == 0) {
            provider.setDividerVisible(true);
        }

        return provider.endConfig().build();
    }

    private Card generateNewCard() {
        return new Card.Builder(getActivity())
                .setTag("BASIC_IMAGE_BUTTONS_CARD")
                .withProvider(new CardProvider())
                .setLayout(R.layout.material_basic_image_buttons_card_layout)
                .setTitle("I'm new")
                .setDescription("I've been generated on runtime!")
                .setDrawable(R.drawable.ic_favorite)
                .endConfig()
                .build();
    }

    public ArrayList<JobModel> getJobsArrayList()
    {
        jobsArrayList = new ArrayList<JobModel>();
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        client.post(getContext(), "http://173.255.245.239/jobs/get_jobs_info.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);

                JsonReader reader = new JsonReader(new StringReader(response));
                //reader.setLenient(true);

                jobsArrayList = new Gson().fromJson(reader, new TypeToken<List<JobModel>>() {}.getType());
                fillArray(jobsArrayList);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
        return jobsArrayList;
    }


}




package me.declangao.wordpressreader.app;


import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import me.declangao.wordpressreader.R;
import me.declangao.wordpressreader.adaptor.PostAdaptor;
import me.declangao.wordpressreader.model.Post;
import me.declangao.wordpressreader.util.Config;
import me.declangao.wordpressreader.util.JSONParser;


/**
 * Fragment to display main UI, including TabLayout and ListView.
 * Activities that contain this fragment must implement the
 * {@link PostListFragment.OnPostSelectedListener} interface
 * to handle interaction events.
 */
public class PostListFragment extends Fragment implements AdapterView.OnItemClickListener,
        AbsListView.OnScrollListener,
        SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "PostListFragment";
    private static final String CAT_ID = "id";

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private PostAdaptor postAdaptor;
    private FrameLayout frameLayout;
    // Widget to show user a loading message
    private TextView tvLoading;

    // List of all posts in the ListView
    private ArrayList<Post> postList = new ArrayList<>();
    // A flag to keep track if the app is currently loading new posts
    private boolean isLoading = false;

    private int mPage = 1; // Page number
    private int mCatId; // Category ID
    private int mPreviousPostNum = 0; // Number of posts in the list
    private int mPostNum; // Number of posts in the "new" list

    private OnPostSelectedListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param id ID of the category.
     * @return A new instance of PostListFragment.
     */
    public static PostListFragment newInstance(int id) {
        PostListFragment fragment = new PostListFragment();
        Bundle args = new Bundle();
        args.putInt(CAT_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    public PostListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCatId = getArguments().getInt(CAT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_post_list, container, false);

        // Pull to refresh layout
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        listView = (ListView) rootView.findViewById(R.id.list_view);
        frameLayout = (FrameLayout) rootView.findViewById(R.id.post_list_container);
        tvLoading = (TextView) rootView.findViewById(R.id.text_view_loading);

        // Pull to refresh listener
        swipeRefreshLayout.setOnRefreshListener(this);

        // Custom list adaptor for Post object
        postAdaptor = new PostAdaptor(getActivity(), postList);

        listView.setAdapter(postAdaptor);
        listView.setOnItemClickListener(this);
        listView.setOnScrollListener(this);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        loadFirstPage();
    }

    /**
     * Load the first page of a category
     */
    public void loadFirstPage(){
        mPage = 1; // Reset page number

        if (postList.isEmpty()) {
            showLoadingView();
            loadPosts(mPage, false);
        } else {
            hideLoadingView();
        }
    }

    /**
     * Load the next page of a category
     */
    public void loadNextPage(){
        mPage ++;
        loadPosts(mPage, true);
    }

    /**
     * Load posts from a specific page number
     *
     * @param page Page number
     * @param showLoadingMsg Flag to determine whether to show the loading msg to inform the user
     */
    private void loadPosts(int page, final boolean showLoadingMsg) {
        Log.d(TAG, "----------------- Loading category id " + mCatId +
                ", page " + String.valueOf(page));

        isLoading = true;

        if (showLoadingMsg) {
            Toast.makeText(getActivity(), getString(R.string.loading_articles),
                    Toast.LENGTH_LONG).show();
        }

        // Construct the proper API Url
        String url;
        if (mCatId == 0) { // The "All" tab
            url = Config.BASE_URL + "?json=get_posts&page=" + String.valueOf(page);
        } else { // Everything else
            url = Config.BASE_URL + "?json=get_category_posts&category_id=" + String.valueOf(mCatId)
                    + "&page=" + String.valueOf(page);
        }

        Log.d(TAG, url);
        // Request post JSON
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        swipeRefreshLayout.setRefreshing(false); // Stop when done

                        // Parse JSON data
                        postList.addAll(JSONParser.parsePosts(jsonObject));

                        // A temporary workaround to avoid duplicate posts in some rare
                        // circumstances by converting ArrayList to a LinkedHashSet
                        // without losing its order
                        Set<Post> set = new LinkedHashSet<>(postList);
                        postList.clear();
                        postList.addAll(new ArrayList<>(set));

                        mPostNum = postList.size(); // The newest post number
                        Log.d(TAG, "Number of posts: " + mPostNum);
                        postAdaptor.notifyDataSetChanged(); // Display the list

                        // Set ListView position
                        if (PostListFragment.this.mPage != 1) {
                            // Move the article list up by one row
                            listView.setSelection(listView.getFirstVisiblePosition() + 1);
                        }

                        // Loading finished. Set flag to false
                        isLoading = false;

                        hideLoadingView();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        isLoading = false;
                        hideLoadingView();
                        swipeRefreshLayout.setRefreshing(false);

                        volleyError.printStackTrace();
                        Log.d(TAG, "----- Error: " + volleyError.getMessage());

                        // Show a Snackbar with a retry button
                        Snackbar.make(frameLayout, R.string.error_load_posts,
                                Snackbar.LENGTH_LONG).setAction(R.string.action_retry,
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //loadFirstPage();
                                        loadPosts(mPage, true);
                                    }
                                }).show();
                    }
                });

        // Set timeout to 10 seconds instead of the default value 5 since my
        // crappy server is quite slow
        request.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Add request to request queue
        AppController.getInstance().addToRequestQueue(request, TAG);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // Get selected post
        Post p = postList.get(position);

        // Send data to MainActivity
        HashMap<String, String> map = new HashMap<>();
        map.put("id", String.valueOf(p.getId()));
        map.put("title", p.getTitle());
        map.put("date", p.getDate());
        map.put("author", p.getAuthor());
        map.put("content", p.getContent());
        map.put("url", p.getUrl());
        map.put("thumbnailURL", p.getThumbnailUrl());
        mListener.onPostSelected(map);
    }

    @Override
    public void onRefresh() {
        // Clear the list
        postList.clear();
        postAdaptor.notifyDataSetChanged();
        loadFirstPage();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        // Automatically load new posts if end of the list is reached
        if (mPostNum > mPreviousPostNum && !postList.isEmpty() && visibleItemCount != 0 &&
                totalItemCount > visibleItemCount && !isLoading &&
                (firstVisibleItem + visibleItemCount) == totalItemCount) {
            loadNextPage();
            // Update post number
            mPreviousPostNum = mPostNum;
            //
            //if (mPostNum > mPreviousPostNum) {
            //    //loading = true;
            //    loadNextPage();
            //    mPreviousPostNum = mPostNum;
            //} else {
            //    Log.d(TAG, "Showing toast!");
            //    Toast.makeText(getActivity(), "You have reached the end!",
            //            Toast.LENGTH_SHORT).show();
            //}
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (OnPostSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                    "must implement OnPostSelectedListener");
        }
    }

    // Interface used to communicate with MainActivity
    protected interface OnPostSelectedListener {
        void onPostSelected(HashMap<String, String> map);
    }

    /**
     * Show the loading view and hide the list
     */
    private void showLoadingView() {
        listView.setVisibility(View.INVISIBLE);
        tvLoading.setVisibility(View.VISIBLE);
    }

    /**
     * Hide the loading view and show the list
     */
    private void hideLoadingView() {
        tvLoading.setVisibility(View.INVISIBLE);
        listView.setVisibility(View.VISIBLE);
    }
}

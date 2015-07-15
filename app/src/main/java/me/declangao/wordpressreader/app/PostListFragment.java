package me.declangao.wordpressreader.app;


import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import me.declangao.wordpressreader.R;
import me.declangao.wordpressreader.adaptor.PostAdaptor;
import me.declangao.wordpressreader.model.Category;
import me.declangao.wordpressreader.model.Post;
import me.declangao.wordpressreader.util.Config;
import me.declangao.wordpressreader.util.JSONParser;


/**
 * Fragment to display main UI, including TabLayout and ListView.
 * Activities that contain this fragment must implement the
 * {@link TabLayoutFragment.OnPostSelectedListener} interface
 * to handle interaction events.
 */
public class PostListFragment extends Fragment implements TabLayout.OnTabSelectedListener,
        AdapterView.OnItemClickListener,
        AbsListView.OnScrollListener,
        SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "PostListFragment";

    // List of all categories
    private ArrayList<Category> categories = null;
    // List of all posts in the ListView
    private ArrayList<Post> postList = new ArrayList<>();

    private ProgressDialog mProgressDialog;
    private int mCatIndex; // Category index in the tabs
    private int mCatId; // Category ID
    // Page number
    private int mPage = 1;

    private SwipeRefreshLayout swipeRefreshLayout;
    private TabLayout tabLayout;
    private ListView listView;
    private PostAdaptor postAdaptor;

    // A flag to keep track if the app is currently loading new posts
    private boolean loading = false;

    private OnPostSelectedListener mListener;

    public PostListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Stops onDestroy() and onCreate() being called when the parent
        // activity is destroyed/recreated on configuration change
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_post_list, container, false);

        // Pull to refresh layout
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout2);
        tabLayout = (TabLayout) rootView.findViewById(R.id.tab_layout);
        listView = (ListView) rootView.findViewById(R.id.list2);

        // Display a progress dialog
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getString(R.string.loading_categories));
        // User cannot dismiss it by touching outside the dialog
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        // Make a request to get categories
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Config.CATEGORY_URL,
                null,
                new Response.Listener<JSONObject>() {
                    // Request succeeded
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.d(TAG, "JSON Dump:\n" + jsonObject.toString());
                        mProgressDialog.dismiss();

                        // Get categories from JSON data
                        categories = JSONParser.parseCategories(jsonObject);
                        // Create tabs for each category
                        createTabs(categories);
                        // Load the first page of "All" category
                        loadPosts();
                    }
                },
                // Request failed
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.d(TAG, "----- Volley Error -----");
                        mProgressDialog.dismiss();
                        //Toast.makeText(MainActivity.this, "Network error. Please try again later...",
                        //        Toast.LENGTH_SHORT).show();
                    }
                });
        // Add the request to request queue
        AppController.getInstance().addToRequestQueue(request);

        // Custom list adaptor for Post object
        postAdaptor = new PostAdaptor(getActivity(), postList);

        listView.setAdapter(postAdaptor);
        listView.setOnItemClickListener(this);
        listView.setOnScrollListener(this);

        // Pull to refresh listener
        swipeRefreshLayout.setOnRefreshListener(this);

        return rootView;
    }

    /**
     * Create tabs for all categories
     *
     * @param cats Category object ArrayList
     */
    private void createTabs(ArrayList<Category> cats) {
        for (int i = 0; i < cats.size(); i++) {
            tabLayout.addTab(tabLayout.newTab().setText(cats.get(i).getName()));
        }
        tabLayout.setOnTabSelectedListener(this);
    }

    /**
     * Load posts
     *
     */
    public void loadPosts(){
        loadPosts(mPage, true);
    }

    /**
     * Load posts from a specific page number
     *
     * @param page Page number
     * @param showProgressDialog flag to determine whether to show a ProgressDialog
     */
    private void loadPosts(int page, final boolean showProgressDialog) {
        Log.d(TAG, "----------------- Loading category " +
                categories.get(mCatIndex).getName() + ", page " + String.valueOf(page));

        loading = true;

        if (showProgressDialog) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage(getString(R.string.loading_articles));
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();
        }

        // Construct the proper API Url
        String url;
        if (mCatId == 0) { // The "All" tab
            url = Config.BASE_URL + "?json=get_posts&page=" + String.valueOf(page);
        } else { // Everything else
            url = Config.BASE_URL + "?json=get_category_posts&category_id=" +
                    String.valueOf(mCatId) + "&page=" + String.valueOf(page);
        }

        Log.d(TAG, url);
        // Request post JSON
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        if (showProgressDialog) {
                            mProgressDialog.dismiss();
                        }
                        swipeRefreshLayout.setRefreshing(false); // Stop when done

                        // Parse JSON data
                        postList.addAll(JSONParser.parsePosts(jsonObject));
                        postAdaptor.notifyDataSetChanged(); // Display the list

                        loading = false; // Loading finished. Set flag to false

                        // Set ListView position
                        if (PostListFragment.this.mPage != 1) {
                            // Move the article list up by one row
                            listView.setSelection(listView.getFirstVisiblePosition() + 1);
                        }
                        // Prepare for the next page
                        PostListFragment.this.mPage++;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        mProgressDialog.dismiss();
                        swipeRefreshLayout.setRefreshing(false);
                        volleyError.printStackTrace();
                        Log.d(TAG, "----- Error: " + volleyError.getMessage());
                        Toast.makeText(getActivity(), "Network error. Please try again later...",
                                Toast.LENGTH_SHORT).show();
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
    public void onTabSelected(TabLayout.Tab tab) {
        Log.d(TAG, "Showing tab: " + categories.get(tab.getPosition()).getName());

        mCatId = categories.get(tab.getPosition()).getId();
        mCatIndex = tab.getPosition();
        mPage = 1; // Set page to 1 to load the first page of a new category
        postList.clear(); // Clear the list before loading a new category
        loadPosts();
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

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
        mPage = 1; // Refresh only the first page
        // Clear the list
        postList.clear();
        postAdaptor.notifyDataSetChanged();
        loadPosts(mPage, false);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                         int totalItemCount) {
        // Automatically load new posts if end of the list is reached
        if (visibleItemCount != 0 && totalItemCount > visibleItemCount &&
                !loading && (firstVisibleItem + visibleItemCount) == totalItemCount) {
            loading = true;
            loadPosts();
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
}

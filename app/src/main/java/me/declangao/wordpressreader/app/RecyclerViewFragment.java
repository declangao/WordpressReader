package me.declangao.wordpressreader.app;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import me.declangao.wordpressreader.R;
import me.declangao.wordpressreader.adaptor.MyRecyclerViewAdaptor;
import me.declangao.wordpressreader.model.Post;
import me.declangao.wordpressreader.util.Config;
import me.declangao.wordpressreader.util.JSONParser;

/**
 * Fragment to display a RecyclerView.
 * Activities that contain this fragment must implement the
 * {@link RecyclerViewFragment.PostListListener} interface
 * to handle interaction events.
 */
public class RecyclerViewFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "RecyclerViewFragment";
    protected static final String CAT_ID = "id";
    protected static final String QUERY = "query";

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private MyRecyclerViewAdaptor mAdaptor;
    private LinearLayoutManager mLayoutManager;
    // Widget to show user a loading message
    private TextView mLoadingView;

    // List of all posts in the ListView
    private ArrayList<Post> postList = new ArrayList<>();
    // A flag to keep track if the app is currently loading new posts
    private boolean isLoading = false;

    private int mPage = 1; // Page number
    private int mCatId; // Category ID
    private int mPreviousPostNum = 0; // Number of posts in the list
    private int mPostNum; // Number of posts in the "new" list
    private String mQuery = ""; // Query string used for search result
    // Flag to determine if current fragment is used to show search result
    private boolean isSearch = false;

    // Keep track of the list items
    private int mPastVisibleItems;
    private int mVisibleItemCount;

    private PostListListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param id ID of the category.
     * @return A new instance of RecyclerViewFragment.
     */
    public static RecyclerViewFragment newInstance(int id) {
        RecyclerViewFragment fragment = new RecyclerViewFragment();
        Bundle args = new Bundle();
        args.putInt(CAT_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Use this factory method to create a new instance of this fragment
     * using the provided parameters to display search result.
     *
     * @param query search query.
     * @return A new instance of RecyclerViewFragment.
     */
    public static RecyclerViewFragment newInstance(String query) {
        RecyclerViewFragment fragment = new RecyclerViewFragment();
        Bundle args = new Bundle();
        args.putString(QUERY, query);
        fragment.setArguments(args);
        return fragment;
    }

    public RecyclerViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mCatId = getArguments().getInt(CAT_ID, -1);
            mQuery = getArguments().getString(QUERY, "");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_recycler_view, container, false);

        // Pull to refresh layout
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mLoadingView = (TextView) rootView.findViewById(R.id.text_view_loading);
        mLayoutManager = new LinearLayoutManager(getActivity());

        // Pull to refresh listener
        mSwipeRefreshLayout.setOnRefreshListener(this);

        // RecyclerView adaptor for Post object
        mAdaptor = new MyRecyclerViewAdaptor(postList, new MyRecyclerViewAdaptor.OnItemClickListener() {
            @Override
            public void onItemClick(Post post) {
                mListener.onPostSelected(post, isSearch);
            }
        });

        mRecyclerView.setHasFixedSize(true); // Every row in the list has the same size
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdaptor);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            // Automatically load new posts if end of the list is reached
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                //super.onScrolled(recyclerView, dx, dy);
                mVisibleItemCount = mLayoutManager.getChildCount();
                mPastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();
                int totalItemCount = mLayoutManager.getItemCount();

                if (mPostNum > mPreviousPostNum && !postList.isEmpty() && mVisibleItemCount != 0 &&
                        totalItemCount > mVisibleItemCount && !isLoading &&
                        (mVisibleItemCount + mPastVisibleItems) >= totalItemCount) {
                    loadNextPage();
                    // Update post number
                    mPreviousPostNum = mPostNum;
                }
            }
        });

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
            // Reset post number to 0
            mPreviousPostNum = 0;
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
     * @param showLoadingMsg Flag to determine whether to show Toast loading msg to inform the user
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
        if (!mQuery.isEmpty()) { // Not empty mQuery means this list is for search result.
            isSearch = true;
            url = Config.BASE_URL + "?json=get_search_results&search=" + mQuery +
                    "&page=" + String.valueOf(page);
        } else { // Empty mQuery means normal list of posts
            isSearch = false;

            if (mCatId == 0) { // The "All" tab
                url = Config.BASE_URL + "?json=get_posts&page=" + String.valueOf(page);
            } else { // Everything else
                isSearch = false;
                url = Config.BASE_URL + "?json=get_category_posts&category_id=" + String.valueOf(mCatId)
                        + "&page=" + String.valueOf(page);
            }

        }

        Log.d(TAG, url);
        // Request post JSON
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        mSwipeRefreshLayout.setRefreshing(false); // Stop when done

                        // Parse JSON data
                        postList.addAll(JSONParser.parsePosts(jsonObject));

                        // A temporary workaround to avoid downloading duplicate posts in some
                        // rare circumstances by converting ArrayList to a LinkedHashSet without
                        // losing its order
                        Set<Post> set = new LinkedHashSet<>(postList);
                        postList.clear();
                        postList.addAll(new ArrayList<>(set));

                        mPostNum = postList.size(); // The newest post number
                        Log.d(TAG, "Number of posts: " + mPostNum);
                        mAdaptor.notifyDataSetChanged(); // Display the list

                        // Set ListView position
                        if (RecyclerViewFragment.this.mPage != 1) {
                            // Move the article list up by one row
                            // We don't actually need to add 1 here since position starts at 0
                            mLayoutManager.scrollToPosition(mPastVisibleItems + mVisibleItemCount);
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
                        mSwipeRefreshLayout.setRefreshing(false);

                        volleyError.printStackTrace();
                        Log.d(TAG, "----- Error: " + volleyError.getMessage());

                        // Show a Snackbar with a retry button
                        Snackbar.make(mRecyclerView, R.string.error_load_posts,
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
    public void onRefresh() {
        // Clear the list
        postList.clear();
        mAdaptor.notifyDataSetChanged();
        loadFirstPage();
    }

    /**
     * Show the loading view and hide the list
     */
    private void showLoadingView() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingView.setVisibility(View.VISIBLE);
    }

    /**
     * Hide the loading view and show the list
     */
    private void hideLoadingView() {
        mLoadingView.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (PostListListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                    "must implement PostListListener");
        }
    }

    // Interface used to communicate with MainActivity
    public interface PostListListener {
        void onPostSelected(Post post, boolean isSearch);
    }

}

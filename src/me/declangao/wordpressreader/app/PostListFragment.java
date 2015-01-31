package me.declangao.wordpressreader.app;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.declangao.wordpressreader.model.Post;
import me.declangao.wordpressreader.adaptor.PostAdaptor;
import me.declangao.wordpressreader.R;


/**
 * A Fragment that contains a list of posts.
 */
public class PostListFragment extends android.support.v4.app.Fragment {
    private static final String TAG = PostListFragment.class.getSimpleName();

    private int catId;
    //private String title;

    // An ArrayList of all posts in the ListView
    private List<Post> postList = new ArrayList<Post>();
    private ListView listView;
    private PostAdaptor postAdaptor;
    // Page number
    private int page=1;

    // A simple flag to determine if the app is currently loading new articles
    private boolean loading = false;

    private ProgressDialog progressDialog;

    public PostListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get catID sent from MainActivity
        Bundle args = getArguments();
        catId = args.getInt("catId");
        //title = args.getString("title");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the fragment layout, which is just a ListView
        View v = inflater.inflate(R.layout.fragment_post_list, null);
        // Create the ListView
        listView = (ListView) v.findViewById(R.id.listView);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Create the custom list adaptor for Post object
        postAdaptor = new PostAdaptor(getActivity(), postList);
        listView.setAdapter(postAdaptor);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            // Load next page when the user scrolls to the end of the list
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {
                // If the end of the list reached and is currently not loading
                if (!loading && (firstVisibleItem + visibleItemCount) == totalItemCount) {
                    loading = true; // Set flag to true
                    loadPosts(page); // Start loading
                    //Toast.makeText(getActivity(), "Loading...", Toast.LENGTH_LONG).show();
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Post p = postList.get(position);

                //Toast.makeText(getActivity(), p.getTitle(), Toast.LENGTH_SHORT).show();

                // Prepare the ArticleActivity
                Intent intent = new Intent(getActivity(), ArticleActivity.class);
                intent.putExtra("title", p.getTitle());
                intent.putExtra("content", p.getContent());
                intent.putExtra("date", p.getDate().split(" ")[0]);
                intent.putExtra("commentCount", p.getCommentCount());
                intent.putExtra("author", p.getAuthor());
                intent.putExtra("id", p.getId());
                intent.putExtra("url", p.getUrl());
                startActivity(intent);

                /*
                Bundle bundle = new Bundle();
                bundle.putString("title", p.getTitle());
                bundle.putString("content", p.getContent());
                bundle.putString("date", p.getDate().split(" ")[0]);
                bundle.putInt("commentCount", p.getCommentCount());
                bundle.putString("author", p.getAuthor());
                bundle.putInt("id", p.getId());

                ArticleFragment articleFragment = ArticleFragment.newInstance(bundle, p);
                articleFragment.setArguments(bundle);

                FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
                ft.addToBackStack(null);
                ft.replace(R.id.main_frame, articleFragment).commit();
                */
            }
        });
    }

    /*
     * Load posts from a specific page number
     * @param page Page number
     */
    public void loadPosts(int page) {
        Log.d(TAG, "----------------- Loading category " + catId + ", page " + String.valueOf(page));

        // Show a progress dialog while downloading posts
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.loading_articles));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        // Construct the proper API Url
        String url;
        if (catId == 0) { // The "All" tab
            url = MainActivity.BASE_URL + "?json=get_posts&page=" + String.valueOf(page);
        } else { // Everything else
            url = MainActivity.BASE_URL + "?json=get_category_posts&category_id=" +
                    String.valueOf(catId) + "&page=" + String.valueOf(page);
        }

        // Make a Json request with Volley
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        //Log.d(TAG, "----------------- Enter OnResponse");
                        progressDialog.dismiss();

                        try {
                            // Parse Json data
                            JSONArray postArray = jsonObject.getJSONArray("posts");
                            //int numPerPage = jsonObject.getInt("count");

                            // Go through each post
                            for (int i = 0; i < postArray.length(); i++) {
                                JSONObject postObject = postArray.getJSONObject(i);

                                // Create a temp Post object
                                Post post = new Post();
                                // Setup the Post object
                                post.setTitle(postObject.getString("title"));
                                post.setThumbnailUrl(postObject.getString("thumbnail"));
                                post.setCommentCount(postObject.getInt("comment_count"));
                                post.setViewCount(postObject.getJSONObject("custom_fields")
                                        .getJSONArray("post_views_count").getString(0));

                                post.setDate(postObject.getString("date"));
                                post.setContent(postObject.getString("content"));
                                post.setAuthor(postObject.getJSONObject("author").getString("name"));
                                post.setId(postObject.getInt("id"));
                                post.setUrl(postObject.getString("url"));

                                postList.add(post); // Add to the ArrayList
                            }

                            postAdaptor.notifyDataSetChanged(); // Data has changed
                            loading = false; // Loading finished. Set flag to false

                            // Set ListView position
                            if (PostListFragment.this.page != 1) {
                                //listView.setSelection((PostListFragment.this.page -1) *
                                //        numPerPage - 1);
                                // Move the article list up by one row
                                listView.setSelection(listView.getFirstVisiblePosition() + 1);
                            }
                            // Add 1 to page to prepare for the next page
                            PostListFragment.this.page++;

                        } catch (JSONException e) {
                            Log.d(TAG, e.getMessage());
                            Log.d(TAG, "----------------- Json Exception");
                            Toast.makeText(getActivity(), "Json Exception. Please try again.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        progressDialog.dismiss();
                        Log.d(TAG, "----------------- Error: " + volleyError.getMessage());
                        Toast.makeText(getActivity(), "Network error. Please try again later...",
                                Toast.LENGTH_SHORT).show();
                    }
                });

        // Add request to request queue
        AppController.getInstance().addToRequestQueue(request, TAG);
    }

}

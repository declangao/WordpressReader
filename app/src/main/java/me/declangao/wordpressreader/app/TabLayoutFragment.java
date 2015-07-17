package me.declangao.wordpressreader.app;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.ArrayList;

import me.declangao.wordpressreader.R;
import me.declangao.wordpressreader.adaptor.PostListFragmentPagerAdaptor;
import me.declangao.wordpressreader.model.Category;
import me.declangao.wordpressreader.util.Config;
import me.declangao.wordpressreader.util.JSONParser;


public class TabLayoutFragment extends Fragment {
    private static final String TAG = "TabLayoutFragment";

    private ProgressDialog mProgressDialog;
    private TabLayout tabLayout;
    private FrameLayout frameLayout;
    private ViewPager viewPager;

    // List of all categories
    protected static ArrayList<Category> categories = null;

    public TabLayoutFragment() {
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
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_tab_layout, container, false);

        tabLayout = (TabLayout) rootView.findViewById(R.id.tab_layout);
        frameLayout = (FrameLayout) rootView.findViewById(R.id.frame_container);
        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        // Preload 1 page to either side of the current tab
        viewPager.setOffscreenPageLimit(1);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        loadCategories();
    }

    /**
     * Download categories and create tabs
     *
     */
    private void loadCategories() {
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
                        mProgressDialog.dismiss();

                        // Get categories from JSON data
                        categories = JSONParser.parseCategories(jsonObject);

                        PostListFragmentPagerAdaptor adaptor = new
                                PostListFragmentPagerAdaptor(getChildFragmentManager(), categories);
                        viewPager.setAdapter(adaptor);
                        tabLayout.setupWithViewPager(viewPager);
                    }
                },
                // Request failed
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.d(TAG, "----- Volley Error -----");
                        mProgressDialog.dismiss();
                        Snackbar.make(frameLayout, R.string.error_load_categories,
                                Snackbar.LENGTH_LONG).setAction(R.string.action_retry,
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        loadCategories();
                                    }
                                }).show();
                    }
                });
        // Add the request to request queue
        AppController.getInstance().addToRequestQueue(request);
    }
}

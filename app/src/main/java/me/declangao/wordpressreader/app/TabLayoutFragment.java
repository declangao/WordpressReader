package me.declangao.wordpressreader.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.ArrayList;

import me.declangao.wordpressreader.R;
import me.declangao.wordpressreader.adaptor.RecyclerViewFragmentPagerAdaptor;
import me.declangao.wordpressreader.model.Category;
import me.declangao.wordpressreader.util.Config;
import me.declangao.wordpressreader.util.JSONParser;

/**
 * Fragment to display TabLayout and ViewPager.
 * Activities that contain this fragment must implement the
 * {@link TabLayoutFragment.TabLayoutListener} interface
 * to handle interaction events.
 */
public class TabLayoutFragment extends Fragment implements SearchView.OnQueryTextListener {
    private static final String TAG = "TabLayoutFragment";

    private ProgressDialog mProgressDialog;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private Toolbar toolbar;
    private SearchView searchView;
    private MenuItem searchMenuItem;

    // List of all categories
    protected static ArrayList<Category> categories = null;

    private TabLayoutListener mListener;

    public TabLayoutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Stops onDestroy() and onCreate() being called when the parent
        // activity is destroyed/recreated on configuration change
        setRetainInstance(true);

        // Display a search menu
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_tab_layout, container, false);

        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((MainActivity)getActivity()).setSupportActionBar(toolbar);

        mTabLayout = (TabLayout) rootView.findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        // Preload 1 page to either side of the current page
        mViewPager.setOffscreenPageLimit(1);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        loadCategories();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(TAG, "onCreateOptionsMenu()");

        inflater.inflate(R.menu.menu_main, menu);

        // Create expandable & collapsible SearchView
        SearchManager searchManager = (SearchManager)
                getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchMenuItem.getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setIconifiedByDefault(false); // Expanded by default
        //searchView.requestFocus();
        searchView.setQueryHint(getString(R.string.search_hint));
        searchView.setOnQueryTextListener(this);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            searchView.requestFocus();
        }
        return true;
    }

    /**
     * Reset the ActionBar to show proper menu and collapse SearchView
     */
    protected void resetActionBar() {
        ((MainActivity)getActivity()).setSupportActionBar(toolbar);
        searchMenuItem.collapseActionView();
    }

    /**
     * Download categories and create tabs
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

                        RecyclerViewFragmentPagerAdaptor adaptor = new
                                RecyclerViewFragmentPagerAdaptor(getChildFragmentManager(), categories);
                        mViewPager.setAdapter(adaptor);
                        mTabLayout.setupWithViewPager(mViewPager);
                    }
                },
                // Request failed
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.d(TAG, "----- Volley Error -----");
                        mProgressDialog.dismiss();
                        // Show an INDEFINITE Snackbar. New in design support lib v22.2.1.
                        Snackbar.make(mTabLayout, R.string.error_load_categories,
                                Snackbar.LENGTH_INDEFINITE).setAction(R.string.action_retry,
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

    @Override
    public boolean onQueryTextSubmit(String query) {
        searchView.clearFocus(); // Hide soft keyboard
        mListener.onSearchSubmitted(query); // Deal with fragment transaction on MainActivity
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (TabLayoutListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                    "must implement PostListListener");
        }
    }

    // Interface used to communicate with MainActivity
    public interface TabLayoutListener {
        void onSearchSubmitted(String query);
    }

}

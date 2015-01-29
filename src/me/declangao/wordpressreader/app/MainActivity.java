package me.declangao.wordpressreader.app;

import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import me.declangao.wordpressreader.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity implements ActionBar.TabListener{
    // Fill in your own WordPress URL, don't forget the "/" in the end
    public static final String BASE_URL = "http://URL_TO_YOUR_OWN_SITE/";

    List<Fragment> fragmentList = new ArrayList<Fragment>();
    Fragment f = null;
    PostListFragment plf = null;

    private ProgressDialog progressDialog;
    public static String[] categoryArray;
    private int[] categoryIdArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set tab navigation layout
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Display a progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading_categories));
        // User cannot dismiss it by touching outside the dialog
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        // Category API URL
        String url = BASE_URL + "?json=get_category_index";

        // Make a Json request using Volley
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    // Request succeeded
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            // Get "categories" Json array
                            JSONArray categories = jsonObject.getJSONArray("categories");
                            // Get category names and Ids
                            // We need to add 1 here since we will manually create an "All" tab later
                            categoryArray = new String[categories.length()+1];
                            categoryIdArray = new int[categories.length()+1];
                            // Manually create an "All" category
                            categoryArray[0] = getString(R.string.tab_all);
                            categoryIdArray[0] = 0;

                            // Go through all categories and get their details
                            for (int i=0; i<categories.length(); i++) {
                                // Get individual category Json object
                                JSONObject catObj = categories.getJSONObject(i);
                                // Get their titles and Ids
                                // Here we add 1 to the indexes since the first tab is "All"
                                categoryArray[i+1] = catObj.getString("title");
                                categoryIdArray[i+1] = catObj.getInt("id");
                            }

                            // Create tabs
                            for (int i=0; i<categoryArray.length ;i++) {
                                ActionBar.Tab tab = actionBar.newTab();
                                tab.setText(categoryArray[i]);
                                tab.setTabListener(MainActivity.this);
                                actionBar.addTab(tab);
                            }
                        } catch (JSONException e) {
                            // Opps...
                            Toast.makeText(MainActivity.this,  "Json Exception. " +
                                    "This is most likely our fault. Please try again later...",
                                    Toast.LENGTH_SHORT).show();
                            Log.d("-------------------------", "Json Exception");
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();
                    }
                },
                // Request failed
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.d("-------------------------", "Volley Error");
                        Toast.makeText(MainActivity.this, "Network error. Please try again later...",
                                Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
        // Add the request to request queue
        AppController.getInstance().addToRequestQueue(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

        if (fragmentList.size() > tab.getPosition()) {
            fragmentList.get(tab.getPosition());
        }
        if (f == null) {
            plf = new PostListFragment();
            // Create a new bundle for PostListFragment
            Bundle args = new Bundle();
            // args.putString("title", categoryArray[tab.getPosition()]);
            args.putInt("catId", categoryIdArray[tab.getPosition()]); // Store category ID in bundle
            plf.setArguments(args);
            fragmentList.add(plf);
        } else {
            plf = (PostListFragment) f;
        }

        fragmentTransaction.replace(android.R.id.content, plf);
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        if (fragmentList.size() > tab.getPosition()) {
            fragmentTransaction.remove(fragmentList.get(tab.getPosition()));
        }
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }
}

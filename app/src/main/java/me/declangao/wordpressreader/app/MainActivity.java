package me.declangao.wordpressreader.app;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.HashMap;

import me.declangao.wordpressreader.R;

public class MainActivity extends AppCompatActivity implements
        PostListFragment.OnPostSelectedListener,
        PostFragment.OnCommentSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String TAB_LAYOUT_FRAGMENT_TAG = "TabLayoutFragment";
    public static final String POST_FRAGMENT_TAG = "PostFragment";
    public static final String COMMENT_FRAGMENT_TAG = "CommentFragment";

    private FragmentManager fm = null;
    private TabLayoutFragment tlf;
    private PostFragment pf;
    private CommentFragment cf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup action bar with the new toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fm = getSupportFragmentManager();

        // Setup fragments
        tlf = new TabLayoutFragment();
        pf = new PostFragment();
        cf = new CommentFragment();

        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.container, pf, POST_FRAGMENT_TAG);
        ft.add(R.id.container, cf, COMMENT_FRAGMENT_TAG);
        ft.add(R.id.container, tlf, TAB_LAYOUT_FRAGMENT_TAG);
        ft.hide(pf);
        ft.hide(cf);
        ft.commit();
    }

    /**
     * Invoked when an article in a list is selected
     *
     * @param map HashMap that stores properties of a Post object
     */
    @Override
    public void onPostSelected(HashMap<String, String> map) {
        pf = (PostFragment) getSupportFragmentManager().findFragmentByTag(POST_FRAGMENT_TAG);
        // Set necessary arguments
        Bundle args = new Bundle();
        args.putInt("id", Integer.valueOf(map.get("id")));
        args.putString("title", map.get("title"));
        args.putString("date", map.get("date"));
        args.putString("author", map.get("author"));
        args.putString("content", map.get("content"));
        args.putString("url", map.get("url"));
        args.putString("thumbnailUrl", map.get("thumbnailURL"));
        //pf.setArguments(args);
        // Setup PostFragment to display the right post
        pf.setUIArguments(args);

        FragmentTransaction ft = fm.beginTransaction();
        ft.hide(tlf);
        ft.show(pf);
        ft.addToBackStack(null);
        ft.commit();
    }

    /**
     * Invoked when comment menu is selected
     *
     * @param id ID of the article, assigned by WordPress
     */
    @Override
    public void onCommentSelected(int id) {
        cf = (CommentFragment) getSupportFragmentManager().findFragmentByTag(COMMENT_FRAGMENT_TAG);
        Bundle args = new Bundle();
        args.putInt("id", id);
        //cf.setArguments(args);
        // Setup CommentFragment to display the right comments page
        cf.setUIArguments(args);

        FragmentTransaction ft = fm.beginTransaction();
        ft.hide(pf);
        ft.show(cf);
        ft.addToBackStack(null);
        ft.commit();
    }


    // Commented out coz we will let fragments handle their own Options Menus
    //@Override
    //public boolean onCreateOptionsMenu(Menu menu) {
    //    // Inflate the menu; this adds items to the action bar if it is present.
    //    getMenuInflater().inflate(R.menu.menu_main, menu);
    //    return true;
    //}

    //@Override
    //public boolean onOptionsItemSelected(MenuItem item) {
    //    // Handle action bar item clicks here. The action bar will
    //    // automatically handle clicks on the Home/Up button, so long
    //    // as you specify a parent activity in AndroidManifest.xml.
    //
    //    //int id = item.getItemId();
    //    //switch (id) {
    //    //    case R.id.action_refresh:
    //    //        plf.loadPosts();
    //    //        return true;
    //    //    case R.id.action_settings:
    //    //        return true;
    //    //    default:
    //    //        return super.onOptionsItemSelected(item);
    //    //}
    //
    //    return true;
    //}
}

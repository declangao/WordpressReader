package me.declangao.wordpressreader.app;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import java.util.HashMap;

import me.declangao.wordpressreader.R;


public class MainActivity extends AppCompatActivity implements
        TabLayoutFragment.OnPostSelectedListener,
        PostFragment.OnCommentSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private FragmentManager fm = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fm = getSupportFragmentManager();

        // The main UI
        TabLayoutFragment tlf = new TabLayoutFragment();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.container, tlf);
        ft.commit();
    }

    // Commented out coz we will let fragments handle their own Options Menu
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

    /**
     * Invoked when an article in a list is selected
     *
     * @param map HashMap that stores properties of a Post object
     */
    @Override
    public void onPostSelected(HashMap<String, String> map) {
        PostFragment pf = new PostFragment();
        // Set necessary arguments
        Bundle args = new Bundle();
        args.putInt("id", Integer.valueOf(map.get("id")));
        args.putString("title", map.get("title"));
        args.putString("date", map.get("date"));
        args.putString("author", map.get("author"));
        args.putString("content", map.get("content"));
        args.putString("url", map.get("url"));
        args.putString("thumbnailUrl", map.get("thumbnailURL"));
        pf.setArguments(args);

        FragmentTransaction ft = fm.beginTransaction();
        //ft.replace(R.id.container, pf);
        ft.add(R.id.container, pf);
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
        CommentFragment commentFragment = new CommentFragment();
        Bundle args = new Bundle();
        args.putInt("id", id);
        commentFragment.setArguments(args);

        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.container, commentFragment);
        ft.addToBackStack(null);
        ft.commit();
    }

}

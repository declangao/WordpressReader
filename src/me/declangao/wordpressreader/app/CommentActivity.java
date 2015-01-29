package me.declangao.wordpressreader.app;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import me.declangao.wordpressreader.R;
import me.declangao.wordpressreader.util.MyWebViewClient;

public class CommentActivity extends ActionBarActivity {
    private String disqusThreadId;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        // Get the intent so we can access data stored in the bundle
        Intent intent = getIntent();
        id = intent.getIntExtra("id", 0); // Get post ID
        // Create disqusThreadId
        disqusThreadId = id + " " + MainActivity.BASE_URL + "?p=" + id;
        // Create Disqus URL for this specific post
        String url = MainActivity.BASE_URL + "showcomments.php?disqus_id=" + disqusThreadId;

        // Setup WebView
        WebView webViewComment = (WebView) findViewById(R.id.webView_comment);
        WebSettings webSettings = webViewComment.getSettings();
        // Enable JavaScript
        webSettings.setJavaScriptEnabled(true);
        // Let the WebView handle links in stead of opening links in external web browsers
        webViewComment.requestFocusFromTouch();
        // User a custom WebViewClient to solve Disqus login and logout issues on Android
        // See http://globeotter.com/blog/disqus-android-code/
        webViewComment.setWebViewClient(new MyWebViewClient(url));
        webViewComment.setWebChromeClient(new WebChromeClient());

        // Lollipop only code
        // Required on Lollipop in order to save login state
        if (Build.VERSION.SDK_INT >= 21) {
            // Enable cookies
            CookieManager.getInstance().setAcceptThirdPartyCookies(webViewComment, true);
        }

        // Load Disqus
        webViewComment.loadUrl(url);
        Log.d("------------------- Comment", disqusThreadId);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_comment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        /*
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        */

        return super.onOptionsItemSelected(item);
    }
}

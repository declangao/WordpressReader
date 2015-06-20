package me.declangao.wordpressreader.app;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import me.declangao.wordpressreader.R;
import me.declangao.wordpressreader.util.Config;
import me.declangao.wordpressreader.util.MyWebViewClient;

public class CommentActivity extends AppCompatActivity {
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
        disqusThreadId = id + " " + Config.BASE_URL + "?p=" + id;
        // Create Disqus URL for this specific post
        String url = Config.BASE_URL + "showcomments.php?disqus_id=" + disqusThreadId;

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
        Log.d("--------------- Comment", disqusThreadId);
    }

}

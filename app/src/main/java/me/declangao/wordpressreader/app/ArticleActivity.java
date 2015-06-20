package me.declangao.wordpressreader.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import me.declangao.wordpressreader.R;

public class ArticleActivity extends AppCompatActivity {
    private WebView webView;
    private Intent intent;
    private ShareActionProvider mShareActionProvider;
    private String url;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        // Get the intent so we can access data stored in the bundle
        intent = getIntent();

        url = intent.getStringExtra("url");
        title = intent.getStringExtra("title");

        // Construct HTML content
        // First some CSS
        String html = "<style>img{max-width:100%;height:auto;} " +
                "iframe{width:100%;height:56%;}</style> ";
        // Article Title
        html += "<h2>" + title +"</h2> ";
        // Date & author
        html += "<h4>" + intent.getStringExtra("date") + "    " +
                intent.getStringExtra("author") + "</h4>";
        // The actual content
        html += intent.getStringExtra("content");

        // Create the WebView
        webView = (WebView) findViewById(R.id.webView);
        // Enable JavaScript in order to be able to Play Youtube videos
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        // Load and display HTML content
        // Use "charset=UTF-8" to support non-English language
        webView.loadData(html, "text/html; charset=UTF-8", null);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_article, menu);

        // Get share menu item
        MenuItem item = menu.findItem(R.id.action_share);
        // Initialise ShareActionProvider
        // Use MenuItemCompat.getActionProvider(item) since we are using AppCompat support library
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        // Share the article URL
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT, title + "\n" + url);
        mShareActionProvider.setShareIntent(i);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_comments) {
            // Create intent for comment activity
            Intent i = new Intent(ArticleActivity.this, CommentActivity.class);
            // The comment activity only needs to access the article's ID
            i.putExtra("id", intent.getIntExtra("id", 0));
            startActivity(i);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

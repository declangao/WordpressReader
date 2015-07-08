package me.declangao.wordpressreader.app;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import me.declangao.wordpressreader.R;
import me.declangao.wordpressreader.util.Config;
import me.declangao.wordpressreader.util.MyWebViewClient;

public class CommentFragment extends Fragment {
    private static final String TAG = "CommentFragment";

    public CommentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();
        int id = args.getInt("id");

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_comment, container, false);

        // Create disqusThreadId
        String disqusThreadId = id + " " + Config.BASE_URL + "?p=" + id;
        // Create Disqus URL for this specific post
        String url = Config.BASE_URL + "showcomments.php?disqus_id=" + disqusThreadId;

        // Setup WebView
        WebView webViewComment = (WebView) v.findViewById(R.id.webView_comment);
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
        Log.d(TAG, "Disqus Thread Id: " + disqusThreadId);

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Properly remove menu
        // Can't seem to be able to remove the menu with setHasOptionsMenu(false)
        // So we will just hide all menu items for now
        menu.findItem(R.id.action_comments).setVisible(false);
        menu.findItem(R.id.action_share).setVisible(false);
        menu.findItem(R.id.action_send_to_wear).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }
}

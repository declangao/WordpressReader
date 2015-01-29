package me.declangao.wordpressreader.util;

import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * This WebViewClient sub class is created to solve Disqus login and logout issues on Android
 * See http://globeotter.com/blog/disqus-login-and-logout/
 */
public class MyWebViewClient extends WebViewClient {
    private String myUrl;

    public MyWebViewClient(String myUrl) {
        this.myUrl = myUrl;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        if(url.indexOf("logout")>-1 || url.indexOf("login-success")>-1 ){
            Log.d("------------ URL login-success: ", url);
            Log.d("------------ URL indexof: ", String.valueOf(url.indexOf("login-success")));

            view.loadUrl(myUrl);
        }
        if(url.indexOf("disqus.com/_ax/twitter/complete")>-1||
                url.indexOf("disqus.com/_ax/facebook/complete")>-1||
                url.indexOf("disqus.com/_ax/google/complete")>-1){
            view.loadUrl(myUrl);
        }
    }
}

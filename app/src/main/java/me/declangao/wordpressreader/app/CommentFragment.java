package me.declangao.wordpressreader.app;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import me.declangao.wordpressreader.R;
import me.declangao.wordpressreader.util.Config;
import me.declangao.wordpressreader.util.MyWebViewClient;

/**
 * Fragment to display a Disqus comments page.
 * Activities that contain this fragment must implement the
 * {@link CommentFragment.CommentListener} interface
 * to handle interaction events.
 */
public class CommentFragment extends Fragment {
    private static final String TAG = "CommentFragment";

    private WebView webView;
    private Toolbar toolbar;

    private CommentListener mListener;

    public CommentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_comment, container, false);

        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);

        // Setup WebView
        webView = (WebView) rootView.findViewById(R.id.webView_comment);

        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mListener.onHomePressed();
        }
        return true;
    }

    /**
     * Since we can't call setArguments() on an existing fragment, we make our own!
     *
     * @param args Bundle containing information about the new comments page
     */
    protected void setUIArguments(final Bundle args) {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                int id = args.getInt("id");

                // Create Disqus Thread ID
                String disqusThreadId = id + " " + Config.BASE_URL + "?p=" + id;
                // Create Disqus URL for this specific post
                String url = Config.BASE_URL + "showcomments.php?disqus_id=" + disqusThreadId;

                WebSettings webSettings = webView.getSettings();
                // Enable JavaScript
                webSettings.setJavaScriptEnabled(true);
                // Let the WebView handle links in stead of opening links in external web browsers
                webView.requestFocusFromTouch();
                // User a custom WebViewClient to solve Disqus login and logout issues on Android
                // See http://globeotter.com/blog/disqus-android-code/
                webView.setWebViewClient(new MyWebViewClient(url));
                webView.setWebChromeClient(new WebChromeClient());

                // Lollipop only code
                // Required on Lollipop in order to save login state
                if (Build.VERSION.SDK_INT >= 21) {
                    // Enable cookies
                    CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
                }

                // Load Disqus
                webView.loadUrl(url);

                Log.d(TAG, "Disqus Thread Id: " + disqusThreadId);

                // Reset Actionbar
                ((MainActivity) getActivity()).setSupportActionBar(toolbar);
                ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                ((MainActivity)getActivity()).getSupportActionBar()
                        .setTitle(getString(R.string.action_comments));
            }
        });
    }

    /**
     * Eliminate the chance of showing previous content by clearing the fragment on hidden.
     *
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        if (webView != null && hidden) {
            webView.loadData("", "text/html; charset=UTF-8", null);
        }

        super.onHiddenChanged(hidden);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (CommentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement CommentListener");
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface CommentListener {
        void onHomePressed();
    }
}

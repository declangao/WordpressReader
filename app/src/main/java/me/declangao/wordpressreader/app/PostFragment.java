package me.declangao.wordpressreader.app;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;

import me.declangao.wordpressreader.R;

/**
 * Fragment to display content of an article in a WebView.
 * Activities that contain this fragment must implement the
 * {@link PostFragment.OnCommentSelectedListener} interface
 * to handle interaction events.
 */
public class PostFragment extends Fragment {
    private static final String TAG = "PostFragment";

    private int id;
    private String title;
    private String content;
    private String url;
    private String thumbnailUrl;

    private OnCommentSelectedListener mListener;

    public PostFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Needed to show Options Menu
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_post, container, false);

        // Get data
        Bundle args = getArguments();
        id = args.getInt("id");
        title = args.getString("title");
        String date = args.getString("date");
        String author = args.getString("author");
        content = args.getString("content");
        url = args.getString("url");
        thumbnailUrl = args.getString("thumbnailUrl");

        // Construct HTML content
        // First, some CSS
        String html = "<style>img{max-width:100%;height:auto;} " +
                "iframe{width:100%;height:56%;}</style> ";
        // Article Title
        html += "<h2>" + title +"</h2> ";
        // Date & author
        html += "<h4>" + date + " " + author + "</h4>";
        // The actual content
        html += content;

        // Create the WebView
        WebView webView = (WebView) v.findViewById(R.id.webview_post);
        // Enable JavaScript in order to be able to Play Youtube videos
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        // Load and display HTML content
        // Use "charset=UTF-8" to support non-English language
        webView.loadData(html, "text/html; charset=UTF-8", null);

        Log.d(TAG, "Showing post, ID: " + id);

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_post, menu);

        // Get share menu item
        MenuItem item = menu.findItem(R.id.action_share);
        // Initialise ShareActionProvider
        // Use MenuItemCompat.getActionProvider(item) since we are using AppCompat support library
        ShareActionProvider shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        // Share the article URL
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT, title + "\n" + url);
        shareActionProvider.setShareIntent(i);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_comments:
                mListener.onCommentSelected(id);
                return true;
            case R.id.action_share:
                return true;
            case R.id.action_send_to_wear:
                sendToWear();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void sendToWear() {
        // Intent used to run app on the phone from watch
        Intent viewIntent = new Intent(getActivity(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, viewIntent, 0);

        // Use BigTextStyle to read long notification
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        //bigTextStyle.setBigContentTitle(title);
        // Use Html.fromHtml() to remove HTML tags
        bigTextStyle.bigText(Html.fromHtml(content));

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity());
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentIntent(pendingIntent)
                .setStyle(bigTextStyle);

        // Load thumbnail as background image
        ImageRequest ir = new ImageRequest(thumbnailUrl, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap bitmap) {
                builder.setLargeIcon(bitmap);

                NotificationManagerCompat notificationManagerCompat =
                        NotificationManagerCompat.from(getActivity());
                notificationManagerCompat.cancel(id);
                notificationManagerCompat.notify(id, builder.build());
            }
        }, 0, 0, null, null);
        AppController.getInstance().getRequestQueue().add(ir);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnCommentSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnCommentSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
    public interface OnCommentSelectedListener {
        void onCommentSelected(int id);
    }

}

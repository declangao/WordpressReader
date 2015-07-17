package me.declangao.wordpressreader.adaptor;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

import me.declangao.wordpressreader.app.AppController;
import me.declangao.wordpressreader.model.Post;
import me.declangao.wordpressreader.R;

/**
 * Custom adaptor for the ListView
 */
public class PostAdaptor extends BaseAdapter {
    private Activity activity;
    private LayoutInflater layoutInflater;
    // A list of posts
    private List<Post> posts;
    // ImageLoader from Volley, used to load images for NetworkImageView
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    // Constructor
    public PostAdaptor(Activity activity, List<Post> posts) {
        this.activity = activity;
        this.posts = posts;
    }

    @Override
    public int getCount() {
        return posts.size();
    }

    @Override
    public Object getItem(int position) {
        return posts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (layoutInflater == null) {
            layoutInflater = (LayoutInflater)
                    activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if (convertView == null) {
            // Inflate list_row layout for each row in the list
            convertView = layoutInflater.inflate(R.layout.list_row, null);

            // Create the ViewHolder
            viewHolder = new ViewHolder();
            viewHolder.networkImageView =
                    (NetworkImageView) convertView.findViewById(R.id.thumbnail);
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            viewHolder.commentCount = (TextView) convertView.findViewById(R.id.comment_count);
            //viewHolder.viewCount = (TextView) convertView.findViewById(R.id.view_count);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Get a post and its properties
        Post post = posts.get(position);
        viewHolder.networkImageView.setImageUrl(post.getThumbnailUrl(), imageLoader);
        viewHolder.title.setText(post.getTitle());
        viewHolder.commentCount.setText(post.getCommentCount() + " Comment(s)");
        //viewHolder.viewCount.setText(post.getViewCount() + " View(s)\t\t");

        return convertView;
    }

    // View holder pattern
    private static class ViewHolder {
        NetworkImageView networkImageView;
        TextView title;
        TextView commentCount;
        //TextView viewCount;
    }
}

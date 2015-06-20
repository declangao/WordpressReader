package me.declangao.wordpressreader.util;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import me.declangao.wordpressreader.app.AppController;
import me.declangao.wordpressreader.model.Post;

/**
 * Created by Declan on 20/06/15.
 */
public class JSONParser {
    private static final String	TAG	= "JSONParser";

    /**
     * Parse JSON data and return a Post instance
     *
     * @param postObject JSONObject represents a single blog post
     */
    public static Post parsePost(JSONObject postObject) {
        Post post = new Post();

        try{
            // Configure the Post object
            post.setTitle(postObject.optString("title", "N/A"));
            // Use a default thumbnail image if one doesn't exist
            post.setThumbnailUrl(postObject.optString("thumbnail", Config.DEFAULT_THUMBNAIL_URL));
            post.setCommentCount(postObject.optInt("comment_count", 0));
            //post.setViewCount(postObject.getJSONObject("custom_fields")
            //        .getJSONArray("post_views_count").getString(0));

            post.setDate(postObject.optString("date", "N/A"));
            post.setContent(postObject.optString("content", "N/A"));
            post.setAuthor(postObject.getJSONObject("author").optString("name", "N/A"));
            post.setId(postObject.optInt("id"));
            post.setUrl(postObject.optString("url"));
        } catch (JSONException e) {
            Log.d(TAG, "----------------- Json Exception");
            Log.d(TAG, e.getMessage());
            AppController.showToast("Json Exception. Please try again.");
        }

        return post;
    }
}

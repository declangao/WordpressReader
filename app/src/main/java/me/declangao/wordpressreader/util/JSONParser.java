package me.declangao.wordpressreader.util;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import me.declangao.wordpressreader.R;
import me.declangao.wordpressreader.app.AppController;
import me.declangao.wordpressreader.model.Category;
import me.declangao.wordpressreader.model.Post;

/**
 * Utility class used to parse JSON data
 */
public class JSONParser {
    private static final String	TAG	= "JSONParser";

    /**
     * Parse JSON data and return an ArrayList of Category objects
     *
     * @param jsonObject JSON data
     * @return A list of Category objects
     */
    public static ArrayList<Category> parseCategories(JSONObject jsonObject) {
        ArrayList<Category> categoryArrayList = new ArrayList<>();

        try {
            // Get "categories" Json array
            JSONArray categories = jsonObject.getJSONArray("categories");

            // Create "All" category
            Category all = new Category();
            all.setId(0);
            all.setName(AppController.getInstance().getString(R.string.tab_all));
            categoryArrayList.add(all);

            // Go through all categories and get their details
            for (int i=0; i<categories.length(); i++) {
                // Get individual category Json object
                JSONObject catObj = categories.getJSONObject(i);
                Log.d(TAG, "Parsing " + catObj.getString("title") + ", ID " + catObj.getInt("id"));
                Category c = new Category();
                c.setId(catObj.getInt("id"));
                c.setName(catObj.getString("title"));
                categoryArrayList.add(c);
            }
        } catch (JSONException e) {
            Log.d(TAG, "----------------- Json Exception");
            e.printStackTrace();
            return null;
        }

        return categoryArrayList;
    }

    /**
     * Parse JSON data and return an ArrayList of Post objects
     *
     * @param jsonObject JSON data
     * @return A list of Post objects
     */
    public static ArrayList<Post> parsePosts(JSONObject jsonObject) {
        ArrayList<Post> posts = new ArrayList<>();

        try{
            JSONArray postArray = jsonObject.getJSONArray("posts");
            // Go through each post
            for (int i = 0; i < postArray.length(); i++) {
                JSONObject postObject = postArray.getJSONObject(i);

                Post post = new Post();
                // Configure the Post object
                post.setTitle(postObject.optString("title", "N/A"));
                // Use a default thumbnail if one doesn't exist
                post.setThumbnailUrl(postObject.optString("thumbnail",
                        Config.DEFAULT_THUMBNAIL_URL));
                post.setCommentCount(postObject.optInt("comment_count", 0));
                //post.setViewCount(postObject.getJSONObject("custom_fields")
                //        .getJSONArray("post_views_count").getString(0));

                post.setDate(postObject.optString("date", "N/A"));
                post.setContent(postObject.optString("content", "N/A"));
                post.setAuthor(postObject.getJSONObject("author").optString("name", "N/A"));
                post.setId(postObject.optInt("id"));
                post.setUrl(postObject.optString("url"));

                JSONObject featuredImages = postObject.optJSONObject("thumbnail_images");
                if (featuredImages != null) {
                    post.setFeaturedImageUrl(featuredImages.optJSONObject("full")
                            .optString("url", Config.DEFAULT_THUMBNAIL_URL));
                }

                posts.add(post);
            }
        } catch (JSONException e) {
            Log.d(TAG, "----------------- Json Exception");
            Log.d(TAG, e.getMessage());
            return null;
        }

        return posts;
    }
}

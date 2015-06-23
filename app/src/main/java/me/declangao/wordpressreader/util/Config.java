package me.declangao.wordpressreader.util;

/**
 * Configuration class
 */
public class Config {
    // Fill in your own WordPress URL, don't forget the "/" at the end
    public static final String BASE_URL = "http://URL_TO_YOUR_WORDPRESS_SITE/";

    public static String CATEGORY_URL = BASE_URL + "?json=get_category_index";
    public static final String DEFAULT_THUMBNAIL_URL = "http://i.imgur.com/D2aUC3s.png";
}

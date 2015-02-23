package com.codepath.ab.instagramphotoviewer;

import java.net.URI;

/**
 * Created by andrewblaich on 2/18/15.
 */
public class HelperVars {
    public static String redirectUrl = "http://codepath.com";
    public static String clientId = ""; //cleared out for check-in
    public static String URL_AUTH_REQUEST = "https://instagram.com/oauth/authorize/?client_id="+clientId+"&redirect_uri="+redirectUrl+"&response_type=token";
    public static String URL_MEDIA_POPULAR = "https://api.instagram.com/v1/media/popular?client_id=";
    public static String URL_MEDIA_POPULAR_AT = "https://api.instagram.com/v1/media/popular?access_token=";
    public static String URL_SELF_FEED = "https://api.instagram.com/v1/users/self/feed?access_token=";
    public static String URL_USERNAME_FEED_1 = "https://api.instagram.com/v1/users/"; //append username
    public static String URL_USERNAME_FEED_2 = "/media/recent/?access_token="; //append access token
    public static boolean useGrid = false;
    public static boolean makeAllImages = true;
    public static String auth_token = "nothing";
    public static boolean enablePopular = true;
    public static String prefname = "localprefs";
}

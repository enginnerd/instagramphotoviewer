package com.codepath.ab.instagramphotoviewer;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class PhotosActivity extends ActionBarActivity {
//    private static final String CLIENT_ID = HelperVars.clientId;
    private static SwipeRefreshLayout swipeContainer;
    private static ArrayList<InstagramPhoto> photos;
    private static InstagramPhotosAdapter aPhotos;
//    private static boolean useGrid = HelperVars.useGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setup();
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity =(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    private void setup(){


        if(!isNetworkAvailable(this)){
            Toast.makeText(this, "A network connection is needed", Toast.LENGTH_LONG).show();
            return;
        }
        if(!HelperVars.useGrid) {
            setContentView(R.layout.activity_photos);
        }else {
            setContentView(R.layout.activity_photos_grid);
        }
        //setup pull to refresh
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchPopularPhotos(null);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        //SEND OUT API REQUEST to POPULAR PHOTOS
        photos = new ArrayList<>();

        //create the adapter linking it to the resource
        aPhotos = new InstagramPhotosAdapter(this, 0, photos);

         ListView lvPhotos = (ListView) findViewById(R.id.lvPhotos);
         lvPhotos.setAdapter(aPhotos);

        fetchPopularPhotos(null);
    }

    private void viewComments(InstagramPhoto sPhoto){
        FragmentManager fm = getSupportFragmentManager();
        FullCommentsDialog fullCommentsDialog = FullCommentsDialog.newInstance(sPhoto);
        fullCommentsDialog.show(fm, "Comments");
    }

    private void startVideo(InstagramPhoto sPhoto){
        FragmentManager fm = getSupportFragmentManager();
        FullVideoDialog fullVideoDialog = FullVideoDialog.newInstance(sPhoto);
        fullVideoDialog.show(fm, "Full Video");
    }
    private void showFullImage(InstagramPhoto sPhoto){
        FragmentManager fm = getSupportFragmentManager();
        FullImageDialog fullImageDialog = FullImageDialog.newInstance(sPhoto);
        fullImageDialog.show(fm, "Full Image");
    }

    public static void fetchPopularPhotos(String username){
        String url;
        if(username==null){
            if(!HelperVars.enablePopular) {
                url = HelperVars.URL_SELF_FEED + HelperVars.auth_token;
            }else{
                url = HelperVars.URL_MEDIA_POPULAR_AT + HelperVars.auth_token;
            }
        }else{
            url = HelperVars.URL_USERNAME_FEED_1 + username + HelperVars.URL_USERNAME_FEED_2 + HelperVars.auth_token;
        }
        aPhotos.clear();
        aPhotos.notifyDataSetChanged();
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, null, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                aPhotos.clear();
                JSONArray photosJSON = null;
                try{
//                    Log.i("DEBUG",  response.getJSONObject("pagination").toString(2));
                    photosJSON = response.getJSONArray("data");
//                    Toast.makeText(getApplicationContext(), "# of items: " + photosJSON.length(), Toast.LENGTH_LONG).show();
                    for(int i=0; i < photosJSON.length(); i++){
                        JSONObject photoJSON = photosJSON.getJSONObject(i);
                        InstagramPhoto photo = new InstagramPhoto();
                        photo.type = photoJSON.getString("type");

                       long time = photoJSON.getLong("created_time");
                        CharSequence createdTimeStr = DateUtils.getRelativeTimeSpanString(time * 1000, System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS);
                        String polishedTime = "";
                        String[] time_tokens = createdTimeStr.toString().split(" ");
                        if(time_tokens.length>=2){
                            polishedTime = time_tokens[0]+time_tokens[1].substring(0,1);
                        }
                        photo.createdTime = polishedTime;
                        photo.userId = photoJSON.getJSONObject("user").getString("id");
                        if(photo.type.equals("image") || photo.type.equals("video")) { //figure out how to handle video later
                            if (photoJSON.getJSONObject("user").has("username")) {
                                photo.username = photoJSON.getJSONObject("user").getString("username");
                            } else {
                                photo.username = "No Username";
                            }

                            photo.usernameUrl = photoJSON.getJSONObject("user").getString("profile_picture");

                            /*
                            "data": [
                                {
                                    "created_time": "1280780324",
                                    "text": "Really amazing photo!",
                                    "from": {
                                        "username": "snoopdogg",
                                        "profile_picture": "http://images.instagram.com/profiles/profile_16_75sq_1305612434.jpg",
                                        "id": "1574083",
                                        "full_name": "Snoop Dogg"
                                    },
                                    "id": "420"
                                },
                                ...
                            ]

                             */
                            if(photoJSON.has("comments")){
                                Log.i("DEBUG", "Comments: " + photoJSON.getJSONObject("comments").toString());
                                photo.commentsObj = photoJSON.getJSONObject("comments");
                            }

//                            if(photoJSON.has("location")){
//                                Log.i("DEBUG", "LOCATION: " + photoJSON.getJSONObject("location").toString(2));
////                                photo.location = photoJSON.getJSONObject("location").getString("name");
//                            }else{
//                                photo.location = null;
//                            }

                            //issues with this
                            try {
                                if (photoJSON.has("caption")) {
                                    if (photoJSON.getJSONObject("caption").has("text")) {
                                        photo.caption = photoJSON.getJSONObject("caption").getString("text");
                                    } else {
                                        photo.caption = "No caption";
                                    }
                                } else {
                                    photo.caption = "No caption";
                                }
                            }catch(Exception e){
                                e.printStackTrace();
                                photo.caption = "No caption";
                            }


                            if (photo.type.equals("image")) {
                                if (photoJSON.getJSONObject("images").getJSONObject("standard_resolution").has("url")) {
                                    photo.imageUrl = photoJSON.getJSONObject("images").getJSONObject("standard_resolution").getString("url");
                                    photo.imageHeight = photoJSON.getJSONObject("images").getJSONObject("standard_resolution").getInt("height");
                                } else {
                                    photo.imageUrl = "http://codepath.com/images/logos/codepath.svg?1423734675";
                                    photo.imageHeight = 50;
                                }

                                photo.imageUrl_thumbnail = photoJSON.getJSONObject("images").getJSONObject("thumbnail").getString("url");
                                photo.imageHeight_thumbnail = photoJSON.getJSONObject("images").getJSONObject("thumbnail").getInt("height");

                            } else if (photo.type.equals("video")) {
//                                photo.username = "VIDEO";
//                                Log.i("DEBUG", "VIDEO: " + photoJSON.toString(2));
//                                if (photoJSON.getJSONObject("videos").getJSONObject("standard_resolution").has("url")) {
                                photo.videoUrl = photoJSON.getJSONObject("videos").getJSONObject("standard_resolution").getString("url");
                                photo.videoHeight = photoJSON.getJSONObject("videos").getJSONObject("standard_resolution").getInt("height");
                                photo.imageUrl = photoJSON.getJSONObject("images").getJSONObject("standard_resolution").getString("url");
                                photo.imageHeight = photoJSON.getJSONObject("images").getJSONObject("standard_resolution").getInt("height");
                                photo.imageUrl_thumbnail = photoJSON.getJSONObject("images").getJSONObject("thumbnail").getString("url");
                                photo.imageHeight_thumbnail = photoJSON.getJSONObject("images").getJSONObject("thumbnail").getInt("height");
//                                }
                            }

                            try{
                                if (photoJSON.getJSONObject("likes").has("count")) {
                                    photo.likesCount = photoJSON.getJSONObject("likes").getInt("count");
                                } else {
                                    photo.likesCount = 0;
                                }
                            }catch(Exception e){
                                e.printStackTrace();
                                photo.likesCount = 0;
                            }


                            //quick hack, to make all types images, fix later
//                            if(HelperVars.makeAllImages)
//                                photo.type = "image";
                            photos.add(photo);
                        }


                    }
                }catch(JSONException e){
                    e.printStackTrace();
                }
                swipeContainer.setRefreshing(false);
                //callback
                aPhotos.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("DEBUG", "Fetch timeline error: " + throwable.toString());
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //this is just buggy, needs fixing, and some sort of view flipper
        if (id == R.id.action_settings) {
            if(HelperVars.enablePopular) {
                item.setTitle("View Popular");
                HelperVars.enablePopular = false;
//                setup();
                fetchPopularPhotos(null);
            }else{
                item.setTitle("View Self");
                HelperVars.enablePopular = true;
//                setup();
                fetchPopularPhotos(null);
            }

            HelperVars.useGrid = true;
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

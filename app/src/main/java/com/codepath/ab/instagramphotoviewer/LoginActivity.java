package com.codepath.ab.instagramphotoviewer;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

/**
 * Created by andrewblaich on 2/20/15.
 */
public class LoginActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences prefs = getSharedPreferences(HelperVars.prefname, MODE_PRIVATE);
        String restoredText = prefs.getString("at", null);
        Log.i("DEBUG", "AT: " + restoredText);
        if (restoredText != null) {
            HelperVars.auth_token = prefs.getString("at", null);
            goIntoMain();
        }else{
            //do nothing
        }

    }
    public void goIntoMain(){
        Intent i = new Intent(this, PhotosActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NO_HISTORY);
        this.startActivity(i);
        finish();
    }
    public void startLogin(View v){
        Intent i = new Intent(this, LoginWebviewActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(i);
        finish();
    }
}

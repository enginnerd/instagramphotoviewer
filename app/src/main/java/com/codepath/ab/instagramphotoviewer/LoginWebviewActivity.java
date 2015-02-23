package com.codepath.ab.instagramphotoviewer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.net.URI;

/**
 * Created by andrewblaich on 2/21/15.
 */
public class LoginWebviewActivity extends Activity {
    private WebView webView;
    private Context mContext;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_webview);
        mContext = this;
        webView = (WebView) findViewById(R.id.wvLogin);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(HelperVars.URL_AUTH_REQUEST);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i("DEBUG-URL", "shouldOverrideUrlLoading: " + url);
                if (url.contains(HelperVars.redirectUrl + "/#access_token=")) {
                    String[] tokens = url.split("=");
                    if (tokens.length >= 2) {
                        String auth_token = tokens[1];
                        Log.i("DEBUG-TOKEN", auth_token);
                        HelperVars.auth_token = auth_token; //need to persist this token somewhere
                        SharedPreferences.Editor editor = getSharedPreferences(HelperVars.prefname, MODE_PRIVATE).edit();
                        editor.putString("at", auth_token);
                        editor.commit();

                        Intent i = new Intent(mContext, PhotosActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(i);
                        finish();
                    }
                } else if(url.contains("error=")){
                    String[] tokens = url.split("\\?");
                    if(tokens.length>=2) {
                        Toast.makeText(mContext, "Error: " + tokens[1], Toast.LENGTH_LONG).show();
                        Intent i = new Intent(mContext, LoginActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(i);
                        finish();
                    }
                }
                return false;
            }
        });
    }
}

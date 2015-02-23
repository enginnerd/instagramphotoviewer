package com.codepath.ab.instagramphotoviewer;

/**
 * Created by andrewblaich on 2/18/15.
 */
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;
// ...

public class FullCommentsDialog extends DialogFragment { //learn more about DialogFragments

    private TextView mComments;

    public FullCommentsDialog() {
        // Empty constructor required for DialogFragment
    }

    public static FullCommentsDialog newInstance(InstagramPhoto photo) {
        FullCommentsDialog frag = new FullCommentsDialog();
        Bundle args = new Bundle();
        String comments = "";
        try {
            JSONObject j_comments = photo.commentsObj;
            JSONArray jArray = j_comments.getJSONArray("data");
            if(jArray.length()<=0){
                comments += "No comments";
            }else{
                for(int i=0; i<jArray.length(); i++) {
                    JSONObject item = jArray.getJSONObject(i);
                    String comment = item.getString("text");
                    String username = item.getJSONObject("from").getString("username");
                    comments += "<b>"+username + ":</b> " + comment + "<br /><br />";
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        args.putString("comments", comments);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE|
                Window.FEATURE_SWIPE_TO_DISMISS);
        getDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Holo_Light);

        View view = inflater.inflate(R.layout.fragment_full_comments, container);
        mComments = (TextView) view.findViewById(R.id.tvComments);
        String comments = getArguments().getString("comments", "none");
        mComments.setText(Html.fromHtml(comments));
        return view;
    }
}

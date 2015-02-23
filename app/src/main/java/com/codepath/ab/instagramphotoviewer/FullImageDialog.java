package com.codepath.ab.instagramphotoviewer;

/**
 * Created by andrewblaich on 2/18/15.
 */
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
// ...

public class FullImageDialog extends DialogFragment { //learn more about DialogFragments

    private ImageView mImageView;
    private ImageView mProfilePhoto;
    private TextView mCaption;
    private TextView mUsername;

    public FullImageDialog() {
        // Empty constructor required for DialogFragment
    }

    public static FullImageDialog newInstance(InstagramPhoto photo) {
        FullImageDialog frag = new FullImageDialog();
        Bundle args = new Bundle();
        args.putString("username", photo.username);
        args.putString("profilePhoto", photo.usernameUrl);
        args.putString("caption", photo.caption);
        args.putString("url", photo.imageUrl);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE|
                Window.FEATURE_SWIPE_TO_DISMISS);
//        getDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Holo_Light);

        View view = inflater.inflate(R.layout.item_photo, container);
        mImageView = (ImageView) view.findViewById(R.id.ivPhoto);
        mProfilePhoto = (ImageView) view.findViewById(R.id.ivUserphoto);
        mCaption = (TextView)view.findViewById(R.id.tvCaption);
        mUsername = (TextView)view.findViewById(R.id.tvUsername);
        String url = getArguments().getString("url", "none");
        String profilePhoto = getArguments().getString("profilePhoto", "none");
        String caption = getArguments().getString("caption", "none");
        String username = getArguments().getString("username", "none");

        mCaption.setText(caption);
        mUsername.setText(username);
        Picasso.with(this.getActivity()).load(profilePhoto).fit().centerCrop().placeholder(R.mipmap.ic_launcher).into(mProfilePhoto);
        Picasso.with(this.getActivity()).load(url).fit().centerCrop().placeholder(R.mipmap.ic_launcher).into(mImageView);
        return view;
    }
}

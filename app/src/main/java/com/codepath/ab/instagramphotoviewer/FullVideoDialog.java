package com.codepath.ab.instagramphotoviewer;

/**
 * Created by andrewblaich on 2/18/15.
 */
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.squareup.picasso.Picasso;
// ...

public class FullVideoDialog extends DialogFragment { //learn more about DialogFragments

    private ImageView mImageView;
    private VideoView mVideoView;
    private ImageView mProfilePhoto;
    private TextView mCaption;
    private TextView mUsername;
    private ProgressDialog progDailog;
//    private static int vHeight = 100;

    public FullVideoDialog() {
        // Empty constructor required for DialogFragment
    }

    public static FullVideoDialog newInstance(InstagramPhoto photo) {
        FullVideoDialog frag = new FullVideoDialog();
        Bundle args = new Bundle();
        args.putString("username", photo.username);
        args.putString("profilePhoto", photo.usernameUrl);
        args.putString("caption", photo.caption);
        args.putString("url", photo.videoUrl);
//        args.putInt("height", photo.videoHeight);
        frag.setArguments(args);
//        vHeight = photo.videoHeight;
        return frag;
    }

//    @Override
//    public Dialog onCreateDialog(final Bundle savedInstanceState) {
//
//        // the content
//        final RelativeLayout root = new RelativeLayout(getActivity());
//        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//
//        // creating the fullscreen dialog
//        final Dialog dialog = new Dialog(getActivity());
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(root);
////        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.YELLOW));
////        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, vHeight);
////        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
////        dialog.getWindow().requestFeature(
////                Window.FEATURE_NO_TITLE|
////                Window.FEATURE_SWIPE_TO_DISMISS);
//        return dialog;
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE|
                Window.FEATURE_SWIPE_TO_DISMISS);
//        getDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Holo_Light);
        View view = inflater.inflate(R.layout.item_video, container);

//        mImageView = (ImageView) view.findViewById(R.id.ivPhoto);
        mVideoView = (VideoView) view.findViewById(R.id.ivVideo);
        mProfilePhoto = (ImageView) view.findViewById(R.id.ivUserphoto);
        mCaption = (TextView)view.findViewById(R.id.tvCaption);
        mUsername = (TextView)view.findViewById(R.id.tvUsername);
        String url = getArguments().getString("url", "none");
        String profilePhoto = getArguments().getString("profilePhoto", "none");
        String caption = getArguments().getString("caption", "none");
        String username = getArguments().getString("username", "none");

//        mCaption.setText(caption);
        mUsername.setText(username);
        Picasso.with(this.getActivity()).load(profilePhoto).fit().centerCrop().placeholder(R.mipmap.ic_launcher).into(mProfilePhoto);

        DisplayMetrics metrics = new DisplayMetrics(); getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        android.widget.LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) mVideoView.getLayoutParams();
        params.width =  metrics.widthPixels;
        params.height = metrics.heightPixels;
        params.leftMargin = 0;
        mVideoView.setLayoutParams(params);

//        Picasso.with(this.getActivity()).load(url).fit().centerCrop().placeholder(R.mipmap.ic_launcher).into(mImageView);
        mVideoView.setVideoURI(Uri.parse(url));
//        MediaController mediaController = new
//                MediaController(this.getActivity());
//        mediaController.setAnchorView(mVideoView);
//        mVideoView.setMediaController(new MediaController(getActivity()));
        mVideoView.setZOrderOnTop(true); //deal with dimming of videoview
        mVideoView.start();

//        progDailog = ProgressDialog.show(getActivity(), "Please wait ...", "Retrieving data ...", true);
//
//        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//
//            public void onPrepared(MediaPlayer mp) {
//                // TODO Auto-generated method stub
//                progDailog.dismiss();
//            }
//        });

        return view;
    }
}


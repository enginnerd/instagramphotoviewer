package com.codepath.ab.instagramphotoviewer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.codepath.ab.instagramphotoviewer.InstagramPhoto;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by andrewblaich on 2/18/15.
 */
public class InstagramPhotosAdapter extends ArrayAdapter<InstagramPhoto> {

    private static class ViewHolder {
        TextView tvUsername;
        TextView tvCaption;
        TextView tvLikeCount;
        ImageView ivPhoto;
        ImageView ivHeart;
        TextView tvTime;
        ImageView ivVideoPreviewPlayButton;
        ImageView ivProfilePhoto;
        TextView tvCommentCount;
    }

    public InstagramPhotosAdapter(Context context, int resource, List<InstagramPhoto> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
    }

    //what our item looks like
    //use template to display photos


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //get data item for position
        final ViewHolder viewHolder;
//        if(!HelperVars.useGrid) {
            InstagramPhoto photo = getItem(position);
            //check if recycled view
            if (convertView == null) {
                //create new view from template
                viewHolder = new ViewHolder();
//                if (photo.type.equals("image") || HelperVars.makeAllImages) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_photo, parent, false);
                    viewHolder.tvUsername =     (TextView) convertView.findViewById(R.id.tvUsername);
                    viewHolder.tvCaption =      (TextView) convertView.findViewById(R.id.tvCaption);;
                    viewHolder.tvLikeCount =    (TextView) convertView.findViewById(R.id.tvLikeCount);;
                    viewHolder.ivPhoto  =       (ImageView) convertView.findViewById(R.id.ivPhoto);
                    viewHolder.ivHeart = (ImageView) convertView.findViewById(R.id.ivHeartIcon);
                    viewHolder.tvTime   = (TextView) convertView.findViewById(R.id.tvTime);
                    viewHolder.ivVideoPreviewPlayButton = (ImageView) convertView.findViewById(R.id.VideoPreviewPlayButton);
                    viewHolder.ivProfilePhoto = (ImageView) convertView.findViewById(R.id.ivUserphoto);
                    viewHolder.tvCommentCount = (TextView) convertView.findViewById(R.id.tvCommentCount);
                convertView.setTag(viewHolder);
//                }
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.ivPhoto.setClickable(false);

            viewHolder.tvTime.setText(photo.createdTime);

            if (photo.type.equals("image") || HelperVars.makeAllImages) {
                try {
                    viewHolder.ivPhoto.setImageResource(0);
                } catch (Exception e) {
//                    e.printStackTrace();
                }
            }

            viewHolder.tvUsername.setText(photo.username/* + " -- " + photo.type*/);
            viewHolder.tvCaption.setText(photo.caption);
            Log.i("DEBUG", "Likes Count: " + photo.likesCount);
            viewHolder.tvLikeCount.setText(formatNumbers(String.valueOf(photo.likesCount) + " likes"));



            //clear out imageview (in case all data is there re: recycled views)
            //use picasso to get image from url (network)
//        ivPhoto.setImageResource(0);
            viewHolder.ivProfilePhoto.setImageResource(0);
//        Picasso.with(getContext()).load(photo.imageUrl).into(ivPhoto);
            if (photo.type.equals("image") || HelperVars.makeAllImages) {
                if ( viewHolder.ivPhoto == null) {
                    viewHolder.ivPhoto = (ImageView) convertView.findViewById(R.id.ivPhoto);
                }
                Picasso.with(getContext()).load(photo.imageUrl).fit().centerCrop().placeholder(R.mipmap.ic_launcher).into( viewHolder.ivPhoto);

            }
            //https://www.iconfinder.com/icons/211754/download/png/128
            Picasso.with(getContext()).load(photo.usernameUrl).fit().centerCrop().placeholder(R.mipmap.ic_launcher).into( viewHolder.ivProfilePhoto);
            Picasso.with(getContext()).load("https://www.iconfinder.com/icons/211754/download/png/128").centerCrop().resize(80,80).placeholder(R.mipmap.ic_launcher).into(viewHolder.ivHeart);
            //return created item as a view
            if(photo.type.equals("video")){
                viewHolder.ivVideoPreviewPlayButton.setVisibility(View.VISIBLE);
            }else{
                viewHolder.ivVideoPreviewPlayButton.setVisibility(View.GONE);
            }


//        }
        final InstagramPhoto sPhoto = photo;
        final View fConvertView = convertView;
            viewHolder.tvCommentCount.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
//                    Activity activity = (Activity)convertView.getContext()
                    FragmentActivity activity = (FragmentActivity)fConvertView.getContext();
                    FragmentManager fm = activity.getSupportFragmentManager();
                    FullCommentsDialog fullCommentsDialog = FullCommentsDialog.newInstance(sPhoto);
                    fullCommentsDialog.show(fm, "Comments");
                }
            });

        if(photo.type.equals("video")) {
            viewHolder.ivPhoto.setClickable(true);
            viewHolder.ivPhoto.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Activity activity = (Activity)fConvertView.getContext();
//                    FragmentActivity activity = (FragmentActivity) fConvertView.getContext();
//                    FragmentManager fm = activity.getSupportFragmentManager();
//                    FullVideoDialog fullVideoDialog = FullVideoDialog.newInstance(sPhoto);
//                    fullVideoDialog.show(fm, "Comments");
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sPhoto.videoUrl));
                    intent.setDataAndType(Uri.parse(sPhoto.videoUrl), "video/mp4");
                    activity.startActivity(intent);
                }
            });
        }

        viewHolder.ivProfilePhoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Activity activity = (Activity)fConvertView.getContext();
                PhotosActivity.fetchPopularPhotos(sPhoto.userId);
            }
        });

        return convertView;
    }

    private String formatNumbers(String input) {
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(input);
        NumberFormat nf = NumberFormat.getInstance();
        StringBuffer sb = new StringBuffer();
        while(m.find()) {
            String g = m.group();
            m.appendReplacement(sb, nf.format(Double.parseDouble(g)));
        }
        return m.appendTail(sb).toString();
    }
}

package com.example.circleapp;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

/**
 * Adapter to populate images in a GridView.
 */
public class ImageAdapter extends BaseAdapter {
    private final Context mContext;
    private final int[] mImageResources = {
            R.drawable.pfp1,
            R.drawable.pfp2,
            R.drawable.pfp3,
            R.drawable.pfp4,
            R.drawable.pfp5,
            R.drawable.pfp6,
    };

    /**
     * Constructor for ImageAdapter. Used to hold a collection of preset images that a user
     * can go through when selecting an image for their profile picture or for an event poster.
     *
     * @param context The context
     */
    public ImageAdapter(Context context) {
        mContext = context;
    }

    /**
     * Gets the image resources.
     *
     * @return An array of image resources
     */
    public int[] getImageResources() {
        return mImageResources;
    }

    @Override
    public int getCount() {
        return mImageResources.length;
    }

    @Override
    public Object getItem(int position) {
        return mImageResources[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(mImageResources[position]);
        return imageView;
    }
}
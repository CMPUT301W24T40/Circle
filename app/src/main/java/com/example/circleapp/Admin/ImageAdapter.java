package com.example.circleapp.Admin;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.circleapp.R;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Adapter for displaying images in a ListView.
 */
public class ImageAdapter extends ArrayAdapter<Uri> {

    /**
     * Constructor for ImageAdapter.
     *
     * @param context The context
     * @param images  The list of images
     * @see AdminBrowseImagesFragment
     */
    public ImageAdapter(Context context, ArrayList<Uri> images) {
        super(context, 0, images);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.image_item, parent, false);
        }

        Uri image = getItem(position);
        String imageType = Objects.requireNonNull(image).toString();

        TextView imageTypeView = convertView.findViewById(R.id.image_type_title);
        if (imageType.contains("profile_pictures")) { imageTypeView.setText(R.string.image_type_pfp); }
        else if (imageType.contains("event_posters") || imageType.contains("default_event")) { imageTypeView.setText(R.string.image_type_poster); }

        ImageView imageView = convertView.findViewById(R.id.image_item_view);
        Glide.with(getContext()).load(image).into(imageView);

        return convertView;
    }
}

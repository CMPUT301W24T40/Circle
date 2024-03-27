package com.example.circleapp.EventDisplay;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.circleapp.BaseObjects.Announcement;
import com.example.circleapp.R;

import java.util.ArrayList;

public class AnnouncementAdapter extends ArrayAdapter<Announcement> {

    public AnnouncementAdapter(Context context, ArrayList<Announcement> announcements){super(context, 0, announcements);}
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.announcement_item, parent, false);
        }

        Announcement announcement = getItem(position);

        TextView announcementContent = convertView.findViewById(R.id.announcement_content);
        TextView announcementTimestamp = convertView.findViewById(R.id.announcement_timestamp);

        if (announcement != null) {
            announcementContent.setText(announcement.getContent());
            announcementTimestamp.setText(announcement.getTimestamp());
        }

        return convertView;
    }


}

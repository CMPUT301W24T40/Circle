package com.example.circleapp;

//REFERENCES: https://stackoverflow.com/questions/45546739/handle-click-events-in-adapter-viewholder
//            https://stackoverflow.com/questions/40584424/simple-android-recyclerview-example

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private List<Event> events;
    private OnItemClickListener listener;
    public interface OnItemClickListener {
        void onItemClick(Event event);
    }
    public EventAdapter(List<Event> events, OnItemClickListener listener){

        this.events = events;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
        return new EventViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = events.get(position);
        holder.eventTitle.setText(event.getEventName());
        holder.eventDescription.setText(event.getDescription());
        holder.eventPoster.setImageDrawable(holder.itemView.getContext().getDrawable(event.getEventPoster()));

        holder.itemView.setOnClickListener(view -> {
            if (listener != null) {
                listener.onItemClick(event);
            }
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        public TextView eventTitle, eventDescription;
        public ImageView eventPoster;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventTitle = itemView.findViewById(R.id.event_title);
            eventDescription = itemView.findViewById(R.id.event_description);
            eventPoster = itemView.findViewById(R.id.event_poster);
        }
    }
}


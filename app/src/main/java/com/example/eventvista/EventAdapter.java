package com.example.eventvista;

import android.content.Context;
import android.util.Log; // Import Log for logging
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide; // Include Glide for image loading
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private final List<DataClass> eventList; // Change to DataClass
    private final Context context;

    public EventAdapter(Context context, List<DataClass> eventList) { // Update constructor
        this.context = context;
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        DataClass event = eventList.get(position); // Use DataClass here
        holder.eventName.setText(event.getDataTitle());
        holder.eventDate.setText(event.getDataDate());
        holder.eventDesc.setText(event.getDataDesc());

        Glide.with(context)
                .load(event.getDataImage())
                .into(holder.eventImage);
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventName;
        TextView eventDate;
        TextView eventDesc;
        ImageView eventImage;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.eventNameTextView);
            eventDate = itemView.findViewById(R.id.eventDateTextView);
            eventDesc = itemView.findViewById(R.id.eventDescTextView);
            eventImage = itemView.findViewById(R.id.eventImageView);
        }
    }
}



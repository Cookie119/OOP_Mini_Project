package com.example.eventvista;

import android.content.Context;
import android.content.Intent;
import android.util.Log; // Import Log for debugging
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private Context context;
    private List<DataClass> dataList;

    public MyAdapter(Context context, List<DataClass> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(com.example.eventvista.R.layout.recycler_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DataClass currentEvent = dataList.get(position);

        Glide.with(context).load(currentEvent.getDataImage()).into(holder.recImage);
        holder.recTitle.setText(currentEvent.getDataTitle());
        holder.recDesc.setText(currentEvent.getDataDesc());
        holder.recDate.setText(currentEvent.getDataDate());

        holder.recCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Log the details of the clicked event
                Log.d("MyAdapter", "Clicked Event Details: ");
                Log.d("MyAdapter", "Title: " + currentEvent.getDataTitle());
                Log.d("MyAdapter", "Description: " + currentEvent.getDataDesc());
                Log.d("MyAdapter", "Date: " + currentEvent.getDataDate());
                Log.d("MyAdapter", "Image URL: " + currentEvent.getDataImage());
                Log.d("MyAdapter", "Key: " + currentEvent.getKey());
                Log.d("MyAdapter", "Block: " + currentEvent.getBlock()); // Log the block name

                // Start the DetailActivity with the event data
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("Image", currentEvent.getDataImage());
                intent.putExtra("Description", currentEvent.getDataDesc());
                intent.putExtra("Title", currentEvent.getDataTitle());
                intent.putExtra("Key", currentEvent.getKey());
                intent.putExtra("Date", currentEvent.getDataDate());
                intent.putExtra("Block", currentEvent.getBlock()); // Pass the block name
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void searchDataList(ArrayList<DataClass> searchList) {
        dataList = searchList;
        notifyDataSetChanged();
    }
}

class MyViewHolder extends RecyclerView.ViewHolder {

    ImageView recImage;
    TextView recTitle, recDesc, recDate;
    CardView recCard;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

        recImage = itemView.findViewById(R.id.recImage);
        recCard = itemView.findViewById(R.id.recCard);
        recDesc = itemView.findViewById(R.id.recDesc);
        recDate = itemView.findViewById(R.id.recDate);
        recTitle = itemView.findViewById(R.id.recTitle);
    }
}

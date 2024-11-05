// BlockAdapter.java
package com.example.eventvista;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BlockAdapter extends RecyclerView.Adapter<BlockAdapter.BlockViewHolder> {
    private static final String TAG = "BlockAdapter"; // Log tag for this adapter
    private Context context;
    private List<String> blockList;

    public BlockAdapter(Context context, List<String> blockList) {
        this.context = context;
        this.blockList = blockList;
        Log.d(TAG, "Adapter initialized with block list size: " + blockList.size());
    }

    @NonNull
    @Override
    public BlockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
        Log.d(TAG, "ViewHolder created");
        return new BlockViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlockViewHolder holder, int position) {
        String blockName = blockList.get(position);
        holder.blockName.setText(blockName);
        Log.d(TAG, "Binding block name: " + blockName + " at position: " + position);

        holder.itemView.setOnClickListener(v -> {
            Log.d(TAG, "Block clicked: " + blockName);
            Intent intent = new Intent(context, EventsActivity.class); // Change to EventsActivity
            intent.putExtra("BlockName", blockName); // Pass the selected block name
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return blockList.size();
    }

    static class BlockViewHolder extends RecyclerView.ViewHolder {
        TextView blockName;

        public BlockViewHolder(@NonNull View itemView) {
            super(itemView);
            blockName = itemView.findViewById(android.R.id.text1);
            Log.d(TAG, "ViewHolder initialized");
        }
    }
}

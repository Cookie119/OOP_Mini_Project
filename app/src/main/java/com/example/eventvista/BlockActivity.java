// BlockActivity.java
package com.example.eventvista;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BlockActivity extends AppCompatActivity {
    private RecyclerView blockRecyclerView;
    private BlockAdapter blockAdapter;
    private List<String> blockList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block);

        blockRecyclerView = findViewById(R.id.blockRecyclerView);
        blockRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        blockList = new ArrayList<>();

        fetchBlocks();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Initialize the profile button
        ImageButton profileButton = findViewById(R.id.profile_button);
        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(BlockActivity.this, ProfileActivity.class);
            startActivity(intent);
        });


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set the initial selected item if necessary
        bottomNavigationView.setSelectedItemId(R.id.blocks);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        startActivity(new Intent(BlockActivity.this, MainActivity.class));
                        return true;
                    case R.id.calendar:
                        startActivity(new Intent(BlockActivity.this, CalendarActivity.class));
                        return true;
                    case R.id.blocks:
                        startActivity(new Intent(BlockActivity.this, BlockActivity.class));
                        return true;
                    case R.id.notifications:
                        startActivity(new Intent(BlockActivity.this, NotificationReceiver.class));
                        return true;
//                    case R.id.other_item:
//                        // Handle other item selection
//                        return true;
                    default:
                        return false; // Default case
                }
            }
        });
    }

    private void fetchBlocks() {
        DatabaseReference blocksRef = FirebaseDatabase.getInstance().getReference("blocks");
        blocksRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot blockSnapshot : dataSnapshot.getChildren()) {
                    String blockName = blockSnapshot.getValue(String.class);
                    if (blockName != null) {
                        blockList.add(blockName);
                    }
                }
                // Set the adapter after fetching the blocks
                blockAdapter = new BlockAdapter(BlockActivity.this, blockList);
                blockRecyclerView.setAdapter(blockAdapter); // Set the adapter here
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BlockActivity.this, "Failed to load blocks", Toast.LENGTH_SHORT).show();
            }
        });
    }

}

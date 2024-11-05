package com.example.eventvista;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class EventsActivity extends AppCompatActivity {

    private static final String TAG = "EventsActivity";
    private String blockName;
    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private List<DataClass> eventList; // Change to DataClass
    private DatabaseReference databaseReference; // Reference to Firebase database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        // Retrieve block name from the intent
        blockName = getIntent().getStringExtra("BlockName");
        Log.d(TAG, "Selected block: " + blockName);

        // Display the block name
        TextView toolbarTitle = findViewById(R.id.toolbarTitle);
        toolbarTitle.setText("Events for " + blockName);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(this, eventList); // Pass the context and the DataClass list
        recyclerView.setAdapter(eventAdapter);

        // Fetch and display events based on blockName
        fetchAndDisplayEvents(blockName);
    }

    // Method to fetch and display events
    private void fetchAndDisplayEvents(String blockName) {
        // Update to the correct path based on your Firebase structure
        databaseReference = FirebaseDatabase.getInstance().getReference("Event Tracker");

        Log.d(TAG, "Fetching events for block: " + blockName); // Log the block being fetched
        databaseReference.orderByChild("block").equalTo(blockName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                eventList.clear(); // Clear the list before adding new events
                int eventCount = 0; // Counter for events

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DataClass event = snapshot.getValue(DataClass.class); // Use DataClass here
                    if (event != null) {
                        event.setKey(snapshot.getKey()); // Set the key if needed
                        eventList.add(event); // Add DataClass object to the list
                        eventCount++;
                    } else {
                        Log.d(TAG, "Event is null for snapshot: " + snapshot.toString());
                    }
                }

                Log.d(TAG, "Number of events found: " + eventCount); // Log the number of events found
                eventAdapter.notifyDataSetChanged(); // Notify adapter about data changes
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Error fetching events: " + databaseError.getMessage());
            }
        });
    }

}

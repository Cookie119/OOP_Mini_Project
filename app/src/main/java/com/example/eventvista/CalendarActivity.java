package com.example.eventvista;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.TextView;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CalendarActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private TextView eventDetailsTextView;
    private FirebaseFirestore db; // Firebase Firestore instance
    private RecyclerView recyclerView;
    private MyAdapter myAdapter;
    private List<DataClass> eventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_calendar);

        calendarView = findViewById(R.id.calendarView);
        eventDetailsTextView = findViewById(R.id.eventDetailsTextView);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Set a date change listener on the CalendarView
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
            // Fetch and display events for the selected date
            displayEventsForDate(selectedDate);
        });

        // Initialize RecyclerView and event list
        recyclerView = findViewById(R.id.recyclerView);
        eventList = new ArrayList<>();
        myAdapter = new MyAdapter(this, eventList); // Initialize your adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(myAdapter);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Initialize the profile button
        ImageButton profileButton = findViewById(R.id.profile_button);
        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(CalendarActivity.this, ProfileActivity.class);
            startActivity(intent);
        });


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set the initial selected item if necessary
        bottomNavigationView.setSelectedItemId(R.id.calendar);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        startActivity(new Intent(CalendarActivity.this, MainActivity.class));
                        return true;
                    case R.id.calendar:
                        startActivity(new Intent(CalendarActivity.this, CalendarActivity.class));
                        return true;
                    case R.id.blocks:
                        startActivity(new Intent(CalendarActivity.this, BlockActivity.class));
                        return true;
                    case R.id.notifications:
                        startActivity(new Intent(CalendarActivity.this, NotificationReceiver.class));
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

    private void displayEventsForDate(String date) {
        // Fetch events from Firebase for the selected date
        getEventsFromFirebase(date);
    }

    private void getEventsFromFirebase(String date) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Event Tracker"); // Adjust to the correct path if necessary

        // Query the events for the selected date
        databaseRef.orderByChild("dataDate").equalTo(date).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventList.clear(); // Clear previous events

                for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                    // Retrieve each field from the snapshot
                    String dataTitle = eventSnapshot.child("dataTitle").getValue(String.class);
                    String dataDesc = eventSnapshot.child("dataDesc").getValue(String.class);
                    String dataDate = eventSnapshot.child("dataDate").getValue(String.class);
                    String dataImage = eventSnapshot.child("dataImage").getValue(String.class);
                    String block = eventSnapshot.child("block").getValue(String.class);

                    // Create a new event object with the retrieved data
                    DataClass event = new DataClass(dataTitle, dataDesc, dataDate, dataImage,block); // Correctly use the retrieved variable names
                    eventList.add(event); // Add new event to the list
                }

                // Notify the adapter of data change
                myAdapter.notifyDataSetChanged();

                // Update the UI with event details
                if (eventList.isEmpty()) {
                    eventDetailsTextView.setText("No events for " + date);
                } else {
                    eventDetailsTextView.setText("Events for " + date + ":   " + eventList.size());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CalendarActivity.this, "Error fetching events: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



}

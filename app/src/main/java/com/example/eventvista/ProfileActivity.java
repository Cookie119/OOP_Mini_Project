package com.example.eventvista;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private TextView nameTextView;  // TextView for user's name
    private TextView emailTextView; // TextView for user's email
    private Button logoutButton;     // Declare logout button
    private FirebaseAuth auth;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        // Initialize the database reference to the "users" node
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        // Initialize TextViews and Button
        nameTextView = findViewById(R.id.nameTextView); // TextView for user's name
        emailTextView = findViewById(R.id.emailTextView); // TextView for user's email
        logoutButton = findViewById(R.id.logoutButton); // Initialize logout button

        if (currentUser != null) {
            // Fetch user details from Firebase
            fetchUserProfile(currentUser.getUid());
        }

        // Set up logout button click listener
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout(); // Call logout method
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set the initial selected item if necessary
        bottomNavigationView.setSelectedItemId(R.id.blocks);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                        return true;
                    case R.id.calendar:
                        startActivity(new Intent(ProfileActivity.this, CalendarActivity.class));
                        return true;
                    case R.id.blocks:
                        startActivity(new Intent(ProfileActivity.this, BlockActivity.class));
                        return true;
                    case R.id.notifications:
                        startActivity(new Intent(ProfileActivity.this, NotificationReceiver.class));
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

    private void fetchUserProfile(String userId) {
        DatabaseReference userRef = usersRef.child(userId); // Path for the specific user
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class); // Assuming your User class has email and name fields
                    if (user != null) {
                        nameTextView.setText(user.getName()); // Display the user's name
                        emailTextView.setText(user.getEmail()); // Display the user's email
                    } else {
                        nameTextView.setText("No name found");
                        emailTextView.setText("No email found");
                    }
                } else {
                    nameTextView.setText("No name found");
                    emailTextView.setText("No email found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void logout() {
        auth.signOut(); // Sign out from Firebase
        // Redirect to the login/register page
        Intent intent = new Intent(ProfileActivity.this, RegisterActivity.class);
        startActivity(intent);
        finish(); // Close the ProfileActivity
    }
}

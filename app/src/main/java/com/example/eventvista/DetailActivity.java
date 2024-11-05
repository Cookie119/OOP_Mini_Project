package com.example.eventvista;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DetailActivity extends AppCompatActivity {

    TextView detailDesc, detailTitle, detailDate, detailBlock;
    ImageView detailImage;
    FloatingActionButton deleteButton, editButton;
    FloatingActionMenu floatingActionMenu;
    String key = "";
    String imageUrl = "";

    private FirebaseAuth auth;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.eventvista.R.layout.activity_detail);

        detailDesc = findViewById(R.id.detailDesc);
        detailImage = findViewById(R.id.detailImage);
        detailTitle = findViewById(R.id.detailTitle);
        deleteButton = findViewById(R.id.deleteButton);
        editButton = findViewById(R.id.editButton);
        detailDate = findViewById(R.id.detailDate);
        detailBlock = findViewById(R.id.detailBlock);
        floatingActionMenu = findViewById(R.id.floatingActionMenu);

        // Initialize Firebase Auth and Database Reference
        auth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        // Getting the data passed from the previous activity
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            detailDesc.setText(bundle.getString("Description"));
            detailTitle.setText(bundle.getString("Title"));
            detailDate.setText(bundle.getString("Date"));
            detailBlock.setText(bundle.getString("Block")); // Ensure this key matches
            key = bundle.getString("Key");
            imageUrl = bundle.getString("Image");
            Glide.with(this).load(imageUrl).into(detailImage);
        }


        // Check user role to manage access
        checkUserRole();

        // Delete Button Click Event
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Event Tracker");
                FirebaseStorage storage = FirebaseStorage.getInstance();

                StorageReference storageReference = storage.getReferenceFromUrl(imageUrl);
                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        reference.child(key).removeValue();
                        Toast.makeText(DetailActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                });
            }
        });

        // Edit Button Click Event
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailActivity.this, UpdateActivity.class)
                        .putExtra("Title", detailTitle.getText().toString())
                        .putExtra("Description", detailDesc.getText().toString())
                        .putExtra("Date", detailDate.getText().toString())
                        .putExtra("Image", imageUrl)
                        .putExtra("Key", key);
                        intent.putExtra("Block", detailBlock.getText().toString());
                startActivity(intent);
            }
        });
    }

    private void checkUserRole() {
        String userId = auth.getCurrentUser().getUid();
        usersRef.child(userId).child("roleStatus").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String roleStatus = snapshot.getValue(String.class);

                if ("EventManager".equals(roleStatus)) {
                    // Enable floating action menu for Event Managers
                    floatingActionMenu.setVisibility(View.VISIBLE);
                } else if ("RegularUser".equals(roleStatus)) {
                    // Hide floating action menu for Regular Users
                    floatingActionMenu.setVisibility(View.GONE);
                } else {
                    Toast.makeText(DetailActivity.this, "Unknown role. Access restricted.", Toast.LENGTH_SHORT).show();
                    finish(); // Close activity if the role is not recognized
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DetailActivity.this, "Failed to retrieve user role.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

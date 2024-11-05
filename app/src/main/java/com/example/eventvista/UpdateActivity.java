package com.example.eventvista;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class UpdateActivity extends AppCompatActivity {
    Spinner updateBlock;
    ImageView updateImage;
    Button updateButton;
    EditText updateDesc, updateTitle, updateDate;
    String title, desc, date, selectedBlock;
    String imageUrl;
    String key, oldImageURL;
    Uri uri;
    DatabaseReference databaseReference;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        updateButton = findViewById(R.id.updateButton);
        updateDesc = findViewById(R.id.updateDesc);
        updateImage = findViewById(R.id.updateImage);
        updateDate = findViewById(R.id.updateDate);
        updateTitle = findViewById(R.id.updateTitle);
        updateBlock = findViewById(R.id.updateBlock);

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            uri = data.getData();
                            updateImage.setImageURI(uri);
                        } else {
                            Toast.makeText(UpdateActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Glide.with(UpdateActivity.this).load(bundle.getString("Image")).into(updateImage);
            updateTitle.setText(bundle.getString("Title"));
            updateDesc.setText(bundle.getString("Description"));
            updateDate.setText(bundle.getString("Date"));
            selectedBlock = bundle.getString("Block");
            key = bundle.getString("Key");
            oldImageURL = bundle.getString("Image");
        }
        databaseReference = FirebaseDatabase.getInstance().getReference("Event Tracker").child(key);

        // Fetch block data from Firebase
        fetchBlocks();

        // Set DatePickerDialog on date field
        updateDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        updateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
            }
        });
    }

    private void fetchBlocks() {
        DatabaseReference blocksRef = FirebaseDatabase.getInstance().getReference("blocks"); // Adjust path if necessary
        blocksRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> blockList = new ArrayList<>();
                for (DataSnapshot blockSnapshot : dataSnapshot.getChildren()) {
                    String blockName = blockSnapshot.getValue(String.class);
                    if (blockName != null) {
                        blockList.add(blockName);
                    }
                }

                // Populate the spinner with block names
                ArrayAdapter<String> adapter = new ArrayAdapter<>(UpdateActivity.this,
                        android.R.layout.simple_spinner_item, blockList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                updateBlock.setAdapter(adapter);

                // Set previously selected block
                if (selectedBlock != null) {
                    int position = adapter.getPosition(selectedBlock);
                    if (position >= 0) {
                        updateBlock.setSelection(position);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UpdateActivity.this, "Failed to load blocks", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Month is 0-based, so add 1
                    String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    updateDate.setText(selectedDate);
                }, year, month, day);
        datePickerDialog.show();
    }

    public void saveData() {
        // Initialize the storage reference for the image
        storageReference = FirebaseStorage.getInstance().getReference().child("Android Images").child(uri != null ? uri.getLastPathSegment() : "defaultImage.jpg");

        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        if (uri != null) {
            // Only upload image if a new image is selected
            storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri urlImage) {
                            imageUrl = urlImage.toString();
                            updateData();
                            dialog.dismiss();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                    Toast.makeText(UpdateActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // If no new image is selected, proceed to update other fields
            updateData();
            dialog.dismiss();
        }
    }

    public void updateData() {
        title = updateTitle.getText().toString().trim();
        desc = updateDesc.getText().toString().trim();
        date = updateDate.getText().toString();
        selectedBlock = updateBlock.getSelectedItem().toString(); // Get selected block

        // Create a map for the updated data
        Map<String, Object> updateMap = new HashMap<>();
        if (!title.isEmpty()) {
            updateMap.put("dataTitle", title);
        }
        if (!desc.isEmpty()) {
            updateMap.put("dataDesc", desc);
        }
        if (!date.isEmpty()) {
            updateMap.put("dataDate", date);
        }
        if (selectedBlock != null && !selectedBlock.isEmpty()) { // Check if block is not empty and update
            updateMap.put("block", selectedBlock);
        }
        if (imageUrl != null) {
            updateMap.put("dataImage", imageUrl);
        }

        // Log the update map
        Log.d("UpdateData", "Update map: " + updateMap.toString());

        // Update only the fields that are not empty
        databaseReference.updateChildren(updateMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    if (oldImageURL != null && !oldImageURL.isEmpty() && uri != null) {
                        StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(oldImageURL);
                        reference.delete();
                    }
                    Toast.makeText(UpdateActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                    // Navigate back to MainActivity after update
                    Intent intent = new Intent(UpdateActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(UpdateActivity.this, "Update Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}

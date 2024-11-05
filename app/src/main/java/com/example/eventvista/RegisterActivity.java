package com.example.eventvista;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText, nameEditText, keyEditText;
    private Button registerButton, signInButton;
    private CheckBox eventManagerCheckBox;
    private DatabaseReference usersRef;
    private FirebaseAuth auth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize views
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        nameEditText = findViewById(R.id.nameEditText);
        keyEditText = findViewById(R.id.keyEditText);
        registerButton = findViewById(R.id.registerButton);
        signInButton = findViewById(R.id.signInButton);
        eventManagerCheckBox = findViewById(R.id.eventManagerCheckBox);

        // Initialize Firebase Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users");

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Initialize Progress Dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering...");

        // Set checkbox listener to show/hide keyEditText
        eventManagerCheckBox.setOnCheckedChangeListener((buttonView, isChecked) ->
                keyEditText.setVisibility(isChecked ? View.VISIBLE : View.GONE)
        );

        registerButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String name = nameEditText.getText().toString().trim();
            String key = keyEditText.getText().toString().trim();

            if (validateInputs(email, password, name, key)) {
                addUser(email, password, name, key);
            }
        });

        signInButton.setOnClickListener(view -> redirectToSignIn());
    }

    private boolean validateInputs(String email, String password, String name, String key) {
        if (email.isEmpty()) {
            emailEditText.setError("Email is required");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Please enter a valid email address");
            return false;
        }
        if (password.isEmpty()) {
            passwordEditText.setError("Password is required");
            return false;
        }
        if (password.length() < 6) {
            passwordEditText.setError("Password should be at least 6 characters long");
            return false;
        }
        if (name.isEmpty()) {
            nameEditText.setError("Name is required");
            return false;
        }
        // Only check key if the user is trying to register as an Event Manager
        if (eventManagerCheckBox.isChecked() && key.isEmpty()) {
            keyEditText.setError("Key is required for Event Manager registration");
            return false;
        }
        return true;
    }

    private void handleDatabaseFailure(@NonNull Exception e) {
        Toast.makeText(RegisterActivity.this, "Database error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private void addUser(String email, String password, String name, String key) {
        progressDialog.show(); // Show progress dialog

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    progressDialog.dismiss(); // Dismiss dialog when done

                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        String userId = firebaseUser.getUid();

                        // Set roleStatus based on key presence and checkbox state
                        String roleStatus = (eventManagerCheckBox.isChecked() && !key.isEmpty()) ? "EventManager" : "RegularUser";

                        // Create user object with role status
                        User user = new User(userId, name, email, roleStatus);

                        // Add user to Firebase Realtime Database
                        usersRef.child(userId).setValue(user)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(RegisterActivity.this, "Registration successful.", Toast.LENGTH_SHORT).show();
                                    redirectToSignIn(); // Redirect to sign-in
                                })
                                .addOnFailureListener(this::handleDatabaseFailure);
                    } else {
                        handleRegistrationFailure(task); // Handle failure here
                    }
                });
    }

    private void handleRegistrationFailure(Task<AuthResult> task) {
        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
            Toast.makeText(RegisterActivity.this, "An account with this email already exists.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(RegisterActivity.this, "Registration failed: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"), Toast.LENGTH_SHORT).show();
        }
    }

    private void redirectToSignIn() {
        Intent intent = new Intent(RegisterActivity.this, Login_Activity.class);
        startActivity(intent);
        finish();
    }
}

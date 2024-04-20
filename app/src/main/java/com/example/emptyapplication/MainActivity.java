package com.example.emptyapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import android.content.Intent;

import com.example.emptyapplication.schemas.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private EditText txtUsername;
    private DatabaseReference usersRef;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtUsername = findViewById(R.id.txtUsername);
        btnLogin = findViewById(R.id.btnLogin);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("Users");
        btnLogin.setOnClickListener(view -> loginUser());

    }

    private void loginUser() {
        String username = txtUsername.getText().toString().trim();
        if (username == null || username.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please enter a username to login.", Toast.LENGTH_SHORT).show();
            return;
        }

        usersRef.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // User exists
                    Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_SHORT).show();
                    // Proceed to RealtimeDatabaseActivity
                    startActivityPlayground(username);
                } else {
                    // User does not exist, create a new one
                    createUser(username);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Database Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createUser(String username) {
        DatabaseReference userRef = usersRef.child(username);

        User user = new User(username);

        userRef.setValue(user)
                .addOnSuccessListener(aVoid -> {
                    // User creation successful
                    Toast.makeText(getApplicationContext(), "New user created: " + username, Toast.LENGTH_SHORT).show();
                    // Proceed to RealtimeDatabaseActivity
                    startActivityPlayground(username);
                })
                .addOnFailureListener(e -> {
                    // Error occurred while creating user
                    Toast.makeText(getApplicationContext(), "Failed to create user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void startActivityPlayground(String username) {
        Intent intent = new Intent(MainActivity.this, PlaygroundActivity.class);
        saveUsernameToPreferences(username);
        startActivity(intent);
    }

    private void saveUsernameToPreferences(String username) {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.apply();
    }
}
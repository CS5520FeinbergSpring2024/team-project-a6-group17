package com.example.emptyapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PlaygroundActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playground);
        TextView textViewPlayground = findViewById(R.id.textViewPlayground);
        Button buttonMakeQuiz = findViewById(R.id.buttonMakeQuiz);
        Button buttonTakeQuiz = findViewById(R.id.buttonTakeQuiz);


        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "to Quizzzzz");

        // Set the welcome message including the username
        textViewPlayground.setText("Welcome " + username + "!");

        buttonMakeQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PlaygroundActivity.this, ManageQuizActivity.class));
            }
        });

        buttonTakeQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PlaygroundActivity.this, TakeQuizActivity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "to Quizzzzz");
        TextView textViewCoins = findViewById(R.id.textViewCoins);
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(username).child("coins");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer currentCoins = dataSnapshot.getValue(Integer.class);
                if (currentCoins == null) currentCoins = 0;
                textViewCoins.setText("You have " + currentCoins + " coins!");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("Failed to read value." + databaseError.toException());
            }
        });
    }
}
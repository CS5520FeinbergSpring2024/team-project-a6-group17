package com.example.emptyapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class PlaygroundActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playground);

        TextView textViewPlayground = findViewById(R.id.textViewPlayground);
        Button buttonMakeQuiz = findViewById(R.id.buttonMakeQuiz);

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
    }
}
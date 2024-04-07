package com.example.emptyapplication;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.annotation.NonNull;

import com.example.emptyapplication.schemas.Question;
import com.example.emptyapplication.schemas.QuestionType;
import com.example.emptyapplication.schemas.Quiz;


import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class CorrectAnswersActivity extends AppCompatActivity {

    private ListView lvCorrectAnswers;
    private List<String> correctAnswersList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_correct_answer);

        lvCorrectAnswers = findViewById(R.id.lvCorrectAnswers);
        correctAnswersList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, correctAnswersList);
        lvCorrectAnswers.setAdapter(adapter);

        String quizId = getIntent().getStringExtra("quiz_id");
        if (quizId != null) {
            loadCorrectAnswers(quizId);
        }
    }

    private void loadCorrectAnswers(String quizId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Quiz").child(quizId).child("questions");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                correctAnswersList.clear();
                for (DataSnapshot questionSnapshot : snapshot.getChildren()) {
                    Question question = questionSnapshot.getValue(Question.class);
                    if (question != null) {
                        String correctAnswer = question.getCorrectAnswerString();
                        correctAnswersList.add(question.getText() + " - Correct answer: " + correctAnswer);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors
            }
        });
    }
}

package com.example.emptyapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.emptyapplication.schemas.Quiz;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TakeQuizActivity extends AppCompatActivity {
    private List<Quiz> quizList;
    private TakeQuizAdapter adapter;
    private DatabaseReference quizzesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_quiz);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewTakeQuiz);
        quizzesRef = FirebaseDatabase.getInstance().getReference("Quiz");
        quizList = new ArrayList<>();
        adapter = new TakeQuizAdapter(quizList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        // Load quizzes from Firebase
        loadQuizzes();

        // Set click listener for item clicks
        adapter.setOnItemClickListener(position -> {
            Quiz selectedQuiz = quizList.get(position);
            String quizId = selectedQuiz.getQuiz_id();

            Intent intent = new Intent(TakeQuizActivity.this, TakeQuizQuestionsActivity.class);
            intent.putExtra("quiz_id", quizId);
            startActivity(intent);
        });
    }

    private void loadQuizzes() {
        quizzesRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                quizList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Quiz quiz = dataSnapshot.getValue(Quiz.class);
                    quizList.add(quiz);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TakeQuizActivity.this, "Failed to load quizzes", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

package com.example.emptyapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.emptyapplication.schemas.Quiz;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ManageQuizActivity extends AppCompatActivity {

    private DatabaseReference quizRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_quiz);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        quizRef = database.getReference("Quiz");
        removeIncompleteQuiz();

        Button buttonManageQuizAdd = findViewById(R.id.buttonManageQuizAdd);
        buttonManageQuizAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageQuizActivity.this, AddQuizActivity.class);

                DatabaseReference newQuizRef = quizRef.push();
                Quiz newQuiz = new Quiz(newQuizRef.getKey(), "new quiz", username, 3);
                newQuizRef.setValue(newQuiz)
                        .addOnSuccessListener(aVoid -> {
                            intent.putExtra("currQuestionNo", 1);
                            intent.putExtra("numQuestions", newQuiz.getNumQuestions());
                            intent.putExtra("newQuizId", newQuiz.getQuiz_id());
                            startActivity(intent);
                        })
                        .addOnFailureListener(e -> {});
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        removeIncompleteQuiz();
    }

    private void removeIncompleteQuiz() {
        Query query = quizRef.orderByChild("completed").equalTo(false);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // Remove the entry
                        snapshot.getRef().removeValue()
                                .addOnSuccessListener(aVoid -> {})
                                .addOnFailureListener(e -> {});
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
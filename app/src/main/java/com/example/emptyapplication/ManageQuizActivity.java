package com.example.emptyapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emptyapplication.schemas.Quiz;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ManageQuizActivity extends AppCompatActivity implements ManageQuizAdapter.OnQuizListener{

    private DatabaseReference quizRef;
    private RecyclerView recyclerViewQuizList;
    private ManageQuizAdapter adapter;


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
        List<Quiz> quizzes = new ArrayList<>();

//        show quiz list
        recyclerViewQuizList = findViewById(R.id.recyclerViewManageQuiz);
        adapter = new ManageQuizAdapter(quizzes, this);
        recyclerViewQuizList.setAdapter(adapter);
        recyclerViewQuizList.setLayoutManager(new LinearLayoutManager(this));

        quizRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                quizzes.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Quiz quiz = snapshot.getValue(Quiz.class);
                    if (quiz.getCreatedBy().equals(username) && quiz.isCompleted()) {
                        quizzes.add(quiz);
                    }
                }
                adapter.notifyDataSetChanged(); // Refresh the list
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });

        Button buttonManageQuizAdd = findViewById(R.id.buttonManageQuizAdd);
        buttonManageQuizAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageQuizActivity.this, AddQuizActivity.class);

                DatabaseReference newQuizRef = quizRef.push();
                Quiz newQuiz = new Quiz(newQuizRef.getKey(), "new quiz", username, System.currentTimeMillis(), 3);
                newQuizRef.setValue(newQuiz)
                        .addOnSuccessListener(aVoid -> {
                            intent.putExtra("currQuestionNo", 1);
                            intent.putExtra("numQuestions", newQuiz.getNumQuestions());
                            intent.putExtra("quiz_id", newQuiz.getQuiz_id());
                            startActivity(intent);
                        })
                        .addOnFailureListener(e -> {});
            }
        });

        //    swipe left to delete
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }


            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                new AlertDialog.Builder(ManageQuizActivity.this)
                        .setTitle("Delete Quiz")
                        .setMessage("Are you sure you want to delete this quiz?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Quiz quizToDelete = quizzes.get(position);
                                quizRef.child(quizToDelete.getQuiz_id()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(getApplicationContext(), "Quiz deleted", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                                        adapter.notifyItemChanged(position);
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                adapter.notifyItemChanged(position);
                            }
                        })
                        .show();
            }
        };

        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerViewQuizList);

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

//    click a quiz item to bring up edit
    @Override
    public void onQuizClick(Quiz quiz) {
        Intent intent = new Intent(this, AddQuizActivity.class);
        intent.putExtra("currQuestionNo", 1);
        intent.putExtra("numQuestions", quiz.getNumQuestions());
        intent.putExtra("quiz_id", quiz.getQuiz_id());
        startActivity(intent);
    }

}
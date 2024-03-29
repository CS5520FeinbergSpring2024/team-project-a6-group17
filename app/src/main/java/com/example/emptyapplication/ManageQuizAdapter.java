package com.example.emptyapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emptyapplication.schemas.Quiz;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ManageQuizAdapter extends RecyclerView.Adapter<ManageQuizAdapter.QuizViewHolder> {
    private List<Quiz> quizList;
    public interface OnQuizListener {
        void onQuizClick(Quiz quiz);
    }
    private OnQuizListener onQuizListener;

    public ManageQuizAdapter(List<Quiz> quizList, OnQuizListener onQuizListener) {
        this.onQuizListener = onQuizListener;
        this.quizList = quizList;
    }

    public class QuizViewHolder extends RecyclerView.ViewHolder {
        public TextView quizNameTextView;
        public TextView numberOfQuestionsTextView;
        public TextView timeCreatedTextView;

        public QuizViewHolder(View v) {
            super(v);
            quizNameTextView = v.findViewById(R.id.txtQuizTittle);
            numberOfQuestionsTextView = v.findViewById(R.id.txtNumQuestion);
            timeCreatedTextView = v.findViewById(R.id.txtCreatedAt);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onQuizListener.onQuizClick(quizList.get(getAdapterPosition()));
                }
            });
        }
    }



    @NonNull
    @Override
    public ManageQuizAdapter.QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.manage_quiz_list_item, parent, false);
        return new QuizViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        Quiz quiz = quizList.get(position);

        Date date = new Date(quiz.getCreatedAt());
        Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateFormatted = formatter.format(date);

        holder.quizNameTextView.setText(quiz.getName());
        holder.numberOfQuestionsTextView.setText(String.valueOf(quiz.getNumQuestions()) + " questions");
        holder.timeCreatedTextView.setText("created at: " + dateFormatted);
    }

    @Override
    public int getItemCount() {
        return quizList.size();
    }



}

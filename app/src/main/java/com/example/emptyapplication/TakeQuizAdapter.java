package com.example.emptyapplication;

import com.example.emptyapplication.schemas.Quiz;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TakeQuizAdapter extends RecyclerView.Adapter<TakeQuizAdapter.ViewHolder> {
    private List<Quiz> quizList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public TakeQuizAdapter(List<Quiz> quizList) {
        this.quizList = quizList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.manage_quiz_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Quiz quiz = quizList.get(position);
        holder.txtQuizTittle.setText(quiz.getName());
        holder.txtNumQuestion.setText(quiz.getNumQuestions() + " questions");
//        holder.txtCreatedAt.setText(quiz.getCreatedAt());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String dateString = dateFormat.format(new Date(quiz.getCreatedAt()));

        holder.txtCreatedAt.setText("Created at: " + dateString);
    }

    @Override
    public int getItemCount() {
        return quizList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtQuizTittle;
        private TextView txtNumQuestion;
        private TextView txtCreatedAt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtQuizTittle = itemView.findViewById(R.id.txtQuizTittle);
            txtNumQuestion = itemView.findViewById(R.id.txtNumQuestion);
            txtCreatedAt = itemView.findViewById(R.id.txtCreatedAt);

            // Set click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }

        public void bind(Quiz quiz) {
            txtQuizTittle.setText(quiz.getName());
            txtNumQuestion.setText(quiz.getNumQuestions() + " questions");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String dateString = dateFormat.format(new Date(quiz.getCreatedAt()));
            txtCreatedAt.setText("Created at: " + dateString);
        }
    }
}

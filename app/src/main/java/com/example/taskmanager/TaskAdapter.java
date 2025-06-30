package com.example.taskmanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
    public interface OnDeleteClickListener { void onDelete(int position); }
    private List<Task> tasks;
    private OnDeleteClickListener deleteListener;

    public TaskAdapter(List<Task> tasks, OnDeleteClickListener deleteListener) {
        this.tasks = tasks;
        this.deleteListener = deleteListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Task t = tasks.get(position);
        holder.title.setText(t.getTitle());
        holder.desc.setText(t.getDescription());
        holder.due.setText("Крайний срок: " + t.getDueDate());
        holder.card.setOnLongClickListener(v -> {
            deleteListener.onDelete(position);
            return true;
        });
    }

    @Override
    public int getItemCount() { return tasks.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView card;
        TextView title, desc, due;
        public ViewHolder(View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.cardView);
            title = itemView.findViewById(R.id.tvTitle);
            desc  = itemView.findViewById(R.id.tvDescription);
            due   = itemView.findViewById(R.id.tvDueDate);
        }
    }
}

package com.example.taskmanager;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DBHelper db;
    private TaskAdapter adapter;
    private List<Task> tasks;
    private static final int ADD_TASK_REQ = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DBHelper(this);
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);

        RecyclerView rv = findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(this));
        tasks = db.getAllTasks();
        adapter = new TaskAdapter(tasks, this::confirmDelete);
        rv.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(v ->
                startActivityForResult(new Intent(this, AddTaskActivity.class), ADD_TASK_REQ)
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_TASK_REQ && resultCode == RESULT_OK) {
            tasks.clear();
            tasks.addAll(db.getAllTasks());
            adapter.notifyDataSetChanged();
        }
    }

    private void confirmDelete(int pos) {
        new AlertDialog.Builder(this)
                .setTitle("Удалить задачу?")
                .setMessage("Вы действительно хотите удалить эту задачу?")
                .setPositiveButton("Да", (d,i)-> {
                    db.deleteTask(tasks.get(pos).getId());
                    tasks.remove(pos);
                    adapter.notifyItemRemoved(pos);
                })
                .setNegativeButton("Нет", null)
                .show();
    }
}

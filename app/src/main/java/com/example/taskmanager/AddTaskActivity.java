package com.example.taskmanager;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import android.widget.AutoCompleteTextView;
import android.content.Intent;
import android.content.ActivityNotFoundException;
import android.speech.RecognizerIntent;
import android.widget.Toast;

import androidx.annotation.Nullable;
import java.util.Calendar;
import java.util.Locale;

public class AddTaskActivity extends AppCompatActivity {
    private TextInputEditText etTask, etDesc, etDue;
    private AutoCompleteTextView autoCompleteList;
    private TextInputLayout layoutTask;

    private DBHelper db;

    private static final int VOICE_REQ = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        db = new DBHelper(this);

        // App Bar
        MaterialToolbar toolbar = findViewById(R.id.topAppBarAdd);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // View references
        etTask           = findViewById(R.id.etTask);
        etDesc           = findViewById(R.id.etDescription);
        etDue            = findViewById(R.id.etDueDate);
        layoutTask       = findViewById(R.id.layoutTask);

        autoCompleteList = findViewById(R.id.autoCompleteList);
        FloatingActionButton fabSave = findViewById(R.id.fabSave);
        layoutTask.setEndIconOnClickListener(v -> startVoiceInput());

        // Dropdown list setup
        String[] lists = { "По умолчанию", "Работа", "Личное", "Учёба" };
        ArrayAdapter<String> listAdapter =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_list_item_1,
                        lists);
        autoCompleteList.setAdapter(listAdapter);
        autoCompleteList.setText(lists[0], false);

        // Date picker
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder
                .datePicker()
                .setTitleText("Выберите дату")
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(selection);
            int year  = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH) + 1;
            int day   = cal.get(Calendar.DAY_OF_MONTH);

            // Time picker after date
            MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                    .setTitleText("Выберите время")
                    .build();

            timePicker.addOnPositiveButtonClickListener(tp -> {
                int hour   = timePicker.getHour();
                int minute = timePicker.getMinute();
                String formatted = String.format(Locale.getDefault(),
                        "%04d-%02d-%02d %02d:%02d",
                        year, month, day, hour, minute);
                etDue.setText(formatted);
            });

            timePicker.show(getSupportFragmentManager(), "TIME_PICKER");
        });

        etDue.setFocusable(false);
        etDue.setClickable(true);
        etDue.setOnClickListener(v -> datePicker.show(getSupportFragmentManager(), "DATE_PICKER"));

        // Save button
        fabSave.setOnClickListener(v -> {
            String task    = etTask.getText().toString().trim();
            String desc    = etDesc.getText().toString().trim();
            String due     = etDue.getText().toString().trim();
            String list    = autoCompleteList.getText().toString().trim();

            boolean valid = true;
            if (task.isEmpty()) {
                etTask.setError("Введите задачу");
                valid = false;
            }
            if (due.isEmpty()) {
                etDue.setError("Установите срок");
                valid = false;
            }

            if (valid) {
                // Note: DBHelper.addTask currently ignores 'list'
                db.addTask(task, desc, due, list);
                setResult(RESULT_OK);
                finish();
            }
        });
    }
    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Говорите");
        try {
            startActivityForResult(intent, VOICE_REQ);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Голосовой ввод не поддерживается", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VOICE_REQ && resultCode == RESULT_OK && data != null) {
            java.util.ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                etTask.setText(result.get(0));
            }
        }
    }
}

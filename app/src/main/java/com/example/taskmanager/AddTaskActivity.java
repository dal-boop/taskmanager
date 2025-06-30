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
import android.widget.AutoCompleteTextView;

import java.util.Calendar;
import java.util.Locale;

public class AddTaskActivity extends AppCompatActivity {
    private TextInputEditText etTask, etDesc, etDue;
    private AutoCompleteTextView autoCompleteList;
    private DBHelper db;

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
        autoCompleteList = findViewById(R.id.autoCompleteList);
        FloatingActionButton fabSave = findViewById(R.id.fabSave);

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
                db.addTask(task, desc, due);
                setResult(RESULT_OK);
                finish();
            }
        });
    }
}

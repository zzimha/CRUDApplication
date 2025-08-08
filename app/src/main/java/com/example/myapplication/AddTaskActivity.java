package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddTaskActivity extends AppCompatActivity {

    EditText editTextTitle, editTextDescription;
    Button buttonSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_task);

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);
        buttonSubmit = findViewById(R.id.buttonSubmit);

        buttonSubmit.setOnClickListener(v -> {
            String title = editTextTitle.getText().toString().trim();
            String description = editTextDescription.getText().toString().trim();

            Task task = new Task(title, description);

            TaskApiService apiService = ApiClient.getRetrofitInstance().create(TaskApiService.class);
            Call<Task> call = apiService.createTask(task);

            call.enqueue(new Callback<Task>() {
                @Override
                public void onResponse(Call<Task> call, Response<Task> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(AddTaskActivity.this, "Task created!", Toast.LENGTH_SHORT).show();
                        finish(); // go back to main screen
                    } else {
                        Toast.makeText(AddTaskActivity.this, "Failed to create task", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Task> call, Throwable t) {
                    Toast.makeText(AddTaskActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}

package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskListActivity extends AppCompatActivity {

    private RecyclerView taskRecyclerView;
    private TaskAdapter adapter;
    private FloatingActionButton addTaskFab;
    private List<Task> taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        taskRecyclerView = findViewById(R.id.taskRecyclerView);
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        addTaskFab = findViewById(R.id.addTaskFab);
        addTaskFab.setOnClickListener(v -> showAddTaskDialog());

        loadTasksFromApi();
    }

    private void loadTasksFromApi() {
        TaskApiService apiService = ApiClient.getRetrofitInstance().create(TaskApiService.class);
        Call<List<Task>> call = apiService.getTasks();

        call.enqueue(new Callback<List<Task>>() {
            @Override
            public void onResponse(Call<List<Task>> call, Response<List<Task>> response) {
                if (response.isSuccessful()) {
                    taskList = response.body();
                    adapter = new TaskAdapter(TaskListActivity.this, taskList);
                    taskRecyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(TaskListActivity.this, "Failed to load tasks", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Task>> call, Throwable t) {
                Toast.makeText(TaskListActivity.this, "API error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddTaskDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_task, null);
        EditText titleInput = dialogView.findViewById(R.id.titleInput);
        EditText descriptionInput = dialogView.findViewById(R.id.descriptionInput);

        new AlertDialog.Builder(this)
                .setTitle("Add Task")
                .setView(dialogView)
                .setPositiveButton("Add", (dialog, which) -> {
                    String title = titleInput.getText().toString().trim();
                    String description = descriptionInput.getText().toString().trim();
                    createTaskOnApi(title, description);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void createTaskOnApi(String title, String description) {
        TaskApiService apiService = ApiClient.getRetrofitInstance().create(TaskApiService.class);
        Task newTask = new Task(title, description);

        apiService.createTask(newTask).enqueue(new Callback<Task>() {
            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(TaskListActivity.this, "Task added", Toast.LENGTH_SHORT).show();
                    loadTasksFromApi(); // refresh list
                } else {
                    Toast.makeText(TaskListActivity.this, "Failed to add task", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Task> call, Throwable t) {
                Toast.makeText(TaskListActivity.this, "API error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

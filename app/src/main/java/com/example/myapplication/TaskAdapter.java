package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private Context context;
    private List<Task> taskList;

    public TaskAdapter(Context context, List<Task> taskList) {
        this.context = context;
        this.taskList = taskList;
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, descriptionTextView;

        public TaskViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.taskTitle);
            descriptionTextView = itemView.findViewById(R.id.taskDescription);

            // Long press to delete
            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    showDeleteDialog(position);
                }
                return true;
            });

            // Short press to edit
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    showEditDialog(position);
                }
            });
        }

        private void showDeleteDialog(int position) {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Task")
                    .setMessage("Are you sure you want to delete this task?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        Task taskToDelete = taskList.get(position);
                        TaskApiService apiService = ApiClient.getRetrofitInstance().create(TaskApiService.class);
                        apiService.deleteTask(taskToDelete.getId()).enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    taskList.remove(position);
                                    notifyItemRemoved(position);
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                // Handle error
                            }
                        });
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }

        private void showEditDialog(int position) {
            Task task = taskList.get(position);

            View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_task, null);
            TextView titleInput = dialogView.findViewById(R.id.titleInput);
            TextView descriptionInput = dialogView.findViewById(R.id.descriptionInput);

            titleInput.setText(task.getTitle());
            descriptionInput.setText(task.getDescription());

            new AlertDialog.Builder(context)
                    .setTitle("Edit Task")
                    .setView(dialogView)
                    .setPositiveButton("Update", (dialog, which) -> {
                        String newTitle = titleInput.getText().toString().trim();
                        String newDescription = descriptionInput.getText().toString().trim();

                        Task updatedTask = new Task(newTitle, newDescription);
                        updatedTask.setId(task.getId()); // Make sure Task has setId()

                        TaskApiService apiService = ApiClient.getRetrofitInstance().create(TaskApiService.class);
                        apiService.updateTask(task.getId(), updatedTask).enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    taskList.set(position, updatedTask);
                                    notifyItemChanged(position);
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                // Handle failure
                            }
                        });
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.titleTextView.setText(task.getTitle());
        holder.descriptionTextView.setText(task.getDescription());
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }
}

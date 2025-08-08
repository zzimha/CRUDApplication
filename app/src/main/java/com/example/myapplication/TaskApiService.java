package com.example.myapplication;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface TaskApiService {

    @GET("Tasks")
    Call<List<Task>> getTasks();

    @POST("Tasks")
    Call<Task> createTask(@Body Task task);

    @PUT("Tasks/{id}")
    Call<Void> updateTask(@Path("id") int id, @Body Task task);

    @DELETE("Tasks/{id}")
    Call<Void> deleteTask(@Path("id") int id);
}

package com.projectstack.api.service;

import com.projectstack.api.model.Task;

import java.util.List;
import java.util.Optional;

public interface TaskService {
    List<Task> getProjectTasks(String projectId);
    Optional<Task> getTaskById(String taskId);
    Task createTask(String projectId, Task task);
    Optional<Task> updateTask(String taskId, Task taskDetails);
    boolean deleteTask(String taskId);
    Task assignTask(String taskId, String userId);
}

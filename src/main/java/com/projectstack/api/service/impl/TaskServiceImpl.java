package com.projectstack.api.service.impl;

import com.projectstack.api.model.Task;
import com.projectstack.api.model.TeamMember;
import com.projectstack.api.model.User;
import com.projectstack.api.repository.TaskRepository;
import com.projectstack.api.repository.UserRepository;
import com.projectstack.api.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<Task> getProjectTasks(String projectId) {
        return taskRepository.findByProjectId(projectId);
    }

    @Override
    public Optional<Task> getTaskById(String taskId) {
        return taskRepository.findById(taskId);
    }

    @Override
    public Task createTask(String projectId, Task task) {
        task.setProjectId(projectId);
        return taskRepository.save(task);
    }

    @Override
    public Optional<Task> updateTask(String taskId, Task taskDetails) {
        Optional<Task> task = taskRepository.findById(taskId);
        if (task.isPresent()) {
            Task updatedTask = task.get();
            updatedTask.setTitle(taskDetails.getTitle());
            updatedTask.setDescription(taskDetails.getDescription());
            updatedTask.setStatus(taskDetails.getStatus());
            updatedTask.setPriority(taskDetails.getPriority());
            updatedTask.setTags(taskDetails.getTags());
            updatedTask.setAssignee(taskDetails.getAssignee());
            updatedTask.setDueDate(taskDetails.getDueDate());

            return Optional.of(taskRepository.save(updatedTask));
        }
        return Optional.empty();
    }

    @Override
    public boolean deleteTask(String taskId) {
        if (taskRepository.existsById(taskId)) {
            taskRepository.deleteById(taskId);
            return true;
        }
        return false;
    }

    @Override
    public Task assignTask(String taskId, String userId) {
        Optional<Task> task = taskRepository.findById(taskId);
        Optional<User> user = userRepository.findById(userId); // Ensure this returns a User

        if (task.isPresent() && user.isPresent()) {
            Task updatedTask = task.get();

            // Convert User to TeamMember (Modify this based on your actual model structure)
            TeamMember teamMember = convertUserToTeamMember(user.get());

            updatedTask.setAssignee(teamMember); // Assign converted TeamMember
            return taskRepository.save(updatedTask);
        }
        return null;
    }

    // Method to convert User to TeamMember (modify as needed)
    private TeamMember convertUserToTeamMember(User user) {
        TeamMember teamMember = new TeamMember();
        teamMember.setId(user.getId());  // Assuming both have an ID
        teamMember.setName(user.getName());  // Assuming they have similar fields
        return teamMember;
    }

}

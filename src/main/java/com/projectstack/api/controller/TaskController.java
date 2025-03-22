
package com.projectstack.api.controller;

import com.projectstack.api.model.Project;
import com.projectstack.api.model.Task;
import com.projectstack.api.payload.response.MessageResponse;
import com.projectstack.api.security.UserDetailsImpl;
import com.projectstack.api.service.ProjectService;
import com.projectstack.api.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/projects/{projectId}/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private ProjectService projectService;

    @GetMapping
    public ResponseEntity<?> getProjectTasks(@PathVariable String projectId,
                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Optional<Project> project = projectService.getProjectById(projectId);
        if (project.isPresent() && projectService.isProjectMember(project.get(), userDetails.getId())) {
            List<Task> tasks = taskService.getProjectTasks(projectId);
            return ResponseEntity.ok(tasks);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> createTask(@PathVariable String projectId,
                                      @Valid @RequestBody Task task,
                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Optional<Project> project = projectService.getProjectById(projectId);
        if (project.isPresent() && projectService.isProjectMember(project.get(), userDetails.getId())) {
            Task savedTask = taskService.createTask(projectId, task);
            return ResponseEntity.ok(savedTask);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<?> updateTask(@PathVariable String projectId,
                                      @PathVariable String taskId,
                                      @Valid @RequestBody Task taskDetails,
                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Optional<Project> project = projectService.getProjectById(projectId);
        
        if (project.isPresent() && projectService.isProjectMember(project.get(), userDetails.getId())) {
            Optional<Task> updatedTask = taskService.updateTask(taskId, taskDetails);
            if (updatedTask.isPresent()) {
                return ResponseEntity.ok(updatedTask.get());
            }
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<?> deleteTask(@PathVariable String projectId,
                                      @PathVariable String taskId,
                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Optional<Project> project = projectService.getProjectById(projectId);
        
        if (project.isPresent() && projectService.isProjectMember(project.get(), userDetails.getId())) {
            boolean deleted = taskService.deleteTask(taskId);
            if (deleted) {
                return ResponseEntity.ok(new MessageResponse("Task deleted successfully"));
            }
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{taskId}/assign")
    public ResponseEntity<?> assignTask(@PathVariable String projectId,
                                       @PathVariable String taskId,
                                       @RequestBody Map<String, String> request,
                                       @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Optional<Project> project = projectService.getProjectById(projectId);
        
        if (project.isPresent() && projectService.isProjectMember(project.get(), userDetails.getId())) {
            String userId = request.get("userId");
            Task updatedTask = taskService.assignTask(taskId, userId);
            if (updatedTask != null) {
                return ResponseEntity.ok(updatedTask);
            }
        }
        return ResponseEntity.notFound().build();
    }
}

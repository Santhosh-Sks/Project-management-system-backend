
package com.projectstack.api.controller;

import com.projectstack.api.model.Comment;
import com.projectstack.api.model.Project;
import com.projectstack.api.payload.response.MessageResponse;
import com.projectstack.api.security.UserDetailsImpl;
import com.projectstack.api.service.CommentService;
import com.projectstack.api.service.ProjectService;
import com.projectstack.api.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/projects/{projectId}/tasks/{taskId}/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private TaskService taskService;

    @GetMapping
    public ResponseEntity<?> getTaskComments(@PathVariable String projectId,
                                           @PathVariable String taskId,
                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Optional<Project> project = projectService.getProjectById(projectId);
        if (project.isPresent() && projectService.isProjectMember(project.get(), userDetails.getId())) {
            List<Comment> comments = commentService.getTaskComments(taskId);
            return ResponseEntity.ok(comments);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> createComment(@PathVariable String projectId,
                                         @PathVariable String taskId,
                                         @RequestBody Map<String, String> request,
                                         @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Optional<Project> project = projectService.getProjectById(projectId);
        if (project.isPresent() && projectService.isProjectMember(project.get(), userDetails.getId())) {
            String text = request.get("text");
            Comment comment = commentService.createComment(taskId, userDetails.getId(), text);
            if (comment != null) {
                return ResponseEntity.ok(comment);
            }
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable String projectId,
                                         @PathVariable String taskId,
                                         @PathVariable String commentId,
                                         @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Optional<Project> project = projectService.getProjectById(projectId);
        if (project.isPresent() && projectService.isProjectMember(project.get(), userDetails.getId())) {
            boolean deleted = commentService.deleteComment(commentId, userDetails.getId());
            if (deleted) {
                return ResponseEntity.ok(new MessageResponse("Comment deleted successfully"));
            }
        }
        return ResponseEntity.notFound().build();
    }
}

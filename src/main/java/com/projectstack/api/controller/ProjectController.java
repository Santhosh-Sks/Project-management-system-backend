package com.projectstack.api.controller;

import com.projectstack.api.model.Project;
import com.projectstack.api.payload.request.InviteRequest;
import com.projectstack.api.payload.response.MessageResponse;
import com.projectstack.api.security.UserDetailsImpl;
import com.projectstack.api.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "*") // Allow frontend requests
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @GetMapping
    public List<Project> getUserProjects(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return projectService.getUserProjects(userDetails.getId());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProject(@PathVariable String id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Optional<Project> project = projectService.getProjectById(id);
        if (project.isPresent() && projectService.isProjectMember(project.get(), userDetails.getId())) {
            return ResponseEntity.ok(project.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> createProject(@Valid @RequestBody Project project,
                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Project savedProject = projectService.createProject(project, userDetails.getId());
        if (savedProject != null) {
            return ResponseEntity.ok(savedProject);
        }
        return ResponseEntity.badRequest().body(new MessageResponse("User not found", false));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProject(@PathVariable String id,
                                           @Valid @RequestBody Project projectDetails,
                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Optional<Project> updatedProject = projectService.updateProject(id, projectDetails, userDetails.getId());
        return updatedProject.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/invites")
    public ResponseEntity<?> inviteMembers(@PathVariable String id,
                                           @Valid @RequestBody InviteRequest inviteRequest,
                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        boolean success = projectService.inviteMembers(id, inviteRequest, userDetails.getId());
        return success ? ResponseEntity.ok(new MessageResponse("Invitations sent successfully"))
                : ResponseEntity.notFound().build();
    }
}

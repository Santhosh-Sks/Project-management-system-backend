
package com.projectstack.api.controller;

import com.projectstack.api.model.ChatMessage;
import com.projectstack.api.model.Project;
import com.projectstack.api.security.UserDetailsImpl;
import com.projectstack.api.service.ChatService;
import com.projectstack.api.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/projects/{projectId}/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private ProjectService projectService;

    @GetMapping
    public ResponseEntity<?> getProjectChat(@PathVariable String projectId,
                                          @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Optional<Project> project = projectService.getProjectById(projectId);
        if (project.isPresent() && projectService.isProjectMember(project.get(), userDetails.getId())) {
            List<ChatMessage> messages = chatService.getProjectMessages(projectId);
            return ResponseEntity.ok(messages);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> sendChatMessage(@PathVariable String projectId,
                                           @RequestBody Map<String, String> request,
                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Optional<Project> project = projectService.getProjectById(projectId);
        if (project.isPresent() && projectService.isProjectMember(project.get(), userDetails.getId())) {
            String text = request.get("text");
            ChatMessage message = chatService.sendMessage(projectId, userDetails.getId(), text);
            if (message != null) {
                return ResponseEntity.ok(message);
            }
        }
        return ResponseEntity.notFound().build();
    }
}


package com.projectstack.api.service;

import com.projectstack.api.model.Project;
import com.projectstack.api.payload.request.InviteRequest;

import java.util.List;
import java.util.Optional;

public interface ProjectService {
    List<Project> getUserProjects(String userId);
    Optional<Project> getProjectById(String id);
    Project createProject(Project project, String userId);
    Optional<Project> updateProject(String id, Project projectDetails, String userId);
    boolean inviteMembers(String projectId, InviteRequest inviteRequest, String userId);
    boolean isProjectMember(Project project, String userId);
    boolean isProjectAdmin(Project project, String userId);
}

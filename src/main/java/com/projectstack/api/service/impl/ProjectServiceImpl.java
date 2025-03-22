package com.projectstack.api.service.impl;

import com.projectstack.api.model.Project;
import com.projectstack.api.model.TeamMember;
import com.projectstack.api.model.User;
import com.projectstack.api.payload.request.InviteRequest;
import com.projectstack.api.repository.ProjectRepository;
import com.projectstack.api.repository.UserRepository;
import com.projectstack.api.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<Project> getUserProjects(String userId) {
        return projectRepository.findByMemberId(userId);
    }

    @Override
    public Optional<Project> getProjectById(String id) {
        return projectRepository.findById(id);
    }

    @Override
    public Project createProject(Project project, String userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            TeamMember creator = new TeamMember();
            creator.setId(user.get().getId());
            creator.setName(user.get().getName());
            creator.setEmail(user.get().getEmail());
            creator.setRole("admin");

            project.getMembers().add(creator);
            return projectRepository.save(project);
        }
        return null;
    }

    @Override
    public Optional<Project> updateProject(String id, Project projectDetails, String userId) {
        Optional<Project> project = projectRepository.findById(id);
        if (project.isPresent() && isProjectAdmin(project.get(), userId)) {
            Project updatedProject = project.get();
            updatedProject.setName(projectDetails.getName());
            updatedProject.setDescription(projectDetails.getDescription());
            updatedProject.setStatus(projectDetails.getStatus());
            updatedProject.setTechStack(projectDetails.getTechStack());

            return Optional.of(projectRepository.save(updatedProject));
        }
        return Optional.empty();
    }

    @Override
    public boolean inviteMembers(String projectId, InviteRequest inviteRequest, String userId) {
        Optional<Project> project = projectRepository.findById(projectId);
        if (project.isPresent() && isProjectAdmin(project.get(), userId)) {
            Project existingProject = project.get();

            for (String email : inviteRequest.getEmails()) {
                Optional<User> user = userRepository.findByEmail(email);
                if (user.isPresent()) {
                    TeamMember newMember = new TeamMember();
                    newMember.setId(user.get().getId());
                    newMember.setName(user.get().getName());
                    newMember.setEmail(user.get().getEmail());
                    newMember.setRole(inviteRequest.getRole());

                    if (!existingProject.getMembers().stream()
                            .anyMatch(m -> m.getId().equals(newMember.getId()))) {
                        existingProject.getMembers().add(newMember);
                    }
                }
            }

            projectRepository.save(existingProject);
            return true;
        }
        return false;
    }

    @Override
    public boolean isProjectMember(Project project, String userId) {
        return project.getMembers().stream()
                .anyMatch(member -> member.getId().equals(userId));
    }

    @Override
    public boolean isProjectAdmin(Project project, String userId) {
        return project.getMembers().stream()
                .anyMatch(member -> member.getId().equals(userId) && member.getRole().equals("admin"));
    }
}

package com.projectstack.api.service;

import com.projectstack.api.model.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentService {
    List<Comment> getTaskComments(String taskId);
    Comment createComment(String taskId, String userId, String text);
    boolean deleteComment(String commentId, String userId);
}


package com.projectstack.api.service.impl;

import com.projectstack.api.model.Comment;
import com.projectstack.api.model.User;
import com.projectstack.api.repository.CommentRepository;
import com.projectstack.api.repository.UserRepository;
import com.projectstack.api.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<Comment> getTaskComments(String taskId) {
        return commentRepository.findByTaskId(taskId);
    }

    @Override
    public Comment createComment(String taskId, String userId, String text) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            Comment comment = new Comment();
            comment.setTaskId(taskId);
            comment.setUserId(userId);
            comment.setUserName(user.get().getName());
            comment.setText(text);
            return commentRepository.save(comment);
        }
        return null;
    }

    @Override
    public boolean deleteComment(String commentId, String userId) {
        Optional<Comment> comment = commentRepository.findById(commentId);
        if (comment.isPresent() && comment.get().getUserId().equals(userId)) {
            commentRepository.deleteById(commentId);
            return true;
        }
        return false;
    }
}

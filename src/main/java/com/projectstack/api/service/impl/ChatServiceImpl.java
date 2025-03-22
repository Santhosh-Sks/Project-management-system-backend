
package com.projectstack.api.service.impl;

import com.projectstack.api.model.ChatMessage;
import com.projectstack.api.model.User;
import com.projectstack.api.repository.ChatMessageRepository;
import com.projectstack.api.repository.UserRepository;
import com.projectstack.api.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Override
    public List<ChatMessage> getProjectMessages(String projectId) {
        return chatMessageRepository.findByProjectIdOrderByTimestampAsc(projectId);
    }

    @Override
    public ChatMessage sendMessage(String projectId, String userId, String text) {
        Optional<User> userOptional = userRepository.findById(userId);
        
        if (userOptional.isPresent() && text != null && !text.trim().isEmpty()) {
            User sender = userOptional.get();
            
            ChatMessage message = new ChatMessage();
            message.setProjectId(projectId);
            message.setSender(sender);
            message.setText(text);
            message.setTimestamp(LocalDateTime.now());
            
            return chatMessageRepository.save(message);
        }
        
        return null;
    }
}

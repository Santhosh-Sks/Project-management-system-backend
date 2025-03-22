
package com.projectstack.api.service;

import com.projectstack.api.model.ChatMessage;

import java.util.List;

public interface ChatService {
    List<ChatMessage> getProjectMessages(String projectId);
    ChatMessage sendMessage(String projectId, String userId, String text);
}

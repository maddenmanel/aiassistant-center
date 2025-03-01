package com.maddenmanel.aiassistant.center.domain;

import java.util.ArrayList;
import java.util.List;

import org.springframework.ai.chat.model.ChatResponse;

public class UserChatHistory {

    private final List<String> userMessages;
    private final List<ChatResponse> aiResponses;

    public UserChatHistory() {
        this.userMessages = new ArrayList<>();
        this.aiResponses = new ArrayList<>();
    }

    public void addUserMessage(String message) {
        userMessages.add(message);
    }

    public void addAIResponse(ChatResponse response) {
        aiResponses.add(response);
    }

    public List<String> getUserMessages() {
        return userMessages;
    }

    public List<ChatResponse> getAIResponses() {
        return aiResponses;
    }

    @Override
    public String toString() {
        return "UserChatHistory{" +
                "userMessages=" + userMessages +
                ", aiResponses=" + aiResponses +
                '}';
    }
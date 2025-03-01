package com.maddenmanel.aiassistant.center.domain;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatSession {
    private String sessionId;
    private String userId;
    private String userName;
    private List<ChatMessage> conversationHistory;  // 存储所有的问答
    private long lastActiveTime;  // 上次活跃时间，用于清理会话过期数据

    // 添加消息的方法
    public void addMessage(String question, String answer) {
        ChatMessage message = ChatMessage.builder()
                .userId(userId)
                .userName(userName)
                .question(question)
                .answer(answer)
                .timestamp(System.currentTimeMillis())
                .build();
        conversationHistory.add(message);
        lastActiveTime = System.currentTimeMillis();
    }
}
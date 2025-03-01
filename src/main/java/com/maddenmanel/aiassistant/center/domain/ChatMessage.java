package com.maddenmanel.aiassistant.center.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private String userId;  // 用户ID
    private String userName;  // 用户名
    private String question;  // 用户提问
    private String answer;  // 机器人回答
    private long timestamp;  // 时间戳
}
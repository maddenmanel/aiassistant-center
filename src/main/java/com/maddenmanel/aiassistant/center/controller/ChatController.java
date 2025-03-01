package com.maddenmanel.aiassistant.center.controller;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import org.springframework.ai.chat.messages.UserMessage;

import com.maddenmanel.aiassistant.center.domain.ChatSession;
import com.maddenmanel.aiassistant.center.dto.AIAnswerDTO;
import com.maddenmanel.aiassistant.center.service.RedisService;
import com.maddenmanel.aiassistant.center.service.impl.GptServiceImpl;
import com.maddenmanel.aiassistant.center.utils.JacksonUtil;

import jakarta.annotation.Resource;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;

@RestController
public class ChatController {

    private final OpenAiChatModel chatModel;

    @Autowired
    private RedisService redisService;

    @Autowired
    public ChatController(OpenAiChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @Resource
    private GptServiceImpl gptService;

    @GetMapping(value = "/completions", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<AIAnswerDTO>> getStream(@RequestParam("messages")String messages) {
        return gptService.doChatGPTStream(messages)
                .map(aiAnswerDTO -> ServerSentEvent.<AIAnswerDTO>builder()
                        .data(aiAnswerDTO)
                        .build()
                )
                .onErrorResume(e -> Flux.empty());
    }



    @GetMapping("/ai/generateStream")
        public Flux<ServerSentEvent<ChatResponse>> generateStream(@RequestParam("message") String message,
                                                                @RequestParam("userId") String userId,
                                                                @RequestParam("userName") String userName) {
            // 获取或创建会话 ID（每天一个）
            String sessionId = getOrCreateSessionId(userId);
            String redisKey = "chat:history:" + sessionId;

            // 构造 Prompt 进行对话
            Prompt prompt = new Prompt(new UserMessage(message));

            // 通过流式方式调用 AI，并存储对话记录
            return this.chatModel.stream(prompt)
                    .flatMap(chatResponse -> {
                        // 追加消息到 Redis
                        saveChatMessage(redisKey, userId, userName, message, chatResponse.getResult().getOutput().getText());

                        // 返回 SSE 响应
                        return Flux.just(ServerSentEvent.<ChatResponse>builder()
                                .data(chatResponse)
                                .build());
                    })
                    .onErrorResume(e -> Flux.empty()); // 处理异常，避免中断
        }


    /**
     * 普通问答接口，支持多轮对话存储
     */
    @GetMapping("/ai/generate")
    public Map<String, Object> generate(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message,
                                        @RequestParam(value = "userId") String userId,
                                        @RequestParam(value = "userName") String userName) {
        // 生成 AI 回复
        String response = this.chatModel.call(message);

        // 获取或创建 sessionId
        String sessionId = getOrCreateSessionId(userId);
        String redisKey = "chat:history:" + sessionId;

        // 追加对话记录
        saveChatMessage(redisKey, userId, userName, message, response);

        // 返回当前轮的问答数据
        return Map.of(
                "sessionId", sessionId,
                "userId", userId,
                "userName", userName,
                "question", message,
                "answer", response
        );
    }

    /**
     * 获取或创建一个基于当天的 sessionId
     */
    private String getOrCreateSessionId(String userId) {
        // 当前日期作为 sessionId 后缀
        String currentDate = LocalDate.now().toString();
        String redisKey = "chat:session:" + userId;

        // 尝试从 Redis 读取 sessionId
        String sessionId = redisService.getData(redisKey, String.class);

        if (sessionId == null || !sessionId.endsWith(currentDate)) {
            // 生成新的 sessionId（每天更新）
            sessionId = "chat:" + userId + ":" + currentDate;

            // 存入 Redis，设置 30 天过期
            redisService.saveData(redisKey, sessionId);
            redisService.setExpire(redisKey, 30 * 24 * 60 * 60);
        }

        return sessionId;
    }

    /**
     * 追加消息到 Redis 中的对话记录
     */
    private void saveChatMessage(String redisKey, String userId, String userName, String question, String answer) {
        // 从 Redis 读取现有的对话记录
        ChatSession chatSession = redisService.getData(redisKey, ChatSession.class);

        if (chatSession == null) {
            chatSession = ChatSession.builder()
                    .sessionId(redisKey)
                    .userId(userId)
                    .userName(userName)
                    .conversationHistory(new ArrayList<>())
                    .lastActiveTime(System.currentTimeMillis())
                    .build();
        }

        // 追加新消息
        chatSession.addMessage(question, answer);

        // 存回 Redis，并重置 30 天过期时间
        redisService.saveData(redisKey, JacksonUtil.objectToJsonStr(chatSession));
        System.out.println(redisService.getData(redisKey, Map.class));
        redisService.setExpire(redisKey, 30 * 24 * 60 * 60);
    }
}
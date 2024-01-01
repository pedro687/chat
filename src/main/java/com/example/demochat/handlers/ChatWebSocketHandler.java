package com.example.demochat.handlers;

import com.example.demochat.models.ChatMessage;
import com.example.demochat.repository.ChatMessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;


@Service
public class ChatWebSocketHandler extends TextWebSocketHandler {
    private final static Logger LOGGER = Logger.getLogger(ChatWebSocketHandler.class.getName());
    private static final String CHAT_CACHE_KEY = "recent_chat_messages";
    public static List<WebSocketSession> sessionList = new ArrayList<>();


    private final RedisTemplate<String, Object> redisTemplate;

    private final ChatMessageRepository chatMessageRepository;

    private final ObjectMapper objectMapper;

    public ChatWebSocketHandler(RedisTemplate<String, Object> redisTemplate, ChatMessageRepository chatMessageRepository, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.chatMessageRepository = chatMessageRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        LOGGER.info("Sessão iniciada: " + session.getId());
        sessionList.add(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {
        ChatMessage chatMessage = objectMapper.readValue(textMessage.getPayload(), ChatMessage.class);
        chatMessageRepository.save(chatMessage);

        // Adicionar a mensagem ao cache do Redis (usando um conjunto ordenado)
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        zSetOperations.add(CHAT_CACHE_KEY, chatMessage, new Date().getTime());
        // Enviar a mensagem para o Redis
        redisTemplate.convertAndSend("chat", chatMessage);
    }
}

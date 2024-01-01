package com.example.demochat.services;

import com.example.demochat.models.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String CHAT_CACHE_KEY = "recent_chat_messages";

    public ChatService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public List<ChatMessage> getRecentMessages(int count) {
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        Set<Object> messages = zSetOperations.reverseRange(CHAT_CACHE_KEY, 0, count - 1);

        // Converte os objetos do cache de volta para mensagens de chat
        List<ChatMessage> recentMessages = messages.stream()
                .map(obj -> (ChatMessage) obj)
                .collect(Collectors.toList());

        return recentMessages;
    }
}
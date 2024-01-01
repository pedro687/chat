package com.example.demochat.redis.sub;

import com.example.demochat.handlers.ChatWebSocketHandler;
import com.example.demochat.models.ChatMessage;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.logging.Logger;

@Component
public class RedisMessageListener implements MessageListener {
    private final Logger LOGGER = Logger.getLogger(RedisMessageListener.class.getName());
    private final ObjectMapper objectMapper;

    public RedisMessageListener(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;

    }

    private void sendMessageToAll(ChatMessage chatMessage) {
        for (WebSocketSession session : ChatWebSocketHandler.sessionList) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatMessage)));
                    LOGGER.info("Devolvido com sucesso!");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            ChatMessage chatMessage = objectMapper.readValue(message.getBody(), ChatMessage.class);
            sendMessageToAll(chatMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package com.example.demochat.controllers;

import com.example.demochat.models.ChatMessage;
import com.example.demochat.services.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/recent-messages")
    public List<ChatMessage> getRecentMessages(@RequestParam int count) {
        return chatService.getRecentMessages(count);
    }
}
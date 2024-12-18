package com.example.pifinity.controller;

import com.example.pifinity.dto.ChatGPTResponse;
import com.example.pifinity.serviceImpl.ChatGPTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ChatBotController {

    @Autowired
    private ChatGPTService chatGPTService;

    @GetMapping("chatbot")
    public String chatBot(@RequestParam("query") String query) {
        ChatGPTResponse response = chatGPTService.getChatGPTRespone(query);

        return response.getChoices().get(0).getMessage().getContent();
    }
}

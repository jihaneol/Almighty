package com.example.A201.chatbot.controller;

import com.example.A201.chatbot.dto.ChatLogDto;
import com.example.A201.chatbot.service.ChatLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatLogController {

    private final ChatLogService chatLogService;

   @PostMapping("/interact")
    public ResponseEntity<ChatLogDto> interactWithBot(@RequestBody ChatLogDto request) {
        ChatLogDto response = chatLogService.getAnswerFromChatGPT(request);
        return ResponseEntity.ok(response);
    }
}

package com.ns.marketservice.Controller;

import com.ns.marketservice.Service.ChatService;
import com.ns.marketservice.Service.chatPubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/chat")
public class ChatController {
    @Autowired
    private ChatService chatService;

    @PostMapping("/send")
    public ResponseEntity<Void> sendMessage(@RequestParam String user1Id, @RequestParam String user2Id, @RequestParam String message) {
        chatService.sendMessage(user1Id, user2Id, message);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/history")
    public ResponseEntity<List<MapRecord<String, Object, Object>>> getChatHistory(@RequestParam String user1Id, @RequestParam String user2Id) {
        List<MapRecord<String, Object, Object>> history = chatService.getChatHistory(user1Id, user2Id);
        return ResponseEntity.ok(history);
    }
}

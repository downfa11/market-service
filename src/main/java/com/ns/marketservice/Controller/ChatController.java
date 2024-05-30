package com.ns.marketservice.Controller;

import com.ns.marketservice.Domain.DTO.ChatHistoryRequest;
import com.ns.marketservice.Domain.DTO.ChatRequest;
import com.ns.marketservice.Domain.DTO.messageEntity;
import com.ns.marketservice.Service.ChatService;
import com.ns.marketservice.Utils.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/send")
    public ResponseEntity<messageEntity> sendMessage(@RequestBody ChatRequest chatRequest) {

        Long idx = jwtTokenProvider.getMembershipIdbyToken();
        if (idx == 0)
            return ResponseEntity.ok().body(new messageEntity("Fail","Not Authorization or boardId is incorrect."));

        String myId = chatRequest.getMyId();
        String yourId = chatRequest.getYourId();
        String message = chatRequest.getMessage();

        chatService.sendMessage(myId, yourId, message);
        // Todo. 나중에 작업할때는 myId가 아니라, idx로 Jwt의 현재 접속한 계정을 받아야한다.

        return ResponseEntity.ok()
                .body(new messageEntity("Success","Send ["+message+"]"));
    }

    @GetMapping("/history")
    public ResponseEntity<messageEntity> getChatHistory(@RequestBody ChatHistoryRequest chatHistoryRequest) {
        String user1Id = chatHistoryRequest.getUser1Id();
        String user2Id = chatHistoryRequest.getUser2Id();

        return ResponseEntity.ok()
                .body(new messageEntity("Success",chatService.getChatHistory(user1Id, user2Id)));
    }
}

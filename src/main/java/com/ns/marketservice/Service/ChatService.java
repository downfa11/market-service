package com.ns.marketservice.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ChatService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedisMessageListenerContainer container;

    @Autowired
    private chatSubListener chatSubListener;

    @Autowired
    private ObjectMapper objectMapper;

    String chatTopic = "chatHistory:users:";

    public void sendMessage(String myId, String yourId, String message) {
        String chatRoomId = createChatRoomId(myId, yourId);
        String reverseId = createChatRoomId(yourId, myId);

        if(chatRoomExists(reverseId))
            chatRoomId = reverseId;

        if (!chatRoomExists(chatRoomId))
            createChatRoom(chatRoomId);

        Map<String, String> messageMap = new HashMap<>();
        messageMap.put("userId", myId);
        messageMap.put("message", message);

        try {
            String messageMapJson = objectMapper.writeValueAsString(messageMap);
            redisTemplate.convertAndSend(chatRoomId, messageMapJson);
            redisTemplate.opsForStream().add(chatRoomId, messageMap);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public List<MapRecord<String, Object, Object>> getChatHistory(String myId, String yourId) {
        String chatRoomId = createChatRoomId(myId, yourId);
        String reverseId = createChatRoomId(yourId, myId);

        if(chatRoomExists(reverseId))
            chatRoomId = reverseId;

        if (!chatRoomExists(chatRoomId))
            createChatRoom(chatRoomId);

        return redisTemplate.opsForStream().range(chatRoomId, Range.unbounded());
    }
    private boolean chatRoomExists(String chatRoomId) {
        return redisTemplate.hasKey(chatRoomId);
    }

    private void createChatRoom(String chatRoomId) {
        container.addMessageListener(chatSubListener, new PatternTopic(chatRoomId));
    }

    private String createChatRoomId(String userId1, String userId2) {
        List<String> userIds = Arrays.asList(userId1, userId2);
        Collections.sort(userIds);
        return chatTopic+String.join(":", userIds);
    }
}


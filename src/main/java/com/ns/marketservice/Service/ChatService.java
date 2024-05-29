package com.ns.marketservice.Service;

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
    private chatSubService chatSubService;

    public void sendMessage(String userId1, String userId2, String message) {
        String chatRoomId = createChatRoomId(userId1, userId2);

        if (!chatRoomExists(chatRoomId)) {
            createChatRoom(chatRoomId);
        }

        Map<String, String> messageMap = new HashMap<>();
        messageMap.put("userId", userId1);
        messageMap.put("message", message);

        redisTemplate.convertAndSend(chatRoomId, message);
        redisTemplate.opsForStream().add(chatRoomId, messageMap);    }

    public List<MapRecord<String, Object, Object>> getChatHistory(String user1Id, String user2Id) {
        String chatRoomId = createChatRoomId(user1Id, user2Id);
        return redisTemplate.opsForStream().range(chatRoomId, Range.unbounded());
    }
    private boolean chatRoomExists(String chatRoomId) {
        return redisTemplate.hasKey(chatRoomId);
    }

    private void createChatRoom(String chatRoomId) {
        // Redis 스트림 생성 (실제 데이터 저장 로직은 생략)

        // 리스너 등록
        container.addMessageListener(chatSubService, new PatternTopic(chatRoomId));
    }

    private String createChatRoomId(String userId1, String userId2) {
        List<String> userIds = Arrays.asList(userId1, userId2);
        Collections.sort(userIds);
        return String.join("-", userIds);
    }
}


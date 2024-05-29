package com.ns.marketservice.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class chatPubService {
    @Autowired
    private StringRedisTemplate redisTemplate;
    public void publish(String chatRoomId, String userId, String message) {
        Map<String, String> messageMap = new HashMap<>();
        messageMap.put("userId", userId);
        messageMap.put("message", message);

        redisTemplate.convertAndSend(chatRoomId, message);
        redisTemplate.opsForStream().add(chatRoomId, messageMap);
    }

    public List<MapRecord<String, Object, Object>> getChatHistory(String chatRoomId) {
        return redisTemplate.opsForStream().range(chatRoomId, Range.unbounded());
    }
}

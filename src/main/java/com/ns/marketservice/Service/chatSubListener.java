package com.ns.marketservice.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

@Service
public class chatSubListener implements MessageListener {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String messageBody = new String(message.getBody());
        try {
            JsonNode jsonNode = objectMapper.readTree(messageBody);
            String userId = jsonNode.get("userId").asText();
            String msg = jsonNode.get("message").asText();

            String channel = new String(message.getChannel());
            System.out.println("user [" + userId + "] : " + msg + " in channel [" + channel + "]");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

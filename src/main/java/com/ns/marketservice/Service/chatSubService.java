package com.ns.marketservice.Service;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

@Service
public class chatSubService implements MessageListener {
    @Override
    public void onMessage(Message message, byte[] pattern) {
        String messageBody = new String(message.getBody());
        String channel = new String(message.getChannel());

        System.out.println("Received message from channel [" + channel + "]: " + messageBody);
    }
}

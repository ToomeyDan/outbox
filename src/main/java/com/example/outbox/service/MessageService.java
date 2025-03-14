package com.example.outbox.service;

import com.example.outbox.entity.Message;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MessageService {

    List<Message> messages = new ArrayList<>();

    private final Settings settings;
    public MessageService(Settings settings) {
        this.settings = settings;

        //add messages
        Message m1 = Message.builder()
                .dateOfLastAction(LocalDateTime.now())
                .id(1L)
                .sent(false)
                .text("This is the first message")
                .tries(0)
                .build();
        messages.add(m1);

        Message m2 = Message.builder()
                .dateOfLastAction(LocalDateTime.now())
                .id(2L)
                .sent(false)
                .text("This is the second message")
                .tries(0)
                .build();
        messages.add(m2);

        Message m3 = Message.builder()
                .dateOfLastAction(LocalDateTime.now())
                .id(3L)
                .sent(false)
                .text("This is the third message")
                .tries(0)
                .build();
        messages.add(m3);
    }

    public List<Message> getMessagesToSend() {
        List<Message> results = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (Message message: messages) {
            // if already sent we are done
            if (message.isSent()) continue;

            int tries = message.getTries();

            // if we have not tried at all
            if (tries == 0) {
                results.add(message);
                continue;
            }

            // has service died?
            if (tries >= settings.buckets) continue;

            // has enough time elapsed for next retry
            Duration duration = Duration.between(message.getDateOfLastAction(), now);
            long elapsedSeconds = duration.getSeconds();
            if (elapsedSeconds >= settings.delays[tries]) {
                results.add(message);
            }
        }
        return results;
    }

    public void retryFailedMessages() {
        for (Message message: messages) {
            if (!message.isSent()) {
                message.setTries(0);
            }
        }
    }
}

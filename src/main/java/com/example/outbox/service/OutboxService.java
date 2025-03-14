package com.example.outbox.service;

import com.example.outbox.entity.Message;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class OutboxService implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(OutboxService.class);

    private static final int SLEEP_BETWEEN_LOOPS = 1;

    private final SleepForSeconds sleepForSeconds;
    private final MessageService messageService;
    private final SenderService senderService;

    public OutboxService(SleepForSeconds sleepForSeconds,
                         MessageService messageService,
                         SenderService senderService) {
        this.sleepForSeconds = sleepForSeconds;
        this.messageService = messageService;
        this.senderService = senderService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.debug("Service started on startup using ApplicationRunner");

        boolean running = true;
        while(running) {

            //find messages to send
            List<Message> messages = messageService.getMessagesToSend();

            for (Message message:messages) {
                // try to send the message
                boolean sent = senderService.sendMessage(message.getText());
                message.setDateOfLastAction(LocalDateTime.now());

                if (sent) {
                    // rem that we did this one
                    message.setSent(true);

                    //once any message works, retry the others that failed
                    messageService.retryFailedMessages();
                } else {
                    // bump message settings
                    message.setTries(message.getTries()+1);
                }
            }

            //wait before starting again
            sleepForSeconds.sleepForSeconds(SLEEP_BETWEEN_LOOPS);
        }
    }
}

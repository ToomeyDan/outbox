package com.example.outbox.service;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Random;

//outbox.send_failure_rate

@Slf4j
@Service
public class SenderService {

    private static final Logger logger = LoggerFactory.getLogger(SenderService.class);

    private static final int SECONDS_TO_SLEEP = 1;

    private final SleepForSeconds sleepForSeconds;
    private final Settings settings;

    public SenderService(SleepForSeconds sleepForSeconds, Settings settings) {
        this.sleepForSeconds = sleepForSeconds;
        this.settings = settings;
    }

    public boolean sendMessage(String message) {
        logger.debug(MessageFormat.format("Sending message :{0}", message));
        sleepForSeconds.sleepForSeconds(SECONDS_TO_SLEEP);

        // chance whether send worked or not - number between 1 and 100
        Random random = new Random();
        int randomNumber = random.nextInt(100) + 1;
        boolean worked = randomNumber > settings.failureRate;
        if (worked) {
            logger.debug(MessageFormat.format("Send message:{0} worked", message));
        } else {
            logger.debug(MessageFormat.format("Send message:{0} failed", message));
        }
        return worked;
    }
}

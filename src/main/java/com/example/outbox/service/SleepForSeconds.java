package com.example.outbox.service;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SleepForSeconds {

    private static final Logger logger = LoggerFactory.getLogger(SleepForSeconds.class);

    public void sleepForSeconds(int secondsToSleep) {
        // wait one second
        try {
            Thread.sleep(secondsToSleep * 1000L);
        } catch (InterruptedException ie) {
            logger.error("Sleep failed", ie);
            Thread.currentThread().interrupt();
        }
    }
}

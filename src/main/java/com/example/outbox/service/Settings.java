package com.example.outbox.service;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Arrays;

/*
Holder for parameters pulled in

# how many times do we wait on failure
outbox.buckets=3

# how many seconds between failures to try again
outbox.delays=1,3,5

# once process starts working do we try to send stale messages
outbox.retry=true
 */

@Slf4j
@Component
public class Settings {

    private static final Logger logger = LoggerFactory.getLogger(Settings.class);

    public final Integer buckets;
    public final Integer[] delays;
    public final Integer failureRate;

    public Settings( @Value("${outbox.delays}") String delaysString,
                     @Value("${outbox.buckets}") String bucketsString,
                     @Value("${outbox.send_failure_rate}") String sfailureRate) {

        this.buckets = Integer.parseInt(bucketsString);
        if (logger.isDebugEnabled())
            logger.debug(MessageFormat.format("Setting buckets :{0}", buckets));

        String[] delaysArray = delaysString.split(",");
        this.delays = Arrays.stream(delaysArray)
                .map(Integer::parseInt)
                .toArray(Integer[]::new);
        assert(buckets == delays.length);
        if (logger.isDebugEnabled()) {
            for (Integer delay : delays) {
                logger.debug(MessageFormat.format("Setting delay :{0}", delay));
            }
        }

        this.failureRate = Integer.parseInt(sfailureRate);
        if (logger.isDebugEnabled())
            logger.debug(MessageFormat.format("Setting failureRate :{0}", failureRate));
    }
}

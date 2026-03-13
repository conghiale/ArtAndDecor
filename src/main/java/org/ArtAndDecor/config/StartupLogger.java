package org.ArtAndDecor.config;

/**
 * Project: ArtAndDecor
 * Date: 2026/03/13
 * Time: 3:37 PM
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @ 2026. All rights reserved
 */

@Component
public class StartupLogger {

    private static final Logger logger = LoggerFactory.getLogger(StartupLogger.class);

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        System.out.println("===== ART AND DECOR APPLICATION STARTED SUCCESSFULLY =====");
        logger.info("===== ART AND DECOR APPLICATION STARTED SUCCESSFULLY =====");
    }
}

package org.ArtAndDecor.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Cart Management
 * Provides endpoints for shopping cart operations including carts, cart states, cart items, and cart item states
 */
@RestController
public class HomeController {

    private static final Logger logger = LogManager.getLogger(HomeController.class);

    @GetMapping("/")
    public String home() {
        logger.debug("Accessing home endpoint");
        return "API is running";
    }
}
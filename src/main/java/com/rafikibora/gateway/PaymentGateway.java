package com.rafikibora.gateway;

import org.jpos.q2.Q2;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PaymentGateway {
    private static final Logger logger = Logger.getLogger(PaymentGateway.class.getName());
    public static void main(String[] args) {
        Q2 q2Server = new Q2();
        q2Server.start();
        logger.info("Gateway successfully started");
    }
}

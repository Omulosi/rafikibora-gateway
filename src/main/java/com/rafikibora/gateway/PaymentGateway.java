package com.rafikibora.gateway;

import org.jpos.q2.Q2;

public class PaymentGateway {
    public static void main(String[] args) {
        System.out.println("Hello Jpos");

        Q2 q2Server = new Q2();
        q2Server.start();
    }
}

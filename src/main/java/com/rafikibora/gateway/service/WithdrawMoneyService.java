package com.rafikibora.gateway.service;


import com.rafikibora.gateway.iso.RequestListener;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOSource;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class WithdrawMoneyService {
    public boolean processTransaction(ISOSource sender, ISOMsg isoMsg) {
        try {
            System.out.printf("Withdraw transaction");
        } catch(Exception ex) {
            Logger.getLogger(RequestListener.class.getName()).log(Level.SEVERE, null, ex);
        }

        return  true;

    }

}

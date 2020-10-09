package com.rafikibora.gateway.service;

import com.rafikibora.gateway.iso.RequestListener;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOSource;
import org.springframework.stereotype.Service;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service(value="depositService")
public class DepositService {
    public boolean processTransaction(ISOSource sender, ISOMsg isoMsg) {

      try {
          System.out.printf("Deposit transaction");
      } catch (Exception ex) {
          Logger.getLogger(DepositService.class.getName()).log(Level.SEVERE, null, ex);
      }
      return true;
    }
}

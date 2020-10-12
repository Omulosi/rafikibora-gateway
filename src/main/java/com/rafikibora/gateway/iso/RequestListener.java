package com.rafikibora.gateway.iso;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISORequestListener;
import org.jpos.iso.ISOSource;

public class RequestListener implements ISORequestListener {

    private final TransactionProcessorImpl transactionProcessor = new TransactionProcessorImpl();

    public boolean process(ISOSource sender, ISOMsg request) {
        try {
            String mti = request.getMTI();

            if("0200".equals(mti)){
                String processingCode = request.getString(3);
                processingCode = processingCode.substring(0, 2);

                switch (processingCode) {
                    // Deposit TTC (21)
                    case "21":
                        ISOMsg respnseISOMsg = transactionProcessor.processDeposit(request);
                        sender.send(respnseISOMsg);
                        break;
                    // Purchase TTC
                    case "00":
                        ISOMsg SalerespnseISOMsg = transactionProcessor.processSale(request);
                        sender.send(SalerespnseISOMsg);
                        break;
                    // Withdrawal TTC (01)
                    case "01":

                        break;
                    // Send money TTC (40)
                    case "34":
                        ISOMsg responseISOMsg = transactionProcessor.processSendMoney(request);
                        sender.send(responseISOMsg);
                       return true;
                    default:
                        return false;
                }

                return false;
            }
            return false;
        } catch (Exception ex) {
            Logger.getLogger(RequestListener.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

}
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
            if ("0800".equals(mti)) {
                ISOMsg response = (ISOMsg) request.clone();
                response.setMTI("0810");
                response.set(39, "00");
                sender.send(response);
                return true;
            }

            if("0200".equals(mti)){
                String processingCode = request.getString(3);
                processingCode = processingCode.substring(0, 2);

                switch (processingCode) {
                    // Deposit TTC
                    case "21":
                        break;
                    // Purchase TTC
                    case "00":
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
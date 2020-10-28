package com.rafikibora.gateway.iso;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jpos.iso.*;

/**
 * This class is a Request Listener that processes the incoming
 * ISO Messages
 */
public class RequestListener implements ISORequestListener {

    private final TransactionProcessorImpl transactionProcessor = new TransactionProcessorImpl();
    private final AuthProcessor authProcessor = new AuthProcessorImpl();

    /**
     * Processes incoming iso message
     *
     * @param sender iso message source
     * @param request iso message to process
     * @return boolean after processing
     */
    public boolean process(ISOSource sender, ISOMsg request) {

        try {

            ISOMsg isoMsg = (ISOMsg) request.clone();
            String mti = isoMsg.getMTI();
            ISOMsg responseISOMsg;

            /** Login request */
            if ("0800".equals(mti)) {
                responseISOMsg = authProcessor.login(isoMsg);
                sender.send(responseISOMsg);
                return true;
            }

            /** Financial Transactions */
            if ("0200".equals(mti)) {
                String processingCode = isoMsg.getString(3);
                processingCode = processingCode.substring(0, 2);

                switch (processingCode) {
                    // Deposit TTC (21)
                    case "21":
                        responseISOMsg = transactionProcessor.processDeposit(isoMsg);
                        sender.send(responseISOMsg);
                        break;
                    // Purchase/Sale TTC (00)
                    case "00":
                        responseISOMsg = transactionProcessor.processSale(isoMsg);
                        sender.send(responseISOMsg);
                        break;
                    // Withdrawal TTC (01)
                    case "01":
                        responseISOMsg = transactionProcessor.processReceiveMoney(isoMsg);
                        sender.send(responseISOMsg);
                        break;
                    // Send money TTC (26)
                    case "26":
                        responseISOMsg = transactionProcessor.processSendMoney(isoMsg);
                        sender.send(responseISOMsg);
                        break;
                    default:
                        isoMsg.setResponseMTI();
                        isoMsg.set(39, "06");
                        sender.send(isoMsg);
                }
            }
        } catch (ISOException | IOException | NullPointerException ex) {
            System.out.println("ERROR MESSAGE.... " + ex.getMessage());
            Logger.getLogger(RequestListener.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
        return true;
    }
}
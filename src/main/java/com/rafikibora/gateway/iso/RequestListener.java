package com.rafikibora.gateway.iso;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISORequestListener;
import org.jpos.iso.ISOSource;

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

        boolean returnVal = false;

        try {
            String mti = request.getMTI();

            if ("0800".equals(mti)) {
                ISOMsg responseISOMsg = authProcessor.login(request);
                sender.send(responseISOMsg);
                return true;
            }

            if ("0200".equals(mti)) {
                String processingCode = request.getString(3);
                processingCode = processingCode.substring(0, 2);

                switch (processingCode) {
                    // Deposit TTC (21)
                    case "21":
                        ISOMsg respnseISOMsg = transactionProcessor.processDeposit(request);
                        sender.send(respnseISOMsg);
                        returnVal = true;
                        break;
                    // Purchase/Sale TTC (00)
                    case "00":
                        ISOMsg SalerespnseISOMsg = transactionProcessor.processSale(request);
                        sender.send(SalerespnseISOMsg);
                        returnVal = true;
                        break;
                    // Withdrawal TTC (01)
                    case "01":
                        ISOMsg withdrawIsoMsg = transactionProcessor.processReceiveMoney(request);
                        sender.send(withdrawIsoMsg);
                        returnVal = true;
                        break;
                    // Send money TTC (26)
                    case "26":
                        ISOMsg responseISOMsg = transactionProcessor.processSendMoney(request);
                        sender.send(responseISOMsg);
                        returnVal = true;
                        break;
                    default:
                        returnVal = false;
                }
            }
        } catch (ISOException | IOException | NullPointerException ex) {
            System.out.println("ERROR MESSAGE.... " + ex.getMessage());
            Logger.getLogger(RequestListener.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
        return returnVal;
    }
}
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

        try {
            ISOMsg isoMsg = (ISOMsg) request.clone();
            String mti = isoMsg.getMTI();

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
                        sender.send(transactionProcessor.processDeposit(request));
                        break;
                    // Purchase/Sale TTC (00)
                    case "00":
                        sender.send(transactionProcessor.processSale(request));
                        break;
                    // Withdrawal TTC (01)
                    case "01":
                        sender.send(transactionProcessor.processReceiveMoney(request));
                        break;
                    // Send money TTC (26)
                    case "26":
                        sender.send(transactionProcessor.processSendMoney(request));
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
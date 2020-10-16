package com.rafikibora.gateway.iso;

import java.io.IOException;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISORequestListener;
import org.jpos.iso.ISOSource;

public class RequestListener implements ISORequestListener {

    private final TransactionProcessorImpl transactionProcessor = new TransactionProcessorImpl();

    /**
     * Process incoming iso message
     *
     * @param sender iso message source
     * @param request iso message to process
     * @return boolean after processing
     */
    public boolean process(ISOSource sender, ISOMsg request) {
        System.out.println("**********REQUEST RECEIVED**********");
        request.dump(System.out, " ");
        System.out.println("**********REQUEST RECEIVED**********");

        try {
            String mti = request.getMTI();

            if ("0200".equals(mti)) {
                String processingCode = request.getString(3);
                processingCode = processingCode.substring(0, 2);

                switch (processingCode) {
                    // Deposit TTC (21)
                    case "21":
                        ISOMsg respnseISOMsg = transactionProcessor.processDeposit(request);
                        sender.send(respnseISOMsg);
                        return true;
                    // Purchase TTC
                    case "00":
                        ISOMsg SalerespnseISOMsg = transactionProcessor.processSale(request);
                        sender.send(SalerespnseISOMsg);
                        return true;
                    // Withdrawal TTC (01)
                    case "26":
                        ISOMsg withdrawIsoMsg = transactionProcessor.processReceiveMoney(request);
                        sender.send(withdrawIsoMsg);
                        return true;
                    // Send money TTC (40)
                    case "34":
                        ISOMsg responseISOMsg = transactionProcessor.processSendMoney(request);
                        sender.send(responseISOMsg);
                        return true;
                    default:
                        return false;
                }
            }
            return false;
        } catch (ISOException | IOException | NullPointerException ex) {
            System.out.println("ERROR MESSAGE.... "+ex.getMessage());
        }
        return false;
    }
}
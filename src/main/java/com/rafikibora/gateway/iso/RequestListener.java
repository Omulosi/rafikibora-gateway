package com.rafikibora.gateway.iso;

import java.io.IOException;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISORequestListener;
import org.jpos.iso.ISOSource;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class RequestListener implements ISORequestListener {

    private final TransactionProcessorImpl transactionProcessor = new TransactionProcessorImpl();

    /**
     * Processes incoming iso message
     *
     * @param sender iso message source
     * @param request iso message to process
     * @return boolean after processing
     */
    public boolean process(ISOSource sender, ISOMsg request) {
//        System.out.println("**********REQUEST RECEIVED**********");
//        request.dump(System.out, " ");
//        System.out.println("**********REQUEST RECEIVED**********");

        boolean returnVal = false;

        try {
            String mti = request.getMTI();

            // Network management: Echo rquest
            if ("0800".equals(mti)) {
                ISOMsg response = (ISOMsg) request.clone();
                response.setMTI("0810");
                response.set(39, "00");
                sender.send(response);
                returnVal = true;
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
                    // Purchase TTC
                    case "00":
                        ISOMsg SalerespnseISOMsg = transactionProcessor.processSale(request);
                        sender.send(SalerespnseISOMsg);
                        returnVal = true;
                        break;
                    // Withdrawal TTC (01)
                    case "26":
                        ISOMsg withdrawIsoMsg = transactionProcessor.processWithdraw(request);
                        sender.send(withdrawIsoMsg);
                        returnVal = true;
                        break;
                    // Send money TTC (40)
                    case "34":
                        ISOMsg responseISOMsg = transactionProcessor.processSendMoney(request);
                        sender.send(responseISOMsg);
                        returnVal = true;
                       break;
                    default:
                        returnVal = false;
                }
                return false;
            }
        } catch (ISOException | IOException | NullPointerException ex) {
            LoggerFactory.getLogger(RequestListener.class).error("Unable to process iso message: "+ ex.getMessage());
            ex.printStackTrace();
        }
        return returnVal;
    }
}
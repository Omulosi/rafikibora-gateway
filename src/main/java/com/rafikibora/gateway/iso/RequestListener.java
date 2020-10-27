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

            String mti = request.getMTI();
            ISOMsg responseISOMsg;

            if ("0800".equals(mti)) {
                responseISOMsg = authProcessor.login(request);

                System.out.println("++++++++++++ ISO MSG TO SEND TO POS++++++++++++");
                System.out.println("Response Code"+responseISOMsg.getString(39));
                System.out.println("Auth Token: "+responseISOMsg.getString(72));
                System.out.println("++++++++++++ ISO MSG TO SEND TO POS ++++++++++++");

                sender.send(responseISOMsg);
                return true;
            }

            if ("0200".equals(mti)) {
                String processingCode = request.getString(3);
                processingCode = processingCode.substring(0, 2);

                switch (processingCode) {
                    // Deposit TTC (21)
                    case "21":
                        responseISOMsg = transactionProcessor.processDeposit(request);

                        System.out.println("++++++++++++ ISO MSG TO SEND TO POS++++++++++++");
                        System.out.println("===Packed ISO Message===\n "+ISOUtil.hexdump(responseISOMsg.pack()));
                        System.out.println("======Resp Code==== "+request.getString(39));
                        System.out.println("++++++++++++ ISO MSG TO SEND TO POS++++++++++++");

                        sender.send(responseISOMsg);
                        break;
                    // Purchase/Sale TTC (00)
                    case "00":
                        responseISOMsg = transactionProcessor.processSale(request);

                        System.out.println("++++++++++++ ISO MSG TO SEND TO POS++++++++++++");
                        System.out.println("===Packed ISO Message===\n "+ISOUtil.hexdump(responseISOMsg.pack()));
                        System.out.println("======Resp Code==== "+request.getString(39));
                        System.out.println("++++++++++++ ISO MSG TO SEND TO POS++++++++++++");

                        sender.send(responseISOMsg);
                        break;
                    // Withdrawal TTC (01)
                    case "01":
                        responseISOMsg = transactionProcessor.processReceiveMoney(request);

                        System.out.println("++++++++++++ ISO MSG TO SEND TO POS++++++++++++");
                        System.out.println("===Packed ISO Message===\n "+ISOUtil.hexdump(responseISOMsg.pack()));
                        System.out.println("======Amount ===="+ responseISOMsg.getString(4));
                        System.out.println("======Resp Code==== "+responseISOMsg.getString(39));
                        System.out.println("++++++++++++ ISO MSG TO SEND TO POS++++++++++++");

                        sender.send(responseISOMsg);
                        break;
                    // Send money TTC (26)
                    case "26":
                        responseISOMsg = transactionProcessor.processSendMoney(request);
                        sender.send(responseISOMsg);
                        break;
                    default:
                        request.setResponseMTI();
                        request.set(39, "06");
                        sender.send(request);
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
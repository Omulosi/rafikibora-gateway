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
            ISOMsg newIso = new ISOMsg();

            if ("0800".equals(mti)) {
               // ISOMsg responseISOMsg = authProcessor.login(request);
                ISOMsg responseISOMsg=new ISOMsg();
                responseISOMsg=request;
                responseISOMsg.set(39,"00");
                //responseISOMsg.set(48,"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ3YW5nZWNpMUBtYWlsLmNvbSIsInJvbGVzIjpbXSwiaWF0IjoxNjAzMTE5NTk0LCJleHAiOjE2MDM0Nzk1OTR9.MhA_ri8xL-39ANsCNBL6rxVNryTr3ZD_Fboy4B_2GBpIZMHSLfRXekuF4Eve7I_MY3tODeR7cYQd9K2h4TdRCA");
                System.out.println(">>>>>>>++++"+responseISOMsg.getString(48));
                System.out.println(">>>>>>>+39+"+responseISOMsg.getString(39));
                sender.send(responseISOMsg);
                return true;
            }

            if ("0200".equals(mti)) {
                String processingCode = request.getString(3);
                processingCode = processingCode.substring(0, 2);

                switch (processingCode) {
                    // Deposit TTC (21)
                    case "21":
                        request=transactionProcessor.processDeposit(request);
                        //sender.send(transactionProcessor.processDeposit(request));
                        sender.send(request);
                        System.out.println("===Packed ISO Message===\n "+ISOUtil.hexdump(request.pack()));
                        System.out.println("======Resp Code==== "+request.getString(39));
                        break;
                    // Purchase/Sale TTC (00)
                    case "00":
                        newIso = transactionProcessor.processSale(request);
                        sender.send(newIso);
                        System.out.println("===Packed ISO Message===\n "+ISOUtil.hexdump(request.pack()));
                        System.out.println("======Resp Code "+request.getString(39));
                        break;
                    // Withdrawal TTC (01)
                    case "01":

                        newIso = transactionProcessor.processReceiveMoney(request);
                        System.out.println("**** response code ****");
                        System.out.println(newIso.getString(39));
                        System.out.println("**** response code ****");

                        sender.send(newIso);
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
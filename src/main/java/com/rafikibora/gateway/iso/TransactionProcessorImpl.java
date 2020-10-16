package com.rafikibora.gateway.iso;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class includes methods for processing financial transactions.
 * A financial transaction can be one of Send Money, Sale, Deposit Money or Withdraw money.
 */
public class TransactionProcessorImpl implements TransactionProcessor {
    private final HttpClient httpClient = new HttpClient();

    /**
     * Processes the send money transaction.
     *
     * @param request An ISOMsg with transaction data.
     * @return IsoMsg An ISoMsg with appropriate response fields set.
     */
    @Override
    public ISOMsg processSendMoney(ISOMsg request) {

        String SEND_MONEY_ENDPOINT = "http://41.215.130.247:10203/api/transactions/send_money";
        ISOMsg response = null;

        try {
            String pan = request.getString(2);
            String amount = request.getString(4);
            String transmissionDateTime = request.getString(7); //YYMMDDhhmmss
            String terminalID = request.getString(41); // Terminal ID
//          String merchantID = request.getString(42); // Merchant ID
            String email = request.getString(47); // Currency Code
            String currencyCode = request.getString(49); // Currency Code


            // Assemble data to send to backend
            Map<String, Object> transactionData = new HashMap<>();
            transactionData.put("pan", pan);
            transactionData.put("amountTransaction", amount);
            transactionData.put("recipientEmail", email);
            transactionData.put("TID", terminalID);
            transactionData.put("dateTimeTransmission", transmissionDateTime);
//          transactionData.put("MID", merchantID);
            transactionData.put("currencyCode", currencyCode);

            // Get response
            Map<String, Object> postResponse = httpClient.post(SEND_MONEY_ENDPOINT, transactionData);
            System.out.println("====================================" + postResponse);
            response = (ISOMsg) request.clone();
            if (postResponse.get("OK")) {
                response.setMTI("0210");
            }




            response.set(39, postResponse.get("code").toString()); // response code
            response.set(104, postResponse.get("message").toString()); // Transaction description
            response.set(7, postResponse.get("timestamp").toString()); // Transmission date and time

        } catch (Exception ex) {
            Logger.getLogger(RequestListener.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return response;
    }

    @Override
    public ISOMsg processDeposit(ISOMsg request) {
        /**01560200F238048000C18000000000000600000051XXXXXXXXXX3147
         * 2100000000000001001011151500000001151500101101000000100011234567
         * 8912345600800312381007143850560714385056**/
//         communicate with backend
        String DEPOSIT_MONEY_ENDPOINT = "http://41.215.130.247:2019/api/auth/deposit/";

        ISOMsg response = null;
        try {
            //Extracting Iso fields
            String merchantPan = (String) request.getValue(2);
            String processingCode = (String) request.getValue(3);
            String amountTransaction = (String) request.getValue(4);
            String dateTimeTransmission = (String) request.getValue(7);
            String terminal = (String) request.getValue(41);//Terminal ID
            String merchant = (String) request.getValue(42);//Merchant ID
            String customerPan = (String) request.getValue(47);
            String amountTransactionCurrencyCode = (String) request.getValue(49);// Currency Code



            // Get iso data and send to backend
            Map<String, Object> depositData= new HashMap<>();
            depositData.put("merchantPan", merchantPan);
            depositData.put("processingCode", processingCode);
            depositData.put("amountTransaction", amountTransaction);
            depositData.put("dateTimeTransmission", dateTimeTransmission);
            depositData.put("tid", terminal);
            depositData.put("mid", merchant);
            depositData.put("customerPan", customerPan);
            depositData.put("amountTransactionCurrencyCode", amountTransactionCurrencyCode);


            // send the request to backend
            Map<String, Object> postResponse = httpClient.post(DEPOSIT_MONEY_ENDPOINT, depositData);


            // communicate with backend
            Map<String, Object> data = httpClient.get("http://41.215.130.247:2019/api/auth/deposit/");


            // build response iso
            response = (ISOMsg) request.clone();
            response.setMTI("0210");
            response.set(104, " " + "Successful");

        } catch (Exception ex) {
            Logger.getLogger(RequestListener.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }

        return response;



    }

    /**
     * Process receive money transaction
     * @param isoMsg FROM pos
     * @return isoMsg
     */
    @Override
    public ISOMsg processReceiveMoney(ISOMsg isoMsg) {
//        String  RECEIVE_ENDPOINT = "http://192.168.254.190:2019/api/auth/receive_money";
        String RECEIVE_ENDPOINT = "http://41.215.130.247:2019/api/auth/receive_money/";
        ISOMsg isoMsgResponse = (ISOMsg) isoMsg.clone();

        HashMap<String, String> isoMsgToSend = new HashMap<>();

        isoMsgToSend.put("pan", isoMsg.getString(2));
        isoMsgToSend.put("processingCode", isoMsg.getString(3));
        isoMsgToSend.put("transmissionDateTime", isoMsg.getString(7));
        isoMsgToSend.put("tid", isoMsg.getString(41));
        isoMsgToSend.put("mid", isoMsg.getString(42));
        isoMsgToSend.put("receiveMoneyToken", isoMsg.getString(47));
        isoMsgToSend.put("txnCurrencyCode", isoMsg.getString(49));

        Map<String, String> response = httpClient.post(RECEIVE_ENDPOINT,isoMsgToSend);

        System.out.println("*************** Response from web portal *********************");
        System.out.println("response code: "+response.get("message"));
        System.out.println("txn amount: "+response.get("txnAmount"));
        System.out.println("*************** Response from web portal *********************");

        try {
            isoMsgResponse.setMTI("0210");
            isoMsgResponse.set(39, response.get("message"));
        } catch (ISOException e) {
            System.out.println("ERROR MESSAGE: "+e.getMessage());
            e.printStackTrace();
        }
        return isoMsgResponse;
    }

    @Override
    public ISOMsg processSale(ISOMsg request) {
        // communicate with backend
        String SALE_MONEY_ENDPOINT = "http://41.215.130.247:2019/api/auth/sale/";

        ISOMsg response = null;
        try {
            //Extracting Iso fields
            String primaryAccNo = (String) request.getValue(2);
            String processingCode = (String) request.getValue(3);
            String amountTransaction = (String) request.getValue(4);
            String transmissionDateTime = (String) request.getValue(7);
            String terminal = (String) request.getValue(41);
            String merchant = (String) request.getValue(42);
            String currencyCode = (String) request.getValue(49);

            // Get iso data and send to backend
            Map<String, Object> saleData= new HashMap<>();
            saleData.put("pan", primaryAccNo);
            saleData.put("processingCode", processingCode);
            saleData.put("amountTransaction", amountTransaction);
            saleData.put("transmissionDateTime", transmissionDateTime);
            saleData.put("tid", terminal);
            saleData.put("mid", merchant);
            saleData.put("currencyCode", currencyCode);


            // send the request to backend
            Map<String, Object> postResponse = httpClient.post(SALE_MONEY_ENDPOINT, saleData);


            // communicate with backend
           Map<String, Object> data = httpClient.get("http://41.215.130.247:2019/api/auth/sale/");


            // build response iso
            response = (ISOMsg) request.clone();
            response.setMTI("0210");
            response.set(104, " " + " Successful");

        } catch (Exception ex) {
            Logger.getLogger(RequestListener.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }

        return response;
    }

}


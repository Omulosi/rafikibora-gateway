package com.rafikibora.gateway.iso;

import org.jpos.iso.ISOMsg;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TransactionProcessorImpl implements TransactionProcessor {
    private final HttpClient httpClient = new HttpClient();

    @Override
    public ISOMsg processSendMoney(ISOMsg request) {
        String SEND_MONEY_ENDPOINT = "";

        String name = "nama";
        String salary = "saldo";
        ISOMsg response = null;
        try {

            String amount = request.getString(4);
            String pan = request.getString(2);
            String transmissionDateTime = request.getString(7); //MMDDhhmmss 19 july,09:52:41
//            String transmissionDateTimeLocal = request.getString(12); //YYMMDDhhmmss
            String transmissionTimeLocal = request.getString(12); //YYMMDDhhmmss 08:53:36?
            String transmissionDateLocal = request.getString(13); //YYMMDDhhmmss 19 july
            String terminalID = request.getString(41); // Terminal ID
            String merchantID = request.getString(42); // Merchant ID
            String currencyCode = request.getString(49); // Currency Code
            String senderAccount = request.getString(102); // Account Identification 1
            String receiverAccount = request.getString(103); // Account Identification 2

            // Assemble data to send to backend
            Map<String, Object> transactionData= new HashMap<>();
            transactionData.put("amount", amount);
            transactionData.put(transmissionDateTime, transmissionDateTime);
//            transactionData.put(transmissionDateTimeLocal, transmissionDateTimeLocal);
            transactionData.put("terminalID", terminalID);
            transactionData.put("merchantID", merchantID);
            transactionData.put("currencyCode", currencyCode);
            transactionData.put("senderAccount", senderAccount);
            transactionData.put("receiverAccount", receiverAccount);

            // Get response
            Map<String, Object> postResponse = httpClient.post(SEND_MONEY_ENDPOINT, transactionData);

            response = (ISOMsg) request.clone();
            response.setMTI("0210");


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
        // communicate with backend
        String DEPOSIT_MONEY_ENDPOINT = "http://localhost:2019/deposit/";

        ISOMsg response = null;
        try {
            //Extracting Iso fields
            String amount = (String) request.getValue(4);
            String Tid =  (String) request.getValue(41);
            String CurrencyCode = (String) request.getValue(49);
            String localTime = (String) request.getValue(12);
            String localDate = (String) request.getValue(13);
            String timeAndDate = localTime + '-' + localDate;
            String destAccount = (String) request.getValue(103);

            // Get iso data and send to backend
            Map<String, Object> depositData= new HashMap<>();
            depositData.put("amount", amount);
            depositData.put("Tid", Tid);
            depositData.put("CurrencyCode", CurrencyCode);
            depositData.put("timeAndDate", timeAndDate);
            depositData.put("destAccount", destAccount);


            // send the request to backend
            Map<String, Object> postResponse = httpClient.post(DEPOSIT_MONEY_ENDPOINT, depositData);


            // communicate with backend
            Map<String, Object> data = httpClient.get("http://localhost:2019/deposit/");


            // build response iso
            response = (ISOMsg) request.clone();
            response.setMTI("0210");
            response.set(104, "Deposit Query");

        } catch (Exception ex) {
            Logger.getLogger(RequestListener.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }

        return response;
    }

    @Override
    public ISOMsg processWithdraw(ISOMsg request) {
        return null;
    }

    @Override
    public ISOMsg processSale(ISOMsg request) {
        return null;
    }
}

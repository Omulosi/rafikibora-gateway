package com.rafikibora.gateway.iso;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOField;
import org.jpos.iso.ISOMsg;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TransactionProcessorImpl implements TransactionProcessor {
    private final HttpClient httpClient = new HttpClient();

    @Override
    public ISOMsg processSendMoney(ISOMsg request) {
        String SEND_MONEY_ENDPOINT = "http://localhost:2019/api/send_money";
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
            Map<String, Object> transactionData = new HashMap<>();
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
        /**01560200F238048000C18000000000000600000051XXXXXXXXXX3147
         * 2100000000000001001011151500000001151500101101000000100011234567
         * 8912345600800312381007143850560714385056**/
        // communicate with backend
        String DEPOSIT_MONEY_ENDPOINT = "http://localhost:2090/api/deposit/";

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
            Map<String, Object> data = httpClient.get("http://localhost:2090/api/deposit/");


            // build response iso
            response = (ISOMsg) request.clone();
            response.setMTI("0210");
            response.set(104, " " + "Successful");

        } catch (Exception ex) {
            Logger.getLogger(RequestListener.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }

        return response;

    }

    @Override
    public ISOMsg processWithdraw(ISOMsg isoMsg) {

        ISOMsg isoMsgResponse = (ISOMsg) isoMsg.clone();

        HashMap<String, String> isoMsgToSend = new HashMap<String, String>();
//        isoMsgToSend.put("pan", isoMsg.getString(2));
//        isoMsgToSend.put("pcode", isoMsg.getString(3));
//        isoMsgToSend.put("txnAmount", isoMsg.getString(4));
//        isoMsgToSend.put("transmissionDateTime", isoMsg.getString(7));
//        isoMsgToSend.put("stan", isoMsg.getString(11));
//        isoMsgToSend.put("txnLocalTime", isoMsg.getString(12));
//        isoMsgToSend.put("txnLocalDate", isoMsg.getString(13));
//        isoMsgToSend.put("posEntryMode", isoMsg.getString(22));
////        isoMsgToSend.put("functionCode", isoMsg.getString(24));
//        isoMsgToSend.put("posConditionCode", isoMsg.getString(25));
//        isoMsgToSend.put("tid", isoMsg.getString(41));
//        isoMsgToSend.put("mid", isoMsg.getString(42));
//        isoMsgToSend.put("receiveMoneyToken", isoMsg.getString(48));
//        isoMsgToSend.put("txnCurrencyCode", isoMsg.getString(49));
//        isoMsgToSend.put("srcAccount", isoMsg.getString(102));
//        isoMsgToSend.put("destAccount", isoMsg.getString(103));
        isoMsgToSend.put("password", this.processStringWithDelimiter(isoMsg.getString(120), '?'));
        isoMsgToSend.put("email", this.processStringWithDelimiter(isoMsg.getString(121), '?'));
//        isoMsgToSend.put("agentAuthToken", this.processStringWithDelimiter(isoMsg.getString(122), '?'));

//        Map<String, String> response = httpClient.post("http://localhost:2019/api/test_withdrawal",isoMsgToSend);
//        Map<String, String> response = httpClient.post("http://192.168.254.189:8080/api/auth/login",isoMsgToSend);
        Map<String, String> response = httpClient.get("http://192.168.254.189:8080/profile");

        System.out.println("************************************");
        System.out.println(response.get("message"));
//        System.out.println(response.get("email"));
//        System.out.println(response.get("authToken"));
        System.out.println("************************************");

        try {
            isoMsgResponse.setMTI("0210");

//            if(response.get("msg").equals("successful"))
//                isoMsgResponse.set(39, "00");
//
//            if(response.get("msg").equals("insufficient funds"))
//                isoMsgResponse.set(39, "16");

        } catch (ISOException e) {
            e.printStackTrace();
        } finally {
            return isoMsgResponse;
        }
    }

    @Override
    public ISOMsg processSale(ISOMsg request) {
        /**01560200F238048000C18000000000000600000051XXXXXXXXXX3147
         * 2100000000000001001011151500000001151500101101000000100011234567
         * 8912345600800312381007143850560714385056**/
        // communicate with backend
        String SALE_MONEY_ENDPOINT = "http://localhost:2090/api/deposit/";

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
           Map<String, Object> data = httpClient.get("http://localhost:2090/api/deposit/");


            // build response iso
            response = (ISOMsg) request.clone();
            response.setMTI("0210");
            response.set(104, " " + " Successful");

        } catch (Exception ex) {
            Logger.getLogger(RequestListener.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }

        return response;
    }


    /**
     * Read string up to first occurrence of a delimiter
     * @param source
     * @param delimiter
     * @return
     */
    private String processStringWithDelimiter(String source, char delimiter){
        char[] src = source.toCharArray();
        String dest = "";
        for(char ch : src){
            if(ch == delimiter) break;
            dest = dest + ch;
        }
        return dest;
    }

}


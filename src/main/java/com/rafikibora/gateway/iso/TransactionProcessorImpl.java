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
        /**01560200F238048000C18000000000000600000051XXXXXXXXXX3147
         * 2100000000000001001011151500000001151500101101000000100011234567
         * 8912345600800312381007143850560714385056**/
        // communicate with backend
        String DEPOSIT_MONEY_ENDPOINT = "http://localhost:2019/deposit/";

        ISOMsg response = null;
        try {
            //Extracting Iso fields
            String secondBitmap = (String) request.getValue(1);
            String processingCode = (String) request.getValue(3);
            String amount = (String) request.getValue(4);
            String transmissionDateTime = (String) request.getValue(7);
            String stan = (String) request.getValue(11);
            String localTime = (String) request.getValue(12);
            String localDate = (String) request.getValue(13);
            String localTimeDate = localTime + '-' + localDate;
            String posEntryMode = (String) request.getValue(22);
            String functionCode = (String) request.getValue(24);
            String posConditionCode = (String) request.getValue(25);
            String track2Data = (String) request.getValue(35);
            String TerminalID = (String) request.getValue(41);
            String cardAcceptorCode = (String) request.getValue(42);
            String additionalData = (String) request.getValue(48);
            String CurrencyCode = (String) request.getValue(49);
            String personalIdentificationNoData = (String) request.getValue(52);
            String iccData = (String) request.getValue(55);
            String messageAuthCode = (String) request.getValue(64);
            String accountOne = (String) request.getValue(102);
            String destAccount = (String) request.getValue(103);

            String password = (String) request.getValue(120);
            String username = (String) request.getValue(121);
            String authToken = (String) request.getValue(122);

            String secondaryMacData = (String) request.getValue(128);

            // Get iso data and send to backend
            Map<String, Object> depositData= new HashMap<>();
            depositData.put("secondBitmap", secondBitmap);
            depositData.put("processingCode", processingCode);
            depositData.put("amount", amount);
            depositData.put("transmissionDateTime", transmissionDateTime);
            depositData.put("stan", stan);
            depositData.put("localTimeDate", localTimeDate);
            depositData.put("posEntryMode", posEntryMode);
            depositData.put("functionCode", functionCode);
            depositData.put("posConditionCode", posConditionCode);
            depositData.put("track2Data", track2Data);
            depositData.put("terminalID", TerminalID);
            depositData.put("cardAcceptorCode", cardAcceptorCode);
            depositData.put("additionalData", additionalData);
            depositData.put("currencyCode", CurrencyCode);
            depositData.put("personalIdentificationNoData", personalIdentificationNoData);
            depositData.put("iccData", iccData);
            depositData.put("messageAuthCode", messageAuthCode);
            depositData.put("accountOne", accountOne);
            depositData.put("destAccount", destAccount);

            depositData.put("password", password);
            depositData.put("username", username);
            depositData.put("authToken", authToken);

            depositData.put("secondaryMacData", secondaryMacData);


            // send the request to backend
            Map<String, Object> postResponse = httpClient.post(DEPOSIT_MONEY_ENDPOINT, depositData);


            // communicate with backend
            Map<String, Object> data = httpClient.get("http://localhost:2019/deposit/");


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
        String SALE_MONEY_ENDPOINT = "http://localhost:2019/sale/";

        ISOMsg response = null;
        try {
            //Extracting Iso fields
            String primaryAccNo = (String) request.getValue(2);
            String processingCode = (String) request.getValue(3);
            String amount = (String) request.getValue(4);
            String transmissionDateTime = (String) request.getValue(7);
            String stan = (String) request.getValue(11);
            String localTime = (String) request.getValue(12);
            String localDate = (String) request.getValue(13);
            String localTimeDate = localTime + '-' + localDate;
            String posEntryMode = (String) request.getValue(22);
            String functionCode = (String) request.getValue(24);
            String posConditionCode = (String) request.getValue(25);
            String track2Data = (String) request.getValue(35);
            String TerminalID = (String) request.getValue(41);
            String cardAcceptorCode = (String) request.getValue(42);
            String additionalData = (String) request.getValue(48);
            String CurrencyCode = (String) request.getValue(49);
            String personalIdentificationNoData = (String) request.getValue(52);
            String additionalAmount = (String) request.getValue(54);
            String iccData = (String) request.getValue(55);
            String messageAuthCode = (String) request.getValue(64);
            String customerAccount = (String) request.getValue(102);
            String merchantAccount = (String) request.getValue(103);

            String password = (String) request.getValue(120);
            String username = (String) request.getValue(121);
            String authToken = (String) request.getValue(122);

            // Get iso data and send to backend
            Map<String, Object> saleData= new HashMap<>();
            saleData.put("primaryAccNo", primaryAccNo);
            saleData.put("processingCode", processingCode);
            saleData.put("amount", amount);
            saleData.put("transmissionDateTime", transmissionDateTime);
            saleData.put("stan", stan);
            saleData.put("localTimeDate", localTimeDate);
            saleData.put("posEntryMode", posEntryMode);
            saleData.put("functionCode", functionCode);
            saleData.put("posConditionCode", posConditionCode);
            saleData.put("track2Data", track2Data);
            saleData.put("terminalID", TerminalID);
            saleData.put("cardAcceptorCode", cardAcceptorCode);
            saleData.put("additionalData", additionalData);
            saleData.put("currencyCode", CurrencyCode);
            saleData.put("personalIdentificationNoData", personalIdentificationNoData);
            saleData.put("additionalAmount", additionalAmount);
            saleData.put("iccData", iccData);
            saleData.put("messageAuthCode", messageAuthCode);

            saleData.put("customerAccount", customerAccount);
            saleData.put("merchantAccount", merchantAccount);

            saleData.put("password", password);
            saleData.put("username", username);
            saleData.put("authToken", authToken);




            // send the request to backend
            Map<String, Object> postResponse = httpClient.post(SALE_MONEY_ENDPOINT, saleData);


            // communicate with backend
            Map<String, Object> data = httpClient.get("http://localhost:2019/sale/");


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


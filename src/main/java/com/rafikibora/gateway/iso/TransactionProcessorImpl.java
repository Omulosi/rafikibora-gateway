package com.rafikibora.gateway.iso;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

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
        String SEND_MONEY_ENDPOINT = "http://127.0.0.1:10203/api/transactions/send_money";
        ISOMsg response = (ISOMsg) request.clone();

        try {
            String token = request.getString(48);
            String pan = request.getString(2);
            String amount = request.getString(4);
            String transmissionDateTime = request.getString(7); //YYMMDDhhmmss
            String terminalID = request.getString(41); // Terminal ID
            //String merchantID = request.getString(42); // Merchant ID
            String email = request.getString(47); // Currency Code
            String currencyCode = request.getString(49); // Currency Code


            // Assemble data to send to backend
            Map<String, Object> transactionData = new HashMap<>();
            transactionData.put("pan", pan);
            transactionData.put("amountTransaction", amount);
            transactionData.put("recipientEmail", email);
            transactionData.put("TID", terminalID);
            transactionData.put("dateTime", transmissionDateTime);
            //transactionData.put("MID", merchantID);
            transactionData.put("currencyCode", currencyCode);


            // Add authentication header
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer "+token);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(transactionData, headers);

            // Get response
            RestTemplate httpClient = new RestTemplate();

            String postResponse = httpClient.postForObject(SEND_MONEY_ENDPOINT, entity, String.class);

            if ("OK".equalsIgnoreCase(postResponse.trim())) {
                response.setMTI("0210");
                // Approve transaction
                response.set(39, "00");
            } else {
                // Transaction declined: send an error
                response.set(39, "06");
            }

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
        String DEPOSIT_MONEY_ENDPOINT = "http://127.0.0.1:10203/api/deposit/";

//        ISOMsg response = null;
        ISOMsg response = (ISOMsg) request.clone();

        try {
            //Extracting Iso fields
            String merchantPan = (String) request.getValue(2);
            String processingCode = (String) request.getValue(3);
            String amountTransaction = (String) request.getValue(4);
            String dateTimeTransmission = (String) request.getValue(7);
            String Terminal = (String) request.getValue(41);//Terminal ID
            String merchant = (String) request.getValue(42);//Merchant ID
            String customerPan = (String) request.getValue(47);
            String amountTransactionCurrencyCode = (String) request.getValue(49);// Currency Code



            // Get iso data and send to backend
            Map<String, Object> depositData= new HashMap<>();
            depositData.put("merchantPan", merchantPan);
            depositData.put("processingCode", processingCode);
            depositData.put("amountTransaction", amountTransaction);
            depositData.put("dateTimeTransmission", dateTimeTransmission);
            depositData.put("terminal", Terminal);
            depositData.put("merchant", merchant);
            depositData.put("customerPan", customerPan);
            depositData.put("currencyCode", amountTransactionCurrencyCode);


            // send the request to backend
        //    Map<String, Object> postResponse = httpClient.post(DEPOSIT_MONEY_ENDPOINT, depositData);



            // communicate with backend
//            Map<String, Object> data = httpClient.get("http://41.215.130.247:2019/api/deposit/");


            // build response iso
//            response.setMTI("0210");
//            response.set(39, "00");
            // Get response
            RestTemplate httpClient = new RestTemplate();
            String postResponse = httpClient.postForObject(DEPOSIT_MONEY_ENDPOINT, depositData, String.class);
            System.out.println("====================================" + postResponse);

            if ("OK".equalsIgnoreCase(postResponse.trim())) {
                response.setMTI("0210");
                // Approve transaction
                response.set(39, "00");
            } else {
                // Transaction declined: send an error
                response.set(39, "06");
            }


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
        final String RECEIVE_MONEY_ENDPOINT = "http://127.0.0.1:10203/api/receive_money";

        ISOMsg isoMsgResponse = (ISOMsg) isoMsg.clone();

        HashMap<String, String> isoMsgToSend = new HashMap<>();

        isoMsgToSend.put("pan", isoMsg.getString(2));
        isoMsgToSend.put("processingCode", isoMsg.getString(3));
        isoMsgToSend.put("transmissionDateTime", isoMsg.getString(7));
        isoMsgToSend.put("tid", isoMsg.getString(41));
        isoMsgToSend.put("mid", isoMsg.getString(42));
        isoMsgToSend.put("receiveMoneyToken", isoMsg.getString(47));
        isoMsgToSend.put("currency", isoMsg.getString(49));

        // Add authentication header
        String authToken = isoMsg.getString(48);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer "+authToken);

//        Map<String, String> response = httpClient.post(RECEIVE_ENDPOINT,isoMsgToSend);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(isoMsgToSend, headers);
        Map<String, String> response = httpClient.post(RECEIVE_MONEY_ENDPOINT,entity);

        System.out.println("*************** Response from web portal *********************");
        System.out.println("========= " + response);
        System.out.println("response code: "+response.get("message"));
        System.out.println("txn amount: "+response.get("txnAmount"));
        System.out.println("*************** Response from web portal *********************");

        try {
            isoMsgResponse.setMTI("0210");
            isoMsgResponse.set(39, response.get("message"));
            isoMsgResponse.set(4, response.get("txnAmount"));
        } catch (ISOException e) {
            System.out.println("ERROR MESSAGE: "+e.getMessage());
            e.printStackTrace();
        }
        return isoMsgResponse;
    }

    @Override
    public ISOMsg processSale(ISOMsg request) {
        // communicate with backend
        String SALE_MONEY_ENDPOINT = "http://127.0.0.1:10203/api/sale";

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
            saleData.put("terminal", terminal);
            saleData.put("merchant", merchant);
            saleData.put("currencyCode", currencyCode);


            // send the request to backend
            Map<String, Object> postResponse = httpClient.post(SALE_MONEY_ENDPOINT, saleData);


            // communicate with backend
           //Map<String, Object> data = httpClient.get("http://41.215.130.247:2019/api/sale/");


            // build response iso
            response = (ISOMsg) request.clone();
            System.out.println("====================================" + postResponse);
            response.setMTI("0210");
            response.set(39, "00");
//            RestTemplate httpClient = new RestTemplate();
//            String postResponse = httpClient.postForObject(SALE_MONEY_ENDPOINT, saleData, String.class);
//            System.out.println("====================================" + postResponse);
//
//            if ("OK".equalsIgnoreCase(postResponse.trim())) {
//                response.setMTI("0210");
//                // Approve transaction
//                response.set(39, "00");
//            } else {
//                // Transaction declined: send an error
//                response.set(39, "06");
//            }

        } catch (Exception ex) {
            Logger.getLogger(RequestListener.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }

        return response;
    }
}


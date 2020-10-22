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
    String authToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ3YW5nZWNpMUBtYWlsLmNvbSIsInJvbGVzIjpbXSwiaWF0IjoxNjAzMTE5NTk0LCJleHAiOjE2MDM0Nzk1OTR9.MhA_ri8xL-39ANsCNBL6rxVNryTr3ZD_Fboy4B_2GBpIZMHSLfRXekuF4Eve7I_MY3tODeR7cYQd9K2h4TdRCA";


    private final String SEND_MONEY_ENDPOINT = "http://127.0.0.1:10203/api/transactions/send_money";
    private final String DEPOSIT_MONEY_ENDPOINT = "http://127.0.0.1:10203/api/transactions/deposit/";
    private final String SALE_ENDPOINT = "http://127.0.0.1:10203/api/transactions/sale";
    private final String RECEIVE_MONEY_ENDPOINT = "http://127.0.0.1:10203/api/transactions/receive_money";

    /**
     * Processes the send money transaction.
     *
     * @param clonedRequest An ISOMsg with transaction data.
     * @return IsoMsg An ISoMsg with appropriate response fields set.
     */
    @Override
    public ISOMsg processSendMoney(ISOMsg clonedRequest) throws ISOException {
        clonedRequest.setMTI("0210");

        try {
            // Assemble data to send to backend
            Map<String, Object> transactionData = new HashMap<>();
            transactionData.put("pan", clonedRequest.getString(2));
            transactionData.put("amountTransaction", clonedRequest.getString(4));
            transactionData.put("recipientEmail", clonedRequest.getString(47));
            transactionData.put("terminalID", clonedRequest.getString(41));
            transactionData.put("dateTime", clonedRequest.getString(7));
            transactionData.put("merchantID", clonedRequest.getString(42));
            transactionData.put("currencyCode", clonedRequest.getString(49));
            transactionData.put("token", clonedRequest.getString(48));

            System.out.println("===================: send Money tx-Data -> " + transactionData);

            // Add authentication header
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            headers.set("Authorization", "Bearer "+authToken);
//
//
//            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(transactionData, headers);

            RestTemplate httpClient = new RestTemplate();
            String postResponse = httpClient.postForObject(SEND_MONEY_ENDPOINT, transactionData, String.class);

            System.out.println("===================: sendMoney resp from backend: -> " + postResponse);

            if ("OK".equalsIgnoreCase(postResponse.trim())) {
                // Transaction approved
                clonedRequest.set(39, "00");
            } else {
                // Transaction declined: send an error
                clonedRequest.set(39, "06");
            }

        } catch (Exception ex) {
            Logger.getLogger(RequestListener.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        } finally {
            // Transaction declined: send an error
            clonedRequest.set(39, "06");
        }
        return clonedRequest;
    }

    /**
     * Processes deposit money transaction
     *
     * @param clonedRequest An ISOMsg with transaction data.
     * @return IsoMsg An ISoMsg with appropriate response fields set.
     */
    @Override

    public ISOMsg processDeposit(ISOMsg clonedRequest) throws ISOException {
        clonedRequest.setMTI("0210");

        try {
            // Extract required data
            String token = clonedRequest.getString(48);
            String merchantPan = clonedRequest.getString(2);
            String processingCode =  clonedRequest.getString(3);
            String amountTransaction =  clonedRequest.getString(4);
            String dateTimeTransmission =  clonedRequest.getString(7);
            String terminalID =  clonedRequest.getString(41);//Terminal ID
            String merchantID =  clonedRequest.getString(42);//Merchant ID
            String customerPan =  clonedRequest.getString(47);
            String amountTransactionCurrencyCode =  clonedRequest.getString(49);// Currency Code


            // Build data object to send to backend
            Map<String, Object> depositData= new HashMap<>();
            depositData.put("merchantPan", merchantPan);
            depositData.put("processingCode", processingCode);
            depositData.put("amountTransaction", amountTransaction);
            depositData.put("dateTimeTransmission", dateTimeTransmission);
            depositData.put("terminal", terminalID);
            depositData.put("merchant", merchantID);
            depositData.put("customerPan", customerPan);
            depositData.put("currencyCode", amountTransactionCurrencyCode);


            // Add authentication header
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            headers.set("Authorization", "Bearer " + token);
//
//            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(depositData, headers);

            RestTemplate httpClient = new RestTemplate();
            String postResponse = httpClient.postForObject(DEPOSIT_MONEY_ENDPOINT, depositData, String.class);

            System.out.println("===================: deposit Resp -> " + postResponse);
            if ("OK".equalsIgnoreCase(postResponse.trim())) {
                // Transaction approved
                clonedRequest.set(39, "00");
            } else {
                // Transaction declined
                clonedRequest.set(39, "06");
            }

        } catch (Exception ex) {
            Logger.getLogger(RequestListener.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return clonedRequest;
    }

    /**
     * Process receive money transaction
     * @param clonedRequest FROM pos
     * @return isoMsg
     */
    @Override
    public ISOMsg processReceiveMoney(ISOMsg clonedRequest) throws ISOException {
        clonedRequest.setMTI("0210");

        HashMap<String, String> isoMsgToSend = new HashMap<>();

        isoMsgToSend.put("pan", clonedRequest.getString(2));
        isoMsgToSend.put("processingCode", clonedRequest.getString(3));
        isoMsgToSend.put("transmissionDateTime", clonedRequest.getString(7));
        isoMsgToSend.put("tid", clonedRequest.getString(41));
        isoMsgToSend.put("mid", clonedRequest.getString(42));
        isoMsgToSend.put("receiveMoneyToken", clonedRequest.getString(47));
        isoMsgToSend.put("currency", clonedRequest.getString(49));

        // Add authentication header
//        String authToken = isoMsg.getString(48);

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
            clonedRequest.set(39, response.get("message"));
            clonedRequest.set(4, response.get("txnAmount"));
        } catch (ISOException e) {
            System.out.println("ERROR MESSAGE: "+e.getMessage());
            e.printStackTrace();
        }
        return clonedRequest;
    }

    /**
     * Processes sale transaction
     *
     * @param clonedRequest A cloned ISOMsg with transaction data.
     * @return IsoMsg An ISoMsg with appropriate response fields set.
     */
    @Override
    public ISOMsg processSale(ISOMsg clonedRequest) throws ISOException {
        clonedRequest.setMTI("0210");

        try {
            // Extract required data
            String token = clonedRequest.getString(48);
            String primaryAccNo = (String) clonedRequest.getValue(2);
            String processingCode = (String) clonedRequest.getValue(3);
            String amountTransaction = (String) clonedRequest.getValue(4);
            String transmissionDateTime = (String) clonedRequest.getValue(7);
            String terminal = (String) clonedRequest.getValue(41);
            String merchant = (String) clonedRequest.getValue(42);
            String currencyCode = (String) clonedRequest.getValue(49);

            // Build data object to send to backend
            Map<String, Object> saleData= new HashMap<>();
            saleData.put("pan", primaryAccNo);
            saleData.put("processingCode", processingCode);
            saleData.put("amountTransaction", amountTransaction);
            saleData.put("transmissionDateTime", transmissionDateTime);
            saleData.put("terminal", terminal);
            saleData.put("merchant", merchant);
            saleData.put("currencyCode", currencyCode);

            // Add authentication header
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            headers.set("Authorization", "Bearer " + token);
//
//            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(saleData, headers);

            RestTemplate httpClient = new RestTemplate();
            String postResponse = httpClient.postForObject(SALE_ENDPOINT, saleData, String.class);


            if ("OK".equalsIgnoreCase(postResponse.trim())) {
                // Transaction approved
                clonedRequest.set(39, "00");
            } else {
                // Transaction declined
                clonedRequest.set(39, "06");
            }

        } catch (Exception ex) {
            Logger.getLogger(RequestListener.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return clonedRequest;
    }
}
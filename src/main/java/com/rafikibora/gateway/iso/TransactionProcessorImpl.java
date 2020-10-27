package com.rafikibora.gateway.iso;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    private final String SEND_MONEY_ENDPOINT = "http://127.0.0.1:10203/api/transactions/send_money";
    private final String DEPOSIT_MONEY_ENDPOINT = "http://127.0.0.1:10203/api/transactions/deposit";
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
            String token = clonedRequest.getString(72).substring(2);

            // Add authentication header
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + token);


            // Send transactional data to the backend
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(transactionData, headers);
            RestTemplate httpClient = new RestTemplate();
            String postResponse = httpClient.postForObject(SEND_MONEY_ENDPOINT, entity, String.class);

            if ("OK".equalsIgnoreCase(postResponse.trim())) {
                // Transaction approved
                clonedRequest.set(39, "00");
            } else {
                // Transaction declined: send an error
                clonedRequest.set(39, "06");
            }

        } catch (Exception ex) {
            Logger.getLogger(RequestListener.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
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

            String token = clonedRequest.getString(72).substring(2);
            System.out.println("========================================> " + token);



            // Add authentication header
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + token);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(depositData, headers);

            RestTemplate httpClient = new RestTemplate();

            // Send transactional data to the backend
            ResponseEntity<String> rsp=httpClient.postForEntity(DEPOSIT_MONEY_ENDPOINT, entity, String.class);
            System.out.println("++++++++++RESPONSE CODE" +rsp.getStatusCode());
            if (rsp.getStatusCode().value() == 201){
                System.out.println("=====RESPONSE BODY===="+rsp.getBody());
                clonedRequest.set(39,"00");
            }
            else{
                clonedRequest.set(39,"06");
            }

        } catch (Exception ex) {
            Logger.getLogger(RequestListener.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return clonedRequest;
    }

    /**
     * Process receive money transaction
     * @param request FROM pos
     * @return isoMsg
     */
    @Override
    public ISOMsg processReceiveMoney(ISOMsg request) throws ISOException {
        request.setMTI("0210");

        HashMap<String, String> isoMsgToSend = new HashMap<>();

        isoMsgToSend.put("pan", request.getString(2));
        isoMsgToSend.put("processingCode", request.getString(3));
        isoMsgToSend.put("transmissionDateTime", request.getString(7));
        isoMsgToSend.put("tid", request.getString(41));
        isoMsgToSend.put("mid", request.getString(42));
        isoMsgToSend.put("receiveMoneyToken", request.getString(47));
        isoMsgToSend.put("currency", request.getString(49));

        // Add authentication header
        String authToken = request.getString(72);

        String token = request.getString(72).substring(2);
        System.out.println("========================================> " + token);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer "+token);

        // Send transactional data to the backend
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(isoMsgToSend, headers);
        Map<String, String> response = httpClient.post(RECEIVE_MONEY_ENDPOINT,entity);

        System.out.println("*************** Response from web portal *********************");
        System.out.println("========= " + response);
        System.out.println("response code: "+response.get("message"));
        System.out.println("txn amount: "+response.get("txnAmount"));
        System.out.println("*************** Response from web portal *********************");

        try {
            request.set(39, response.get("message"));
            request.set(4, response.get("txnAmount"));
        } catch (ISOException e) {
            System.out.println("ERROR MESSAGE: "+e.getMessage());
            e.printStackTrace();
        }
        return request;
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

            String token = clonedRequest.getString(72).substring(2);
            System.out.println("========================================> " + token);

            // Add authentication header
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + token);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(saleData, headers);
            RestTemplate httpClient = new RestTemplate();

            // Send transactional data to the backend
            ResponseEntity<String> rsp=httpClient.postForEntity(SALE_ENDPOINT, entity, String.class);
            System.out.println("++++++++++RESPONSE CODE " +rsp.getStatusCode());
            if (rsp.getStatusCode().value() == 201){
                System.out.println("=====RESPONSE BODY==== "+rsp.getBody());
                clonedRequest.set(39,"00");
            }
            else{
                clonedRequest.set(39,"06");
            }
        } catch (Exception ex) {
            Logger.getLogger(RequestListener.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return clonedRequest;
    }
}
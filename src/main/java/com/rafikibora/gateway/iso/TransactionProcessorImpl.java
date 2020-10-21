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

    private final String SEND_MONEY_ENDPOINT = "http://127.0.0.1:10203/api/transactions/send_money";
    private final String DEPOSIT_MONEY_ENDPOINT = "http://127.0.0.1:10203/api/transactions/deposit/";
    private final String SALE_ENDPOINT = "http://127.0.0.1:10203/api/transactions/sale";
    private final String RECEIVE_MONEY_ENDPOINT = "http://127.0.0.1:10203/api/transactions/receive_money";

    /**
     * Processes the send money transaction.
     *
     * @param request An ISOMsg with transaction data.
     * @return IsoMsg An ISoMsg with appropriate response fields set.
     */
    @Override
    public ISOMsg processSendMoney(ISOMsg request) throws ISOException {
        ISOMsg response = (ISOMsg) request.clone();
        response.setMTI("0210");

        try {
            String token = request.getString(48);
            String pan = request.getString(2);
            String amount = request.getString(4);
            String transmissionDateTime = request.getString(7); //YYMMDDhhmmss
            String terminalID = request.getString(41); // Terminal ID
            //String merchantID = request.getString(42); // Merchant ID
            String email = request.getString(47); // Currency Code
            String currencyCode = request.getString(49); // Currency Code
            String processingCode = request.getString(3); // processing Code


            // Assemble data to send to backend
            Map<String, Object> transactionData = new HashMap<>();
            transactionData.put("pan", pan);
            transactionData.put("amountTransaction", amount);
            transactionData.put("recipientEmail", email);
            transactionData.put("terminalID", terminalID);
            transactionData.put("dateTime", transmissionDateTime);
            //transactionData.put("MID", merchantID);
            transactionData.put("currencyCode", currencyCode);


            // Add authentication header
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + token);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(transactionData, headers);

            RestTemplate httpClient = new RestTemplate();
            String postResponse = httpClient.postForObject(SEND_MONEY_ENDPOINT, entity, String.class);

            if ("OK".equalsIgnoreCase(postResponse.trim())) {
                // Transaction approved
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
     * Processes deposit money transaction
     *
     * @param request An ISOMsg with transaction data.
     * @return IsoMsg An ISoMsg with appropriate response fields set.
     */
    @Override
    public ISOMsg processDeposit(ISOMsg request) throws ISOException {
        ISOMsg response = (ISOMsg) request.clone();
        response.setMTI("0210");

        try {
            // Extract required data
            String token = request.getString(48);
            String merchantPan = request.getString(2);
            String processingCode =  request.getString(3);
            String amountTransaction =  request.getString(4);
            String dateTimeTransmission =  request.getString(7);
            String terminalID =  request.getString(41);//Terminal ID
            String merchantID =  request.getString(42);//Merchant ID
            String customerPan =  request.getString(47);
            String amountTransactionCurrencyCode =  request.getString(49);// Currency Code


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
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + token);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(depositData, headers);

            RestTemplate httpClient = new RestTemplate();
            String postResponse = httpClient.postForObject(DEPOSIT_MONEY_ENDPOINT, entity, String.class);

            if ("OK".equalsIgnoreCase(postResponse.trim())) {
                // Transaction approved
                response.set(39, "00");
            } else {
                // Transaction declined
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
    public ISOMsg processReceiveMoney(ISOMsg isoMsg) throws ISOException {
        ISOMsg isoMsgResponse = (ISOMsg) isoMsg.clone();
        isoMsgResponse.setMTI("0210");

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
            isoMsgResponse.set(39, response.get("message"));
            isoMsgResponse.set(4, response.get("txnAmount"));
        } catch (ISOException e) {
            System.out.println("ERROR MESSAGE: "+e.getMessage());
            e.printStackTrace();
        }
        return isoMsgResponse;
    }

    /**
     * Processes sale transaction
     *
     * @param request An ISOMsg with transaction data.
     * @return IsoMsg An ISoMsg with appropriate response fields set.
     */
    @Override
    public ISOMsg processSale(ISOMsg request) throws ISOException {
        ISOMsg response = (ISOMsg) request.clone();
        response.setMTI("0210");

        try {
            // Extract required data
            String token = request.getString(48);
            String primaryAccNo = (String) request.getValue(2);
            String processingCode = (String) request.getValue(3);
            String amountTransaction = (String) request.getValue(4);
            String transmissionDateTime = (String) request.getValue(7);
            String terminal = (String) request.getValue(41);
            String merchant = (String) request.getValue(42);
            String currencyCode = (String) request.getValue(49);

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
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + token);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(saleData, headers);

            RestTemplate httpClient = new RestTemplate();
            String postResponse = httpClient.postForObject(SALE_ENDPOINT, entity, String.class);

            if ("OK".equalsIgnoreCase(postResponse.trim())) {
                // Transaction approved
                response.set(39, "00");
            } else {
                // Transaction declined
                response.set(39, "06");
            }

        } catch (Exception ex) {
            Logger.getLogger(RequestListener.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return response;
    }
}
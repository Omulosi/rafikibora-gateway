package com.rafikibora.gateway.iso;

import org.jpos.iso.ISOMsg;

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
            String transmissionDateTime = request.getString(7);
            String localDateTime = request.getString(12);
            String terminalID = request.getString(41);
            String merchantID = request.getString(42);
            String currencyCode = request.getString(49);
            String accName = request.getString(102);
            Map<String, Object> data = httpClient.get(SEND_MONEY_ENDPOINT);


            response = (ISOMsg) request.clone();
            response.setMTI("0210");

            response.set(39, "00");
            response.set(104, data.get(name).toString());
            response.set(4, data.get(salary).toString());

            response.set(1, "");


        } catch (Exception ex) {
            Logger.getLogger(RequestListener.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }

        return response;

    }

    @Override
    public ISOMsg processDeposit(ISOMsg request) {
        return null;
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

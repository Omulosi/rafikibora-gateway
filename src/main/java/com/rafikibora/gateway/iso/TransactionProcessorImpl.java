package com.rafikibora.gateway.iso;

import org.jpos.iso.ISOMsg;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TransactionProcessorImpl implements TransactionProcessor {
    private final HttpClient httpClient = new HttpClient();

    @Override
    public ISOMsg processSendMoney(ISOMsg request) {
        String name = "nama";
        String salary = "saldo";
        ISOMsg response = null;
        try {
            response = (ISOMsg) request.clone();
            response.setMTI("0210");

            String accName = request.getString(102);
            Map<String, Object> data = httpClient.get("http://localhost:2019/api/rekening/"+accName+"/");


            response.set(39, "00");
            response.set(104, data.get(name).toString());
            response.set(4, data.get(salary).toString());

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

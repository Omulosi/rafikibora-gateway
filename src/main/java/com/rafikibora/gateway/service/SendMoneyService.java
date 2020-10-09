package com.rafikibora.gateway.service;

import com.rafikibora.gateway.iso.RequestListener;
import com.rafikibora.gateway.service.RequestService;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOSource;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.io.IOException;

@Service
public class SendMoneyService {
    private final String SEND_MONEY_ENDPOINT = "http://localhost:2019/api/rekening/";

    public ISOMsg processTransaction(ISOMsg isoMsg, RequestService requestService)  {
        ISOMsg response = null;
        try {
            System.out.printf("Send transaction");
            Logger.getLogger(SendMoneyService.class.getName()).log(Level.INFO, "Send transaction", new Exception());

            response = (ISOMsg) isoMsg.clone();
            response.setMTI("0210");

            String accName = isoMsg.getString(102);

            // Get data from backend
            Map<String, Object> data = requestService.get(SEND_MONEY_ENDPOINT + accName + "/");
//
////            RestTemplate httpClient = new RestTemplate();
////            Map<String, Object> hasil = httpClient.getForObject("http://localhost:2019/api/rekening/"+nomorAkun+"/", HashMap.class);
//
//
//            // set response fields in iso Msg
//            response.set(39, "00");
//            response.set(104, res.get("nama").toString());
//            response.set(4, res.get("saldo").toString());
//
//            sender.send(response);
//            return true;
            response.setMTI("0210");
            response.set(39, "00");

        } catch (Exception ex) {
            Logger.getLogger(SendMoneyService.class.getName()).log(Level.SEVERE, null, ex);
        }

        return response;
    }
}

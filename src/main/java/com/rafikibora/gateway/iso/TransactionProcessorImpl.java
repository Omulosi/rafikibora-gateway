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
        return null;
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

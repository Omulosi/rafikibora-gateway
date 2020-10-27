package com.rafikibora.gateway.iso;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AuthProcessorImpl implements AuthProcessor {

    private final HttpClient httpClient = new HttpClient();
    private final String AUTH_ENDPOINT = "http://127.0.0.1:10203/api/auth/login";

    /**
     * Process authentication requests
     * @param request FROM pos
     * @return isoMsg
     */
    @Override
    public ISOMsg login (ISOMsg request) throws ISOException {
        try {
           // request.setMTI("0810");
            request.setResponseMTI(); // same to request.setMTI("0810");

            String email = request.getString(57);
            String password = request.getString(58);
            System.out.println("========================");
            System.out.println(email);
            System.out.println(password);
            System.out.println("========================");

            // Assemble data to send to backend
            Map<String, Object> authData = new HashMap<>();
            authData.put("email", email);
            authData.put("password", password);

//            Map<String, Object> postResponse = httpClient.post(AUTH_ENDPOINT, authData);
            Map<String, String> postResponse = httpClient.post(AUTH_ENDPOINT, authData);

            System.out.println("============: post response " + postResponse);

//            if (postResponse.get("status") == "OK") {
//                String authToken = postResponse.get("authToken");
//                request.set(39, "00");
//                request.set(48, authToken);
//                System.out.println("***********RESPONSE FROM SERVER***********");
//                System.out.println("token: ");
//                System.out.println(authToken);
//                System.out.println("***********RESPONSE FROM SERVER***********");
//            } else {
//                request.set(39, "06");
//            }

            String authToken = postResponse.get("authToken");
            request.set(39, "00");
            request.set(48, authToken);
            System.out.println("***********RESPONSE FROM SERVER***********");
            System.out.println("token: ");
            System.out.println(authToken);
            System.out.println("***********RESPONSE FROM SERVER***********");

        } catch (Exception ex) {
            request.set(39, "06");
            request.set(48, "LOGIN FAILED");
            Logger.getLogger(RequestListener.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }

        return request;
    }
}

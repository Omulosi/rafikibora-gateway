package com.rafikibora.gateway.iso;

import org.jpos.iso.ISOMsg;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AuthProcessorImpl implements AuthProcessor {

    private final HttpClient httpClient = new HttpClient();

    /**
     * Process authentication requests
     * @param request FROM pos
     * @return isoMsg
     */
    @Override
    public ISOMsg login (ISOMsg request) {
        ISOMsg response = (ISOMsg) request.clone();
        String AUTH_ENDPOINT = "http://41.215.130.247:2019/api/auth/login";

        try {
            response.setMTI("0810");

            String email = request.getString(57);
            String password = request.getString(58);

            // Assemble data to send to backend
            Map<String, Object> authData = new HashMap<>();
            authData.put("email", email);
            authData.put("password", password);


            Map<String, Object> postResponse = httpClient.post(AUTH_ENDPOINT, authData);

            if (postResponse.get("status") == "OK") {
                String token = postResponse.get("token").toString();
                response.set(39, "00");
                response.set(48, token);
            } else {
                response.set(39, "06");
            }

        } catch (Exception ex) {
            Logger.getLogger(RequestListener.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }

        return response;
    }
}

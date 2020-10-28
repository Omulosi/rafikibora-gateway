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
     *
     * @param request ISO msg from remote client
     * @return isoMsg
     */
    @Override
    public ISOMsg login (ISOMsg request) throws ISOException {
        try {
            request.setResponseMTI();
            String emailPassword = request.getString(72);
            int sep = emailPassword.indexOf("|");
            String email = emailPassword.substring(0, sep);
            String password = emailPassword.substring(sep + 1);

            // Assemble data to send to backend
            Map<String, Object> authData = new HashMap<>();
            authData.put("email", email);
            authData.put("password", password);

            Map<String, String> postResponse = httpClient.post(AUTH_ENDPOINT, authData);
            String authToken = postResponse.get("authToken");

            request.set(39, "00");
            request.set(72, authToken);

        } catch (Exception ex) {
            request.set(39, "06");
            request.set(72, "LOGIN FAILED");
            Logger.getLogger(RequestListener.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return request;
    }
}

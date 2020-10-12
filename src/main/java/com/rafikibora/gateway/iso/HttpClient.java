package com.rafikibora.gateway.iso;

import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class HttpClient {
    RestTemplate httpClient = new RestTemplate();

    public HashMap post (String url, HashMap data) {
        RestTemplate httpClient = new RestTemplate();
        HashMap result = null;
        try {
            result = httpClient.postForObject(url,
                    data,
                    HashMap.class);
        } catch (Exception ex) {
            Logger.getLogger(HttpClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return  result;
    }

    // Returns response data as a Map object
    public HashMap get (String url) {
        HashMap result = null;
        try {
            result =  httpClient.getForObject(url, HashMap.class);

        } catch (Exception ex) {
            Logger.getLogger(HttpClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }



}


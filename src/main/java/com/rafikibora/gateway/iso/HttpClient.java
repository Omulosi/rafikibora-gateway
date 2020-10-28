package com.rafikibora.gateway.iso;

import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class encapsulates methods for consuming a remote API
 */
public class HttpClient {
    RestTemplate httpClient = new RestTemplate();

    /**
     * Send data via POST method
     *
     * @param url
     * @param data
     * @return
     */
    public HashMap post (String url, Map data) {
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

    /**
     * Send data via POST method
     *
     * @param url
     * @param data
     * @return
     */
    public HashMap post (String url, HttpEntity data) {
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

    /**
     * Fetch data
     *
     * @param url
     * @return
     */
    public HashMap get (String url) {
        HashMap result = null;
        try {
            result = httpClient.getForObject(url, HashMap.class);

        } catch (Exception ex) {
            Logger.getLogger(HttpClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
}


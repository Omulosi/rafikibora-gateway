package com.rafikibora.gateway.iso;

import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class HttpClient {
    RestTemplate httpClient = new RestTemplate();

    /* Returns response status as a String - Ok or otherwise

     */
//    public String post (String url, HashMap data) {
//        RestTemplate httpClient = new RestTemplate();
//
//        String result = null;
//
//        try {
//            result = httpClient.postForObject(url,
//                    data,
//                    String.class);
//
//            result = result.trim();
//        } catch (Exception ex) {
//            Logger.getLogger(HttpClient.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        return  result;
//    }

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
        //RestTemplate httpClient = new RestTemplate();
        HashMap result = null;
        try {
            result =  httpClient.getForObject(url, HashMap.class);

        } catch (Exception ex) {
            Logger.getLogger(HttpClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }



}


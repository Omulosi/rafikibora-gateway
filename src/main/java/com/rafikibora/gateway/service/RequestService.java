package com.rafikibora.gateway.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


@Service
public class RequestService {
    RestTemplate httpClient = new RestTemplate();


     /* Returns response status as a String - Ok or otherwise

     */
    public String post (String url, HashMap data) {
        RestTemplate httpClient = new RestTemplate();

        String result = null;

        try {
            result = httpClient.postForObject(url,
                    data,
                    String.class);

            result = result.trim();
        } catch (Exception ex) {
            Logger.getLogger(RequestService.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(RequestService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public static void main(String[] args) {
        RequestService requestService = new RequestService();
        Map<String, Object> data = requestService.get("http://localhost:2019/api/rekening/");
        System.out.println(data);
    }

}

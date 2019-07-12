package com.demo.transport;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONObject;
import org.springframework.boot.json.GsonJsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;

public class HttpClientHelper {
    private static HttpClientHelper instance = null;

    public static HttpClientHelper getInstance() {
        if (instance == null) {
            instance = new HttpClientHelper();
        }
        return instance;
    }

    public JSONObject sendTo(String path, String method, JSONObject data) {
        JSONObject res = null;
        try {
            HttpClient client = HttpClientBuilder.create().build();
            
            switch (method){
                case "GET":

            }

            HttpPost post = new HttpPost(path);
            StringEntity entity = new StringEntity(data.toJSONString());
            post.setEntity(entity);
            post.setHeader("Accept", "application/json");
            post.setHeader("Content-type", "application/json");
            HttpResponse response = client.execute(post);
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            if (response.getStatusLine().getStatusCode() >= 200 && response.getStatusLine().getStatusCode() <= 299) {
                Map<String, Object> resObjectMap = new GsonJsonParser().parseMap(result.toString());
                res = new JSONObject(resObjectMap);
            }
            return res;
        } catch (Exception ex) {

        }
        return res;
    }


}

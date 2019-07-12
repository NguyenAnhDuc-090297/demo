package com.demo.transport;

import com.sun.net.httpserver.Headers;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.hibernate.validator.internal.util.privilegedactions.GetMethod;
import org.json.simple.JSONObject;
import org.springframework.boot.json.GsonJsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Map;

public class HttpClientHelper {
    private static HttpClientHelper instance = null;

    public static HttpClientHelper getInstance() {
        if (instance == null) {
            instance = new HttpClientHelper();
        }
        return instance;
    }

    public enum METHODS {
        GET, POST, PUT, DELETE
    }

    public JSONObject sendTo(String path, METHODS method, JSONObject data) {
        JSONObject res = null;
        try {
            HttpClient client = HttpClientBuilder.create().build();
            StringBuffer result = new StringBuffer();
            String line = "";
            HttpResponse response;
            BufferedReader rd;

            switch (method) {
                case GET:
                    HttpGet httpGET = new HttpGet(path);
                    httpGET.setHeader("Accept", "application/json");
                    httpGET.setHeader("Content-type", "application/json");
                    response = client.execute(httpGET);
                    rd = new BufferedReader(
                            new InputStreamReader(response.getEntity().getContent()));
                    while ((line = rd.readLine()) != null) {
                        result.append(line);
                    }
                    if (response.getStatusLine().getStatusCode() >= 200 && response.getStatusLine().getStatusCode() <= 300) {
                        Map<String, Object> resObjectMap = new GsonJsonParser().parseMap(result.toString());
                        res = new JSONObject(resObjectMap);
                    }
                    break;
                case PUT:
                    HttpPut httpPUT = new HttpPut(path);
                    httpPUT.setHeader("Accept", "application/json");
                    httpPUT.setHeader("Content-type", "application/json");
                    response = client.execute(httpPUT);
                    rd = new BufferedReader(
                            new InputStreamReader(response.getEntity().getContent()));
                    while ((line = rd.readLine()) != null) {
                        result.append(line);
                    }
                    if (response.getStatusLine().getStatusCode() >= 200 && response.getStatusLine().getStatusCode() <= 300) {
                        Map<String, Object> resObjectMap = new GsonJsonParser().parseMap(result.toString());
                        res = new JSONObject(resObjectMap);
                    }
                    break;
                case POST:
                    StringEntity entity = new StringEntity(data.toJSONString());
                    HttpPost httpPOST = new HttpPost(path);
                    httpPOST.setHeader("Accept", "application/json");
                    httpPOST.setHeader("Content-type", "application/json");
                    httpPOST.setEntity(entity);
                    response = client.execute(httpPOST);
                    rd = new BufferedReader(
                            new InputStreamReader(response.getEntity().getContent()));
                    while ((line = rd.readLine()) != null) {
                        result.append(line);
                    }
                    if (response.getStatusLine().getStatusCode() >= 200 && response.getStatusLine().getStatusCode() <= 300) {
                        Map<String, Object> resObjectMap = new GsonJsonParser().parseMap(result.toString());
                        res = new JSONObject(resObjectMap);
                    }
                    break;
                case DELETE:
                    HttpDelete httpDELETE = new HttpDelete(path);
                    httpDELETE.setHeader("Accept", "application/json");
                    httpDELETE.setHeader("Content-type", "application/json");
                    response = client.execute(httpDELETE);
                    rd = new BufferedReader(
                            new InputStreamReader(response.getEntity().getContent()));
                    while ((line = rd.readLine()) != null) {
                        result.append(line);
                    }
                    if (response.getStatusLine().getStatusCode() >= 200 && response.getStatusLine().getStatusCode() <= 300) {
                        Map<String, Object> resObjectMap = new GsonJsonParser().parseMap(result.toString());
                        res = new JSONObject(resObjectMap);
                    }
                    break;
            }
            return res;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }


}

package com.demo.api;

import com.demo.configuration.Configuration;
import com.demo.util.Encryption;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.StringContent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/auth")
public class API {

    @Value("${app.api.login}")
    private String API_LOGIN;
    @Value("${app.integrationkey}")
    private String INTEGRATION_KEY;
    @Value("${app.secretkey}")
    private String SECRET_KEY;


    @PostMapping(path = "/login", consumes = "application/json", produces = "application/json")
    private JSONObject authentication(@RequestBody JSONObject auth) {
        try {
            String username = (String) auth.get("username");
            String password = (String) auth.get("password");
            String unixTimeStamp = String.valueOf(System.currentTimeMillis() / 1000L);
            String hmacData = username + password + INTEGRATION_KEY + unixTimeStamp;
            String hmac = Encryption.encrypt(SECRET_KEY, hmacData);
            HttpClient client = HttpClientBuilder.create().build();
            HttpPost post = new HttpPost(API_LOGIN);
            JSONObject data = new JSONObject();
            data.put("username", username);
            data.put("password", password);
            data.put("integrationKey", INTEGRATION_KEY);
            data.put("unixTimestamp", unixTimeStamp);
            data.put("hmac", hmac);

            StringEntity entity = new StringEntity(data.toJSONString());
            post.setEntity(entity);
            post.setHeader("Accept", "application/json");
            post.setHeader("Content-type", "application/json");

            HttpResponse response = client.execute(post);
            System.out.println("Response Code : "
                    + response.getStatusLine().getStatusCode());

            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }

            System.out.println(result);

            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }


}

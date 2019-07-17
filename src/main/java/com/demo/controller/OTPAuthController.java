package com.demo.controller;


import com.demo.authenticationhandler.OTPAuthentication;
import com.demo.model.AuthModel;
import com.demo.model.OTPAuthModel;
import com.demo.response.DataResponse;
import com.demo.util.Encryption;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping(value = "/otp")
public class OTPAuthController {

    @Value("${app.otp.auth}")
    private String API_OTP;
    @Value("${app.integrationkey}")
    private String INTEGRATION_KEY;
    @Value("${app.secretkey}")
    private String SECRET_KEY;

    @RequestMapping(value = {"/auth"}, method = RequestMethod.POST)
    private DataResponse OTPAuth(@RequestBody String body, HttpSession session) {
        try {
            JsonObject data = new Gson().fromJson(body, JsonObject.class);
            String ammount = data.get("amount").getAsString();
            String fromaccount = data.get("fromaccount").getAsString();
            String toaccount = data.get("toaccount").getAsString();
            String effdate = data.get("effdate").getAsString();
            String otp = data.get("otp").getAsString();
            AuthModel authModel = (AuthModel) session.getAttribute("auth");
            String username = authModel.getUsername();

            String unixTimeStamp = String.valueOf(System.currentTimeMillis() / 1000L);
            String hmacData = username + otp + INTEGRATION_KEY + unixTimeStamp;
            String hmac = Encryption.encrypt(SECRET_KEY, hmacData);

            OTPAuthModel auth = OTPAuthentication.getInstance().Auth(API_OTP, username, otp,
                    INTEGRATION_KEY, unixTimeStamp, hmac);
            if (auth != null) {
                return DataResponse.SUCCESSFUL;
            } else {
                return DataResponse.FAILED;
            }
        } catch (Exception ex) {
            return DataResponse.FAILED;
        }
    }
}

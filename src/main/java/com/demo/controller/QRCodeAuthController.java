package com.demo.controller;

import com.demo.authenticationhandler.OTPAuthentication;
import com.demo.util.Encryption;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/qr")
public class QRCodeAuthController {

    @Value("${app.qr.auth}")
    private String API_OTP;
    @Value("${app.integrationkey}")
    private String INTEGRATION_KEY;
    @Value("${app.secretkey}")
    private String SECRET_KEY;

    @RequestMapping(value = {"/auth"}, method = RequestMethod.POST)
    private String AdaptiveAuth(@RequestBody String body) {
        JsonObject data = new Gson().fromJson(body.toString(), JsonObject.class);
        String code = data.get("body").getAsString();

        String unixTimeStamp = String.valueOf(System.currentTimeMillis() / 1000L);
        String hmacData = "tronsm" + code + INTEGRATION_KEY + unixTimeStamp;
        String hmac = Encryption.encrypt(SECRET_KEY, hmacData);

        OTPAuthentication.getInstance().Auth(API_OTP, "tronsm", code,
                INTEGRATION_KEY, unixTimeStamp, hmac);


        return "";
    }

}


package com.demo.controller;

import com.demo.authenticationhandler.OTPAuthentication;
import com.demo.authenticationhandler.QRCodeAuthentication;
import com.demo.authenticationhandler.RequestQRCode;
import com.demo.model.QRCodeModel;
import com.demo.model.RequestQrCodeModel;
import com.demo.util.Encryption;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Base64;


@RestController
@RequestMapping(value = "/qr")
public class QRCodeAuthController {

    @Value("${app.qr.auth}")
    private String API_QR_CODE;
    @Value("${app.requestQrCode}")
    private String API_REQUEST_QR;
    @Value("${app.integrationkey}")
    private String INTEGRATION_KEY;
    @Value("${app.secretkey}")
    private String SECRET_KEY;

    @RequestMapping(value = {"/auth"}, method = RequestMethod.POST)
    private String AdaptiveAuth(@RequestBody String body, HttpSession session) {
        JsonObject data = new Gson().fromJson(body.toString(), JsonObject.class);
        String code = data.get("body").getAsString();
        String detail = Base64.getEncoder().encodeToString("aaaaaa".getBytes());

        String unixTimeStamp = String.valueOf(System.currentTimeMillis() / 1000L);
        String hmacDataRequest = "tronsm" + detail + INTEGRATION_KEY + unixTimeStamp;
        String hmacReq = Encryption.encrypt(SECRET_KEY, hmacDataRequest);
        RequestQrCodeModel request = RequestQRCode.getInstance().request(API_REQUEST_QR,
                "tronsm", detail,
                INTEGRATION_KEY, unixTimeStamp, hmacReq);

        String hmacData = "tronsm" + request.getQrCode() + request.getOtpChallenge() + detail + INTEGRATION_KEY + unixTimeStamp;
        String hmac = Encryption.encrypt(SECRET_KEY, hmacData);

        QRCodeModel auth = QRCodeAuthentication.getInstance().Auth(API_QR_CODE, "tronsm", request.getQrCode(),
                request.getOtpChallenge()
                , detail, INTEGRATION_KEY, unixTimeStamp, hmac);


        return "";
    }

}


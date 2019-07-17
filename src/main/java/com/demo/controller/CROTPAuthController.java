package com.demo.controller;

import com.demo.authenticationhandler.CROTPAuthentication;
import com.demo.authenticationhandler.RequestOtpChallenge;
import com.demo.model.AuthModel;
import com.demo.model.CROTPAuthModel;
import com.demo.model.RequestOtpChallengeModel;
import com.demo.response.DataResponse;
import com.demo.response.ResponseCode;
import com.demo.util.Encryption;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping(value = "/crotp")
public class CROTPAuthController {
    @Value("${app.authCrOtp.auth}")
    private String API_AUTHCROTP;
    @Value("${app.requestOtpChallenge}")
    private String API_REQUEST_AUTHCROTP;
    @Value("${app.integrationkey}")
    private String INTEGRATION_KEY;
    @Value("${app.secretkey}")
    private String SECRET_KEY;

    @RequestMapping(value = {"/request"}, method = RequestMethod.POST)
    private DataResponse Request(HttpSession session) {
        try {
            AuthModel authModel = (AuthModel) session.getAttribute("auth");
            String username = authModel.getUsername();

            String unixTimeStamp = String.valueOf(System.currentTimeMillis() / 1000L);
            String hmacData = username + INTEGRATION_KEY + unixTimeStamp;
            String hmac = Encryption.encrypt(SECRET_KEY, hmacData);

            RequestOtpChallengeModel request = RequestOtpChallenge.getInstance().request(API_REQUEST_AUTHCROTP, username, INTEGRATION_KEY, unixTimeStamp, hmac);

            if (request != null) {
                String challenge = request.getOtpChallenge();
                return new DataResponse(ResponseCode.SUCCESSFUL, "SUCCESSFUL", challenge);
            } else {
                return DataResponse.FAILED;
            }
        } catch (Exception ex) {
            return DataResponse.FAILED;
        }
    }

    @RequestMapping(value = {"/auth"}, method = RequestMethod.POST)
    private DataResponse CROTPAuth(@RequestBody String body, HttpSession session) {
        try {
            JsonObject data = new Gson().fromJson(body, JsonObject.class);
            String crotp = data.get("crotp").getAsString();
            String challenge = data.get("challenge").getAsString();

            AuthModel authModel = (AuthModel) session.getAttribute("auth");
            String username = authModel.getUsername();

            String unixTimeStamp = String.valueOf(System.currentTimeMillis() / 1000L);
            String hmacData = username + crotp + challenge + INTEGRATION_KEY + unixTimeStamp;
            String hmac = Encryption.encrypt(SECRET_KEY, hmacData);

            CROTPAuthModel auth = CROTPAuthentication.getInstance().Auth(API_AUTHCROTP, username, challenge, crotp,
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

package com.demo.controller;

import com.demo.authenticationhandler.OTPAuthentication;
import com.demo.authenticationhandler.QRCodeAuthentication;
import com.demo.authenticationhandler.RequestQRCode;
import com.demo.model.AuthModel;
import com.demo.model.QRCodeModel;
import com.demo.model.RequestQrCodeModel;
import com.demo.response.DataResponse;
import com.demo.response.ResponseCode;
import com.demo.util.Encryption;
import com.demo.util.QRCodeGenerator;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.Base64;


@RestController
@RequestMapping(value = "/qr")
public class QRCodeAuthController {

    @Value("${app.qr.auth}")
    private String API_QR_CODE;
    @Value("${app.requestQrCode}")
    private String API_REQUEST_QR;
    @Value("${app.statecheck}")
    private String API_STATECHECK;
    @Value("${app.integrationkey}")
    private String INTEGRATION_KEY;
    @Value("${app.secretkey}")
    private String SECRET_KEY;

    @RequestMapping(value = {"/request"}, method = RequestMethod.POST)
    private DataResponse Request(@RequestBody String body, HttpSession session) {
        try {
            JsonObject data = new Gson().fromJson(body, JsonObject.class);
            AuthModel authModel = (AuthModel) session.getAttribute("auth");
            String username = authModel.getUsername();
            String ammount = data.get("amount").getAsString();
            String fromaccount = data.get("fromaccount").getAsString();
            String toaccount = data.get("toaccount").getAsString();
            String effdate = data.get("effdate").getAsString();
            String detail = "Transaction from " + fromaccount + " to " + toaccount
                    + "\nAmount : " + ammount
                    + "\nEffective Date :" + effdate;
            detail = Base64.getEncoder().encodeToString(detail.getBytes());
            String unixTimeStamp = String.valueOf(System.currentTimeMillis() / 1000L);
            String hmacDataRequest = username + detail + INTEGRATION_KEY + unixTimeStamp;
            String hmacReq = Encryption.encrypt(SECRET_KEY, hmacDataRequest);
            RequestQrCodeModel request = RequestQRCode.getInstance().request(API_REQUEST_QR,
                    username, detail,
                    INTEGRATION_KEY, unixTimeStamp, hmacReq);
            String authToken = request.getAuthToken();

            if (request != null) {
                String challenge = request.getOtpChallenge();
                String qrCode = request.getQrCode();
                String plainText = request.getPlainText();
                QRCodeGenerator.generateQRCodeImage(qrCode, 300, 300, QRCodeGenerator.QR_CODE_IMAGE_PATH);
                String path = "/images/QRCode.png";
                byte[] qrCodeImageByteArray = QRCodeGenerator.getQRCodeImageByteArray(qrCode, 300, 300);
                return new DataResponse(ResponseCode.SUCCESSFUL, "SUCCESSFUL", qrCode + "||" + challenge + "||" + path + "||" + qrCodeImageByteArray + "||" + authToken);
            } else {
                return DataResponse.FAILED;
            }
        } catch (Exception ex) {
            return DataResponse.FAILED;
        }
    }


    @RequestMapping(value = {"/auth"}, method = RequestMethod.POST)
    private String QRAuth(@RequestBody String body, HttpSession session) {
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


    @RequestMapping(value = {"/statecheck"}, method = RequestMethod.POST)
    private DataResponse StateCheck(@RequestBody String body, HttpSession session) {
        try {
            JsonObject data = new Gson().fromJson(body, JsonObject.class);
            String authToken = data.get("authToken").getAsString();
            AuthModel authModel = (AuthModel) session.getAttribute("auth");
            String username = authModel.getUsername();
            String authMethod = "QRCODE";

            String unixTimeStamp = String.valueOf(System.currentTimeMillis() / 1000L);
            String hmacData = username + authMethod + INTEGRATION_KEY + unixTimeStamp + authToken;
            String hmac = Encryption.encrypt(SECRET_KEY, hmacData);

            JsonObject res = QRCodeAuthentication.getInstance().StateCheck(API_STATECHECK, username,
                    authMethod, authToken, INTEGRATION_KEY, unixTimeStamp, hmac);

            if (res != null) {
                return new DataResponse(ResponseCode.SUCCESSFUL, ResponseCode.SUCCESSFUL, res.toString());
            } else {
                return DataResponse.FAILED;
            }
        } catch (Exception ex) {
            return DataResponse.FAILED;
        }
    }


}


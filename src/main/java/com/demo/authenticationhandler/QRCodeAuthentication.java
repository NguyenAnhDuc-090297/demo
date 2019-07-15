package com.demo.authenticationhandler;

import com.demo.model.OTPAuthModel;
import com.demo.model.QRCodeModel;
import com.demo.transport.HttpClientHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;

public class QRCodeAuthentication {
    private static QRCodeAuthentication instance;

    public static QRCodeAuthentication getInstance() {
        if (instance == null) {
            instance = new QRCodeAuthentication();
        }
        return instance;
    }

    public QRCodeModel Auth(String path, String username, String otp, String challenge, String details,
                            String integrationKey,
                            String unixTimestamp, String hmac) {
        try {
            JsonObject data = new JsonObject();
            data.addProperty("username", username);
            data.addProperty("otp", otp);
            data.addProperty("challenge", challenge);
            data.addProperty("details", details);
            data.addProperty("integrationKey", integrationKey);
            data.addProperty("unixTimestamp", unixTimestamp);
            data.addProperty("hmac", hmac);
            JsonObject response = HttpClientHelper.getInstance().sendTo(path, HttpClientHelper.METHODS.POST, data);
            ObjectMapper mapper = new ObjectMapper();
            QRCodeModel qrCodeModel = mapper.readValue(response.toString(), QRCodeModel.class);
            return qrCodeModel;
        } catch (Exception ex) {
        }
        return null;
    }
}

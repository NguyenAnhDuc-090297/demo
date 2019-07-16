package com.demo.controller;


import com.demo.model.AuthModel;
import com.demo.model.LoginModel;
import com.demo.transport.HttpClientHelper;
import com.demo.util.Encryption;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

@RestController
@RequestMapping(path = "/")
public class HomeController {

    @Value("${app.api.login}")
    private String API_LOGIN;
    @Value("${app.integrationkey}")
    private String INTEGRATION_KEY;
    @Value("${app.secretkey}")
    private String SECRET_KEY;

    @RequestMapping(value = {"/", "/home"}, method = RequestMethod.GET)
    private ModelAndView home(HttpSession session) {
        ModelAndView mav = null;
        AuthModel authModel = (AuthModel) session.getAttribute("auth");
        if (authModel == null || authModel.getAuthToken().isEmpty()) {
            mav = new ModelAndView("login");
            mav.addObject("loginModel", new LoginModel());
        } else {
            mav = new ModelAndView("home");
        }
        return mav;
    }

    @RequestMapping(value = {"/login"}, method = RequestMethod.GET)
    private ModelAndView login(HttpSession session) {
        ModelAndView mav = null;
        AuthModel authModel = (AuthModel) session.getAttribute("auth");
        if (authModel == null || authModel.getAuthToken().isEmpty()) {
            mav = new ModelAndView("login");
            mav.addObject("loginModel", new LoginModel());
        } else {
            mav = new ModelAndView("home");
        }
        return mav;
    }

    @RequestMapping(value = {"/login"}, method = RequestMethod.POST)
    private ModelAndView login(@ModelAttribute LoginModel loginModel, HttpSession session) {
        ModelAndView mav = null;
        try {
            String password = loginModel.getPassword();
            String username = loginModel.getUsername();
            if (username.isEmpty()) {
                mav = new ModelAndView("login");
                mav.addObject("errors", "Username not empty !");
                return mav;
            }
            if (password.isEmpty()) {
                mav = new ModelAndView("login");
                mav.addObject("errors", "Password not empty !");
                return mav;
            }

            String unixTimeStamp = String.valueOf(System.currentTimeMillis() / 1000L);
            String hmacData = username + password + INTEGRATION_KEY + unixTimeStamp;
            String hmac = Encryption.encrypt(SECRET_KEY, hmacData);

            JsonObject data = new JsonObject();
            data.addProperty("username", username);
            data.addProperty("password", password);
            data.addProperty("integrationKey", INTEGRATION_KEY);
            data.addProperty("unixTimestamp", unixTimeStamp);
            data.addProperty("hmac", hmac);

            JsonObject result = HttpClientHelper.getInstance().sendTo(API_LOGIN, HttpClientHelper.METHODS.POST, data);
            if (result != null && !result.has("code")) {
                ObjectMapper mapper = new ObjectMapper();
                AuthModel authModel = mapper.readValue(result.toString(), AuthModel.class);
                session.setAttribute("auth", authModel);
                mav = new ModelAndView("home");
                mav.addObject("authModel", authModel);
                return mav;
            } else {
                mav = new ModelAndView("login");
                mav.addObject("errors", result.get("message").getAsString());
                return mav;
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return new ModelAndView("login");
        }
    }

}

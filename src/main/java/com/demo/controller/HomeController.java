package com.demo.controller;


import com.demo.model.LoginModel;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

@RestController
@RequestMapping(path = "/")
public class HomeController {
    @RequestMapping(value = {"/", "/home"}, method = RequestMethod.GET)
    private ModelAndView home(Locale locale, Model model) {
        Date date = new Date();
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
        String formattedDate = dateFormat.format(date);
        ModelAndView mav = new ModelAndView("login");
        mav.addObject("serverTime", formattedDate);
        mav.addObject("loginModel", new LoginModel());
        return mav;
    }
    @RequestMapping(value = {"/login"}, method = RequestMethod.POST)
    private ModelAndView login(@ModelAttribute LoginModel loginModel){
        String password = loginModel.getPassword();
        loginModel.getUsername();

        ModelAndView mav = new ModelAndView("home");
        return mav;
    }

}

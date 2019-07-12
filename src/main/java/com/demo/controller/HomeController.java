package com.demo.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

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
        ModelAndView mav = new ModelAndView("index");
        mav.addObject("serverTime", formattedDate);
        return mav;
    }

}

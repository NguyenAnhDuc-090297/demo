package com.demo.util;

public class HTTPSender {
    private static HTTPSender instance = null;

    public static HTTPSender getInstance() {
        if (instance == null) {
            instance = new HTTPSender();
        }
        return instance;
    }

    public String POST(){



        return "";
    }

}

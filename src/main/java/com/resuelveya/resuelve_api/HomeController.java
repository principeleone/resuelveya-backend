package com.resuelveya.resuelve_api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

    @GetMapping("/")
    @ResponseBody
    public String home() {
        return "<html><body><h1>✅ Backend conectado correctamente</h1></body></html>";
    }
}
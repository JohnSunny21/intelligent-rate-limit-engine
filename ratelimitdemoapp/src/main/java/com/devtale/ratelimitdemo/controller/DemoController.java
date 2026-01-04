package com.devtale.ratelimitdemo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    @GetMapping("/search")
    public String search(){
        return "Search ok";
    }

    @GetMapping("/login")
    public String login(){
        return "Login Ok";
    }

    @GetMapping("/orders")
    public String orders() {
        return "Orders Ok";
    }
}

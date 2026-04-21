package com.dev.gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LandingController {

    @GetMapping("/login-success")
    public String success() {
        return "Login Successful! You are authenticated.";
    }

    @GetMapping("/")
    public String home() {
        return "Welcome! <a href='/api/me'>View Profile</a> | <a href='/logout'>Logout</a>";
    }
}
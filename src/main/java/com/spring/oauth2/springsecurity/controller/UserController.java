package com.spring.oauth2.springsecurity.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping
    public String getAccount(Principal principal) {
        return "Welcome back user : " + principal.getName();
    }
}

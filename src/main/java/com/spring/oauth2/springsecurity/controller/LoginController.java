package com.spring.oauth2.springsecurity.controller;
import com.spring.oauth2.springsecurity.user.UserDto;
import com.spring.oauth2.springsecurity.user.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginController {

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/api/auth/login")
    public String login(){
        return "login2";
    }

    @GetMapping("/403")
    public String forbidden(){
        return "403";
    }

    @GetMapping("/api/auth/register")
    public String register(){
        return "register";
    }

    @PostMapping("/api/auth/register")
    public String register(@ModelAttribute(name = "user") UserDto user){
        userService.registerAccount(user);
        return "redirect:login2";
    }
}

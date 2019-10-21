package ru.itis.chat.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.itis.chat.dto.UserDto;
import ru.itis.chat.services.UsersService;

@Controller
public class UsersController {
    @Autowired
    private UsersService usersService;

    @PostMapping("/register")
    @ResponseBody
    public UserDto registerUser(@RequestParam("name") String name) {
        return usersService.registerUserByToken(name);
    }

}

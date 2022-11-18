package org.example.controller;

import org.example.annotation.Controller;
import org.example.annotation.Inject;
import org.example.service.UserService;

@Controller
public class UserController {
    private final UserService userService;

    @Inject
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // userService 를 조회한다. Inject 가 잘되어있으면 Null 이 아닐 것이다.
    public UserService getUserService() {
        return userService;
    }
}

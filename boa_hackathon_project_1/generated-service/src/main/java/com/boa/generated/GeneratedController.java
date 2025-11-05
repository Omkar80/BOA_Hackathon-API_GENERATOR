package com.boa.generated;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;

@RestController
@RequestMapping("/api")
public class GeneratedController {

    @Autowired
    private GeneratedBusinessService business;

    @PostMapping("/createUser")
    public UserDto createUser(@RequestParam("username") String username, @RequestParam("age") Integer age) {
        return business.createUser(username, age);
    }

    @GetMapping("/getUser")
    public UserDto getUser(@RequestParam("id") Long id) {
        return business.getUser(id);
    }

}

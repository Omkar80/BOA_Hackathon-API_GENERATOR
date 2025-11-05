package com.boa.generated;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;

@RestController
@RequestMapping("/api")
public class GeneratedController {

    @Autowired
    private GeneratedBusinessService business;

    @PostMapping("/createPayout")
    public UserDto createPayout(@RequestParam("username") String username, @RequestParam("age") Integer age) {
        return business.createPayout(username, age);
    }

    @GetMapping("/getPayout")
    public UserDto getPayout(@RequestParam("id") Long id) {
        return business.getPayout(id);
    }

}

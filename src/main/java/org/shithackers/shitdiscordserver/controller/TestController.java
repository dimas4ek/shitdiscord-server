package org.shithackers.shitdiscordserver.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tests")
public class TestController {
    @GetMapping(path = {"/test", "/tests/test"})
    public String test() {
        return "test";
    }
    
    @GetMapping("/test2")
    public String test2() {
        return "test2";
    }
}

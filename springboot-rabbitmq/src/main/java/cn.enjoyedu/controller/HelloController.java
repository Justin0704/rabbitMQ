package cn.enjoyedu.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class HelloController {

    @RequestMapping("/hello")
    public String sayHello(){
        return "hello";
    }
}

package com.cxh.tool.Controller;


import com.cxh.tool.Service.Json2Class;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class TestController {

    @Autowired
    Json2Class JC;

    @GetMapping("/test")
    public String test(@RequestParam String name){
        return "name:"+name;
    }

    @PostMapping("/test_json2class")
    public String json2class(@RequestBody String s){
        return JC.scanJson(s);
    }

}

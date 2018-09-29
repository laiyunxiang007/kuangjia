package com.example.kuangjia;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
public class test {

    @RequestMapping("/a")
    @ResponseBody
    public String a(){
        log.error("你好");
        return "nihao";
    }
}


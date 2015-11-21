package org.rmcc.ccc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ViewController {
    @RequestMapping("/")
    public String index() {
        return "redirect:/ui";
    }

    @RequestMapping("/ui/**")
    public String app() {
        return "/index.html";
    }
}
package com.example.simplezakka.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TopController {

    @GetMapping("/top")
    public String topPage() {
        return "top"; // templates/top.html を表示する
    }
}

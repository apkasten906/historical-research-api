package com.historicalrpg.research.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DocumentationController {

    @GetMapping("/")
    public String documentation() {
        return "redirect:/docs";
    }
}

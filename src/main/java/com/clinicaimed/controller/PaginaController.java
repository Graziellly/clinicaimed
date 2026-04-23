package com.clinicaimed.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PaginaController {


    @GetMapping("/site")
    public String index() {
        return "index";
    }
}
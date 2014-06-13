package com.zappos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Created by maxkeene on 6/11/14.
 */
@Controller
public class HomeController {

    @RequestMapping("/")
    public String index(Model model) {
        return "index";
    }


}

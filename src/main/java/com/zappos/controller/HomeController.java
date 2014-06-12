package com.zappos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Created by maxkeene on 6/11/14.
 */
@Controller
public class HomeController {

    @RequestMapping("/")
    @ResponseBody
    public String index() {
        return "TRI-FI HOME PAGE";
    }


}

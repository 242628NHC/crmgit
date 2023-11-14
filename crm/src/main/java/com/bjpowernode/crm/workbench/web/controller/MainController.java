package com.bjpowernode.crm.workbench.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainController {
    /**
     * 跳转到工作台
     * @return
     */
    @RequestMapping("/workbench/main/index")
    public String index(){
        //跳转到main/index
        return "workbench/main/index";

    }
}

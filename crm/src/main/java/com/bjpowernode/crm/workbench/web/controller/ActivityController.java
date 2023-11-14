package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class ActivityController {
    @Autowired
    private UserService userService;
    @RequestMapping("/workbench/activity/index")
    public String index(HttpServletRequest request){
        //调用service层方法，查询所有在职用户
        List<User> userList=userService.queryAllUsers();
        request.setAttribute("userList",userList);
        return "workbench/activity/index";
    }
}

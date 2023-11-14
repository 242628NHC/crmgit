package com.bjpowernode.crm.settings.service;

import com.bjpowernode.crm.settings.domain.User;

import java.util.List;
import java.util.Map;

public interface UserService {
    //查询用户
    User queryUserByLoginActAndPwd(Map<String,Object> map);
    //查询所有在职用户
    List<User> queryAllUsers();
}

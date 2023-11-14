package com.bjpowernode.crm.settings.web.controller;

import com.bjpowernode.crm.commons.contants.Contants;
import com.bjpowernode.crm.commons.domain.ReturnObject;
import com.bjpowernode.crm.commons.utils.DataUtils;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.jstl.core.Config;
import javax.xml.crypto.Data;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class UserController {
    //注入service层对象
    @Autowired
    private UserService userService;

    /**
     *url要和controller方法处理完请求之后，响应信息返回的页面的资源目录保持一致
     */
    @RequestMapping("/settings/qx/user/toLogin")
    public String toLogin(){
        //请求转发到登录页面
        return "settings/qx/user/login";
    }

    /**
     * 登录
     * @param loginAct 账号
     * @param loginPwd 密码
     * @param idRemPwd 是否十天免登录
     * @param request 请求
     * @param session 会话
     * @param response 响应
     * @return
     */
    @RequestMapping("/settings/qx/user/login")
    @ResponseBody
    public Object login(String loginAct, String loginPwd, String idRemPwd, HttpServletRequest request, HttpSession session, HttpServletResponse response){//使用Object的原因，前端AJAX请求，返回JSON字符串，可以转成相应的格式
        Map<String,Object> map=new HashMap<>();
        map.put("loginAct",loginAct);
        map.put("loginPwd",loginPwd);
        //调用service层方法，查询用户
        User user = userService.queryUserByLoginActAndPwd(map);
        //根据查询结果，返回响应信息
        ReturnObject returnObject=new ReturnObject();
        //判断是否符号登录要求
        //判断：账户，密码，URL，
        if(user==null){
            //登录失败，用户名，或密码错误
            returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("用户名或密码错误");
        }else {//进一步判断账号是否合法
            String nowStr= DataUtils.formateDateTime(new Date());//获取当前时间
            if (nowStr.compareTo(user.getEditTime())>0) {//compareTo()是一个比较函数，它会返回一个整数值来表示两个时间的顺序关系
                //登录失败，账号已过期
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("账号已失效");
            }else if ("0".equals(user.getLockState())){
                //登录失败，状态被锁定
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("账号已封禁");
            }else if (!user.getAllowIps().contains(request.getRemoteAddr())){
                //登录失败，ip受限
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("当前ip地址有风险");
            }else {
                //把user保存到session中
                session.setAttribute(Contants.SESSION_USER,user);

                //登录成功
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_SUCCESS);
                returnObject.setMessage("欢迎");

                //判断是否10天免登录
                if ("true".equals(idRemPwd)){
                    //创建两个Cookie分别保存账号和密码
                    Cookie c1=new Cookie("loginAct",loginAct);
                    Cookie c2=new Cookie("loginPwd",loginPwd);
                    //设置Cookie存在的事件
                    c1.setMaxAge(60*60*24*10);
                    c2.setMaxAge(60*60*24*10);
                    //将Cookie响应给浏览器
                    response.addCookie(c1);
                    response.addCookie(c2);
                }else {
                    //把没有过期的cookie删除
                    //没有办法在服务器删除在客户端的数据，只能采取覆盖的方式删除
                    //创建两个Cookie分别保存账号和密码
                    Cookie c1=new Cookie("loginAct","1");
                    Cookie c2=new Cookie("loginPwd","1");
                    //设置Cookie存在的事件
                    c1.setMaxAge(0);
                    c2.setMaxAge(0);
                    //将Cookie响应给浏览器
                    response.addCookie(c1);
                    response.addCookie(c2);
                }
            }
        }
        return returnObject;
    }

    /**
     * 安全退出
     * @param response 响应
     * @param session 会话
     * @return
     */
    @RequestMapping("/settings/qx/user/logout")
    public String logout(HttpServletResponse response,HttpSession session){
        //删除cookie
        Cookie c1=new Cookie("loginAct","1");
        Cookie c2=new Cookie("loginPwd","1");
        c1.setMaxAge(0);
        c2.setMaxAge(0);
        response.addCookie(c1);
        response.addCookie(c2);
        //销毁session
        session.invalidate();
        //跳转到首页,重定向跳转
        return "redirect:/";
    }
}

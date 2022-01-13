package com.yjxxt.crm.controller;

import com.yjxxt.crm.base.BaseController;
import com.yjxxt.crm.bean.User;
import com.yjxxt.crm.service.PermissionService;
import com.yjxxt.crm.service.UserService;
import com.yjxxt.crm.utils.LoginUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class IndexController extends BaseController {
    @Autowired
    private UserService userService;
    @Autowired
    private PermissionService permissionService;


    @RequestMapping("index")
    public String index(){
        return "index";
    }

    @RequestMapping("welcome")
    public String welcome(){
        return "welcome";
    }

    @RequestMapping("main")
    public String main(HttpServletRequest request){
        //通过工具类获取userId
        int userid = LoginUserUtil.releaseUserIdFromCookie(request);
        //调用service层的方法，通过id查对象
        User user =(User)userService.selectByPrimaryKey(userid);
        //将用户设置到request作用域中
        request.setAttribute("user",user);

        //将用户权限的acl_value存到cookie中
        List<String> permissions=permissionService.queryUserHasRolesHasPermissions(userid);
        request.getSession().setAttribute("permissions",permissions);
        return "main";
    }
}

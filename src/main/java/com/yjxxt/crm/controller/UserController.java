package com.yjxxt.crm.controller;

import com.yjxxt.crm.base.BaseController;
import com.yjxxt.crm.base.ResultInfo;
import com.yjxxt.crm.bean.SaleChance;
import com.yjxxt.crm.bean.User;
import com.yjxxt.crm.exceptions.ParamsException;
import com.yjxxt.crm.model.UserModel;
import com.yjxxt.crm.query.UserQuery;
import com.yjxxt.crm.service.UserService;
import com.yjxxt.crm.utils.LoginUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController extends BaseController {
    @Autowired
    private UserService userService;


    @ResponseBody
    @PostMapping("login")
    public ResultInfo userLogin(String userName,String userPwd){
        ResultInfo resultInfo=new ResultInfo();
       // try{
            //调用service层得到返回的用户对象
            UserModel userModel = userService.userLogin(userName, userPwd);
            /**
             * 登录成功后，有两种处理：
             * 1. 将用户的登录信息存入 Session （ 问题：重启服务器，Session 失效，客户端需要重复登录 ）
             * 2. 将用户信息返回给客户端，由客户端（Cookie）保存
             */

            // 将返回的UserModel对象设置到 ResultInfo 对象中
            resultInfo.setResult(userModel);

       /* }
        catch (ParamsException e){
            //自定义异常
            e.printStackTrace();
            resultInfo.setCode(e.getCode());
            resultInfo.setMsg(e.getMsg());
        }
        catch (Exception e){
            e.printStackTrace();
            resultInfo.setCode(500);
            resultInfo.setMsg("操作失败");
        }*/
        return resultInfo;
    }


    @ResponseBody
    @PostMapping("update")
    public ResultInfo updateUserPassWord(HttpServletRequest request,String oldPassWord,String newPassWord,String confirmPassword){
            ResultInfo resultInfo=new ResultInfo();
            //try{
                //捕获uid
                int uid = LoginUserUtil.releaseUserIdFromCookie(request);
                //调用service修改密码
               userService.updateUserPassword(uid,oldPassWord,newPassWord,confirmPassword);
           /*  }catch (ParamsException e){
                e.printStackTrace();
                resultInfo.setCode(e.getCode());
                resultInfo.setMsg(e.getMsg());
            }catch (Exception e){
                e.printStackTrace();
                resultInfo.setCode(300);
                resultInfo.setMsg(e.getLocalizedMessage());
            }
*/
            return resultInfo;
    }



    @RequestMapping("toPasswordPage")
    public String toPasswordPage(){
        return "user/password";
    }


    //修改基本资料
    @RequestMapping("/toSettingPage")
    public  String setting(HttpServletRequest req){
        //获取id
        int uid = LoginUserUtil.releaseUserIdFromCookie(req);
        //调用方法
        User user = (User)userService.selectByPrimaryKey(uid);
        //存储
        req.setAttribute("user",user);
        return "user/setting";
    }

    @RequestMapping("setting")
    @ResponseBody
    public ResultInfo sayUpdate(User user){
        ResultInfo resultInfo=new ResultInfo();
        userService.updateByPrimaryKeySelective(user);
        return resultInfo;
    }


    //查询所有的销售人员
    @RequestMapping("sales")
    @ResponseBody
    public List<Map<String,Object>> findSales(){
        return userService.querySales();
    }


    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> queryUserByParams(UserQuery userQuery){
        return userService.queryUserByParams(userQuery);
    }

    @RequestMapping("/index")
    public String index(){
        return "user/user";
    }


    @RequestMapping("save")
    @ResponseBody
    public ResultInfo saveSaleChance(User user){
        userService.saveUser(user);
        return success("添加成功了");
    }

    @RequestMapping("update1")
    @ResponseBody
    public ResultInfo updateUser(User user){
        userService.updateUser(user);
        return success("修改成功了");
    }



    //添加视图转发
    @RequestMapping("addOrUpdateUserPage")
    public String addUserPage(Integer id, Model model){
        if (id!=null){
            model.addAttribute("user",userService.selectByPrimaryKey(id));
        }
        return "user/add_update";
    }


    //删除数据
    @RequestMapping("delete")
    @ResponseBody
    public ResultInfo deleteUser(Integer[] ids){
        userService.deleteUserByIds(ids);
        return success("删除成功了");
    }
}

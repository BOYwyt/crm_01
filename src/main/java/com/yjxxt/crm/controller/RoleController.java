package com.yjxxt.crm.controller;

import com.yjxxt.crm.annotation.RequirePermission;
import com.yjxxt.crm.base.BaseController;
import com.yjxxt.crm.base.ResultInfo;
import com.yjxxt.crm.bean.Role;
import com.yjxxt.crm.query.RoleQuery;
import com.yjxxt.crm.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("role")
public class RoleController extends BaseController {

    @Autowired
    private  RoleService roleService;

    @RequestMapping("queryAllRoles")
    @ResponseBody
    public List<Map<String,Object>> queryAllRoles(Integer userId){
        return roleService.queryAllRoles(userId);
    }


    //授权
    @RequestMapping("toRoleGrantPage")
    public String toRoleGrantPage(Integer roleId,Model model){
        model.addAttribute("roleId",roleId);
        return  "role/grant";
    }


    @RequestMapping("index")
    public String index(){
        return  "role/role";
    }


    @RequestMapping("/list")
    @ResponseBody
    @RequirePermission(code = "60")
    public Map<String,Object> list(RoleQuery roleQuery){
        //System.out.println( roleService.queryByParamsForTable(roleQuery));
        return roleService.queryByParamsForTable(roleQuery);
    }


    //添加
    @RequestMapping("save")
    @ResponseBody
    public ResultInfo save(Role role){
        roleService.addRole(role);
        return success("添加成功");
    }

    //修改
    @RequestMapping("update")
    @ResponseBody
    public ResultInfo update(Role role){
        roleService.changeRole(role);
        return success("修改成功");
    }

    @RequestMapping("toAddOrUpdate")
    public String toAddOrUpdate(Integer id, Model model){
        if(id!=null){
            Role role = roleService.selectByPrimaryKey(id);
            model.addAttribute(role);
        }
        return "role/add_update";
    }



    //删除
    @RequestMapping("delete")
    @ResponseBody
    public ResultInfo deleteRole(Integer id){
        roleService.deleteRole(id);
        return success("角色删除成功");
    }



    //添加角色资源权限
    @RequestMapping("addGrant")
    @ResponseBody
    public ResultInfo addGrant(Integer[] mids,Integer roleId ){
        roleService.addGrant(mids,roleId);
        return success("权限分配成功");
    }
}

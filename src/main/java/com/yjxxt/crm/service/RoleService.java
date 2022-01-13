package com.yjxxt.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yjxxt.crm.base.BaseService;
import com.yjxxt.crm.bean.Permission;
import com.yjxxt.crm.bean.Role;
import com.yjxxt.crm.mapper.ModuleMapper;
import com.yjxxt.crm.mapper.PermissionMapper;
import com.yjxxt.crm.mapper.RoleMapper;
import com.yjxxt.crm.query.RoleQuery;
import com.yjxxt.crm.utils.AssertUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class RoleService extends BaseService<Role,Integer> {

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private PermissionMapper permissionMapper;

    @Resource
    private ModuleMapper moduleMapper;


    //查询所有角色
    public List<Map<String,Object>> queryAllRoles(Integer userId){
        return roleMapper.queryAllRoles(userId);
    }



    //角色条件查询和分页
    public Map<String,Object> queryByParamsForTable(RoleQuery roleQuery){
        //
        Map<String,Object> map=new HashMap<String,Object>();
        //开启分页
        PageHelper.startPage(roleQuery.getPage(),roleQuery.getLimit());
        PageInfo<Role> pageInfo=new PageInfo<>(selectByParams(roleQuery));
        map.put("code",0);
        map.put("msg","success");
        map.put("count",pageInfo.getTotal());
        map.put("data",pageInfo.getList());
        return map;
    }

    //添加角色
    public void addRole(Role role){
        //验证
            //角色非空
        AssertUtil.isTrue(StringUtils.isBlank(role.getRoleName()),"角色名不能为空");
            //角色是否存在
        Role temp=roleMapper.selectRoleByName(role.getRoleName());
        AssertUtil.isTrue(temp!=null,"角色已经存在");
            //设定默认值
        role.setIsValid(1);
        role.setCreateDate(new Date());
        role.setUpdateDate(new Date());
        //判断成功与否
        AssertUtil.isTrue(roleMapper.insertHasKey(role)<1,"添加失败");
    }


    public void changeRole(Role role){
        //验证当前对象是否存在
        Role temp = roleMapper.selectByPrimaryKey(role.getId());
        AssertUtil.isTrue(null==role.getId()||temp==null,"用户不存在");
        //角色名唯一
        Role temp2 = roleMapper.selectRoleByName(role.getRoleName());
        AssertUtil.isTrue(temp2!=null &&(temp2.getId().equals(role.getId())),"");
        //设定默认值
        role.setUpdateDate(new Date());
        //修改是否成功
        AssertUtil.isTrue(updateByPrimaryKeySelective(role)<1,"修改失败");
    }


    //删除角色
    public void deleteRole(Integer roleId){
        Role temp = roleMapper.selectByPrimaryKey(roleId);
        AssertUtil.isTrue(roleId==null || temp==null,"待删除的记录不存在");
        temp.setIsValid(0);
        AssertUtil.isTrue(roleMapper.updateByPrimaryKeySelective(temp)<1,"删除记录失败");
    }


    //添加角色权限资源
    public void addGrant(Integer[] mids,Integer roleId){
        //如果有资源先删除
        // 批量添加权限记录到t_permission
        Role temp = selectByPrimaryKey(roleId);
        AssertUtil.isTrue(temp==null || roleId==null,"没找到需要授权的角色");
        //判断原角色有没有权限
        int count=permissionMapper.countPermissionByRoleId(roleId);
        if (count>0){
            AssertUtil.isTrue(permissionMapper.deletePermissionsByRoleId(roleId)<count,"权限分配失败");
        }
        if (mids!=null && mids.length>0){
            List<Permission> pList=new ArrayList<>();
            for (Integer mid:mids){
                Permission permission=new Permission();
                permission.setCreateDate(new Date());
                permission.setUpdateDate(new Date());
                permission.setModuleId(mid);
                permission.setRoleId(roleId);
                permission.setAclValue(moduleMapper.selectByPrimaryKey(mid).getOptValue());
                pList.add(permission);
            }
            AssertUtil.isTrue(permissionMapper.insertBatch(pList)<1,"权限添加失败");
        }
    }


}








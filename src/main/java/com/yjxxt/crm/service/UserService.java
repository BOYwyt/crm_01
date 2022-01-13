package com.yjxxt.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yjxxt.crm.base.BaseService;
import com.yjxxt.crm.bean.User;
import com.yjxxt.crm.bean.UserRole;
import com.yjxxt.crm.mapper.UserMapper;
import com.yjxxt.crm.mapper.UserRoleMapper;
import com.yjxxt.crm.model.UserModel;
import com.yjxxt.crm.query.UserQuery;
import com.yjxxt.crm.utils.AssertUtil;
import com.yjxxt.crm.utils.Md5Util;
import com.yjxxt.crm.utils.PhoneUtil;
import com.yjxxt.crm.utils.UserIDBase64;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.annotation.AfterThrowing;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;


@Service
public class UserService extends BaseService<User,Integer> {

    @Resource
    private  UserMapper userMapper;

    @Resource
    private UserRoleMapper userRoleMapper;

    //登录
     public UserModel userLogin(String userName,String userPwd){


        //1.验证参数
       checkLoginParams(userName,userPwd);

        //2.根据用户名查询用户对象
        User user = userMapper.queryUserByUserName(userName);

        //3.判断用户是否存在
        AssertUtil.isTrue(user==null,"用户不存在");

        //4.用户存在，校验密码，密码不正确，方法结束
        checkLoginPwd(user.getUserPwd(),userPwd);

        //5.密码正确
        return buildUserInfo(user);
    }

    public void checkLoginParams(String userName,String userPwd){
        AssertUtil.isTrue(StringUtils.isBlank(userName),"用户名不能为空");
        AssertUtil.isTrue(StringUtils.isBlank(userPwd),"密码不能为空");
    }

    public void checkLoginPwd(String upwd,String userPwd){
        //对密码进行加密，在比较
        userPwd= Md5Util.encode(userPwd);
        AssertUtil.isTrue(!userPwd.equals(upwd),"密码不正确");
    }

    public UserModel buildUserInfo(User user){
        UserModel userModel=new UserModel();
        //设置用户信息
        userModel.setUserIdStr(UserIDBase64.encoderUserID(user.getId()));
        userModel.setUserName(user.getUserName());
        userModel.setTrueName(user.getTrueName());
        return userModel;
    }


    //修改密码
    public void updateUserPassword(Integer uid,String oldPassWord,String newPassWord,String confirmPassword){
         //通过uid获取用户信息
        User user = userMapper.selectByPrimaryKey(uid);
        System.out.println(user);
        //参数校验
        checkPasswordParams(user,oldPassWord,newPassWord,confirmPassword);
        //修改密码
        user.setUserPwd(Md5Util.encode(newPassWord));
        //密码是否修改成功
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user)<1,"修改失败");
    }

    public void checkPasswordParams(User user,String oldPassWord,String newPassWord,String confirmPassword){
        //判断用户是否登录
        AssertUtil.isTrue(user==null,"未登录");
        //判断原始密码是否为空
        AssertUtil.isTrue(StringUtils.isBlank(oldPassWord),"请输入密码");
        //判断新密码与加密的原始密码是否一致
        AssertUtil.isTrue(Md5Util.encode(newPassWord).equals(user.getUserPwd()),"新密码与原密码一致");
        //判断新密码是否为空
        AssertUtil.isTrue(StringUtils.isBlank(newPassWord),"新密码为空");
        //确认密码是否为空
        AssertUtil.isTrue(StringUtils.isBlank(confirmPassword),"确认密码为空");
        //判断新密码与确认密码是否一致
        AssertUtil.isTrue(!newPassWord.equals(confirmPassword),"新密码与确认密码不一致");
    }

    //查询所有的销售
    public List<Map<String,Object>> querySales(){
          return   userMapper.selectSales();
    }

    //分页查询
    public Map<String,Object> queryUserByParams(UserQuery userQuery){
         Map<String,Object> map=new HashMap<>();
         //开启分页
        PageHelper.startPage(userQuery.getPage(),userQuery.getLimit());
        PageInfo<User> pageInfo=new PageInfo<>(userMapper.selectByParams(userQuery));
        map.put("code",0);
        map.put("msg","");
        map.put("count",pageInfo.getTotal());
        map.put("data",pageInfo.getList());
        return map;
    }

    //添加用户
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveUser(User user){
         //一、验证
        checkParams(user.getUserName(),user.getEmail(),user.getPhone());
         //验证用户名是否唯一，非空
        //验证邮箱是否为空
        //验证电话是否为空，且格式正确

        //二、设定默认值 isValid createDate updateDate
        user.setIsValid(1);
        user.setCreateDate(new Date());
        user.setUpdateDate(new Date());
        //三、密码加密
        user.setUserPwd(Md5Util.encode("123456"));
        //四、判断是否成功
        AssertUtil.isTrue(userMapper.insertSelective(user)<1,"添加失败");
        //AssertUtil.isTrue(userMapper.insertHasKey(user)<1,"添加失败");

        relaionUserRole(user.getId(),user.getRoleIds());

    }

    /**
     * 对中间表进行关联
     * @param userId  用户id
     * @param roleIds   角色id
     *
     * 如果用户原始角色存在，先删除原始角色，后再重新添加新的角色
     */

    private void relaionUserRole(Integer userId, String roleIds) {
        //原始用户是否有角色id
        int count=userRoleMapper.countUserRoleByUserId(userId);
        //判断如果有，删除
        if(count>0){
            AssertUtil.isTrue(userRoleMapper.deleteUserRoleByUserId(userId)!=count,"用户角色分配失败");
        }
        if (StringUtils.isNotBlank(roleIds)){
            List<UserRole> userRoles=new ArrayList<>();
            for(String s:roleIds.split(",")){
                //new 应该新的UserRole
                UserRole userRole=new UserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(Integer.parseInt(s));
                userRole.setCreateDate(new Date());
                userRole.setUpdateDate(new Date());
                //System.out.println(userRole);
                //把list放入集合
                userRoles.add(userRole);
            }
            AssertUtil.isTrue(userRoleMapper.insertBatch(userRoles)<userRoles.size(),"用户角色分配失败");
        }
    }


    //修改用户信息
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateUser(User user){
      //一、验证
        // 用户id是否存在,通过id查出名字
        User temp = userMapper.selectByPrimaryKey(user.getId());

        if (user.getId()==null){
            //添加操作
            AssertUtil.isTrue(temp!=null,"用户名重名，请重新输入");
            checkParams(user.getUserName(),user.getEmail(),user.getPhone());
        }else {
            // 更新操作 (数据存在，且id不是当前数据本身)
            AssertUtil.isTrue(temp!=null && !temp.getId().equals(user.getId()),"用户名已经被使用");
        }
        //验证用户名是否唯一，非空
        //验证邮箱是否为空
        //验证电话是否为空，且格式正确
      //修改信息
        //设置修改时间
        temp.setUpdateDate(new Date());
      //判断是否成功
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user)<1,"更新失败");
        Integer userId = userMapper.queryUserByUserName(user.getUserName()).getId();
        relaionUserRole(userId,user.getRoleIds());
    }


    public void checkParams(String name,String email,String phone){
        User temp = userMapper.queryUserByUserName(name);
        AssertUtil.isTrue(temp!=null,"用户已经存在");
        AssertUtil.isTrue(StringUtils.isBlank(name),"用户名为空");
        AssertUtil.isTrue(StringUtils.isBlank(email),"邮箱不能为空");
        AssertUtil.isTrue(StringUtils.isBlank(phone),"电话不能为空");
        AssertUtil.isTrue(!PhoneUtil.isMobile(phone),"电话格式不正确");

    }



    /*删除用户*/
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteUserByIds(Integer[] ids) {
        //请选择要删除的数据
        AssertUtil.isTrue(ids==null||ids.length==0,"请选择要删除的数据");
        //原始用户是否有角色id
        for (Integer userId:ids) {  //判断如果有，删除
            int count=userRoleMapper.countUserRoleByUserId(userId);
            if(count>0){
                System.out.println("------"+userId+"---"+count);
                AssertUtil.isTrue(userRoleMapper.deleteUserRoleByUserId(userId)!=count,"用户角色分配失败");
            }
            
        }

        //判断是否删除成功
        AssertUtil.isTrue(userMapper.deleteBatch(ids)<1,"删除失败");
    }

}

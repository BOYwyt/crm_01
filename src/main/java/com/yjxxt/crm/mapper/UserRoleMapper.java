package com.yjxxt.crm.mapper;

import com.yjxxt.crm.base.BaseMapper;
import com.yjxxt.crm.bean.UserRole;

public interface UserRoleMapper extends BaseMapper<UserRole,Integer> {

    int deleteUserRoleByUserId(Integer userId);

    int countUserRoleByUserId(Integer userId);
}
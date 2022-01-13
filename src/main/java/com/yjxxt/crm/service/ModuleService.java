package com.yjxxt.crm.service;

import com.yjxxt.crm.base.BaseService;
import com.yjxxt.crm.bean.Module;
import com.yjxxt.crm.dto.TreeDto;
import com.yjxxt.crm.mapper.ModuleMapper;
import com.yjxxt.crm.mapper.PermissionMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class ModuleService extends BaseService<Module,Integer> {

    @Resource
    private ModuleMapper moduleMapper;
    @Resource
    private PermissionMapper permissionMapper;



    public List<TreeDto> queryAllModules(){
        return moduleMapper.queryAllModules();
    }



    public List<TreeDto> queryAllModules02(Integer roleId){
        //所有的权限资源
        List<TreeDto> tlist=moduleMapper.queryAllModules();
        //根据角色id获取角色所拥有的权限
        List<Integer> roleHasMids=permissionMapper.queryRoleHasAllModuleIdsByRoleId(roleId);
        //遍历
        for (TreeDto treeDto:tlist){
            if (roleHasMids.contains(treeDto.getId())){
                treeDto.setChecked(true);
            }
        }
        return tlist;
    }
    public Map<String, Object> queryModules() {
        //准备数据
        Map<String,Object> map=new HashMap<String,Object>();
        //查询所有的资源
        List<Module> mlist=moduleMapper.selectAllModules();
        //准备数据项
        map.put("code",0);
        map.put("msg","success");
        map.put("count",mlist.size());
        map.put("data",mlist);
        //返回目标map
        return map;
    }


}

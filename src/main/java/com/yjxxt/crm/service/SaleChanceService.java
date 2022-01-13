package com.yjxxt.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yjxxt.crm.base.BaseService;
import com.yjxxt.crm.bean.SaleChance;
import com.yjxxt.crm.mapper.SaleChanceMapper;
import com.yjxxt.crm.query.SaleChanceQuery;
import com.yjxxt.crm.utils.AssertUtil;
import com.yjxxt.crm.utils.PhoneUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.beans.Transient;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SaleChanceService extends BaseService<SaleChance,Integer> {

    @Resource
    private SaleChanceMapper saleChanceMapper;

    public Map<String, Object> querySaleChanceByParams (SaleChanceQuery query) {
        Map<String,Object> map = new HashMap<String,Object>();
        //实例化分页单位
        PageHelper.startPage(query.getPage(), query.getLimit());
        //开始分页
        List<SaleChance> alist = saleChanceMapper.selectByParams(query);
        //System.out.println(alist.size());
        PageInfo<SaleChance> pageInfo =new PageInfo<SaleChance>(alist);
       // System.out.println(pageInfo);

        System.out.println(pageInfo);
        map.put("code",0);
        map.put("msg", "");
        map.put("count", pageInfo.getTotal());
        map.put("data", pageInfo.getList());
        System.out.println(
                map
        );
        return map;
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public void saveSaleChance(SaleChance saleChance){
        //验证
        checkSaleChancePrams(saleChance.getCustomerName(),saleChance.getLinkMan(),saleChance.getLinkPhone());
        //未分配
        if(StringUtils.isNotBlank(saleChance.getAssignMan())){
            //设定默认值（0-未开发 1-开发中 2-开发成功 3-开发失败）
            saleChance.setState(0);
            saleChance.setDevResult(0);
        }
        //已分配
        if (StringUtils.isBlank(saleChance.getAssignMan())){
            saleChance.setState(1);
            saleChance.setDevResult(1);
            saleChance.setAssignTime(new Date());
        }
        //分配时间
        saleChance.setCreateDate(new Date());
        saleChance.setUpdateDate(new Date());
        //判断是否成功
        AssertUtil.isTrue(insertSelective(saleChance)<1,"添加失败了");
    }

    public void checkSaleChancePrams(String customerName,String linkMan,String phone){
        //客户名非空
        AssertUtil.isTrue(StringUtils.isBlank(customerName),"客户名不能为空");
        //联系人非空
        AssertUtil.isTrue(StringUtils.isBlank(linkMan),"联系人不能为空");
        //联系电话非空
        AssertUtil.isTrue(StringUtils.isBlank(phone),"请输入联系方式");
        AssertUtil.isTrue(!PhoneUtil.isMobile(phone),"请输入正确联系方式");
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public void updateSaleChance(SaleChance saleChance){
        //1.参数校验，根据id查记录
        SaleChance temp = saleChanceMapper.selectByPrimaryKey(saleChance.getId());
        //判断是否为空
        AssertUtil.isTrue(temp==null,"修改数据为空");
        checkSaleChancePrams(saleChance.getCustomerName(),saleChance.getLinkMan(),saleChance.getLinkPhone());
        //2.设置相关参数
        //原为 未分配,修改后为已分配
        if (StringUtils.isBlank(temp.getAssignMan())&&StringUtils.isNotBlank(saleChance.getAssignMan())){
            saleChance.setState(1);
            saleChance.setDevResult(1);
            saleChance.setAssignTime(new Date());
        }
        // 如果原始记录已分配，修改后改为未分配
        if (StringUtils.isNotBlank(temp.getAssignMan())&&StringUtils.isBlank(saleChance.getAssignMan())){
            saleChance.setState(0);
            saleChance.setDevResult(0);
            saleChance.setAssignTime(null);
            saleChance.setAssignMan("");
        }
        //设置默认值
        saleChance.setUpdateDate(new Date());
        //判断是否成功
        AssertUtil.isTrue(updateByPrimaryKeySelective(saleChance)<1,"修改失败");
    }



    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteSaleChance(Integer[] ids){
        //判断要删除的id是否为空
        AssertUtil.isTrue(ids==null||ids.length==0,"请选择删除的数据");
        //删除数据
        AssertUtil.isTrue(saleChanceMapper.deleteBatch(ids) < 0, "营销机会数据删除失败！");
    }
}

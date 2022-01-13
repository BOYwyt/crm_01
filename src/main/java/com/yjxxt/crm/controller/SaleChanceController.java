package com.yjxxt.crm.controller;

import com.yjxxt.crm.base.BaseController;
import com.yjxxt.crm.base.ResultInfo;
import com.yjxxt.crm.bean.SaleChance;
import com.yjxxt.crm.query.SaleChanceQuery;
import com.yjxxt.crm.service.SaleChanceService;
import com.yjxxt.crm.service.UserService;
import com.yjxxt.crm.utils.LoginUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("sale_chance")
public class SaleChanceController extends BaseController {

    @Autowired
    private SaleChanceService saleChanceService;
    @Autowired
    private UserService userService;

    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> querySaleChanceByParams(SaleChanceQuery saleChanceQuery){
        return saleChanceService.querySaleChanceByParams(saleChanceQuery);

    }

    @RequestMapping("index")
    public String index(){
        return "saleChance/sale_chance";
    }


    @RequestMapping("addOrUpdateDialog")
    public String addOrUpdate(){return "saleChance/add_update";}




    @RequestMapping("save")
    @ResponseBody
    public ResultInfo saveSaleChance(HttpServletRequest request, SaleChance saleChance){
        //获取id
        int uid = LoginUserUtil.releaseUserIdFromCookie(request);
        String trueName = userService.selectByPrimaryKey(uid).getTrueName();
        //创建人
        saleChance.setCreateMan(trueName);
        //添加操作
        saleChanceService.insertSelective(saleChance);
        return success("添加成功了");
    }

    @RequestMapping("update")
    @ResponseBody
    public ResultInfo updateSaleChance(SaleChance saleChance){
        //修改操作
        saleChanceService.updateSaleChance(saleChance);
        return success("修改成功");
    }

    @RequestMapping("/dels")
    @ResponseBody
    public ResultInfo deleteSaleChance(Integer[] ids){
        //删除营销机会的数据
        saleChanceService.deleteBatch(ids);
        return success("删除成功");
    }


}

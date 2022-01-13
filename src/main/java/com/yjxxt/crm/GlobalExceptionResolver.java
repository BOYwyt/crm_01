package com.yjxxt.crm;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.yjxxt.crm.base.ResultInfo;
import com.yjxxt.crm.exceptions.NoLoginException;
import com.yjxxt.crm.exceptions.ParamsException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@Component
public class GlobalExceptionResolver implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest req, HttpServletResponse resp, Object handler, Exception ex) {
        //拦截未登录异常
        if (ex instanceof NoLoginException){
            //捕捉未登录异常，重定向登录页面
            ModelAndView modelAndView=new ModelAndView("redirect:/index");
            return modelAndView;
        }
        
        ModelAndView mv=new ModelAndView();
       //设置默认异常
        mv.setViewName("error");
        mv.addObject("code",400);
        mv.addObject("msg","系统异常，请稍后再试");

        // 判断 HandlerMethod
        if (handler instanceof HandlerMethod){
            //类型转换
            HandlerMethod handlerMethod=(HandlerMethod) handler;
            //获取方法上的 ResponseBody 注解
            ResponseBody responseBody =handlerMethod.getMethod().getDeclaredAnnotation(ResponseBody.class);
            // 判断 ResponseBody 注解是否存在 (如果不存在，表示返回的是视图;如果存在，表示返回的是JSON)
            if (null == responseBody) {
                //方法返回视图
                if (ex instanceof ParamsException){
                    ParamsException pe=(ParamsException) ex;
                    mv.addObject("code",pe.getCode());
                    mv.addObject("msg",pe.getMsg());
                 }
                return mv;
            }else{
                ResultInfo resultInfo=new ResultInfo();
                resultInfo.setCode(300);
                resultInfo.setMsg("系统异常，请重试");
                //如果捕获的是自定义异常
                if(ex instanceof ParamsException){
                    ParamsException pe=(ParamsException) ex;
                    resultInfo.setCode(pe.getCode());
                    resultInfo.setMsg(pe.getMsg());
                }
                // 设置响应类型和编码格式 （响应JSON格式）
                resp.setContentType("application/json;charset=utf-8");
                // 得到输出流
                PrintWriter out = null;
                try{
                    out = resp.getWriter();
                    // 将对象转换成JSON格式，通过输出流输出 响应给请求的前台
                    out.write(JSON.toJSONString(resultInfo));
                    out.flush();
                }catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    if (out!=null){
                        out.close();
                    }
                }
                return null;
            }
        }

        return mv;
    }
}

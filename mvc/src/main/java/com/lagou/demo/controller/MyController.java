package com.lagou.demo.controller;

import com.lagou.edu.mvcframework.annotations.LagouController;
import com.lagou.edu.mvcframework.annotations.LagouRequestMapping;
import com.lagou.edu.mvcframework.annotations.Security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@LagouController
@LagouRequestMapping("/work")
@Security({"zhangsan","lisi","wangwu"})
public class MyController {

    @LagouRequestMapping("/handler01")
    @Security({"zhangsan"})
    public void handler01(HttpServletRequest request, HttpServletResponse response,String username){
        System.out.println("我是handler01，只允许zhangsan访问");
    }

    @LagouRequestMapping("/handler02")
    @Security({"lisi"})
    public void handler02(HttpServletRequest request, HttpServletResponse response,String username){
        System.out.println("我是handler02，只允许lisi访问");
    }

    @LagouRequestMapping("/handler03")
    @Security({"zhangsan","lisi"})
    public void handler03(HttpServletRequest request, HttpServletResponse response,String username){
        System.out.println("我是handler03，允许zhangsan,lisi访问");
    }

}

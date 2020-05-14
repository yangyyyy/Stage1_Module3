package com.lagou.edu.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.lagou.edu.pojo.Resume;
import com.lagou.edu.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/resume")
public class ResumeController {

    @Autowired
    private ResumeService resumeService;

    @RequestMapping("/login")
    public String login(String username, String password){
        if("admin".equals(username) && "admin".equals(password)){

            return "list";
        }
        return "index";
    }

    @RequestMapping("/queryAll")
    @ResponseBody
    public List<Resume> queryAll(HttpServletResponse response) throws IOException {
        List<Resume> resumeList = resumeService.queryAll();

        return resumeList;
    }

    @RequestMapping("/getResumeById")
    public ModelAndView getResumeById(Long id){
        Resume resume = resumeService.getResumeById(id);
        Map model=new HashMap();
        model.put("resume", resume);
        ModelAndView modelAndView = new ModelAndView("edit",model);
        return modelAndView;
    }

    @RequestMapping("/addOrUpdateResume")
    public ModelAndView addOrUpdateResume(Resume resume){
        Resume newResume = resumeService.addOrUpdateResume(resume);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("list");
        modelAndView.addObject(newResume);
        return modelAndView;
    }

    @RequestMapping("/deleteResume")
    public String deleteResume(Long id){
        resumeService.deleteResume(id);
        return "list";
    }

    @RequestMapping("/add")
    public String add(){

        return "add";
    }
}

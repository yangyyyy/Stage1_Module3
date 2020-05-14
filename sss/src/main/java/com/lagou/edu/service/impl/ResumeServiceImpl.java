package com.lagou.edu.service.impl;

import com.lagou.edu.dao.ResumeDao;
import com.lagou.edu.pojo.Resume;
import com.lagou.edu.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ResumeServiceImpl implements ResumeService {

    @Autowired
    private ResumeDao resumeDao;


    @Override
    public List<Resume> queryAll() {
        return resumeDao.findAll();
    }

    @Override
    public Resume getResumeById(Long id) {
        return resumeDao.findById(id).get();
    }

    @Override
    public Resume addOrUpdateResume(Resume resume) {

        return resumeDao.saveAndFlush(resume);
    }

    @Override
    public void deleteResume(Long id) {
        resumeDao.deleteById(id);
    }
}

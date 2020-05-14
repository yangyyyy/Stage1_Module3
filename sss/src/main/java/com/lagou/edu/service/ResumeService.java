package com.lagou.edu.service;

import com.lagou.edu.pojo.Resume;

import java.util.List;

public interface ResumeService {

    /**
     * 查询所有简历
     * @return
     */
    List<Resume> queryAll();

    /**
     * 根据ID查询
     * @param id
     * @return
     */
    Resume getResumeById(Long id);

    /**
     * 新增/更新简历
     * @param resume
     * @return
     */
    Resume addOrUpdateResume(Resume resume);

    /**
     * 删除简历
     * @param id
     */
    void deleteResume(Long id);

}

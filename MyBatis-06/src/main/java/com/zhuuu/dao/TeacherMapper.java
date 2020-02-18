package com.zhuuu.dao;

import com.zhuuu.pojo.Teacher;

public interface TeacherMapper {
    //获取指定老师，及老师下的所有学生
    public Teacher getTeacher(int id);
}

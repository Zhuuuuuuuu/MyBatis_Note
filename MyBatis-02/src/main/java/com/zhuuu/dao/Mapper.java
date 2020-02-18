package com.zhuuu.dao;


import com.zhuuu.pojo.User;

import java.util.List;

public interface Mapper {

    //查询全部用户
    List<User> getUserList();

    //根据ID查询用户
    User getUserbyID(int id);

    //insert一个用户
    int addUser(User user);

    //修改一个用户
    int updateUser(User user);

    //根据id删除用户
    int deleteUser(int id);

    // 模糊查询
    List<User> getLikeUser(String value);
}

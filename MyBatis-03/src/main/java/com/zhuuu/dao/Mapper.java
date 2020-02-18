package com.zhuuu.dao;

import com.zhuuu.pojo.User;

public interface Mapper {

    //根据ID查询用户
    User selectUserById(int id);

}

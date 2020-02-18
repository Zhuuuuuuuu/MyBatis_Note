package com.zhuuu.dao;


import com.zhuuu.pojo.User;

import java.util.List;
import java.util.Map;

public interface Mapper {

    //limit分页
    List<User> getUserList(Map<String,Integer> map);


    //RowBounds分页
    List<User> getUserByRowBounds();
}

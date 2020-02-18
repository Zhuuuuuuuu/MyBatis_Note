package com.zhuuu.dao;

import com.zhuuu.pojo.User;
import com.zhuuu.utils.MybatisUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

public class Mytest {
    @Test
    public void selectUserByID(){
        SqlSession session = MybatisUtils.getSession();

        Mapper mapper = session.getMapper(Mapper.class);

        User user = mapper.selectUserById(1);

        System.out.println(user);

        session.close();
    }
}

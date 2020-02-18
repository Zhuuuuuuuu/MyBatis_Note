package com.zhuuu.dao;

import com.zhuuu.pojo.User;
import com.zhuuu.utils.MybatisUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.util.List;

public class daoTest {
    @Test
    public void test(){

//          获取session对象
        SqlSession session = MybatisUtils.getSession();

//          执行sql
        Mapper mapper = session.getMapper(Mapper.class);
        List<User> userList = mapper.getUserList();

        for (User user : userList) {
            System.out.println(user);
        }


//        关闭sqlsession
        session.close();
    }
    @Test
    public void getUserByID(){
        SqlSession session = MybatisUtils.getSession();

        Mapper mapper = session.getMapper(Mapper.class);
        User userbyID = mapper.getUserbyID(1);
        System.out.println(userbyID);

        session.close();
    }

    @Test
    public void  addUser(){
        SqlSession session = MybatisUtils.getSession();
        Mapper mapper = session.getMapper(Mapper.class);
        mapper.addUser(new User("呵呵",5,"123123"));
        session.commit();//提交事务,重点!不写的话不会提交到数据库
        session.close();
    }


    @Test
    public void  updateUser(){
        SqlSession session = MybatisUtils.getSession();
        Mapper mapper = session.getMapper(Mapper.class);
        mapper.updateUser(new User("哈哈",4,"123123"));
        session.commit();//提交事务,重点!不写的话不会提交到数据库
        session.close();
    }

    @Test
    public void deleteUser(){
        SqlSession session = MybatisUtils.getSession();
        Mapper mapper = session.getMapper(Mapper.class);
        int i = mapper.deleteUser(5);
        System.out.println(i);
        session.commit(); //提交事务,重点!不写的话不会提交到数据库
        session.close();
    }

    @Test
    public void getLikeUser(){
        SqlSession session = MybatisUtils.getSession();
        Mapper mapper = session.getMapper(Mapper.class);
        System.out.println(mapper.getLikeUser("%李%"));
        session.close();
    }
}

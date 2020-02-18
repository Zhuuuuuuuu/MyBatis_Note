package com.zhuuu.pojo;

public class User {
//    编写实体类

    private String name;

    private int id;

    private String pwd;

    public User(String name, int id, String pwd) {
        this.name = name;
        this.id = id;
        this.pwd = pwd;
    }

    public User() {
    }


    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", pwd='" + pwd + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
}

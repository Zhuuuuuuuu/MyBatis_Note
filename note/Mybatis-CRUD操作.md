---
title: Mybatis CRUD操作
date: 2019-07-09 11:00:35
tags: MyBatis
---

# MyBatis CRUD操作

## 关于namespace

**配置文件中namespace中的名称为对应Mapper接口或者Dao接口的完整包名,必须一致**



## 查询 select

id

- 命名空间中唯一的标识符
- 接口中的**方法名**与映射文件中的SQL语句ID 一一对应



resultType

- SQL语句返回值类型。【完整的类名或者别名】



parameterType

- 传入SQL语句的参数类型 。【Map】



<!--more-->



**需求：根据id查询用户**

1. 在UserMapper中添加对应方法

```java
public interface UserMapper {
    //查询全部用户
    List<User> selectUser();
    //根据id查询用户
    User selectUserById(int id);
}
```

2. 在UserMapper.xml中添加Select语句

```java
<select id="selectUserById" resultType="com.zhuuu.pojo.User">
  select * from user where id = #{id}
</select>
```

3. 测试类中测试

```java
@Test
public void tsetSelectUserById() {
    SqlSession session = MybatisUtils.getSession();  //获取SqlSession连接
    UserMapper mapper = session.getMapper(UserMapper.class);
    User user = mapper.selectUserById(1);
    System.out.println(user);
    session.close();
}
```

思路二：根据 密码 和 名字 查询用户

**使用map来查询或者插入**

1. 在接口方法中，参数直接传递Map；

```xml
User selectUserByNP2(Map<String,Object> map);
```

2. 编写sql语句的时候，需要传递参数类型，参数类型为map

```xml
<select id="selectUserByNP2" parameterType="map" resultType="com.zhuuu.pojo.User">
select * from user where name = #{username} and pwd = #{pwd}
</select>
```

3. 在使用方法的时候，Map的 key 为 sql中取的值即可，没有顺序要求！

```java
Map<String, Object> map = new HashMap<String, Object>();
map.put("username","小明");
map.put("pwd","123456");
User user = mapper.selectUserByNP2(map);
```

总结：

 如果参数过多，我们可以考虑直接使用Map实现，如果参数比较少，直接传递参数即可



## 插入 insert

一般使用insert标签进行插入操作，它的配置和select标签差不多！

1. 在UserMapper接口中添加对应的方法

```java
//添加一个用户
int addUser(User user);
```

2. 在UserMapper.xml中添加insert语句

```xml
<insert id="addUser" parameterType="com.zhuuu.pojo.User">
     insert into user (id,name,pwd) values (#{id},#{name},#{pwd})
</insert>
```

3. 测试

```java
@Test
public void testAddUser() {
    SqlSession session = MybatisUtils.getSession();
    UserMapper mapper = session.getMapper(UserMapper.class);
    User user = new User(5,"王五","zxcvbn");
    int i = mapper.addUser(user);
    System.out.println(i);
    session.commit(); //提交事务,重点!不写的话不会提交到数据库
    session.close();
}
```

**注意点：增、删、改操作需要提交事务！**



## 改 update

**需求：修改用户的信息**

1. 同理，编写接口方法

```java
//修改一个用户
int updateUser(User user);
```

2. 编写对应的配置文件SQL

```xml
<update id="updateUser" parameterType="com.zhuuu.pojo.User">
    update user set name=#{name},pwd=#{pwd} where id = #{id}
</update>
```

3. 测试

```xml
@Test
public void testUpdateUser() {
    SqlSession session = MybatisUtils.getSession();
    UserMapper mapper = session.getMapper(UserMapper.class);
    User user = mapper.selectUserById(1);
    user.setPwd("asdfgh");
    int i = mapper.updateUser(user);
    System.out.println(i);
    session.commit(); //提交事务,重点!不写的话不会提交到数据库
    session.close();
}
```



## 删除 delete

**需求：根据id删除一个用户**

1. 同理，编写接口方法

```java
//根据id删除用户
int deleteUser(int id);
```

2. 编写对应的配置文件SQL

```xml
<delete id="deleteUser" parameterType="int">
    delete from user where id = #{id}
</delete>
```

3. 测试

```java
@Test
public void testDeleteUser() {
    SqlSession session = MybatisUtils.getSession();
    UserMapper mapper = session.getMapper(UserMapper.class);
    int i = mapper.deleteUser(5);
    System.out.println(i);
    session.commit(); //提交事务,重点!不写的话不会提交到数据库
    session.close();
}
```



**小结：**

- 所有的增删改操作都需要提交事务！
- 接口所有的普通参数，尽量都写上@Param参数，尤其是多个参数时，必须写上！
- 有时候根据业务的需求，可以考虑使用map传递参数！
- 为了规范操作，在SQL的配置文件中，我们尽量将Parameter参数和resultType都写上！





## 补充：模糊查询

**模糊查询like语句该怎么写?**

1. 添加java代码

```java
    // 模糊查询
    List<User> getLikeUser(String value);
```

2. 在xml中添加查询

```xml
<select id="getLikeUser" resultType="com.zhuuu.pojo.User">
    select * from mybatis.user where name like #{value}
</select>
```

3. 测试

```java
@Test
public void getLikeUser(){
    SqlSession session = MybatisUtils.getSession();
    Mapper mapper = session.getMapper(Mapper.class);
    System.out.println(mapper.getLikeUser("%李%"));
    session.close();
}
```



**注意：在sql语句中拼接通配符，会引起sql注入**




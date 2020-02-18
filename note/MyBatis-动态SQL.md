---
title: MyBatis-动态SQL
date: 2019-07-17 11:12:22
tags: MyBatis
---

# MyBatis 动态SQL

动态SQL 官方文档：[文档链接](https://mybatis.org/mybatis-3/zh/dynamic-sql.html)

## 介绍

**什么是动态SQL:就是根据不同的查询条件，自动生成不同的Sql语句**

```txt
官网描述：
    MyBatis 的强大特性之一便是它的动态 SQL。如果你有使用 JDBC 或其它类似框架的经验，你就能体会到根据不同条件拼接 SQL 语句的痛苦。例如拼接时要确保不能忘记添加必要的空格，还要注意去掉列表最后一个列名的逗号。利用动态 SQL 这一特性可以彻底摆脱这种痛苦。
    虽然在以前使用动态 SQL 并非一件易事，但正是 MyBatis 提供了可以被用在任意 SQL 映射语句中的强大的动态 SQL 语言得以改进这种情形。
    动态 SQL 元素和 JSTL 或基于类似 XML 的文本处理器相似。在 MyBatis 之前的版本中，有很多元素需要花时间了解。MyBatis 3 大大精简了元素种类，现在只需学习原来一半的元素便可。MyBatis 采用功能强大的基于 OGNL 的表达式来淘汰其它大部分元素。
    
    -------------------------------
    - if
    - choose (when, otherwise)
    - trim (where, set)
    - foreach
    -------------------------------
```

<!--more-->

- 之前写的 SQL 语句都比较简单，如果有比较复杂的业务，我们需要写复杂的 SQL 语句，往往需要拼接，而拼接 SQL ，稍微不注意，由于引号，空格等缺失可能都会导致错误。
- 那么怎么去解决这个问题呢？这就要使用 mybatis 动态SQL，通过 if, choose, when, otherwise, trim, where, set, foreach等标签，可组合成非常灵活的SQL语句，从而在提高 SQL 语句的准确性的同时，也大大提高了开发人员的效率。



## 搭建环境

1. 新建一个数据库表

   字段：id，title，author，create_time，views

```mysql
CREATE TABLE `blog` (
  `id` varchar(50) NOT NULL COMMENT '博客id',
  `title` varchar(100) NOT NULL COMMENT '博客标题',
  `author` varchar(30) NOT NULL COMMENT '博客作者',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `views` int(30) NOT NULL COMMENT '浏览量'
) ENGINE=InnoDB DEFAULT CHARSET=utf8
```

2. 创建MyBatis工程

![](https://zhuuu-bucket.oss-cn-beijing.aliyuncs.com/img/20200217175227.png)

3. 编写IDutil工具类

```java
public class IDUtil {

    public static String genId(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }

}
```

4. 实体类编写

```java
import java.util.Date;

public class Blog {

    private String id;
    private String title;
    private String author;
    private Date createTime;
    private int views;
    //set，get....
}
```

5. 编写Mapper和Mapper.xml

```java
public interface BlogMapper {
}
```

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.zhuuu.dao.BlogMapper">

</mapper>
```

6. 在MyBatis核心配置文件中，下划线驼峰自动转换

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>

    <properties resource="db.properties"/>

    <settings>
        <setting name="mapUnderscoreToCamelCase" value="true"/>
        <setting name="logImpl" value="STDOUT_LOGGING"/>
    </settings>

    <typeAliases>
        <package name="com.zhuuu.pojo"/>
    </typeAliases>

    <environments default="development">
        <environment id="development">
            <transactionManager type="JDBC"/>
            <dataSource type="POOLED">
                <property name="driver" value="${driver}"/>
                <property name="url" value="${url}"/>
                <property name="username" value="${username}"/>
                <property name="password" value="${password}"/>
            </dataSource>
        </environment>
    </environments>

<!--    每一个Mapper,XML都需要在mybatis核心配置文件中注册-->
    <mappers>
        <package name="com.zhuuu.dao"/>
    </mappers>

</configuration>
```

7. 插入初始的数据

  编写一个接口

```java
//新增一个博客
int addBlog(Blog blog);
```

  Mapper配置文件

```xml
<insert id="addBlog" parameterType="blog">
    insert into blog (id, title, author, create_time, views)
    values (#{id},#{title},#{author},#{createTime},#{views});
</insert>
```

初始化博客方法

```java
@Test
public void addInitBlog(){
    SqlSession session = MybatisUtils.getSession();
    BlogMapper mapper = session.getMapper(BlogMapper.class);

    Blog blog = new Blog();
    blog.setId(IDUtil.genId());
    blog.setTitle("Mybatis如此简单");
    blog.setAuthor("Zhuuu");
    blog.setCreateTime(new Date());
    blog.setViews(9999);

    mapper.addBlog(blog);

    blog.setId(IDUtil.genId());
    blog.setTitle("Java如此简单");
    mapper.addBlog(blog);

    blog.setId(IDUtil.genId());
    blog.setTitle("Spring如此简单");
    mapper.addBlog(blog);

    blog.setId(IDUtil.genId());
    blog.setTitle("微服务如此简单");
    mapper.addBlog(blog);

    session.close();
}
```

## 动态-if语句

**需求：根据作者名字和博客名字来查询博客！如果作者名字为空，那么只根据博客名字查询，反之，则根据作者名来查询**

1. 编写接口类

```java
//需求1
List<Blog> queryBlogIf(Map map);
```

2. 编写sql语句

```xml
<!--需求1：
根据作者名字和博客名字来查询博客！
如果作者名字为空，那么只根据博客名字查询，反之，则根据作者名来查询

select * from blog where title = #{title} and author = #{author}
-->
<select id="queryBlogIf" parameterType="map" resultType="blog">
    select * from blog where
    <if test="title != null">
        title = #{title}
    </if>
    <if test="author != null">
        and author = #{author}
    </if>
</select>
```

3. 测试

```java
@Test
public void testQueryBlogIf(){
    SqlSession session = MybatisUtils.getSession();
    BlogMapper mapper = session.getMapper(BlogMapper.class);

    HashMap<String, String> map = new HashMap<String, String>();
    map.put("title","Mybatis如此简单");
    map.put("author","狂神说");
    List<Blog> blogs = mapper.queryBlogIf(map);

    System.out.println(blogs);

    session.close();
}
```

会遇到以下几种情况：

```java
// 1.author 为 null的情况  正常查出
Checking to see if class com.zhuuu.dao.BlogMapper matches criteria [is assignable to Object]
Opening JDBC Connection
Created connection 376416077.
==>  Preparing: select * from blog where title = ? 
==> Parameters: Mybatis如此简单(String)
<==    Columns: id, title, author, create_time, views
<==        Row: 62d300fbe0174864af4ebb089ff6952f, Mybatis如此简单, Zhuuu, 2020-02-17 17:51:03.0, 9999
<==      Total: 1
[Blog(id=62d300fbe0174864af4ebb089ff6952f, title=Mybatis如此简单, author=Zhuuu, createTime=Mon Feb 17 17:51:03 CST 2020, views=9999)]
Closing JDBC Connection [com.mysql.jdbc.JDBC4Connection@166fa74d]
Returned connection 376416077 to pool.

Process finished with exit code 0
```

```java
// 2.title 为 null的情况  and导致报错
Opening JDBC Connection
Created connection 376416077.
==>  Preparing: select * from blog where and author = ? 
==> Parameters: Zhuuu(String)
```

## 动态-where语句

修改上面的情况2：

```xml
<select id="queryBlogIf" parameterType="map" resultType="blog">
    select * from blog 
    <where>
        <if test="title != null">
            title = #{title}
        </if>
        <if test="author != null">
            and author = #{author}
        </if>
    </where>
</select>
```

这个"where"标签会知道如果它包含标签中返回值的话，它就会插入一个“where”。此外，如果标签返回的内容是AND或OR开头的，则它会剔除掉。

## 动态-Set

1. 编写接口方法

```java
int updateBlog(Map map);
```

2. sql配置文件

```xml
<!--注意set是用的逗号隔开-->
<update id="updateBlog" parameterType="map">
    update blog
      <set>
          <if test="title != null">
              title = #{title},
          </if>
          <if test="author != null">
              author = #{author}
          </if>
      </set>
    where id = #{id};
</update>
```

3. 测试

```java
    //动态set测试
    @Test
    public void setTest(){
        SqlSession session = MybatisUtils.getSession();
        BlogMapper mapper = session.getMapper(BlogMapper.class);

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("title","动态SQL");
        map.put("author","Zhuuu");
        map.put("id","f279c70cf48d4b55a62d663610d9d541");//这里的uuid要根据自己的数据库id来取

        mapper.updateBlog(map);


        session.close();
    }
```

## 动态-choose

- 有时候，我们不想用到所有的查询条件，只想选择其中的一个，查询条件有一个满足即可，使用 choose 标签可以解决此类问题，类似于 Java 的 switch 语句

1. 编写接口方法

```java
List<Blog> queryBlogChoose(Map map);
```

2. sql配置文件

```xml
<select id="queryBlogChoose" parameterType="map" resultType="blog">
    select * from blog
    <where>
        <choose>
            <when test="title != null">
                 title = #{title}
            </when>
            <when test="author != null">
                and author = #{author}
            </when>
            <otherwise>
                and views = #{views}
            </otherwise>
        </choose>
    </where>
</select>
```

3. 测试类

```java
@Test
public void testQueryBlogChoose(){
    SqlSession session = MybatisUtils.getSession();
    BlogMapper mapper = session.getMapper(BlogMapper.class);

    HashMap<String, Object> map = new HashMap<String, Object>();
    map.put("title","Java如此简单");
    map.put("author","Zhuuu");
    map.put("views",9999);
    List<Blog> blogs = mapper.queryBlogChoose(map);

    System.out.println(blogs);

    session.close();
}
```

```java
//返回的结果:只要满足第一个，后面的就不会在查了
Opening JDBC Connection
Created connection 843467284.
==>  Preparing: select * from blog WHERE title = ? 
==> Parameters: Java如此简单(String)
<==    Columns: id, title, author, create_time, views
<==        Row: 78739c74d1c04075b898648e8dba74e7, Java如此简单, Zhuuu, 2020-02-17 17:51:03.0, 1000
<==      Total: 1
[Blog(id=78739c74d1c04075b898648e8dba74e7, title=Java如此简单, author=Zhuuu, createTime=Mon Feb 17 17:51:03 CST 2020, views=1000)]
Closing JDBC Connection [com.mysql.jdbc.JDBC4Connection@32464a14]
Returned connection 843467284 to pool.

Process finished with exit code 0
```

## 提取sql片段

- 有时候可能某个 sql 语句我们用的特别多，为了增加代码的重用性，简化代码，我们需要将这些代码抽取出来，然后使用时直接调用。
- 说白了就是代码的复用

1. 提取sql片段

```xml
<sql id="if-title-author">
    <if test="title != null">
        title = #{title}
    </if>
    <if test="author != null">
        and author = #{author}
    </if>
</sql>
```

2. 引用sql片段

```xml
<select id="queryBlogIf" parameterType="map" resultType="blog">
    select * from blog
    <where>
        <!-- 引用 sql 片段，如果refid 指定的不在本文件中，那么需要在前面加上 namespace -->
        <include refid="if-title-author"></include>
        <!-- 在这里还可以引用其他的 sql 片段 -->
    </where>
</select>
```

**注意：**

①、最好基于 单表来定义 sql 片段，提高片段的可重用性
②、在 sql 片段中不要包括 where



## 动态-foreach

将数据库中前三个数据的id修改为1,2,3；

需求：我们需要查询 blog 表中 id 分别为1,2,3的博客信息

1. 编写结口

```java
List<Blog> queryBlogForeach(Map map);
```

2. 编写sql语句

```xml
<select id="queryBlogForeach" parameterType="map" resultType="blog">
    select * from blog
    <where>
        <!--
        collection:指定输入对象中的集合属性
        item:每次遍历生成的对象
        open:开始遍历时的拼接字符串
        close:结束时拼接的字符串
        separator:遍历对象之间需要拼接的字符串
        select * from blog where 1=1 and (id=1 or id=2 or id=3)
      -->
        <foreach collection="ids"  item="id" open="and (" close=")" separator="or">
            id=#{id}
        </foreach>
    </where>
</select>
```

3. 测试

```java
@Test
public void testQueryBlogForeach(){
    SqlSession session = MybatisUtils.getSession();
    BlogMapper mapper = session.getMapper(BlogMapper.class);

    HashMap map = new HashMap();
    List<Integer> ids = new ArrayList<Integer>();
    ids.add(1);
    ids.add(2);
    ids.add(3);
    map.put("ids",ids);

    List<Blog> blogs = mapper.queryBlogForeach(map);

    System.out.println(blogs);

    session.close();
}
```



结果如下：(查出前三条记录)

```makefile
Opening JDBC Connection
Created connection 1904324159.
==>  Preparing: select * from blog WHERE ( id = ? or id = ? or id = ? ) 
==> Parameters: 1(Integer), 2(Integer), 3(Integer)
<==    Columns: id, title, author, create_time, views
<==        Row: 1, Mybatis如此简单, Zhuuu, 2020-02-17 17:51:03.0, 9999
<==        Row: 2, Java如此简单, Zhuuu, 2020-02-17 17:51:03.0, 1000
<==        Row: 3, Spring如此简单, Zhuuu, 2020-02-17 17:51:03.0, 9999
<==      Total: 3
[Blog(id=1, title=Mybatis如此简单, author=Zhuuu, createTime=Mon Feb 17 17:51:03 CST 2020, views=9999), Blog(id=2, title=Java如此简单, author=Zhuuu, createTime=Mon Feb 17 17:51:03 CST 2020, views=1000), Blog(id=3, title=Spring如此简单, author=Zhuuu, createTime=Mon Feb 17 17:51:03 CST 2020, views=9999)]
Closing JDBC Connection [com.mysql.jdbc.JDBC4Connection@7181ae3f]
Returned connection 1904324159 to pool.

```

## 小结

- 动态sql语句的编写往往就是一个拼接的问题
- 为了保证拼接准确，首先我们需要把原生的sql语句写出来，然后再通过mybatis动态sql对照着改
- 要多实践才能掌握技巧
---

title: MyBatis-日志和分页的实现
date: 2019-07-15 15:04:39
tags: MyBatis
---

# MyBatis 日志和分页的实现

## 日志的实现

### 为什么需要日志？

- 以往的开发过程，我们会经常使用到debug模式来调节，跟踪我们的代码执行过程。
- 但是现在使用Mybatis是基于接口，配置文件的源代码执行过程。因此，我们必须选择日志工具来作为我们开发，调节程序的工具。



**Mybatis内置的日志工厂提供日志功能，具体的日志实现有以下几种工具：**

- SLF4J
- Apache Commons Logging
- Log4j 2
- Log4j
- JDK logging

<!--more-->



### 标准日志的实现

指定MyBatis应该是用那个日志记录实现。如果此设置不存在，则会自动发现日志记录实现。

实现方式：在resource文件夹下的mybatisconfig.xml添加

```xml
<settings>
        <setting name="logImpl" value="STDOUT_LOGGING"/>
</settings>
```



日志输出结果：

```java
E:\jdk1.8\bin\java.exe -ea -Didea.test.cyclic.buffer.size=1048576 "-javaagent:E:\IntelliJ IDEA 2019.1\lib\idea_rt.jar=54966:E:\IntelliJ IDEA 2019.1\bin" -Dfile.encoding=UTF-8 -classpath "E:\IntelliJ IDEA 2019.1\lib\idea_rt.jar;E:\IntelliJ IDEA 2019.1\plugins\junit\lib\junit-rt.jar;E:\IntelliJ IDEA 2019.1\plugins\junit\lib\junit5-rt.jar;E:\jdk1.8\jre\lib\charsets.jar;E:\jdk1.8\jre\lib\deploy.jar;E:\jdk1.8\jre\lib\ext\access-bridge-64.jar;E:\jdk1.8\jre\lib\ext\cldrdata.jar;E:\jdk1.8\jre\lib\ext\dnsns.jar;E:\jdk1.8\jre\lib\ext\jaccess.jar;E:\jdk1.8\jre\lib\ext\jfxrt.jar;E:\jdk1.8\jre\lib\ext\localedata.jar;E:\jdk1.8\jre\lib\ext\nashorn.jar;E:\jdk1.8\jre\lib\ext\sunec.jar;E:\jdk1.8\jre\lib\ext\sunjce_provider.jar;E:\jdk1.8\jre\lib\ext\sunmscapi.jar;E:\jdk1.8\jre\lib\ext\sunpkcs11.jar;E:\jdk1.8\jre\lib\ext\zipfs.jar;E:\jdk1.8\jre\lib\javaws.jar;E:\jdk1.8\jre\lib\jce.jar;E:\jdk1.8\jre\lib\jfr.jar;E:\jdk1.8\jre\lib\jfxswt.jar;E:\jdk1.8\jre\lib\jsse.jar;E:\jdk1.8\jre\lib\management-agent.jar;E:\jdk1.8\jre\lib\plugin.jar;E:\jdk1.8\jre\lib\resources.jar;E:\jdk1.8\jre\lib\rt.jar;E:\MyBatis-study\MyBatis-04\target\test-classes;E:\MyBatis-study\MyBatis-04\target\classes;E:\maven_local_repo\mysql\mysql-connector-java\5.1.46\mysql-connector-java-5.1.46.jar;E:\maven_local_repo\org\mybatis\mybatis\3.5.2\mybatis-3.5.2.jar;E:\maven_local_repo\junit\junit\4.13\junit-4.13.jar;E:\maven_local_repo\org\hamcrest\hamcrest-core\1.3\hamcrest-core-1.3.jar" com.intellij.rt.execution.junit.JUnitStarter -ideVersion5 -junit4 daoTest,getUserByID
Logging initialized using 'class org.apache.ibatis.logging.stdout.StdOutImpl' adapter.
PooledDataSource forcefully closed/removed all connections.
PooledDataSource forcefully closed/removed all connections.
PooledDataSource forcefully closed/removed all connections.
PooledDataSource forcefully closed/removed all connections.
Opening JDBC Connection
Created connection 1375995437.
Setting autocommit to false on JDBC Connection [com.mysql.jdbc.JDBC4Connection@5204062d]
==>  Preparing: select * from mybatis.user where id = ? 
==> Parameters: 1(Integer)
<==    Columns: id, name, pwd
<==        Row: 1, zhuuu, 123456
<==      Total: 1
User{id=1, name='zhuuu', password='null'}
Resetting autocommit to true on JDBC Connection [com.mysql.jdbc.JDBC4Connection@5204062d]
Closing JDBC Connection [com.mysql.jdbc.JDBC4Connection@5204062d]
Returned connection 1375995437 to pool.

Process finished with exit code 0

```



### Log4j的实现

**简介：**

- Log4j是Apache的一个开源项目

- 通过使用Log4j，我们可以控制日志信息输送的目的地：控制台，文本，GUI组件....
- 我们也可以控制每一条日志的输出格式；

- 这些可以通过一个配置文件来灵活地进行配置，而不需要修改应用的代码。





**使用步骤**

1. 导入log4j的包(pom.xml中)

```xml
<dependency>
    <groupId>log4j</groupId>
    <artifactId>log4j</artifactId>
    <version>1.2.17</version>
</dependency>
```

2. 编写配置文件（在resource文件夹下新建log4j.properties）

```python
#将等级为DEBUG的日志信息输出到console和file这两个目的地，console和file的定义在下面的代码
log4j.rootLogger=DEBUG,console,file

#控制台输出的相关设置
log4j.appender.console = org.apache.log4j.ConsoleAppender
log4j.appender.console.Target = System.out
log4j.appender.console.Threshold=DEBUG
log4j.appender.console.layout = org.apache.log4j.PatternLayout
log4j.appender.console.layout.ConversionPattern=[%c]-%m%n

#文件输出的相关设置
log4j.appender.file = org.apache.log4j.RollingFileAppender
log4j.appender.file.File=./log/zhuuu.log
log4j.appender.file.MaxFileSize=10mb
log4j.appender.file.Threshold=DEBUG
log4j.appender.file.layout=org.apache.log4j.PatternLayout
log4j.appender.file.layout.ConversionPattern=[%p][%d{yy-MM-dd}][%c]%m%n

#日志输出级别
log4j.logger.org.mybatis=DEBUG
log4j.logger.java.sql=DEBUG
log4j.logger.java.sql.Statement=DEBUG
log4j.logger.java.sql.ResultSet=DEBUG
log4j.logger.java.sql.PreparedStatement=DEBUG
```

3. Setting设置日志实现

```xml
<settings>
    <setting name="logImpl" value="LOG4J"/>
</settings>
```

4. 在程序中实现Log4j进行输出

```java
//注意导包：org.apache.log4j.Logger
static Logger logger = Logger.getLogger(MyTest.class);

@Test
public void selectUser() {
    logger.info("info：进入selectUser方法");
    logger.debug("debug：进入selectUser方法");
    logger.error("error: 进入selectUser方法");
    SqlSession session = MybatisUtils.getSession();
    UserMapper mapper = session.getMapper(UserMapper.class);
    List<User> users = mapper.selectUser();
    for (User user: users){
        System.out.println(user);
    }
    session.close();
}
```

输出结果如下：

```java
E:\jdk1.8\bin\java.exe -ea -Didea.test.cyclic.buffer.size=1048576 "-javaagent:E:\IntelliJ IDEA 2019.1\lib\idea_rt.jar=55311:E:\IntelliJ IDEA 2019.1\bin" -Dfile.encoding=UTF-8 -classpath "E:\IntelliJ IDEA 2019.1\lib\idea_rt.jar;E:\IntelliJ IDEA 2019.1\plugins\junit\lib\junit-rt.jar;E:\IntelliJ IDEA 2019.1\plugins\junit\lib\junit5-rt.jar;E:\jdk1.8\jre\lib\charsets.jar;E:\jdk1.8\jre\lib\deploy.jar;E:\jdk1.8\jre\lib\ext\access-bridge-64.jar;E:\jdk1.8\jre\lib\ext\cldrdata.jar;E:\jdk1.8\jre\lib\ext\dnsns.jar;E:\jdk1.8\jre\lib\ext\jaccess.jar;E:\jdk1.8\jre\lib\ext\jfxrt.jar;E:\jdk1.8\jre\lib\ext\localedata.jar;E:\jdk1.8\jre\lib\ext\nashorn.jar;E:\jdk1.8\jre\lib\ext\sunec.jar;E:\jdk1.8\jre\lib\ext\sunjce_provider.jar;E:\jdk1.8\jre\lib\ext\sunmscapi.jar;E:\jdk1.8\jre\lib\ext\sunpkcs11.jar;E:\jdk1.8\jre\lib\ext\zipfs.jar;E:\jdk1.8\jre\lib\javaws.jar;E:\jdk1.8\jre\lib\jce.jar;E:\jdk1.8\jre\lib\jfr.jar;E:\jdk1.8\jre\lib\jfxswt.jar;E:\jdk1.8\jre\lib\jsse.jar;E:\jdk1.8\jre\lib\management-agent.jar;E:\jdk1.8\jre\lib\plugin.jar;E:\jdk1.8\jre\lib\resources.jar;E:\jdk1.8\jre\lib\rt.jar;E:\MyBatis-study\MyBatis-04\target\test-classes;E:\MyBatis-study\MyBatis-04\target\classes;E:\maven_local_repo\log4j\log4j\1.2.17\log4j-1.2.17.jar;E:\maven_local_repo\mysql\mysql-connector-java\5.1.46\mysql-connector-java-5.1.46.jar;E:\maven_local_repo\org\mybatis\mybatis\3.5.2\mybatis-3.5.2.jar;E:\maven_local_repo\junit\junit\4.13\junit-4.13.jar;E:\maven_local_repo\org\hamcrest\hamcrest-core\1.3\hamcrest-core-1.3.jar" com.intellij.rt.execution.junit.JUnitStarter -ideVersion5 -junit4 daoTest,getUserByID
[daoTest]-info:进入了selectUser方法
[daoTest]-debug:进入了selectUser方法
[daoTest]-error:进入了selectUser方法
[org.apache.ibatis.logging.LogFactory]-Logging initialized using 'class org.apache.ibatis.logging.log4j.Log4jImpl' adapter.
[org.apache.ibatis.logging.LogFactory]-Logging initialized using 'class org.apache.ibatis.logging.log4j.Log4jImpl' adapter.
[org.apache.ibatis.datasource.pooled.PooledDataSource]-PooledDataSource forcefully closed/removed all connections.
[org.apache.ibatis.datasource.pooled.PooledDataSource]-PooledDataSource forcefully closed/removed all connections.
[org.apache.ibatis.datasource.pooled.PooledDataSource]-PooledDataSource forcefully closed/removed all connections.
[org.apache.ibatis.datasource.pooled.PooledDataSource]-PooledDataSource forcefully closed/removed all connections.
[org.apache.ibatis.transaction.jdbc.JdbcTransaction]-Opening JDBC Connection
[org.apache.ibatis.datasource.pooled.PooledDataSource]-Created connection 673186785.
[org.apache.ibatis.transaction.jdbc.JdbcTransaction]-Setting autocommit to false on JDBC Connection [com.mysql.jdbc.JDBC4Connection@282003e1]
[com.zhuuu.dao.Mapper.getUserbyID]-==>  Preparing: select * from mybatis.user where id = ? 
[com.zhuuu.dao.Mapper.getUserbyID]-==> Parameters: 1(Integer)
[com.zhuuu.dao.Mapper.getUserbyID]-<==      Total: 1
User{id=1, name='zhuuu', password='null'}
[org.apache.ibatis.transaction.jdbc.JdbcTransaction]-Resetting autocommit to true on JDBC Connection [com.mysql.jdbc.JDBC4Connection@282003e1]
[org.apache.ibatis.transaction.jdbc.JdbcTransaction]-Closing JDBC Connection [com.mysql.jdbc.JDBC4Connection@282003e1]
[org.apache.ibatis.datasource.pooled.PooledDataSource]-Returned connection 673186785 to pool.

Process finished with exit code 0

```



## 分页的实现

### 为什么需要分页？

- 在学习mybatis等持久层框架的时候，会经常对数据进行增删改查操作，使用最多的是对数据库进行查询操作
- 如果查询大量数据的时候，我们往往使用分页进行查询，也就是每次处理小部分数据，这样对数据库压力就在可控范围内。

### limit实现分页

```mysql
#语法
select * from table Limit startIndex,pageSize

#如:
SELECT * FROM table LIMIT 5,10; // 检索记录行 6-15 

#如果只给定一个参数，它表示返回最大的记录行数目：    
SELECT * FROM table LIMIT 5; //检索前 5 个记录行 

#换句话说，LIMIT n 等价于 LIMIT 0,n。 
```



使用步骤：

1. 修改Mapper文件

```xml
<select id="selectUser" parameterType="map" resultType="user">
    select * from user limit #{startIndex},#{pageSize}
</select>
```

2. Mapper接口修改入参为map

```java 
//选择全部用户实现分页
List<User> selectUser(Map<String,Integer> map);
```

3. 在测试类中传入参数

- 推断：起始位置 = （当前页面 - 1 ） * 页面大小

```java
import com.zhuuu.dao.Mapper;
import com.zhuuu.pojo.User;
import com.zhuuu.utils.MybatisUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;

public class daoTest {
    @Test
    public void test(){
        SqlSession session = MybatisUtils.getSession();
        Mapper mapper = session.getMapper(Mapper.class);

        int curentPage = 1;//第几页
        int pageSize = 2;//每页显示几个

        HashMap<String, Integer> map = new HashMap<String, Integer>();
        map.put("startIndex",(curentPage-1)*pageSize);
        map.put("pageSize",pageSize);

        List<User> userList = mapper.getUserList(map);
        for (User user : userList) {
            System.out.println(user);
        }
        session.close();
    }
}
```

### RowBounds分页

- 除了使用Limit在SQL层面实现分页，也可以使用RowBounds在Java代码层面实现分页，
- 当然此种方式作为了解即可。我们来看下如何实现的！

**步骤：**

1. mapper接口

```java
//选择全部用户RowBounds实现分页
List<User> getUserByRowBounds();
```

2. mapper.xml文件

```xml
<select id="getUserByRowBounds" resultType="user">
  select * from user
</select>
```

3. 测试类	

   在这里使用RowBounds类

```java
@Test
public void testUserByRowBounds() {
    SqlSession session = MybatisUtils.getSession();

    int currentPage = 2;  //第几页
    int pageSize = 2;  //每页显示几个
    RowBounds rowBounds = new RowBounds((currentPage-1)*pageSize,pageSize);

    //通过session.**方法进行传递rowBounds，[此种方式现在已经不推荐使用了]
    List<User> users = session.selectList("com.zhuuu.mapper.UserMapper.getUserByRowBounds", null, rowBounds);

    for (User user: users){
        System.out.println(user);
    }
    session.close();
}
```



## 分页插件：



官方文档：https://pagehelper.github.io/



![](https://zhuuu-bucket.oss-cn-beijing.aliyuncs.com/img/20200213160022.png)
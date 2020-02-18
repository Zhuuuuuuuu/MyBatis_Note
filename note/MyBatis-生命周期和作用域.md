---
title: MyBatis-生命周期和作用域
date: 2019-07-13 11:12:22
tags: MyBatis
---

# MyBatis 生命周期和作用域

## **作用域（Scope）和生命周期**

- 理解我们目前已经讨论过的不同作用域和生命周期类是至关重要的，因为错误的使用会导致非常严重的并发问题。

- 画一个流程图，分析一下Mybatis的执行过程！

![](https://zhuuu-bucket.oss-cn-beijing.aliyuncs.com/img/20200213111632.png)



[官方说明](https://mybatis.org/mybatis-3/zh/getting-started.html)

<!--more-->



### SqlSessionFactoryBuilder

- 这个类可以被实例化、使用和丢弃，一旦创建了 SqlSessionFactory，就不再需要它了。
-  局部方法变量



### SqlSessionFactory

- SqlSessionFactory 可以被认为是一个数据库连接池，它的作用是创建 SqlSession 接口对象。
- 因为 MyBatis 的本质就是 Java 对数据库的操作，所以 SqlSessionFactory 的生命周期存在于整个 MyBatis 的应用之中，所以一旦创建了 SqlSessionFactory，就要长期保存它，直至不再使用 MyBatis 应用
- 因此 SqlSessionFactory 的最佳作用域是应用作用域

- 最简单的就是使用单例模式或者静态单例模式。



### SqlSession

- 连接到连接池的一个请求！

- 它的最佳的作用域是请求或方法作用域。
-  如果你现在正在使用一种 Web 框架，要考虑 SqlSession 放在一个和 HTTP 请求对象相似的作用域中。 换句话说，每次收到的 HTTP 请求，就可以打开一个 SqlSession，返回一个响应，就关闭它。 这个关闭操作是很重要的。





![](https://zhuuu-bucket.oss-cn-beijing.aliyuncs.com/img/20200213112344.png)


## 简介

> zhifou是以知乎平台为原型，基于Springboot的Java web项目，使用python爬取数据填充数据库，数据库使用了Redis和MySQL。实现了用户登录注册，首页按照关注热度排序，问题，消息的发布，用户评论等功能。

## 负责事宜

用户登录注册时的验证；

异步消息队列实现登录异常时的邮件通知，消息通知；

利用Redis中的数据结构实现对评论点赞和点踩功能；

实现统一的评论中心，可用于论坛的回帖，题目的回复，课程的回答等；

仿照Hacker News的排序算法，按照问题的关注热度，对首页问题进行排序。 

## 快速开始

### 运行环境

- **Java Version:** JDK1.8
- **Springboot Version:** 1.3.6
- **开发工具:**  IDEA2019.1
- **服务器：** Ubnutu14.04
- **数据库:** MySQL5.7.24 + Redis3.2 

### 下载运行

- #### 下载

`Download Zip`或者 `git clone`

> https://github.com/benjieqiang/zhifou.git

- #### 导入IDEA

> IDEA会自动根据pom.xml文件进行导包工作。

- #### 启动

## 预览图
暂时没有
package com.ben.service;

import com.ben.dao.LoginTicketDAO;
import com.ben.dao.UserDAO;
import com.ben.model.LoginTicket;
import com.ben.model.User;
import com.ben.util.WendaUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @ClassName: UserService
 * @author: benjamin
 * @version: 1.0
 * @description: 用户的业务层调用UserDao
 * @createTime: 2019/08/17/09:11
 */

@Service
public class UserService {
    @Autowired
    UserDAO userDAO;

    @Autowired
    LoginTicketDAO loginTicketDAO;

    // 增
    public int addUser(User user){
        return userDAO.addUser(user);
    }
    // 删
    public void deleteById(int id){
        userDAO.deleteById(id);
    }
    // 改
    public void updatePassword(int id){
        userDAO.updatePassword(id);
    }

    // 根据用户id查
    public User getUser(int id){
        return userDAO.selectById(id);
    }
    // 根据用户名查
    public User selectByName(String name){
        return userDAO.selectByName(name);
    }


    // 登录检测
    public Map<String,Object> login(String username,String password){
        Map<String, Object> map = new HashMap<>();
        // 1. 判断数据是否为空；
        if(StringUtils.isBlank(username)||StringUtils.isBlank(password)){
            map.put("msg","用户名或密码不能为空");
            return map;
        }
        // 2. 用户名和密码是否正确
        // 2.1 用户名是否存在
        User user = userDAO.selectByName(username);//先去数据库查该用户
//        System.out.println("user"+user);
        if(user == null){
            map.put("msg","用户名不存在");
            return map;
        }
        // 打印进行比较密码是否一致
//        System.out.println(WendaUtil.MD5(password+user.getSalt()));
//        System.out.println(user.getPassword());
        // 2.2 密码是否正确
        if(!WendaUtil.MD5(password+user.getSalt()).equals(user.getPassword())){
            map.put("msg","密码不正确");
            return map;
        }

        // 如果登录成功，map里面放入ticket，返回给控制器
        String ticket = addLoginTicket(user.getId());
        map.put("ticket",ticket);
        return map;
    }

    // 注册检测
    public Map<String,Object> register(String username,String password){
        Map<String, Object> map = new HashMap<>();
        // 1. 判断数据是否为空；
        if(StringUtils.isBlank(username)||StringUtils.isBlank(password)){
            map.put("msg","用户名或密码不能为空");
            return map;
        }
        // 2. 用户名和密码是否正确
        // 2.1 用户名是否存在
        User user = userDAO.selectByName(username);//先去数据库查该用户
//        System.out.println("user"+user);
        if(user != null){
            map.put("msg","用户名已经被注册");
            return map;
        }
        // 将user信息存入数据库，用户名，盐，密码，用户头像默认，
        user = new User();
        user.setName(username);
        user.setSalt(UUID.randomUUID().toString().substring(0,5));
        String head = String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000));
        user.setHeadUrl(head);
        user.setPassword(WendaUtil.MD5(password+user.getSalt()));
        userDAO.addUser(user);

        // 如果注册成功，map里面不带任何值
        return map;
    }


    // 每登录一次，进行ticket的记录
    private String addLoginTicket(int userId) {
        LoginTicket ticket = new LoginTicket();
        ticket.setUserId(userId);
        Date date = new Date();
        date.setTime(date.getTime()+1000*3600*24); // 设置为1天后的时间
        ticket.setExpired(date);
        ticket.setStatus(0); // 0 表示状态是有效的
//        System.out.println(UUID.randomUUID().toString()); // 0acafe55-9668-42d3-8a50-8f8c82fc8e65
        ticket.setTicket(UUID.randomUUID().toString().replaceAll("-", ""));
        loginTicketDAO.addTicket(ticket);
        return ticket.getTicket();
    }

    // 登出
    public void logout(String ticket){
        // 将状态设置为1，则表示登出
        loginTicketDAO.updateStatus(ticket,1);
    }

}

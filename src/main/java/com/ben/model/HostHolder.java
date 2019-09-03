package com.ben.model;

import org.springframework.stereotype.Component;

/**
 * @ClassName: HostHolder
 * @author: benjamin
 * @version: 1.0
 * @description: TODO
 * @createTime: 2019/08/18/20:25
 */
@Component
public class HostHolder {

    //每个 Thread 内有自己的实例副本，且该副本只能由当前 Thread 使用。
    // 场景：如果所有的线程都在访问这个页面，每个用户又都是登录用户，
    // 登录用户，每个用户都有自己的变量，将变量存到对应的线程里。
    private static ThreadLocal<User> users = new ThreadLocal<User>();

    public User getUser() {
        return users.get();
    }

    public void setUser(User user) {
        users.set(user);
    }

    public void clear() {
        users.remove();
    }
}

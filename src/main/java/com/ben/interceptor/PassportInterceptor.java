package com.ben.interceptor;

import com.ben.dao.LoginTicketDAO;
import com.ben.dao.UserDAO;
import com.ben.model.HostHolder;
import com.ben.model.LoginTicket;
import com.ben.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * @ClassName: PassportInterceptor
 * @author: benjamin
 * @version: 1.0
 * @description: 用来进行用户的权限验证的拦截器
 * @createTime: 2019/08/18/20:17
 */
@Component
public class PassportInterceptor implements HandlerInterceptor {

    @Autowired
    private LoginTicketDAO loginTicketDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private HostHolder hostHolder;
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        String ticket = null;
        // 从浏览器发送的cookies里找到名字叫ticket的cookie
        if (httpServletRequest.getCookies() != null) { // 如果cookies不为空，循环遍历找到ticket这个cookie
            for (Cookie cookie : httpServletRequest.getCookies()) {
                if (cookie.getName().equals("ticket")) {
                    ticket = cookie.getValue();
                    break;
                }
            }
        }
//        System.out.println(ticket+"打印的ticket");
        // 如果ticket不等于null，
        if (ticket != null) {
            // 根据ticket查询login_ticket表，判断是否有记录
            LoginTicket loginTicket = loginTicketDAO.selectByTicket(ticket);
            // 记录为null，在当前时间点之前则表明过期，状态为不为0,说明用户未登录；
            if (loginTicket == null || loginTicket.getExpired().before(new Date()) || loginTicket.getStatus() != 0) {
                return true;
            }

            // 取出用户：
            // 如果loginticket存在，根据其存放的userId查询User表中的用户信息，放入spring的上下文，需要用到DI
            User user = userDAO.selectById(loginTicket.getUserId());

//            System.out.println(user); // 拿到数据
            // ThreadLocal的应用
            hostHolder.setUser(user);
        }
        return true;
    }

    // 在渲染之前将user放入velocity的上下文
    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        if (modelAndView != null && hostHolder.getUser() != null) {
            modelAndView.addObject("user", hostHolder.getUser());
        }
//        System.out.println("在渲染之前将user放入");
    }


    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        hostHolder.clear();
//        System.out.println("清理hostHolder");
    }
}

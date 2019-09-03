package com.ben.controller;

import com.ben.async.EventModel;
import com.ben.async.EventProducer;
import com.ben.async.EventType;
import com.ben.service.UserService;
import com.sun.xml.internal.ws.resources.HttpserverMessages;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: LoginController
 * @author: benjamin
 * @version: 1.0
 * @description: 注册，登录，登出业务处理
 * @createTime: 2019/08/17/16:46
 */

@Controller
public class LoginController {

    // 记录日志
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    UserService userService;

    @Autowired
    EventProducer eventProducer;

    /*
     * TODO:
     *
     * 待补充：
     * 1. 邮箱激活功能，数据库需要设置标志位；
     * 2. 短信验证码功能
     * */

    // 注册
    // get请求
    @RequestMapping(value = {"/register"}, method = RequestMethod.GET)
    public String register() {
        // 完成用户的注册
        return "login";
    }

    // post请求
    @RequestMapping(value = {"/register"}, method = RequestMethod.POST)
    public String register(Model model, @RequestParam("username") String username,
                           @RequestParam("password") String password,
                           HttpServletResponse response
    ) {
        try {
            // 参数校验
            Map<String, Object> map = userService.register(username, password);
            // 返回响应
            if (map.containsKey("msg")) {
                model.addAttribute("msg", map.get("msg"));
                return "login";
            } else {
                return "redirect:/";
            }

        } catch (Exception e) {
            logger.error("注册异常" + e.getMessage());
            model.addAttribute("msg", "服务器错误");
            return "login";
        }
    }

    // 登录
    // get请求，如果带有next参数，就先将next参数埋到index.html里面
    @RequestMapping(value = {"/login"}, method = RequestMethod.GET)
    public String login(Model model,
                        @RequestParam(value = "next", required = false) String next) {
        model.addAttribute("next", next);
        return "login";
    }

    // post请求
    @RequestMapping(value = {"/login"}, method = {RequestMethod.POST})
    public String login(Model model, @RequestParam("username") String username,
                        @RequestParam("password") String password,
                        @RequestParam(value = "next", required = false) String next,
                        @RequestParam(value = "rememberme", defaultValue = "false") boolean rememberme,
                        HttpServletRequest request,
                        HttpServletResponse response) {
        // 接受数据
        // 数据校验，在UserService层做了
        try {
            Map<String, Object> map = userService.login(username, password);
            //拿到的map里有两种情况，一种是带 ticket的，一种是带 msg的
            if (map.containsKey("msg")) { // map含 msg的key，则表示有错误信息
                model.addAttribute("msg", map.get("msg"));//将msg信息传递给前端进行显示
                return "login";
            } else {
                // 登录正常，将ticket写到cookie里面
                Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
                //设置路径，这个路径即该工程下都可以访问该cookie;
                // 如果不设置路径，那么只有设置该cookie路径及其子路径可以访问
                cookie.setPath("/");
                // 如果用户设置了记住我，设置cookie的有效时间为5天；
                if (rememberme) {
                    cookie.setMaxAge(3600 * 24 * 5);
                }
                response.addCookie(cookie);
                // 如果用户的next不为空，则跳转到next的连接上
//  有点小问题，报空指针异常
//                eventProducer.fireEvent(new EventModel(EventType.LOGIN)
//                        .setExt("username", username).setExt("email", "benjieqiang@163.com")
//                        .setActorId((int)map.get("userId")));

                // 获取用户的登录ip
                String ip = getIpAddr(request);
//                System.out.println(ip);
                // 把登录ip加入到这个发邮件的事件中；

                if (StringUtils.isNotBlank(next)) {
                    return "redirect:" + next;
                }
                return "redirect:/";
            }
        } catch (Exception e) {
            logger.error("登录异常" + e);
            return "login";
        }
    }

    @RequestMapping(path = {"/logout"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String logout(@CookieValue("ticket") String ticket) {
//        System.out.println("此时ticket+"+ticket);
        userService.logout(ticket);
//        System.out.println("此时ticket+"+ticket);
        return "redirect:/";
    }

    /*
     * 获取访问者 IP
     * @param request
     * @return ip
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Real-IP");
        if (!StringUtils.isBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        ip = request.getHeader("X-Forwarded-For");
        if (!StringUtils.isBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个IP值，第一个为真实IP。
            int index = ip.indexOf(',');
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        } else {
            return request.getRemoteAddr();
        }
    }
}

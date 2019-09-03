package com.ben.controller;

import com.ben.model.HostHolder;
import com.ben.model.Message;
import com.ben.model.User;
import com.ben.model.ViewObject;
import com.ben.service.MessageService;
import com.ben.service.UserService;
import com.ben.util.WendaUtil;
import org.apache.catalina.Host;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import javax.swing.text.View;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ClassName: MessageController
 * @author: benjamin
 * @version: 1.0
 * @description: 消息入口
 * @createTime: 2019/08/20/17:06
 */

@Controller
public class MessageController {
    // 记录日志
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    // 注入用户
    @Autowired
    HostHolder hostHolder;

    // 注入消息service层
    @Autowired
    MessageService messageService;

    // UserService层
    @Autowired
    UserService userService;

    // 增加消息
    @RequestMapping(value = "/msg/addMessage", method = {RequestMethod.POST})
    @ResponseBody
    public String addMessage(@RequestParam("toName") String toName,
                             @RequestParam("content") String content) {
        try {
            // 用户登录检测
            if (hostHolder.getUser() == null) {
                return WendaUtil.getJSONString(999, "未登录");//用户未登录返回999
            }
            User user = userService.selectByName(toName);
            // 检测toName是否存在
            if (user == null) {
                return WendaUtil.getJSONString(1, "用户不存在");
            }
            Message message = new Message();
            message.setFromId(hostHolder.getUser().getId());
            message.setContent(content);
            message.setToId(user.getId()); // 需要根据名字查到接受者的id
            message.setCreatedDate(new Date());
            message.setHasRead(0); // 0表示未读

            messageService.addMessage(message);
            return WendaUtil.getJSONString(0);

        } catch (Exception e) {
            System.out.println("添加失败了");
            logger.error("增加消息失败" + e.getMessage());
            return WendaUtil.getJSONString(1, "发送消息失败");
        }
    }


    // 列出该用户所有的消息
    @RequestMapping(value = "/msg/list", method = {RequestMethod.GET})
    public String conversationDetail(Model model) {
        try {
            // 获取当前登录用户id
            int localUserId = hostHolder.getUser().getId();
            List<ViewObject> conversations = new ArrayList<ViewObject>();
            // 查到所有用户给当前用户发送的消息，放入一个列表
            List<Message> conversationList = messageService.getConversationList(localUserId, 0, 10);
            for (Message msg : conversationList) {
                // System.out.println("取出的所有消息列表："+msg);
                ViewObject vo = new ViewObject();
                vo.set("conversation", msg); // 将消息列表存入vo中；
                // 取非当前用户
                int targetId = msg.getFromId() == localUserId ? msg.getToId() : msg.getFromId();
                User user = userService.getUser(targetId);
                vo.set("user", user);
                // 获得未读消息数目
                vo.set("unread", messageService.getConversationUnreadCount(localUserId, msg.getConversationId()));
                conversations.add(vo);
            }
            model.addAttribute("conversations", conversations);
            //System.out.println(model);
        } catch (Exception e) {
            logger.error("获取站内信列表失败" + e.getMessage());
        }
        return "letter";
    }

    // 列出该用户与某个用户的全部私信消息
    @RequestMapping(path = {"/msg/detail"}, method = {RequestMethod.GET})
    public String conversationDetail(Model model, @Param("conversationId") String conversationId) {
        //System.out.println(conversationId);
        try {
            // 根据conversationId拿到对应的降序消息
            List<Message> conversationList = messageService.getConversationDetail(conversationId, 0, 10);
            // 存入viewObject中
            List<ViewObject> messages = new ArrayList<>();
            for (Message msg : conversationList) {
                ViewObject vo = new ViewObject();
                vo.set("message", msg);
                // 拿到发送者的信息
                User user = userService.getUser(msg.getFromId());
                if (user == null) {
                    continue;
                }
                // 将头像，id放入vo中
                vo.set("headUrl", user.getHeadUrl());
                vo.set("userId", user.getId());
                messages.add(vo);
            }
            model.addAttribute("messages", messages);
        } catch (Exception e) {
            logger.error("获取详情消息失败" + e.getMessage());
        }
        return "letterDetail";
    }

}

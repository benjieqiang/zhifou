package com.ben.controller;

import com.ben.model.*;
import com.ben.service.*;
import com.ben.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ClassName: QuestionController
 * @author: benjamin
 * @version: 1.0
 * @description: TODO
 * @createTime: 2019/08/19/19:36
 */
@Controller
public class QuestionController {
    // 记录日志
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    HostHolder hostHolder;
    @Autowired
    QuestionService questionService;

    @Autowired
    CommentService commentService;

    @Autowired
    UserService userService;

    @Autowired
    LikeService likeService;

    @Autowired
    FollowService followService;

    // 增加问题
    @RequestMapping(value = "/question/add", method = {RequestMethod.POST})
    @ResponseBody
    public String addQuestion(@RequestParam("title") String title,
                              @RequestParam("content") String content) {
        try {
            //业务
            Question question = new Question();
            // 标题，内容，日期，user
            question.setTitle(title);
            question.setContent(content);
            question.setCreatedDate(new Date());
            // 进行用户user的检测，如果登录，直接从HostHolder中拿到用户ID
            if (hostHolder.getUser() != null) {
                question.setUserId(hostHolder.getUser().getId());
            } else {  // 未登录直接跳转
//                question.setUserId(WendaUtil.ANONYMOUS_USERID);
                return WendaUtil.getJSONString(999); // popupAdd中的业务999表示跳转至登录页面
            }
//            System.out.println(questionService.addQuestion(question));//成功则返回用户id
            if (questionService.addQuestion(question) > 0) {
                return WendaUtil.getJSONString(0);
            }
        } catch (Exception e) {
            logger.error("增加题目失败" + e.getMessage());
        }
        return WendaUtil.getJSONString(1, "失败");
    }

    @RequestMapping(value = "/question/{qid}", method = {RequestMethod.GET})
    public String questionDetail(Model model, @PathVariable("qid") int qid) {
        //业务，用qid去数据库查到对应的信息
        Question question = questionService.selectById(qid);
        // 详情页需要展示的信息：问题信息
        model.addAttribute("question", question);
        // 对该问题的评论信息：用该问题id去查它的评论
        List<Comment> commentList = commentService.getCommentsByEntity(qid, EntityType.ENTITY_QUESTION);
        List<ViewObject> comments = new ArrayList<ViewObject>();
        for (Comment comment : commentList) {
            ViewObject vo = new ViewObject();
            vo.set("comment", comment);
            // 如果用户未登录是没法点赞的
//            System.out.println(comment.getUserId()+"当前日期"+comment.getCreatedDate());
            if (hostHolder.getUser() == null) {
                vo.set("liked", 0);
            } else {
                // 如果用户登录了，那么设置的liked数是查redis中的该评论的点赞数目：1表示点赞，-1点踩
                vo.set("liked", likeService.getLikeStatus(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, comment.getId()));
            }

            vo.set("likeCount", likeService.getLikeCount(EntityType.ENTITY_COMMENT, comment.getId()));
            vo.set("user", userService.getUser(comment.getUserId()));
            comments.add(vo);
        }
//        System.out.println("comments"+comments);
        model.addAttribute("comments", comments);

        List<ViewObject> followUsers = new ArrayList<ViewObject>();
        // 获取关注的用户信息
        List<Integer> users = followService.getFollowers(EntityType.ENTITY_QUESTION, qid, 20);
        for (Integer userId : users) {
            ViewObject vo = new ViewObject();
            User u = userService.getUser(userId);
            if (u == null) {
                continue;
            }
            vo.set("name", u.getName());
            vo.set("headUrl", u.getHeadUrl());
            vo.set("id", u.getId());
            followUsers.add(vo);
        }
        model.addAttribute("followUsers", followUsers);
        if (hostHolder.getUser() != null) {
            model.addAttribute("followed", followService.isFollower(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION, qid));
        } else {
            model.addAttribute("followed", false);
        }
//        System.out.println(model); // 测试发送到detail页面的数据

        return "detail";
    }

}

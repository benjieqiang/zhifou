package com.ben.controller;

import com.ben.model.Comment;
import com.ben.model.EntityType;
import com.ben.model.HostHolder;
import com.ben.service.CommentService;
import com.ben.service.QuestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.HtmlUtils;

import java.util.Date;

/**
 * @ClassName: CommentController
 * @author: benjamin
 * @version: 1.0
 * @description: 用户评论
 * @createTime: 2019/08/20/09:18
 */
@Controller
public class CommentController {
    // 日志记录
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);


    // 用户
    @Autowired
    HostHolder hostHolder;

    // 评论业务层
    @Autowired
    CommentService commentService;

    @Autowired
    QuestionService questionService;

    // 添加评论信息，post方式，需要接受参数：问题id，内容
    @RequestMapping(value = "/addComment", method = {RequestMethod.POST})
    public String addComment(@RequestParam("questionId") int questionId,
                             @RequestParam("content") String content) {
        try {
            // 业务处理
            // 1. content进行html和敏感词过滤
            content = HtmlUtils.htmlEscape(content);
            // 2. 新建一个comment对象，存入当前问题的评论
            Comment comment = new Comment();
            // 判断用户是否登录，未登录直接跳转到登录页面
            if (hostHolder.getUser() == null) {
                return "redirect:/login";
                // comment.setUserId(WendaUtil.ANONYMOUS_USERID); 设置成3,匿名用户
            }
            // 存评论数据
            comment.setUserId(hostHolder.getUser().getId());
            comment.setContent(content);
            comment.setCreatedDate(new Date());
            comment.setEntityId(questionId); // 拿到的前端问题id
            comment.setEntityType(EntityType.ENTITY_QUESTION); // 1表示是对问题的评论
            comment.setStatus(1);//1表示评论存在
            // 增加评论
            commentService.addComment(comment);
            // 更新题目里的评论数量，需要做成异步处理的方式。
            int count = commentService.getCommentCount(comment.getEntityId(), comment.getEntityType());
            questionService.updateCommentCount(comment.getEntityId(), count);

        } catch (Exception e) {
            logger.error("评论错误" + e.getMessage());
        }
        // 返回该页面
        return "redirect:/question/" + String.valueOf(questionId);

    }

}

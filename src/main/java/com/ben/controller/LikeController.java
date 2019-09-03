package com.ben.controller;

import com.ben.async.EventHandler;
import com.ben.async.EventModel;
import com.ben.async.EventProducer;
import com.ben.async.EventType;
import com.ben.model.Comment;
import com.ben.model.EntityType;
import com.ben.model.HostHolder;
import com.ben.service.CommentService;
import com.ben.service.LikeService;
import com.ben.util.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @ClassName: LikeController
 * @author: benjamin
 * @version: 1.0
 * @description: 用户点赞或踩，接着要在QuestionController进行体现
 * @createTime: 2019/08/22/18:09
 */
@Controller
public class LikeController {

    @Autowired
    HostHolder hostHolder;
    @Autowired
    LikeService likeService;
    @Autowired
    EventProducer eventProducer;
    @Autowired
    CommentService commentService;


    // 用户点赞操作
    @RequestMapping(value = {"/like"},method = {RequestMethod.POST})
    @ResponseBody
    public String like(@RequestParam("commentId") int commentId){
        // 参数校验
        // 用户是否登录
        if (hostHolder.getUser() == null) {
            return WendaUtil.getJSONString(999);
        }

        // 通过评论id找到该评论
        Comment comment = commentService.getCommentById(commentId);
        // 首先建一个点赞的事件+谁发的+评论id+评论的类型+发给谁+发的内容
        eventProducer.fireEvent(new EventModel(EventType.LIKE)
                .setActorId(hostHolder.getUser().getId()).setEntityId(commentId)
                .setEntityType(EntityType.ENTITY_COMMENT).setEntityOwnerId(comment.getUserId())
                .setExt("questionId", String.valueOf(comment.getEntityId())));

        // 拿到点赞数，当前用户的id,评论类型，评论id
        long likeCount = likeService.like(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, commentId);
        // 返回响应
        return WendaUtil.getJSONString(0, String.valueOf(likeCount));
    }

    // 用户踩，拿到踩后的赞数目
    @RequestMapping(path = {"/dislike"}, method = {RequestMethod.POST})
    @ResponseBody
    public String dislike(@RequestParam("commentId") int commentId) {
        if (hostHolder.getUser() == null) {
            return WendaUtil.getJSONString(999);
        }

        long likeCount = likeService.disLike(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, commentId);
        return WendaUtil.getJSONString(0, String.valueOf(likeCount));
    }

}

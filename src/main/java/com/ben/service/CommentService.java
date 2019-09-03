package com.ben.service;

import com.ben.dao.CommentDAO;
import com.ben.model.Comment;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @ClassName: CommentService
 * @author: benjamin
 * @version: 1.0
 * @description: 调CommentDAO层
 * @createTime: 2019/08/20/09:11
 */

@Service
public class CommentService {

    @Autowired
    CommentDAO commentDAO;

    // 插入评论
    public int addComment(Comment comment) {
        return commentDAO.addComment(comment);
    }

    // 删除评论，也就是将status置为0，需要传入三个参数
    public void deleteComment(int entityId, int entityType, int status) {
        commentDAO.updateStatus(entityId, entityType, status);
    }

    // 选出一个entity下的所有评论，并按照时间倒序，需要传入两个参数
    public List<Comment> getCommentsByEntity(int entityId, int entityType) {
//        System.out.println("选出一个entity下的所有评论");
        return commentDAO.selectByEntity(entityId, entityType);
    }

    // 计算一个entity下有多少评论，传入两个参数
    public int getCommentCount(int entityId, int entityType) {
        return commentDAO.getCommentCount(entityId, entityType);
    }

    public Comment getCommentById(int id) {
        return commentDAO.getCommentById(id);
    }
    // 获取某一用户的评论总数
    public int getUserCommentCount(int userId) {
        return commentDAO.getUserCommentCount(userId);
    }


}

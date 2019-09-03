package com.ben.dao;

import com.ben.model.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @ClassName: CommentDAO，操作表
 * @author: benjamin
 * @createTime: 2019/08/20/08:59
 */
@Mapper
public interface CommentDAO {


    String TABLE_NAME = " comment ";
    String INSERT_FIELDS = " user_id, content, created_date, entity_id, entity_type, status ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    // 插入评论
    // insert into comment (user_id,content,created_date,entity_id,entity_type status)
    // values (#{userId},#{content},#{createdDate},#{entityId},#{entityType},#{status})
    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{userId},#{content},#{createdDate},#{entityId},#{entityType},#{status})"})
    int addComment(Comment comment);

    // 删除该评论，也就是将status置为0，需要传入三个参数
    // update comment set status = #{status} where entity_id = #{entityId}
    // and entity_type = #{entityType}
    @Update({"update ", TABLE_NAME, " set status=#{status} where entity_id=#{entityId} and entity_type=#{entityType}"})
    void updateStatus(@Param("entityId") int entityId, @Param("entityType") int entityType, @Param("status") int status);

    // 选出一个问题或者其他评论实体下的所有评论，并按照时间倒序，需要传入两个参数
    // select user_id,content,created_date,entity_id,entity_type status
    // from comment where entity_id = #{entityId} and entity_type = #{entityType} order by created_date desc
    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME,
            " where entity_id=#{entityId} and entity_type=#{entityType} order by created_date desc"})
    List<Comment> selectByEntity(@Param("entityId") int entityId, @Param("entityType") int entityType);

    // 计算一个entity下有多少评论，传入两个参数
    // select count(id) from comment where entity_id = #{entityId} and entity_Type = #{entityType}
    @Select({"select count(id) from ", TABLE_NAME, " where entity_id=#{entityId} and entity_type=#{entityType} "})
    int getCommentCount(@Param("entityId") int entityId, @Param("entityType") int entityType);

    // 通过id选择对应的评论
    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where id=#{id}"})
    Comment getCommentById(int id);

    // 获取某id的评论数目
    @Select({"select count(id) from ", TABLE_NAME, " where user_id=#{userId}"})
    int getUserCommentCount(int userId);

}

package com.ben.dao;

import com.ben.model.Message;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @ClassName: MessageDAO
 * @author: benjamin
 * @createTime: 2019/08/20/11:21
 * 操作Message表
 */
@Mapper
public interface MessageDAO {
    String TABLE_NAME = " message ";
    String INSERT_FIELDS = " from_id, to_id, content, has_read, conversation_id, created_date ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;


    // 增加message ,在popupMsg中，/msg/addMessage
    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{fromId},#{toId},#{content},#{hasRead},#{conversationId},#{createdDate})"})
    int addMessage(Message message);

    // 查询消息的具体内容，一个收发的id，从哪里开始，查几个；
    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where conversation_id=#{conversationId} order by id desc limit #{offset}, #{limit}"})
    List<Message> getConversationDetail(@Param("conversationId") String conversationId,
                                        @Param("offset") int offset, @Param("limit") int limit);

    // 消息未读个数，未读标志位，当前用户的id，A_当前用户id
    @Select({"select count(id) from ", TABLE_NAME, " where has_read=0 and to_id=#{userId} and conversation_id=#{conversationId}"})
    int getConversationUnreadCount(@Param("userId") int userId, @Param("conversationId") String conversationId);

    // 查所有用户给当前用户发的消息
    // 先查到最新评论，
    // 注意这里把count(id)的值设到了id字段，这样在前台直接取id就可以拿到发送者与当前用户未读消息的数目
    @Select({"select ", INSERT_FIELDS, " , count(id) as id from ( select * from ", TABLE_NAME,
            " where from_id=#{userId} or to_id=#{userId} order by created_date desc) tt group by conversation_id order by created_date desc"})
    List<Message> getConversationList(@Param("userId") int userId, @Param("offset") int offset, @Param("limit") int limit);
}

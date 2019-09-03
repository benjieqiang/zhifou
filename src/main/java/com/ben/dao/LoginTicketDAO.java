package com.ben.dao;

import com.ben.model.LoginTicket;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @ClassName: LoginTicketDAO
 * @author: benjamin
 * @createTime: 2019/08/18/17:38
 */
@Mapper
public interface LoginTicketDAO {

    String TABLE_NAME = "login_ticket";
    String INSERT_FIELDS = " user_id, expired, status, ticket ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    // 增
    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS, ") values (#{userId},#{expired},#{status},#{ticket})"})
    int addTicket(LoginTicket ticket);

    // 查询 ticket
    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where ticket=#{ticket}"})
    LoginTicket selectByTicket(String ticket);

    @Update({"update ", TABLE_NAME, " set status=#{status} where ticket=#{ticket}"})
    void updateStatus(@Param("ticket") String ticket, @Param("status") int status);
}

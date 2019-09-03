package com.ben.dao;

import com.ben.model.Question;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @ClassName: QuestionDAO
 * @author: benjamin
 * @createTime: 2019/08/17/09:09
 * 查询方法比较复杂的时候借助于xml,比如selectLatestQuestions
 */
@Mapper
public interface QuestionDAO {
    String TABLE_NAME = " question ";
    String INSERT_FIELDS = " title, content, created_date, user_id, comment_count ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;


    // 增加问题
    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS, " " +
            " ) values (#{title},#{content},#{createdDate},#{userId},#{commentCount})"})
    int addQuestion(Question question);


    // xml配置，查询用户最新的提问，也就是说需要在表中根据条件检索问题
    List<Question> selectLatestQuestions(@Param("userId") int userId, @Param("offset") int offset,
                                         @Param("limit") int limit);

    // 根据问题id查
    @Select("select * from question where id = #{id}")
    Question selectById(int id);

    // 更新问题评论的数目
    @Update({"update ", TABLE_NAME, " set comment_count = #{commentCount} where id=#{id}"})
    int updateCommentCount(@Param("id") int id, @Param("commentCount") int commentCount);

    // 根据问题评论数目倒序查问题
    @Select("select * from question order by comment_count desc limit #{limit} , #{offset}")
    List<Question> selectByCommentCount(@Param("limit")int limit,@Param("offset")int offset);


}

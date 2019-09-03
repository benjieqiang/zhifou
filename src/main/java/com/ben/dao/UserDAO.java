package com.ben.dao;

import com.ben.model.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @ClassName: UserDAO
 * @author: benjamin
 * @createTime: 2019/08/17/09:09
 * 用户表的持久层操作，增删改查
 */
@Mapper
public interface UserDAO {
    // 我们将代码中的公共字段抽取出来，这样便于修改字段
    String TABLE_NAME = " user ";
    String INSERT_FIELDS = " name,password,salt,head_url";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    // 增加用户
    @Insert({"insert into", TABLE_NAME, "(",INSERT_FIELDS,
            ") values (#{name},#{password},#{salt},#{headUrl}) "})
    int addUser(User user);

    @Delete({"delete from ",TABLE_NAME, "where id = #{id}"})
    // 删除用户
    void deleteById(int id);

    // 修改用户密码
    @Update({"udpate ", TABLE_NAME, " set password=#{password} where id = #{id}"})
    void updatePassword(int id);
    // 根据用户ID查询
    @Select({"select ",SELECT_FIELDS,"from",TABLE_NAME," where id=#{id}"})
    User selectById(int id);

    // 根据用户名查询

    @Select({"select ",SELECT_FIELDS,"from",TABLE_NAME," where name= #{name}"})
    User selectByName(String name);

    // 遍历输出User
    @Select("select * from user")
    List<User> findAll();

}

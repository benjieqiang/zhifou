package com.ben;

import com.ben.dao.MessageDAO;
import com.ben.dao.QuestionDAO;
import com.ben.dao.UserDAO;
import com.ben.model.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Date;

/**
 * @ClassName: InitDatabaseTests
 * @author: benjamin
 * @version: 1.0
 * @description: 测试给数据库添加数据，同时查询数据
 * @createTime: 2019/08/17/15:03
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WendaApplication.class)
public class InitDatabaseTests {

    @Autowired
    UserDAO userDAO;

    @Autowired
    QuestionDAO questionDAO;

    @Autowired
    HostHolder hostHolder;
    @Autowired
    MessageDAO messageDAO;
    @Test
    public void contextLoads(){

        Random random  = new Random();
        // 利用for循环生成10个随机用户
        for(int i = 0;i<10;i++){
            User user = new User();
            user.setName(String.format("USER%d",i));
            user.setPassword("");
            user.setSalt("");
            user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", random.nextInt(1000)));
            userDAO.addUser(user);

            // 对问题表中的内容进行填充
            Question question = new Question();
            question.setTitle(String.format("如何评价Title%d",i));
            question.setContent(String.format("哈哈哈哈哈，知否知否%d",i));
            question.setUserId(i);
            question.setCreatedDate(new Date());
            question.setCommentCount(random.nextInt(10000));
            questionDAO.addQuestion(question);
        }

//        // 遍历输出用户
//        List<User> users = userDAO.findAll();
//        for(User user:users){
//            System.out.println(user);
//        }
//
//        // 修改用户密码
//        User user = new User();
//        user.setPassword("newpassword");
//        userDAO.updatePassword(1);


    }

    @Test
    public void testRandom(){
        Random random  = new Random();
        String headurl = String.format("http://images.nowcoder.com/head/%dt.png", random.nextInt(1000));

        System.out.println(headurl);//http://images.nowcoder.com/head/803t.png

    }

    @Test
    public void questionTest(){
        List<Question> questions = questionDAO.selectLatestQuestions(1, 2, 3);
        for(Question question:questions){
            System.out.println(question);
        }

    }

    @Test
    public void testMessageDAO(){
        List<Message> list = messageDAO.getConversationList(1010, 0, 10);
        for(Message msg:list){
            System.out.println(msg);
        }

    }


}

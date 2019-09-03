package com.ben;

import com.ben.dao.CommentDAO;
import com.ben.model.Comment;
import com.ben.model.ViewObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: WendaApplicationTests
 * @author: benjamin
 * @version: 1.0
 * @description: TODO
 * @createTime: 2019/08/17/09:08
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes=WendaApplication.class)
@WebAppConfiguration
public class WendaApplicationTests {

    @Autowired
    CommentDAO commentDAO;

    @Test
    public void contextLoads() {
    }

    @Test
    public void testselectByEntity(){
        List<Comment> comments = commentDAO.selectByEntity(14, 1);
//        List<ViewObject> vos = new ArrayList<>();
        for (Comment comment : comments) {
            ViewObject vo = new ViewObject();
            vo.set("comment", comment);
//            vos.add(vo);
            System.out.println(vo);
        }
    }

    // 定时
    public static void main(String[] args) {
        Runnable runnable = new Runnable() {
            public void run() {
                // task to run goes here
                System.out.println("Hello !!");
            }
        };
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        // 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
        //scheduleAtFixedRate(TimerTask task, long delay, long period)：
        // 安排指定的任务在指定的延迟后开始进行重复的固定速率执行。
        service.scheduleAtFixedRate(runnable, 10, 1, TimeUnit.SECONDS);
    }
}

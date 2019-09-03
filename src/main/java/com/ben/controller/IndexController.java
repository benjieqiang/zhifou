package com.ben.controller;

import com.ben.dao.UserDAO;
import com.ben.model.*;
import com.ben.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: IndexController
 * @author: benjamin
 * @version: 1.0
 * @description: 首页的表现层
 * @createTime: 2019/08/17/09:12
 */

@Controller
public class IndexController {

    // 记录日志
    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    QuestionService questionService;

    @Autowired
    UserService userService;

    @Autowired
    FollowService followService;

    @Autowired
    CommentService commentService;
    @Autowired
    HostHolder hostHolder;

    // 在首页显示该用户的评论点赞数目
    @Autowired
    LikeService likeService;

    // 首页
    /*
        TODO:
         1.首页最新动态中，用户头像的显示，
         2.首页遍历出每个用户答题显示用户的ID，用户的签名，用户的答题内容，评论信息
         3.首页用户如果未登录的话，只显示登录注册按钮，如果登录了话，用户头像替代
    */

    /**
     * @Description: getQuestion作用是查询question表中的数据，存入ViewObject中
     * @Param: [userId, offset, limit] 用户id, 从第几个开始，查多少个数据
     * @return: 返回一个List的集合
     * @Author: benjamin
     * @Date: 2019/8/17
     */
    private List<ViewObject> getQuestions(int userId, int offset, int limit) {
        List<Question> questionList = questionService.getLatestQuestions(userId, offset, limit);
        List<ViewObject> vos = new ArrayList<>();
        for (Question question : questionList) {
            ViewObject vo = new ViewObject();
            vo.set("question", question);
            vo.set("followCount", followService.getFollowerCount(EntityType.ENTITY_QUESTION, question.getId()));
            vo.set("user", userService.getUser(question.getUserId()));
            vos.add(vo);
        }
        return vos;
    }

    // 用户访问首页，即可以看到10条问题信息
    @RequestMapping(value = {"/", "/index"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String index(Model model) {

        model.addAttribute("vos", getQuestions(0, 0, 10));
        return "index";
    }

    // 用户输入/user/1 可以看到该用户回答的问题
    @RequestMapping(path = {"/user/{userId}"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String userIndex(Model model, @PathVariable("userId") int userId) {
        model.addAttribute("vos", getQuestions(userId, 0, 10));

        User user = userService.getUser(userId);
        ViewObject vo = new ViewObject();
        vo.set("user", user);
        vo.set("commentCount", commentService.getUserCommentCount(userId));
        vo.set("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, userId));
        vo.set("followeeCount", followService.getFolloweeCount(userId, EntityType.ENTITY_USER));
        if (hostHolder.getUser() != null) {
            vo.set("followed", followService.isFollower(hostHolder.getUser().getId(), EntityType.ENTITY_USER, userId));
        } else {
            vo.set("followed", false);
        }
        model.addAttribute("profileUser", vo);
        return "profile";
    }

    // 用户访问排名页
    @RequestMapping(value = {"/rank"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String rank(Model model) {
        model.addAttribute("vos", getQuestionsByCommentCount(0, 9));
        return "rank";
    }

    /**
     * @Description: 首页中问题的排序算法
     * Score= (P-1)/(T+1.5) ^G
     * P：问题评论数
     * T：单位小时。+1.5小时
     * G: 重力加速度，分值根据时间降低速率，选择1.8
     * @Param: [userId, qid] 谁评论的，评论给哪个问题
     * @return:
     * @Author: benjamin
     * @Date: 2019/8/28
     */
    private long hackNewsSort(Question question) {
        // 定义一个权重；
        double score = 0;
        // 返回结果
        long result = 0;

        // 获取该问题的评论人数
        int commentCount = question.getCommentCount();
        // 如果大于1，先将自己的那一份给排除掉；
        if (commentCount > 1) {
            long p = commentCount - 1;
        }
        // 自定义一个重力加速度G
        double gravity = 1.5;

        // 拿到一组问题，遍历将这组问题的id插入到redis中；
        // 计算score值
        try {
            int qid = question.getId();
            SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");//如2019-08-10 20:40
            String postDate = simpleFormat.format(question.getCreatedDate());
            String nowDate = simpleFormat.format(new Date());

            long from = simpleFormat.parse(postDate).getTime();
            long to = simpleFormat.parse(nowDate).getTime();
            int hours = (int) ((to - from) / (1000 * 60 * 60));
            System.out.println(hours);
            // 默认加 1.5
            double t = hours + 1;
//            System.out.println(t + "时间");
            // 计算score值
            score = commentCount / Math.pow(t, gravity);
            System.out.println(score);
            // 放入zset中；zadd key score1 member1 score2 member2 ...
            result = questionService.addQuetionByScore(score, qid);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;

    }

    // 拿到评论前10的问题，
    private List<ViewObject> getQuestionsByCommentCount(int offset, int limit) {
        List<Question> questionList = questionService.getQuestionsByCommentCount(0, 9);
//        System.out.println(questionList);
        List<ViewObject> vos = new ArrayList<>();
        System.out.println(vos);
        // 插入到Zset中
        // 这里将userId存入队列中
        Queue<Integer> userlist = new LinkedList<>();
        for (Question que : questionList) {
            hackNewsSort(que);
            userlist.add(que.getUserId());
        }
        // 添加问题到vo中；
        Set<String> set = questionService.getQuetionByScore();
        for (String qid : set) {
//            System.out.print(qid);
            // 通过该问题qid拿到回答问题的人
            int id = Integer.parseInt(qid);
            ViewObject vo = new ViewObject();
            // 先放入集合中，记录score值
            vo.set("question", questionService.selectById(id));
            vo.set("followCount", followService.getFollowerCount(EntityType.ENTITY_QUESTION, id));
            vo.set("user", userService.getUser(userlist.poll()));
            vos.add(vo);
        }
//        System.out.println(vos);
        return vos;
    }
}

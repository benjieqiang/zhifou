package com.ben.service;

import com.ben.dao.QuestionDAO;
import com.ben.model.Question;
import com.ben.util.JedisAdapter;
import com.ben.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;
import java.util.Set;

/**
 * @ClassName: QuestionService
 * @author: benjamin
 * @version: 1.0
 * @description: 问题的service层
 * @createTime: 2019/08/17/09:11
 */
@Service
public class QuestionService {

    @Autowired
    QuestionDAO questionDAO;

    @Autowired
    SensitiveService sensitiveService;

    @Autowired
    JedisAdapter jedisAdapter;

    // 查询最新的问题
    public List<Question> getLatestQuestions(int userId, int offset, int limit) {
        return questionDAO.selectLatestQuestions(userId, offset, limit);
    }

    // 增加问题，在增加之前需要进行xss过滤与敏感词过滤
    public int addQuestion(Question question) {
        // xss过滤
        question.setTitle(HtmlUtils.htmlEscape(question.getTitle()));
        question.setContent(HtmlUtils.htmlEscape(question.getContent()));

        // 敏感词过滤（还未实现）
//        question.setTitle(sensitiveService.filter(question.getTitle()));
//        question.setContent(sensitiveService.filter(question.getContent()));

        return questionDAO.addQuestion(question) > 0 ? question.getUserId() : 0;
    }

    // 通过ID查询对应的表数据
    public Question selectById(int id) {
        return questionDAO.selectById(id);
    }

    public int updateCommentCount(int id, int count) {
        return questionDAO.updateCommentCount(id, count);
    }


    // 自己添加的功能
    public List<Question> getQuestionsByCommentCount(int limit,int offset) {
        return questionDAO.selectByCommentCount(limit,offset);
    }

    // 在redis中存入该问题的score值；
    public long addQuetionByScore(double score,int qid){
        return jedisAdapter.zadd("QUESTION:SCORE",score,String.valueOf(qid));
    }
    // 读取redis中的排序好的问题
    public Set<String> getQuetionByScore(){
        return jedisAdapter.zrevrange("QUESTION:SCORE",0,9);
    }

}

package com.ben.service;

import com.ben.dao.MessageDAO;
import com.ben.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName: MessageService
 * @author: benjamin
 * @version: 1.0
 * @description: 消息处理业务层
 * @createTime: 2019/08/20/17:05
 */
@Service
public class MessageService {

    @Autowired
    MessageDAO messageDAO;

    // 增加消息
    // 这里需要进行敏感词过滤
    public int addMessage(Message message){
        return messageDAO.addMessage(message);
    }

    // 获取该用户与某用户的消息详情页
    public List<Message> getConversationDetail(String conversationId, int offset, int limit) {
        return messageDAO.getConversationDetail(conversationId, offset, limit);
    }
    // 获取该用户的消息列表
    public List<Message> getConversationList(int userId, int offset, int limit) {
        return messageDAO.getConversationList(userId, offset, limit);
    }

    // 获取该用户未读消息的数目，即就是查询数据表中status=0的数目
    public int getConversationUnreadCount(int userId, String conversationId) {
        return messageDAO.getConversationUnreadCount(userId, conversationId);
    }
}

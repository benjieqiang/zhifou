package com.ben.async.handler;


import com.ben.async.EventHandler;
import com.ben.async.EventModel;
import com.ben.async.EventType;
import com.ben.model.Message;
import com.ben.model.User;
import com.ben.service.MessageService;
import com.ben.service.UserService;
import com.ben.util.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * like事件的处理者
 */
@Component
public class LikeHandler implements EventHandler {
    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Override
    public void doHandle(EventModel model) {
        Message message = new Message();
        message.setFromId(WendaUtil.SYSTEM_USERID); // 这里用管理员的id 是4；
        message.setToId(model.getEntityOwnerId()); // 发给谁，
        message.setCreatedDate(new Date());
        User user = userService.getUser(model.getActorId());
        message.setContent("用户" + user.getName()
                + "赞了你的评论,http://127.0.0.1:8080/question/" + model.getExt("questionId"));

        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LIKE);
    }
}

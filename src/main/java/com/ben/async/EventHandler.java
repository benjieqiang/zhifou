package com.ben.async;

import java.util.List;

/**
 * 处理事件的接口
 */
public interface EventHandler {
    // 每个handler处理事件的方法
    void doHandle(EventModel model);
    // 注册自己，让别人知道自己是关注哪些事件的；
    List<EventType> getSupportEventTypes();
}

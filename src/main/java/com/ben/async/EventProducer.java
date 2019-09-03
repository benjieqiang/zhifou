package com.ben.async;

import com.alibaba.fastjson.JSONObject;
import com.ben.util.JedisAdapter;
import com.ben.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by nowcoder on 2016/7/30.
 */
@Service
public class EventProducer {
    @Autowired
    JedisAdapter jedisAdapter;

    // 发送事件 eventModel，保存到redis中
    public boolean fireEvent(EventModel eventModel) {
        try {
            // 先进行序列化，转为字符串；
            String json = JSONObject.toJSONString(eventModel);
            // 队列的key
            String key = RedisKeyUtil.getEventQueueKey();
            jedisAdapter.lpush(key, json);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

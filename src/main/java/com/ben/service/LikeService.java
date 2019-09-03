package com.ben.service;

import com.ben.util.JedisAdapter;
import com.ben.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName: LikeService
 * @author: benjamin
 * @version: 1.0
 * @description: 点赞的业务层
 * @createTime: 2019/08/22/17:34
 */

@Service
public class LikeService {

    @Autowired
    JedisAdapter jedisAdapter;

    // 获取某个评论点赞的总数目
    public long getLikeCount(int entityType, int entityId) {
        // 拿到对应的key
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        // 用key去找点赞的数目
        return jedisAdapter.scard(likeKey);
    }

    // 获取该用户点赞还是点了踩，点赞返回1，点踩返回-1，否则返回0
    public int getLikeStatus(int userId, int entityType, int entityId) {
        // 首先拿到key
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        // 如果该用户在like这个key里面，就返回1
        if (jedisAdapter.sismember(likeKey, String.valueOf(userId))) {
            return 1;
        }
        // 查询该用户是否在dislike中也有，如果有返回-1
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
        return jedisAdapter.sismember(disLikeKey, String.valueOf(userId)) ? -1 : 0;
    }

    // 用户点击like，先将是哪个用户点的赞放到响应type（比如评论）中，如果该用户点过dislike，取消踩，也就是说用户点赞和点踩的业务不能同时出现，统计出有多少人赞了这个评论
    public long like(int userId, int entityType, int entityId) {
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        jedisAdapter.sadd(likeKey, String.valueOf(userId));

        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
        jedisAdapter.srem(disLikeKey, String.valueOf(userId));

        return jedisAdapter.scard(likeKey);
    }

    // 用户点击dislike，先加入哪些用户踩了，然后将该用户点击赞减1，返回该评论的点赞数
    public long disLike(int userId, int entityType, int entityId) {
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
        jedisAdapter.sadd(disLikeKey, String.valueOf(userId));

        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        jedisAdapter.srem(likeKey, String.valueOf(userId));

        return jedisAdapter.scard(likeKey);
    }


}

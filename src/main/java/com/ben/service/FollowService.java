package com.ben.service;

import com.ben.util.JedisAdapter;
import com.ben.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @ClassName: FollowService
 * @author: benjamin
 * @version: 1.0
 * @description: 关注与被关注服务
 * @createTime: 2019/08/22/16:09
*/
@Service
public class FollowService {
    @Autowired
    JedisAdapter jedisAdapter;

    /**
     * @Description:  用户关注了某个实体,可以关注问题,关注用户,关注评论等任何实体
     * @Param: [userId, entityType, entityId] 谁关注了，关注了什么类型》该类型的id是多少
     * @return: boolean 成功返回true
     * @Author: benjamin
     * @Date: 2019/8/28
     */
    public boolean follow(int userId, int entityType, int entityId) {

        // 获取某个实体的粉丝key
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        // 获取关注对象的key
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        Date date = new Date();

        Jedis jedis = jedisAdapter.getJedis();
        // 开启事务
        Transaction tx = jedisAdapter.multi(jedis);
        // 实体的粉丝增加当前用户
        tx.zadd(followerKey, date.getTime(), String.valueOf(userId));
        // 当前用户对这类实体关注+1，比如说对某个问题的关注数目+1
        tx.zadd(followeeKey, date.getTime(), String.valueOf(entityId));
        // 提交事务
        List<Object> ret = jedisAdapter.exec(tx, jedis);
        return ret.size() == 2 && (Long) ret.get(0) > 0 && (Long) ret.get(1) > 0;
    }

    /**
     * @Description: 取消关注，某个实体的粉丝列表删除该用户id，该用户的关注（这类）列表中删除该实体的id。
     * @Param: [userId, entityType, entityId]
     * @return: boolean
     * @Author: benjamin
     * @Date: 2019/8/28
     */
    public boolean unfollow(int userId, int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        Date date = new Date();
        Jedis jedis = jedisAdapter.getJedis();
        Transaction tx = jedisAdapter.multi(jedis);
        // 实体的粉丝列表删除当前用户
        tx.zrem(followerKey, String.valueOf(userId));
        // 当前用户对这类实体关注-1
        tx.zrem(followeeKey, String.valueOf(entityId));
        List<Object> ret = jedisAdapter.exec(tx, jedis);
        return ret.size() == 2 && (Long) ret.get(0) > 0 && (Long) ret.get(1) > 0;
    }
    // 获取当前用户的粉丝集合
    public List<Integer> getFollowers(int entityType, int entityId, int count) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return getIdsFromSet(jedisAdapter.zrevrange(followerKey, 0, count));
    }
    // 返回某个范围内的粉丝集合
    public List<Integer> getFollowers(int entityType, int entityId, int offset, int count) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return getIdsFromSet(jedisAdapter.zrevrange(followerKey, offset, offset+count));
    }
    // 关注对象的集合
    public List<Integer> getFollowees(int userId, int entityType, int count) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return getIdsFromSet(jedisAdapter.zrevrange(followeeKey, 0, count));
    }
    // 返回某个范围内的关注对象的集合
    public List<Integer> getFollowees(int userId, int entityType, int offset, int count) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return getIdsFromSet(jedisAdapter.zrevrange(followeeKey, offset, offset+count));
    }
    // 获取某个实体的总粉丝数目
    public long getFollowerCount(int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return jedisAdapter.zcard(followerKey);
    }
    // 获取关注列表的总数目
    public long getFolloweeCount(int userId, int entityType) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return jedisAdapter.zcard(followeeKey);
    }
    //  将字符串集合转为整数类型的列表
    private List<Integer> getIdsFromSet(Set<String> idset) {
        List<Integer> ids = new ArrayList<>();
        for (String str : idset) {
            ids.add(Integer.parseInt(str));
        }
        return ids;
    }

    /**
     *  判断用户是否关注了某个实体
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    public boolean isFollower(int userId, int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return jedisAdapter.zscore(followerKey, String.valueOf(userId)) != null;
    }
}

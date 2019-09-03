package com.ben.util;

/**
 * @ClassName: RedisKeyUtil
 * @author: benjamin
 * @version: 1.0
 * @description: Redis的key的形式
 * @createTime: 2019/08/22/17:39
 */

public class RedisKeyUtil {
    private static String SPLIT = ":";
    // 用户的点赞和点踩
    private static String BIZ_LIKE = "LIKE";
    private static String BIZ_DISLIKE = "DISLIKE";
    // 事件队列名称
    private static String BIZ_EVENTQUEUE = "EVENT_QUEUE";

    // 获取粉丝
    private static String BIZ_FOLLOWER = "FOLLOWER";
    // 关注对象
    private static String BIZ_FOLLOWEE = "FOLLOWEE";
    private static String BIZ_TIMELINE = "TIMELINE";

    // 传入评论的标识和评论id,数据库中存放的形式：LIKE:2:4583，2表示评论type,4583是该评论的id
    public static String getLikeKey(int entityType, int entityId) {
        return BIZ_LIKE + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
    }
    // 不喜欢的类型的key
    public static String getDisLikeKey(int entityType, int entityId) {
        return BIZ_DISLIKE + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
    }

    public static String getEventQueueKey() {
        return BIZ_EVENTQUEUE;
    }


    // 某个实体的粉丝key，这个实体可以是问题，可以是某个人。比如对问题的关注FOLLOWER:1:16
    public static String getFollowerKey(int entityType, int entityId) {
        return BIZ_FOLLOWER + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
    }

    // 每个用户对某类实体的关注key：FOLLOWEE:1000:1解释：1000表示用户id，1表示问题；
    public static String getFolloweeKey(int userId, int entityType) {
        return BIZ_FOLLOWEE + SPLIT + String.valueOf(userId) + SPLIT + String.valueOf(entityType);
    }

    public static String getTimelineKey(int userId) {
        return BIZ_TIMELINE + SPLIT + String.valueOf(userId);
    }

}

package com.ben;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ben.model.User;
import redis.clients.jedis.BinaryClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

/**
 * @ClassName: JedisTests
 * @author: benjamin
 * @version: 1.0
 * @description: TODO
 * @createTime: 2019/08/22/16:13
 */

public class JedisTests {
    public static void main(String[] args) {
        // 填自己redis服务器的IP和端口
        // 格式：redis://ip:6379/要用的redis数据库
        Jedis jedis = new Jedis("redis://192.168.214.128:6379/2");
        jedis.flushDB();

        // set get
        jedis.set("name","benjie");
        print(1,jedis.get("name"));
        // 重命名key
        jedis.rename("name", "ben");
        //print(1, jedis.get("name")); // 报错
        print(1, jedis.get("ben"));
        // 设置键值及过期时间，以秒为单位
        jedis.setex("hello2", 3, "world");

        // 自增
        jedis.set("pv", "100");
        jedis.incr("pv");
        jedis.incrBy("pv", 5); // 步进值为5
        print(2, jedis.get("pv"));
        jedis.decrBy("pv", 2); // 减少2
        print(2, jedis.get("pv"));

        // keys *
        print(3, jedis.keys("*"));

        // 插入一个list，lpush key value1 在左侧插⼊数据
        String listName = "list";
        jedis.del(listName); // 如果list存在先删除
        for (int i = 0; i < 10; ++i) {
            jedis.lpush(listName, "a" + String.valueOf(i));
        }
        // lrange key start stop
        print(4, jedis.lrange(listName, 0, 12));
        // 列表所有元素
        print(4, jedis.lrange(listName, 0, -1));
        // 列表长度
        print(5, jedis.llen(listName));
        // lpop listName 从左边弹lpop元素
        print(6, jedis.lpop(listName));
        print(7, jedis.llen(listName));
        print(8, jedis.lrange(listName, 2, 6));
        // 获取指定位置的元素 lindex key index
        print(9, jedis.lindex(listName, 3));
        // linsert key BEFORE|AFTER pivot value
        print(10, jedis.linsert(listName, BinaryClient.LIST_POSITION.AFTER, "a4", "xx"));
        print(10, jedis.linsert(listName, BinaryClient.LIST_POSITION.BEFORE, "a4", "bb"));
        print(11, jedis.lrange(listName, 0 ,12));

        // hash:对象属性，不定长属性数
        String userKey = "userxx";
        // hset key field value
        jedis.hset(userKey, "name", "jim");
        jedis.hset(userKey, "age", "12");
        jedis.hset(userKey, "phone", "18618181818");
        // hget key field 获取⼀个属性的值
        print(12, jedis.hget(userKey, "name"));
        // hgetAll key  : 获取属性和值
        print(13, jedis.hgetAll(userKey));
        jedis.hdel(userKey, "phone");
        print(14, jedis.hgetAll(userKey));
        // 判断是否存在field
        print(15, jedis.hexists(userKey, "email"));
        print(16, jedis.hexists(userKey, "age"));
        // hkeys key
        print(17, jedis.hkeys(userKey));
        print(18, jedis.hvals(userKey));
        // hsetnx hahs field value
        // field不存在，值设置为 value；field存在，命令不执行
        //如果哈希表 hash 不存在， 那么一个新的哈希表将被创建并执行 HSETNX 命令。
        jedis.hsetnx(userKey, "school", "zju");
        jedis.hsetnx(userKey, "name", "yxy");
        print(19, jedis.hgetAll(userKey));

        // set 无顺序集合。点赞，抽奖，共同好友。唯一性，不重复
        String likeKey1 = "commentLike1";
        String likeKey2 = "commentLike2";
        // sadd key member1 member2 ...
        for (int i = 0; i < 10; ++i) {
            jedis.sadd(likeKey1, String.valueOf(i));
            jedis.sadd(likeKey2, String.valueOf(i*i));
        }
        // smembers key 返回所有的元素
        print(20, jedis.smembers(likeKey1));
        print(21, jedis.smembers(likeKey2));
        // 并
        print(22, jedis.sunion(likeKey1, likeKey2));
        //  前面有，后面没有
        print(23, jedis.sdiff(likeKey1, likeKey2));
        // 交集
        print(24, jedis.sinter(likeKey1, likeKey2));
        // 是否存在某个值
        print(25, jedis.sismember(likeKey1, "12"));
        print(26, jedis.sismember(likeKey2, "16"));
        // 删除指定元素
        jedis.srem(likeKey1, "5");
        print(27, jedis.smembers(likeKey1));

        jedis.smove(likeKey2, likeKey1, "25");
        print(28, jedis.smembers(likeKey1));
        // 集合的数量。 当集合 key 不存在时，返回 0 。
        print(29, jedis.scard(likeKey1));
        // 用于返回集合中的一个随机元素。 抽奖
        // 如果 count 为正数，且小于集合基数，那么命令返回一个包含 count 个元素的数组，数组中的元素各不相同。如果 count 大于等于集合基数，那么返回整个集合。
        //如果 count 为负数，那么命令返回一个数组，数组中的元素可能会重复出现多次，而数组的长度为 count 的绝对值。
        print(29,jedis.srandmember(likeKey1,2));


        // 有序集合
        String rankKey = "rankKey";
        // zadd key score1 member1 score2 member2 ...
        jedis.zadd(rankKey, 15, "jim");
        jedis.zadd(rankKey, 60, "Ben");
        jedis.zadd(rankKey, 90, "Lee");
        jedis.zadd(rankKey, 75, "Lucy");
        jedis.zadd(rankKey, 80, "Mei");
        print(222,jedis.zrange(rankKey,0,-1));
        //获取有序集合的成员数
        print(30, jedis.zcard(rankKey));
        // 在有序集合中计算指定字典区间内成员数量
        print(31, jedis.zcount(rankKey, 61, 100));
        // 返回有序集中，成员的分数值
        print(32, jedis.zscore(rankKey, "Lucy"));
        //	ZINCRBY key increment member
        //有序集合中对指定成员的分数加上增量 increment
        jedis.zincrby(rankKey, 2, "Lucy");
        print(33, jedis.zscore(rankKey, "Lucy"));
        jedis.zincrby(rankKey, 2, "Luc");
        print(34, jedis.zscore(rankKey, "Luc"));
        print(35, jedis.zrange(rankKey, 0, 100));
        print(36, jedis.zrange(rankKey, 0, 10));
        print(36, jedis.zrange(rankKey, 1, 3));

        //返回有序集中指定区间内的成员，通过索引，分数从高到底
        print(36, jedis.zrevrange(rankKey, 1, 3));
        for (Tuple tuple : jedis.zrangeByScoreWithScores(rankKey, "60", "100")) {
            print(37, tuple.getElement() + ":" + String.valueOf(tuple.getScore()));
        }

        print(38, jedis.zrank(rankKey, "Ben"));
        print(39, jedis.zrevrank(rankKey, "Ben"));

        String setKey = "zset";
        jedis.zadd(setKey, 1, "a");
        jedis.zadd(setKey, 1, "b");
        jedis.zadd(setKey, 1, "c");
        jedis.zadd(setKey, 1, "d");
        jedis.zadd(setKey, 1, "e");

        print(40, jedis.zlexcount(setKey, "-", "+"));
        print(41, jedis.zlexcount(setKey, "(b", "[d"));
        print(42, jedis.zlexcount(setKey, "[b", "[d"));
        jedis.zrem(setKey, "b");
        print(43, jedis.zrange(setKey, 0, 10));
        jedis.zremrangeByLex(setKey, "(c", "+");
        print(44, jedis.zrange(setKey, 0 ,2));

        /*
         redis池，默认8个，
        JedisPool pool = new JedisPool();
        for (int i = 0; i < 100; ++i) {
            Jedis j = pool.getResource();
            print(45, j.get("pv"));
            j.close();
        }*/

        // 存入对象
        User user = new User();
        user.setName("xx");
        user.setPassword("ppp");
        user.setHeadUrl("a.png");
        user.setSalt("salt");
        user.setId(1);
        print(46, JSONObject.toJSONString(user));
        jedis.set("user1", JSONObject.toJSONString(user));

        // 取出对象
        String value = jedis.get("user1");
        User user2 = JSON.parseObject(value, User.class);
        print(47, user2);

    }
    // 控制行打印
    private static void print(int index, Object obj) {
        System.out.println(String.format("%d, %s", index, obj.toString()));
    }
}

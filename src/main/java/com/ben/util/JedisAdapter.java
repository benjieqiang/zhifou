package com.ben.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * @ClassName: JedisAdapter
 * @author: benjamin
 * @version: 1.0
 * @description: 处理redis的工具类
 * @createTime: 2019/08/22/16:09
 */

@Service
public class JedisAdapter implements InitializingBean {

    String name = "java";
    private static final Logger logger = LoggerFactory.getLogger(JedisAdapter.class);
    private JedisPool pool;

    @Override
    public void afterPropertiesSet() throws Exception {
        pool = new JedisPool("redis://192.168.214.128:6379/2");
    }

    /**
     * @Description: 获取线程池
     * @Param: []
     * @return: redis.clients.jedis.Jedis
     * @Author: benjamin
     * @Date: 2019/8/28
     */
    public Jedis getJedis() {
        return pool.getResource();
    }
    /**
     * @Description: 完成set 无序集合的添加
     * @Param: [key, value]
     * @return: long
     * @Author: benjamin
     * @Date: 2019/8/22
     */
    public long sadd(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.sadd(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    /**
     * @Description: set的删除
     * @Param: [key, value]
     * @return: long
     * @Author: benjamin
     * @Date: 2019/8/22
     */
    public long srem(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.srem(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    /**
     * @Description: 返回集合的数量，当集合 key 不存在时，返回 0 。
     * @Param: [key]
     * @return: long
     * @Author: benjamin
     * @Date: 2019/8/22
     */
    public long scard(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.scard(key);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    /**
     * @Description: 集合中是否存在某个值
     * @Param: [key, value]
     * @return: boolean 1：存在 ，0 不存在
     * @Author: benjamin
     * @Date: 2019/8/22
     */
    public boolean sismember(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sismember(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return false;
    }

    /**
     * @Description: RPOP key 命令的阻塞版本，当给定列表内没有任何元素可供弹出的时候，
     * 连接将被 BRPOP 命令阻塞，直到等待超时或发现可弹出元素为止。
     * @Param: [timeout, key]
     * @return: List
     * @Author: benjamin
     * @Date: 2019/8/22
     */
    public List<String> brpop(int timeout, String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.brpop(timeout, key);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    /**
     * @Description: list列表从左插入数据
     * @Param: [key, value]
     * @return: long
     * @Author: benjamin
     * @Date: 2019/8/22
     */
    public long lpush(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lpush(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    // 关注与被关注服务，用到zset

    /**
     * @Description: 集合中添加 zadd key score value
     * @Param: [key, score, value]
     * @return: long
     * @Author: benjamin
     * @Date: 2019/8/28
     */
    public long zadd(String key, double score, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zadd(key, score, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    /**
     * @Description: 删除key下的值
     * @Param: [key, value]
     * @return: long
     * @Author: benjamin
     * @Date: 2019/8/28
     */
    public long zrem(String key, String value) {

        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrem(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    /**
     * @Description: 事务块内的多条命令会按照先后顺序被放进一个队列当中，
     *               最后由 EXEC 命令原子性(atomic)地执行。
     * @Param: [jedis]
     * @return: redis.clients.jedis.Transaction
     * @Author: benjamin
     * @Date: 2019/8/28
     */
    public Transaction multi(Jedis jedis) {
        try {
            return jedis.multi();
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
        }
        return null;
    }

    /**
     * @Description: 执行所有事务块内的命令。
     * @Param: [tx, jedis]
     * @return: java.util.List<java.lang.Object>
     * @Author: benjamin
     * @Date: 2019/8/28
     */
    public List<Object> exec(Transaction tx, Jedis jedis) {
        try {
            return tx.exec();
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
            tx.discard();
        } finally {
            if (tx != null) {
                try {
                    tx.close();
                } catch (IOException ioe) {
                    // ..
                }
            }

            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    /**
     * @Description:  返回指定范围内的元素
     * @Param: [key, start, end]
     * @return: java.util.Set<java.lang.String>
     * @Author: benjamin
     * @Date: 2019/8/28
     */
    public Set<String> zrange(String key, int start, int end) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrange(key, start, end);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    /**
     * @Description: 逆序返回指定范围内的元素
     * @Param: [key, start, end]
     * @return: java.util.Set<java.lang.String>
     * @Author: benjamin
     * @Date: 2019/8/28
     */
    public Set<String> zrevrange(String key, int start, int end) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrevrange(key, start, end);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    /**
     * @Description: 用于计算集合中响应key的元素的数量
     * @Param: [key] key
     * @return: long
     * @Author: benjamin
     * @Date: 2019/8/28
     */
    public long zcard(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zcard(key);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    /**
     * @Description: 返回有序集中，成员的分数值，值在redis中以字符串的形式存储；
     * @Param: [key, member]
     * @return: java.lang.Double
     * @Author: benjamin
     * @Date: 2019/8/28
     */
    public Double zscore(String key, String member) {

        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zscore(key, member);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

}

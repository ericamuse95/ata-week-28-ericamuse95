package com.kenzie.caching.goodreads.caching;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;

import javax.inject.Inject;

public class CacheClient {

    private JedisPool jedisPool;
    @Inject
    public CacheClient(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public void setValue(String key, String value, int ttl) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.setex(key, ttl, value);
        }
    }
    public String getValue(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
        return  jedis.get(key);
        }
    }
    public void delete(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(key);
        }
    }


}

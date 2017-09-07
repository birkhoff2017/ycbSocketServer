package com.ycb.socket.service;


import com.zipeiyi.xpower.cache.IRedis;
import com.zipeiyi.xpower.cache.impl.RedisAutoConfigCacheFactory;
import org.springframework.stereotype.Service;

/**
 * Created by zhuhui on 16-12-8.
 */
@Service
public class RedisService {
    RedisAutoConfigCacheFactory redisAutoConfigCacheFactory = RedisAutoConfigCacheFactory.getInstance();

    public IRedis getRedis() {
        return redisAutoConfigCacheFactory.getCache("ycb");
    }

}

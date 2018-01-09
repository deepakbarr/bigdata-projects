package com.demo.storm.excercise;

import org.apache.storm.redis.common.mapper.RedisDataTypeDescription;
import org.apache.storm.redis.common.mapper.RedisStoreMapper;
import org.apache.storm.tuple.ITuple;

/**
 * Created by dbarr on 12/19/17.
 */
public class MyRedisDataMapper implements RedisStoreMapper {
    private RedisDataTypeDescription description;
    private final String hashKey = "key";
    private String KEY_PREFIX;

    public MyRedisDataMapper(String keyPrefix) {
        description = new RedisDataTypeDescription(
                RedisDataTypeDescription.RedisDataType.STRING, hashKey);
        this.KEY_PREFIX = keyPrefix;
    }

    @Override
    public RedisDataTypeDescription getDataTypeDescription() {
        return description;
    }

    @Override
    public String getKeyFromTuple(ITuple tuple) {
        return KEY_PREFIX.concat(tuple.getStringByField("key"));
    }

    @Override
    public String getValueFromTuple(ITuple tuple) {
        return tuple.getStringByField("value");
    }
}

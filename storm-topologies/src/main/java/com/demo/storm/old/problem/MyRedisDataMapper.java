package com.demo.storm.old.problem;

import org.apache.storm.redis.common.mapper.RedisDataTypeDescription;
import org.apache.storm.redis.common.mapper.RedisStoreMapper;
import org.apache.storm.tuple.ITuple;

/**
 * Created by dbarr on 12/19/17.
 */
public class MyRedisDataMapper implements RedisStoreMapper {
    private RedisDataTypeDescription description;
    private final String hashKey = "wordCount";


    public MyRedisDataMapper() {
        description = new RedisDataTypeDescription(
                RedisDataTypeDescription.RedisDataType.STRING, hashKey);
    }

    @Override
    public RedisDataTypeDescription getDataTypeDescription() {
        return description;
    }

    @Override
    public String getKeyFromTuple(ITuple tuple) {
        return tuple.getStringByField("key1");
    }

    @Override
    public String getValueFromTuple(ITuple tuple) {
        return tuple.getStringByField("value1");
    }
}

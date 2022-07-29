package com.alibaba.csp.sentinel.dashboard.rule.redis;

import java.util.List;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.FlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRuleProvider;
import com.alibaba.csp.sentinel.dashboard.util.RedisUtils;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author: asong
 * @time: 2022/7/29
 */
@Component("flowRuleRedisProvider")
public class FlowRuleRedisProvider implements DynamicRuleProvider<List<FlowRuleEntity>> {

    String ruleKey = "sentinel.rules.flow.ruleKey";
    String channel = "sentinel.rules.flow.channel";


    @Autowired
    private RedisUtils redisUtils;

    @Override
    public List<FlowRuleEntity> getRules(String appName) throws Exception {
        return JSONObject.parseArray(redisUtils.getString(ruleKey),FlowRuleEntity.class);
    }
}

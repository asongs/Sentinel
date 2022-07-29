package com.alibaba.csp.sentinel.dashboard.repository.metric;

import javax.persistence.Query;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.MetricEntity;
import com.alibaba.csp.sentinel.util.StringUtil;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

/**
 * @author: asong
 * @time: 2022/7/29
 */
@Repository("elasticMetricsRepository")
public class ElasticMetricsRepository implements MetricsRepository<MetricEntity> {

    @Autowired
    private ElasticsearchRestTemplate em;

    @Override
    public void save(MetricEntity metric) {
        if (metric == null || StringUtil.isBlank(metric.getApp())) {
            return;
        }
        ElasticMetricEntity elasticMetricEntity = new ElasticMetricEntity();
        BeanUtils.copyProperties(metric, elasticMetricEntity);
        elasticMetricEntity.setGmtCreate(metric.getGmtCreate().getTime());
        elasticMetricEntity.setGmtModified(metric.getGmtModified().getTime());
        elasticMetricEntity.setTimestamp(metric.getTimestamp().getTime());
        em.save(elasticMetricEntity);
    }

    @Override
    public void saveAll(Iterable<MetricEntity> metrics) {
        if (metrics == null) {
            return;
        }
        metrics.forEach(this::save);
    }

    @Override
    public List<MetricEntity> queryByAppAndResourceBetween(String app, String resource, long startTime, long endTime) {
        List<MetricEntity> results = new ArrayList<MetricEntity>();
        if (StringUtil.isBlank(app)) {
            return results;
        }
        if (StringUtil.isBlank(resource)) {
            return results;
        }
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
//        boolQueryBuilder.must(QueryBuilders.termQuery("app", app));
//        boolQueryBuilder.must(QueryBuilders.termQuery("resource", resource));
//        boolQueryBuilder.must(QueryBuilders.rangeQuery("gmtCreate").gte(startTime).lte(endTime));
        SearchHits<ElasticMetricEntity> result = em.search(queryBuilder.withQuery(boolQueryBuilder).build(), ElasticMetricEntity.class);

        if (!result.hasSearchHits()) {
            return results;
        }
        for (SearchHit searchHit : result) {
            MetricEntity metricEntity = new MetricEntity();
            ElasticMetricEntity entity = (ElasticMetricEntity) searchHit.getContent();
            BeanUtils.copyProperties(searchHit.getContent(), metricEntity);
            metricEntity.setGmtCreate(new Date(entity.getGmtCreate()));
            metricEntity.setGmtModified(new Date(entity.getGmtModified()));
            metricEntity.setTimestamp(new Date(entity.getTimestamp()));
            results.add(metricEntity);
        }
        return results;
    }

    @Override
    public List<String> listResourcesOfApp(String app) {
        List<String> results = new ArrayList<>();
        if (StringUtil.isBlank(app)) {
            return results;
        }

        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.must(QueryBuilders.termQuery("app", app));

        long startTime = System.currentTimeMillis() - 1000 * 60 * 60;
        boolQueryBuilder.must(QueryBuilders.rangeQuery("gmtCreate").gte(Date.from(Instant.ofEpochMilli(startTime)).getTime()));
        SearchHits<ElasticMetricEntity> searchHits = em.search(queryBuilder.withQuery(boolQueryBuilder).build(), ElasticMetricEntity.class);

        if (!searchHits.hasSearchHits()) {
            return results;
        }

        List<MetricEntity> metricEntities = new ArrayList<MetricEntity>();
        for (SearchHit searchHit : searchHits) {
            MetricEntity metricEntity = new MetricEntity();
            BeanUtils.copyProperties(searchHit.getContent(), metricEntity);
            metricEntities.add(metricEntity);
        }

        Map<String, MetricEntity> resourceCount = new HashMap<>(32);

        for (MetricEntity metricEntity : metricEntities) {
            String resource = metricEntity.getResource();
            if (resourceCount.containsKey(resource)) {
                MetricEntity oldEntity = resourceCount.get(resource);
                oldEntity.addPassQps(metricEntity.getPassQps());
                oldEntity.addRtAndSuccessQps(metricEntity.getRt(), metricEntity.getSuccessQps());
                oldEntity.addBlockQps(metricEntity.getBlockQps());
                oldEntity.addExceptionQps(metricEntity.getExceptionQps());
                oldEntity.addCount(1);
            } else {
                resourceCount.put(resource, MetricEntity.copyOf(metricEntity));
            }
        }

        // Order by last minute b_qps DESC.
        return resourceCount.entrySet()
                .stream()
                .sorted((o1, o2) -> {
                    MetricEntity e1 = o1.getValue();
                    MetricEntity e2 = o2.getValue();
                    int t = e2.getBlockQps().compareTo(e1.getBlockQps());
                    if (t != 0) {
                        return t;
                    }
                    return e2.getPassQps().compareTo(e1.getPassQps());
                })
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}

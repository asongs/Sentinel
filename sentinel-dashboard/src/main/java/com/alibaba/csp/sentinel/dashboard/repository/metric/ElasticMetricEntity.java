package com.alibaba.csp.sentinel.dashboard.repository.metric;


import javax.persistence.Id;



import org.springframework.data.elasticsearch.annotations.Document;

/**
 * @author: asong
 * @time: 2022/7/29
 */
@Document(indexName = "sentinel_metric")
public class ElasticMetricEntity {


    /**id，主键*/
    @Id
    private String id;

    /**创建时间*/
    private Long gmtCreate;

    /**修改时间*/
    private Long gmtModified;

    /**应用名称*/
    private String app;

    /**统计时间*/
    private Long timestamp;

    /**资源名称*/
    private String resource;

    /**通过qps*/
    private Long passQps;

    /**成功qps*/
    private Long successQps;

    /**限流qps*/
    private Long blockQps;

    /**发送异常的次数*/
    private Long exceptionQps;

    /**所有successQps的rt的和*/
    private Double rt;

    /**本次聚合的总条数*/
    private Integer count;

    /**资源的hashCode*/
    private Integer resourceCode;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Long gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Long getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Long gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public Long getPassQps() {
        return passQps;
    }

    public void setPassQps(Long passQps) {
        this.passQps = passQps;
    }

    public Long getSuccessQps() {
        return successQps;
    }

    public void setSuccessQps(Long successQps) {
        this.successQps = successQps;
    }

    public Long getBlockQps() {
        return blockQps;
    }

    public void setBlockQps(Long blockQps) {
        this.blockQps = blockQps;
    }

    public Long getExceptionQps() {
        return exceptionQps;
    }

    public void setExceptionQps(Long exceptionQps) {
        this.exceptionQps = exceptionQps;
    }

    public Double getRt() {
        return rt;
    }

    public void setRt(Double rt) {
        this.rt = rt;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getResourceCode() {
        return resourceCode;
    }

    public void setResourceCode(Integer resourceCode) {
        this.resourceCode = resourceCode;
    }
}

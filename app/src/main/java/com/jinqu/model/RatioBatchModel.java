package com.jinqu.model;

public class RatioBatchModel {

    private String batch;

    private String name;//详细属性名

    private int cate_id;//属性id

    private int totalcount;

    private int hasscancount;

    private int othercount;

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotalcount() {
        return totalcount;
    }

    public void setTotalcount(int totalcount) {
        this.totalcount = totalcount;
    }

    public int getHasscancount() {
        return hasscancount;
    }

    public void setHasscancount(int hasscancount) {
        this.hasscancount = hasscancount;
    }

    public int getOthercount() {
        return othercount;
    }

    public void setOthercount(int othercount) {
        this.othercount = othercount;
    }

    public int getCate_id() {
        return cate_id;
    }

    public void setCate_id(int cate_id) {
        this.cate_id = cate_id;
    }
}

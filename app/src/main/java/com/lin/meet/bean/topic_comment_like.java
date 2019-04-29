package com.lin.meet.bean;

import cn.bmob.v3.BmobObject;

public class topic_comment_like extends BmobObject {
    private String id;
    private String uid;
    private int level;
    private int floor;

    public topic_comment_like setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public topic_comment_like setId(String id) {
        this.id = id;
        return this;
    }

    public String getUid() {
        return uid;
    }

    public String getId() {
        return id;
    }

    public topic_comment_like setLevel(int level) {
        this.level = level;
        return this;
    }

    public topic_comment_like setFloor(int floor) {
        this.floor = floor;
        return this;
    }

    public int getLevel() {
        return level;
    }

    public int getFloor() {
        return floor;
    }
}

package com.lin.meet.bean;

import cn.bmob.v3.BmobObject;

public class comment_like extends BmobObject {
    private String uri;
    private String uid;
    private String id;
    private int floor;
    private boolean like;

    public String getId() {
        return id;
    }

    public String getUid() {
        return uid;
    }

    public String getUri() {
        return uri;
    }

    public int getFloor() {
        return floor;
    }

    public boolean getLike() {
        return like;
    }

    public comment_like setLike(boolean like) {
        this.like = like;
        return this;
    }

    public comment_like setId(String id) {
        this.id = id;
        return this;
    }

    public comment_like setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public comment_like setUri(String uri) {
        this.uri = uri;
        return this;
    }

    public comment_like setFloor(int floor) {
        this.floor = floor;
        return this;
    }
}

package com.lin.meet.bean;

import cn.bmob.v3.BmobObject;

public class MapFlag extends BmobObject {
    private String uid;
    private String id;
    private String content;
    private String image;
    private double x;
    private double y;

    public MapFlag setContent(String content) {
        this.content = content;
        return this;
    }

    public String getId() {
        return id;
    }

    public String getUid() {
        return uid;
    }

    public MapFlag setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public MapFlag setId(String id) {
        this.id = id;
        return this;
    }

    public MapFlag setImage(String image) {
        this.image = image;
        return this;
    }

    public String getImage() {
        return image;
    }

    public String getContent() {
        return content;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public MapFlag setX(double x) {
        this.x = x;
        return this;
    }

    public MapFlag setY(double y) {
        this.y = y;
        return this;
    }
}

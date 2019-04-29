package com.lin.meet.bean;

import cn.bmob.v3.BmobObject;

public class topic_main extends BmobObject {
    private String title;
    private String id;
    private String content;
    private String uid;
    private String one_uri = "@null";
    private String two_uri = "@null";
    private String three_uri = "@null";
    private String four_uri = "@null";
    private String five_uri = "@null";
    private String six_uri = "@null";
    private String type = "";
    private String location = "@null";

    public String getId() {
        return id;
    }

    public String getUid() {
        return uid;
    }

    public topic_main setId(String id) {
        this.id = id;
        return this;
    }

    public topic_main setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public String getContent() {
        return content;
    }

    public String getFive_uri() {
        return five_uri;
    }

    public String getFour_uri() {
        return four_uri;
    }

    public String getOne_uri() {
        return one_uri;
    }

    public String getSix_uri() {
        return six_uri;
    }

    public String getThree_uri() {
        return three_uri;
    }

    public String getTitle() {
        return title;
    }

    public String getTwo_uri() {
        return two_uri;
    }

    public topic_main setContent(String content) {
        this.content = content;
        return this;
    }

    public topic_main setFive_uri(String five_uri) {
        this.five_uri = five_uri;
        return this;
    }

    public topic_main setFour_uri(String four_uri) {
        this.four_uri = four_uri;
        return this;
    }

    public topic_main setOne_uri(String one_uri) {
        this.one_uri = one_uri;
        return this;
    }

    public void setSix_uri(String six_uri) {
        this.six_uri = six_uri;
    }

    public void setThree_uri(String three_uri) {
        this.three_uri = three_uri;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTwo_uri(String two_uri) {
        this.two_uri = two_uri;
    }

    public topic_main setType(String type) {
        this.type = type;
        return this;
    }

    public String getLocation() {
        return location;
    }

    public String getType() {
        return type;
    }

    public topic_main setLocation(String location) {
        this.location = location;
        return this;
    }
}

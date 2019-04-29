package com.lin.meet.bean;

import cn.bmob.v3.BmobObject;

public class topic_comment extends BmobObject {
    private String id;//内容id
    private String uid;
    private String uri;//unknow
    private int floor;//楼层
    private int level = 0;//0表示主楼
    private String content;//内容
    private int comment = 0;
    private int like = 0;
    private String uid_1;
    private String uid_2;
    private String content_1;
    private String content_2;

    public topic_comment setContent(String content) {
        this.content = content;
        return this;
    }

    public String getContent() {
        return content;
    }

    public topic_comment setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public topic_comment setId(String id) {
        this.id = id;
        return this;
    }

    public String getUid() {
        return uid;
    }

    public String getId() {
        return id;
    }

    public topic_comment setLike(int like) {
        this.like = like;
        return this;
    }

    public int getFloor() {
        return floor;
    }

    public int getComment() {
        return comment;
    }

    public int getLevel() {
        return level;
    }

    public int getLike() {
        return like;
    }

    public String getContent_1() {
        return content_1;
    }

    public topic_comment setComment(int comment) {
        this.comment = comment;
        return this;
    }

    public String getContent_2() {
        return content_2;
    }

    public topic_comment setFloor(int floor) {
        this.floor = floor;
        return this;
    }

    public String getUid_1() {
        return uid_1;
    }

    public String getUid_2() {
        return uid_2;
    }

    public topic_comment setContent_1(String content_1) {
        this.content_1 = content_1;
        return this;
    }

    public topic_comment setContent_2(String content_2) {
        this.content_2 = content_2;
        return this;
    }

    public topic_comment setLevel(int level) {
        this.level = level;
        return this;
    }

    public topic_comment setUid_1(String uid_1) {
        this.uid_1 = uid_1;
        return this;
    }

    public topic_comment setUid_2(String uid_2) {
        this.uid_2 = uid_2;
        return this;
    }

    public String getUri() {
        return uri;
    }

    public topic_comment setUri(String uri) {
        this.uri = uri;
        return this;
    }
}

package com.lin.meet.bean;

import cn.bmob.v3.BmobObject;

public class KnowBean extends BmobObject {
    private String id;
    private String uid;
    private Boolean isSolve;
    private String img;
    private String content;

    public KnowBean setId(String id) {
        this.id = id;
        return this;
    }

    public KnowBean setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public String getUid() {
        return uid;
    }

    public String getId() {
        return id;
    }

    public Boolean getSolve() {
        return isSolve;
    }

    public String getImg() {
        return img;
    }

    public KnowBean setImg(String img) {
        this.img = img;
        return this;
    }

    public KnowBean setSolve(Boolean solve) {
        isSolve = solve;
        return this;
    }

    public KnowBean setContent(String content) {
        this.content = content;
        return this;
    }

    public String getContent() {
        return content;
    }
}

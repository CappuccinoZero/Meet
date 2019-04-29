package com.lin.meet.bean;

import cn.bmob.v3.BmobObject;

public class topic_islike extends BmobObject {
    private String uid;
    private String id;
    private boolean islike = false;

    public String getId() {
        return id;
    }

    public String getUid() {
        return uid;
    }

    public topic_islike setId(String id) {
        this.id = id;
        return this;
    }

    public topic_islike setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public topic_islike setIslike(boolean islike) {
        this.islike = islike;
        return this;
    }

    public boolean getIslike() {
        return islike;
    }
}

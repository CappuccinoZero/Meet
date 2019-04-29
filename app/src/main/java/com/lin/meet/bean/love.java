package com.lin.meet.bean;

import cn.bmob.v3.BmobObject;

public class love extends BmobObject{
    private String uri;
    private String uid;
    private String id;
    private int type = 0;

    public love setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public love setId(String id) {
        this.id = id;
        return this;
    }

    public String getUri() {
        return uri;
    }

    public String getUid() {
        return uid;
    }

    public String getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public love setType(int type) {
        this.type = type;
        return this;
    }

    public love setUri(String uri) {
        this.uri = uri;
        return this;
    }
}

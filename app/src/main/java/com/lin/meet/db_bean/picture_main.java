package com.lin.meet.db_bean;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;

public class picture_main extends BmobObject implements Serializable {
    private String id;
    private String uid;
    private String uri;
    private String tltle;

    private String content;

    public String getId() {
        return id;
    }

    public String getUid() {
        return uid;
    }

    public picture_main setId(String id) {
        this.id = id;
        return this;
    }

    public picture_main setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public String getTltle() {
        return tltle;
    }



    public picture_main setTltle(String tltle) {
        this.tltle = tltle;
        return this;
    }

    public String getUri() {
        return uri;
    }

    public picture_main setUri(String uri) {
        this.uri = uri;
        return this;
    }

    public String getContent() {
        return content;
    }

    public picture_main setContent(String content) {
        this.content = content;
        return this;
    }
}

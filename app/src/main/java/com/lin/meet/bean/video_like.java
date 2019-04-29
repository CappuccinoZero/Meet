package com.lin.meet.bean;

import cn.bmob.v3.BmobObject;

public class video_like extends BmobObject {
    private String uid;
    private String id;

    public String getId() {
        return id;
    }

    public String getUid() {
        return uid;
    }

    public video_like setId(String id) {
        this.id = id;
        return this;
    }

    public video_like setUid(String uid) {
        this.uid = uid;
        return this;
    }

}

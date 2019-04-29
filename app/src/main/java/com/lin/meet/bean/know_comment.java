package com.lin.meet.bean;

import cn.bmob.v3.BmobObject;

public class know_comment extends BmobObject {
    private String id;//内容id
    private String uid;
    private int floor;//楼层
    private String content;//内容
    private int agree = 0;

    public know_comment setContent(String content) {
        this.content = content;
        return this;
    }

    public String getContent() {
        return content;
    }

    public know_comment setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public know_comment setId(String id) {
        this.id = id;
        return this;
    }

    public String getUid() {
        return uid;
    }

    public String getId() {
        return id;
    }


    public int getFloor() {
        return floor;
    }



    public know_comment setFloor(int floor) {
        this.floor = floor;
        return this;
    }

    public int getAgree() {
        return agree;
    }

    public know_comment setAgree(int agree) {
        this.agree = agree;
        return this;
    }
}

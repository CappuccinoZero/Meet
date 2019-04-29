package com.lin.meet.bean;

import cn.bmob.v3.BmobObject;

public class know_agree extends BmobObject {
    private String id;//内容id
    private String uid;
    private int floor;//楼层


    public know_agree setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public know_agree setId(String id) {
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


    public know_agree setFloor(int floor) {
        this.floor = floor;
        return this;
    }
}

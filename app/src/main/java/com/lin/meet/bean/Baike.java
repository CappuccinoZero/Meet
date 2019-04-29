package com.lin.meet.bean;

import cn.bmob.v3.BmobObject;

public class Baike extends BmobObject {
    private int id;
    private int flag;
    private String cnName;//
    private String enName;//
    private String imageUri;//
    private String brief;
    private String uri;//
    private String type;

    public Baike setUri(String uri) {
        this.uri = uri;
        return this;
    }

    public int getId() {
        return id;
    }

    public Baike setId(int id) {
        this.id = id;
        return this;
    }

    public String getUri() {
        return uri;
    }

    public String getBrief() {
        return brief;
    }

    public String getCnName() {
        return cnName;
    }

    public String getEnName() {
        return enName;
    }

    public String getImageUri() {
        return imageUri;
    }

    public Baike setBrief(String brief) {
        this.brief = brief;
        return this;
    }

    public Baike setCnName(String cnName) {
        this.cnName = cnName;
        return this;
    }

    public Baike setEnName(String enName) {
        this.enName = enName;
        return this;
    }

    public Baike setImageUri(String imageUri) {
        this.imageUri = imageUri;
        return this;
    }

    public String getType() {
        return type;
    }

    public Baike setType(String type) {
        this.type = type;
        return this;
    }

    public int getFlag() {
        return flag;
    }

    public Baike setFlag(int flag) {
        this.flag = flag;
        return this;
    }
}

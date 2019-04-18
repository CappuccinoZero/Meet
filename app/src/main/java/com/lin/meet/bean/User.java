package com.lin.meet.bean;

import cn.bmob.v3.BmobUser;

public class User extends BmobUser {

    private String nickName = ""; //昵称
    private String sex= "秘密";//性别
    private String work= "";//职业
    private String brith= "未知";//出生
    private String e_mail= "";//Email
    private String area = "未知";//地区
    private String signature = "";//签名
    private String introduce = "";//介绍
    private String headerUri = "";
    private String backgroundUri = "";
    private String uid = "";
    private int fan = 0;
    private int attention = 0;

    public int getAttention() {
        return attention;
    }

    public int getFan() {
        return fan;
    }

    public void setAttention(int attention) {
        this.attention = attention;
    }

    public void setFan(int fan) {
        this.fan = fan;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getArea() {
        return area;
    }

    public String getIntroduce() {
        return introduce;
    }

    public String getNickName() {
        return nickName;
    }

    public String getSex() {
        return sex;
    }

    public String getSignature() {
        return signature;
    }

    public String getWork() {
        return work;
    }

    public void setArea(String area) {
        this.area = area;
    }


    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getBackgroundUri() {
        return backgroundUri;
    }

    public String getHeaderUri() {
        return headerUri;
    }

    public void setBackgroundUri(String backgroundUri) {
        this.backgroundUri = backgroundUri;
    }

    public void setHeaderUri(String headerUri) {
        this.headerUri = headerUri;
    }

    public String getBrith() {
        return brith;
    }

    public void setBrith(String brith) {
        this.brith = brith;
    }

    public String getE_mail() {
        return e_mail;
    }

    public void setE_mail(String e_mail) {
        this.e_mail = e_mail;
    }
}

package com.lin.meet.db_bean;

import cn.bmob.v3.BmobObject;

public class recomment_main extends BmobObject {
    private String uri = "";
    private String time = "";
    private int comment = 0;//cancel
    private int like = 0;//cancel
    private String title;
    private String img;
    private String author;
    private int flag = 0;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getUri() {
        return uri;
    }

    public int getComment() {
        return comment;
    }

    public int getLike() {
        return like;
    }

    public recomment_main setComment(int comment) {
        this.comment = comment;
        return this;
    }

    public recomment_main setLike(int like) {
        this.like = like;
        return this;
    }

    public recomment_main setUri(String uri) {
        this.uri = uri;
        return this;
    }


    public void setImg(String img) {
        this.img = img;
    }

    public String getImg() {
        return img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public int getFlag() {
        return flag;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

package com.lin.meet.jsoup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LoveNewsBean implements Serializable {
     private String title;
     private String time;
     private String content;
     private String img;
     private String author;
     private String contentUri;
     private int flag = 0;
     private List<String> contents = new ArrayList<>();
     private List<String> imgs = new ArrayList<>();


    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public List<String> getImgs() {
        return imgs;
    }

    public String getAuthor() {
        return author;
    }


    public String getImg() {
        return img;
    }

    public String getTitle() {
        return title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }


    public void setImg(String img) {
        this.img = img;
    }

    public void setTitle(String title) {
        String temps[] = title.split("、");
        this.title = temps[1];
    }

    public void setAbsoluteTitle(String title) {
        this.title = title;
    }

    public String getContentUri() {
        return contentUri;
    }

    public void setContentUri(String contentUri) {
        this.contentUri = contentUri;
    }

    public void addImg(String url){
        if(!imgs.contains(url))
            imgs.add(url);
    }

    public List<String> getContents() {
        return contents;
    }

    public void addContent(String content){
        contents.add(content);
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}

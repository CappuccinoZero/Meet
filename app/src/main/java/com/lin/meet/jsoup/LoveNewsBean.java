package com.lin.meet.jsoup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LoveNewsBean implements Serializable {
     private String title;
     private String time;
     private String img;
     private String type;
     private String author;
     private String contentUri;
    private List<String> contents = new ArrayList<>();
     private List<String> imgs = new ArrayList<>();

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

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

    public void setImgs(List<String> imgs) {
        this.imgs = imgs;
    }

    public void setTitle(String title) {
        String temps[] = title.split("、");
        this.title = temps[1];
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

    public String removeImg(int i){
        return imgs.remove(i);
    }

    public List<String> getContents() {
        return contents;
    }

    public void setContents(List<String> contents) {
        this.contents = contents;
    }

    public void addContent(String content){
        contents.add(content);
    }

    public String removeContent(int i){
        return contents.remove(i);
    }
}

package com.lin.meet.jsoup;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaikeBean implements Serializable {
    private String Summary="";//物种概述
    private String flavus = null;
    private List<String> titles = new ArrayList<>();
    private List<String> content = new ArrayList<>();
    private List<String> images = new ArrayList<>();
    private Map<String,Integer> map = new HashMap<>();

    public void setContent(List<String> content) {
        this.content = content;
    }

    public List<String> getContent() {
        return content;
    }

    public List<String> getImages() {
        return images;
    }

    public List<String> getTitles() {
        return titles;
    }

    public void insertTitle(String title){
        if(!titles.contains(title)){
            titles.add(title);
        }
    }

    public void insertImage(String uri){
        if(!images.contains(uri)){
            images.add(uri);
        }
    }

    public boolean insertContent(String str){
        if(!content.contains(str)&&!str.equals(getSummary())){
            content.add(str);
            return true;
        }else
            return false;
    }

    public String getSummary() {
        return Summary;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public void setSummary(String summary) {
        Summary = summary;
    }

    public void setTitles(List<String> titles) {
        this.titles = titles;
    }

    public Map<String, Integer> getMap() {
        return map;
    }

    public String getFlavus() {
        return flavus;
    }

    public void insertContent(String str,String flavus){
        this.flavus = flavus;
        map.put(flavus,content.size());
        content.add(str);
    }
}

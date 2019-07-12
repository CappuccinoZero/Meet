package com.lin.meet.db_bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;

public class love extends BmobObject{
    private String uri;//废物,之后删除

    private String uid;//收藏人ID
    private String id;//主题ID
    private int type = 0;// = flag 0 - 3

    public love setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public love setId(String id) {
        this.id = id;
        return this;
    }

    public String getUri() {
        return uri;
    }

    public String getUid() {
        return uid;
    }

    public String getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public love setType(int type) {
        this.type = type;
        return this;
    }

    public love setUri(String uri) {
        this.uri = uri;
        return this;
    }

    public static love createLove(String id,String uid,int type){
        if(!BmobUser.isLogin())
            return null;
        love love = new love();
        love.id = id;
        love.uid = uid;
        love.type = type;
        return love;
    }
}

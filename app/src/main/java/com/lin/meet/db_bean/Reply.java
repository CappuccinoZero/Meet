package com.lin.meet.db_bean;


public class Reply {
    public comment bean;
    public String headUri = "";
    public String nickName = "";
    public int likeCount = 0;
    public int replyCount = 0;
    public boolean like = false;
    public Reply(comment bean){
        this.bean = bean;
    }
}

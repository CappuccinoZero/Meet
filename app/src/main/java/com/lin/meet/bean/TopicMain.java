package com.lin.meet.bean;

import com.lin.meet.db_bean.topic_main;

import java.io.Serializable;

public class TopicMain implements Serializable {
    private String nickName;
    private String headerUri;
    public String images[] = new String[6];
    private int commentCount;
    private int likeCount;
    public topic_main bean;

    public TopicMain(topic_main bean){
        this.bean = bean;
        images[0] = bean.getOne_uri();
        images[1] = bean.getTwo_uri();
        images[2] = bean.getThree_uri();
        images[3] = bean.getFour_uri();
        images[4] = bean.getFive_uri();
        images[5] = bean.getSix_uri();
    }

    public int getCommentCount() {
        return commentCount;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public String getHeaderUri() {
        return headerUri;
    }

    public String getNickName() {
        return nickName;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public void setHeaderUri(String headerUri) {
        this.headerUri = headerUri;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}

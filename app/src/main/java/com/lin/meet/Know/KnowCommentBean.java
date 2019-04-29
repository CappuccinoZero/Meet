package com.lin.meet.Know;

import com.lin.meet.bean.know_comment;

public class KnowCommentBean {
    private know_comment bean;
    private String nickName;
    private String headUri;
    private Boolean agree = false;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public know_comment getBean() {
        return bean;
    }

    public String getHeadUri() {
        return headUri;
    }

    public void setHeadUri(String headUri) {
        this.headUri = headUri;
    }

    KnowCommentBean(know_comment bean){
        this.bean = bean;
    }

    public Boolean getAgree() {
        return agree;
    }

    public void setAgree(Boolean agree) {
        this.agree = agree;
    }
}

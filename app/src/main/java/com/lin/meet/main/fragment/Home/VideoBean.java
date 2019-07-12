package com.lin.meet.main.fragment.Home;

import com.lin.meet.db_bean.video_main;

public class VideoBean {
    private video_main bean;
    private String nickName;
    private String headerUri;

    public VideoBean(video_main bean){
        this.bean = bean;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public video_main getBean() {
        return bean;
    }

    public void setHeaderUri(String headerUri) {
        this.headerUri = headerUri;
    }

    public String getNickName() {
        return nickName;
    }

    public String getHeaderUri() {
        return headerUri;
    }
}

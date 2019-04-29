package com.lin.meet.bean;

public class ReplyBean {
    private recommentBean.recomment_comment bean;
    private topic_comment topic_bean;
    private video_comment video_main;
    public String content;
    public String nickName0 = null;
    public String nickName1 = null;
    public String nickName2 = null;
    public String header = null;
    public String content1 = null;
    public String content2 = null;
    public String time;
    public boolean like = false;
    public int floor;
    public long updateTime;
    public int commentCount;
    public int likeCount;
    public ReplyBean(recommentBean.recomment_comment bean){
        this.bean = bean;
        content = bean.getContent();
        time = bean.getCreatedAt();
        commentCount = bean.getComment();
        likeCount = bean.getLike();
        nickName0 = bean.getUid();
        content1 = bean.getContent1();
        content2 = bean.getContent2();
        floor = bean.getFloor();
        initTimeFormat(bean.getUpdatedAt());
    }
    public ReplyBean(topic_comment bean){
        this.topic_bean = bean;
        content = bean.getContent();
        time = bean.getCreatedAt();
        commentCount = bean.getComment();
        likeCount = bean.getLike();
        nickName0 = bean.getUid();
        content1 = bean.getContent_1();
        content2 = bean.getContent_2();
        floor = bean.getFloor();
        initTimeFormat(bean.getUpdatedAt());
    }

    public ReplyBean(video_comment bean){
        this.video_main = bean;
        content = bean.getContent();
        time = bean.getCreatedAt();
        commentCount = bean.getComment();
        likeCount = bean.getLike();
        nickName0 = bean.getUid();
        content1 = bean.getContent_1();
        content2 = bean.getContent_2();
        floor = bean.getFloor();
        initTimeFormat(bean.getUpdatedAt());
    }


    public recommentBean.recomment_comment getBean() {
        return bean;
    }
    public topic_comment getTopic() {
        return topic_bean;
    }

    public video_comment getVideo() {
        return video_main;
    }

    private void initTimeFormat(String time){
        StringBuilder builder = new StringBuilder();
        for(int i=0;i<time.length();i++){
            if(time.charAt(i)>='0'&&time.charAt(i)<='9'){
                builder.append(time.charAt(i));
            }
        }
        updateTime = Long.valueOf(builder.toString());
    }
}

package com.lin.meet.topic;

import com.lin.meet.bean.ReplyBean;
import com.lin.meet.bean.TopicMain;

public interface TopicConstract  {
    interface View{
        void initResult(int resultCode, TopicMain bean);
        void toast(String msg);
        int insertComment(ReplyBean reply,Boolean isRoll);
        void sendSonResult(int ResultCode,int position,int level,String msg,String nickName);
        void setCommentCount(int count);
        void setThumbCount(int position);
        void setCommentLike(int position,int count);
        void likeResult(int resultCode,boolean like);
        void likeCommentResult(int resultCode,int position,boolean like);
        void setLikeNoAnim(boolean like);
        void setCommentLike(int position,boolean like);
        void initStart();
        void onStartResult(int resultCode,boolean star);
    }
    interface Presenter{
        void initData(String id,boolean isSender);
        void senComment(String msg);
        void onSendOnMessage(int floor,String msg,int postion);
        void onClickLike();
        void onClickCommentLike(int floor,int position);
        void onStar();
    }
}

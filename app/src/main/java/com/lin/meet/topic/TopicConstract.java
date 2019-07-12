package com.lin.meet.topic;

import com.lin.meet.bean.TopicMain;
import com.lin.meet.db_bean.Reply;
import com.lin.meet.db_bean.comment;

public interface TopicConstract  {
    interface View{
        void initResult(int resultCode, TopicMain bean);
        void toast(String msg);
        int insertComment(Reply reply, Boolean isRoll);
        void senMessageResult(int code,Reply reply);
        void setCommentCount(int count);
        void setThumbCount(int position);
        void likeResult(int resultCode,boolean like);
        void setLikeNoAnim(boolean like);
        void initStart();
        void sonSendResult(int resultCode,int position);
    }
    interface Presenter{
        void initData(String id,boolean isSender,int flag);
        void initData(String id,boolean isSender,String parentUid);
        void senComment(String msg);
        void onSendOnMessage(comment comment, String msg,  int position);
        void onClickLike(boolean like);
        void onClickCommentLike(String parentId,String parentUid,boolean like);
        void onStar();
    }
}

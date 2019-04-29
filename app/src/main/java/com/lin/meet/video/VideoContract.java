package com.lin.meet.video;

import com.lin.meet.bean.ReplyBean;

public interface VideoContract {
    interface SendView{
        void showVideoImage(boolean show);
        void showUriEdit(boolean show);
        void showAddVideo(boolean show);
        void showStartVideo(boolean show);
        void setVideoImage(String uri);
        void setVideoState(Boolean life);
        void UseUri(boolean isUse);
        boolean readySend();
        void checkUri(boolean send);
        void toast(String msg);
        void createProgressDialog();
        void closeProgressDialog();
        void saveVideoResult(int resultCode,String id);
    }
    interface SendPresenter{
        void onSendVideoFromPath(String path,String title);
        void onSendVideoFromUri(String path,String title);
        void cancelLoad();
    }
    interface View{
        void playVideo(String uri,String title);
        void setHeader(String uri);
        void setNickName(String nickName);
        void toast(String msg);
        int insertComment(ReplyBean reply, Boolean isRoll);
        void sendSonResult(int ResultCode,int position,int level,String msg,String nickName);
        void likeResult(int resultCode,boolean like);
        void onStarResult(int resultCode,boolean star);
        void likeCommentResult(int resultCode,int position,boolean like);
        void setCommentLikeCount(int position,int count);
        void setCommentLike(int position, boolean like);
    }
    interface presenter{
        void initData(String id);
        void senComment(String msg);
        void onSendOnMessage(int floor,String msg,int postion);
        void onClickLike();
        void onClickCommentLike(int floor,int position);
        void onStar();
    }
}

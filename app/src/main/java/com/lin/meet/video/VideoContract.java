package com.lin.meet.video;

import com.lin.meet.db_bean.Reply;
import com.lin.meet.db_bean.comment;

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
        void setNullCommentVisiable(boolean show);
        void playVideo(String uri,String title);
        void setHeader(String uri);
        void setNickName(String nickName);
        void toast(String msg);
        int insertComment(Reply reply, Boolean isRoll);
        void likeResult(int resultCode,boolean like);
        void onStarResult(int resultCode,boolean star);

        void senMessageResult(int resultCode, Reply msg);
        void sonSendResult(int resultCode,int position);
    }
    interface presenter{
        void initData(String id,String uid);
        void senComment(String msg);
        void onSendSonMessage(comment comment, String msg, int i);
        void onClickLike(boolean like);
        void onClickCommentLike(String parentId, String parentUid, boolean like);
        void onStar();
    }
}

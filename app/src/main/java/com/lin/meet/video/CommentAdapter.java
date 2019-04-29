package com.lin.meet.video;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lin.meet.R;
import com.lin.meet.bean.ReplyBean;
import com.lin.meet.bean.ReplyViewHolder;
import com.lin.meet.bean.video_comment;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;

public class CommentAdapter extends RecyclerView.Adapter<ReplyViewHolder> {
    private List<ReplyBean> replys = new ArrayList<>();
    private Context context;
    private VideoCallback callback;

    CommentAdapter(VideoCallback callback){
        this.callback = callback;
    }

    @NonNull
    @Override
    public ReplyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(context==null)
            context = viewGroup.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.comment_item,viewGroup,false);
        return new ReplyViewHolder(view);
    }

    private boolean isNoLogin(){
        return !BmobUser.isLogin();
    }

    @Override
    public void onBindViewHolder(@NonNull ReplyViewHolder viewHolder, int index) {
        viewHolder.content.setText(replys.get(index).content);
        viewHolder.nickName0.setText(replys.get(index).nickName0);
        viewHolder.time.setText(replys.get(index).time);
        viewHolder.loadHeader(context,replys.get(index).header);
        viewHolder.setLickCount(replys.get(index).likeCount);
        viewHolder.setLike(replys.get(index).like);
        if(replys.get(index).commentCount>0)
            viewHolder.replyCount.setText(String.valueOf(replys.get(index).commentCount)+"回复");
        else
            viewHolder.replyCount.setText("回复");
        viewHolder.setReplyCount(replys.get(index).commentCount);
        if(replys.get(index).nickName1!=null&&replys.get(index).content1!=null){
            viewHolder.content1.setText(replys.get(index).content1);
            viewHolder.nickName1.setText(replys.get(index).nickName1+"：");
        }
        if(replys.get(index).nickName2!=null&&replys.get(index).content2!=null){
            viewHolder.content2.setText(replys.get(index).content2);
            viewHolder.nickName2.setText(replys.get(index).nickName2+"：");
        }
        viewHolder.item.setOnClickListener((v)->{
            callback.showSonEdit(replys.get(index).getVideo(),index);
        });
        viewHolder.like.setOnClickListener(v->{
            if(isNoLogin())return;
            replys.get(index).like = viewHolder.onClickLike();
            callback.onCommentLike(index,replys.get(index).floor);
        });
    }

    @Override
    public int getItemCount() {
        return replys.size();
    }

    synchronized int insertComment(ReplyBean bean){
        if(replys.size()==0){
            replys.add(bean);
            notifyDataSetChanged();
            return replys.size();
        }
        for(int i=0;i<replys.size();i++){
            if(replys.get(i).updateTime<bean.updateTime){
                replys.add(i,bean);
                notifyDataSetChanged();
                return i;
            }
        }
        replys.add(bean);
        notifyDataSetChanged();
        return replys.size();
    }

    void updateSonReply(String msg,String nickName,int position,int floor){
        if(floor == 1){
            replys.get(position).content1 = msg;
            replys.get(position).nickName1 = nickName;
        }
        else if(floor==2){
            replys.get(position).content2 = msg;
            replys.get(position).nickName2 = nickName;
        }
        replys.get(position).commentCount = floor;
        notifyDataSetChanged();
    }

    void updateCommentLike(int position,boolean like){
        replys.get(position).like = like;
        if(like)
            replys.get(position).likeCount = replys.get(position).likeCount +1;
        else
            replys.get(position).likeCount = replys.get(position).likeCount -1;
        notifyDataSetChanged();
    }

    void setCommentLikeCount(int position,int count){
        replys.get(position).likeCount = count;
        notifyDataSetChanged();
    }

    void setCommentLike(int position,boolean like){
        replys.get(position).like = like;
        notifyDataSetChanged();
    }

    interface VideoCallback{
        void showSonEdit(video_comment comment, int position);
        void onCommentLike(int position,int floor);
    }
}

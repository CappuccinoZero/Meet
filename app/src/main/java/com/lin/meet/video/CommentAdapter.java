package com.lin.meet.video;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lin.meet.R;
import com.lin.meet.comment.CommentActivity;
import com.lin.meet.db_bean.Reply;
import com.lin.meet.db_bean.ReplyHolder;
import com.lin.meet.db_bean.comment;
import com.lin.meet.personal.PersonalActivity;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;

public class CommentAdapter extends RecyclerView.Adapter<ReplyHolder> {
    private List<Reply> replys = new ArrayList<>();
    private Context context;
    private VideoCallback callback;
    private int tempIndex = -1;
    private int tempCount = -1;

    CommentAdapter(VideoCallback callback){
        this.callback = callback;
    }

    @NonNull
    @Override
    public ReplyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(context==null)
            context = viewGroup.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.comment_item,viewGroup,false);
        return new ReplyHolder(view);
    }

    private boolean isNoLogin(){
        return !BmobUser.isLogin();
    }

    @Override
    public void onBindViewHolder(@NonNull ReplyHolder viewHolder, int index) {
        viewHolder.content.setText(replys.get(index).bean.getContent());
        viewHolder.nickName.setText(replys.get(index).nickName);
        viewHolder.time.setText(replys.get(index).bean.getCreatedAt());
        viewHolder.loadHeader(context,replys.get(index).headUri);
        viewHolder.setLickCount(replys.get(index).likeCount);
        viewHolder.setLike(replys.get(index).like);
        if(replys.get(index).replyCount>0)
            viewHolder.replyCount.setText(String.valueOf(replys.get(index).replyCount)+"回复");
        else
            viewHolder.replyCount.setText("回复");
        viewHolder.item.setOnClickListener((v)->{
            Intent intent = new Intent(context, CommentActivity.class);
            intent.putExtra("id",replys.get(index).bean.getId());
            intent.putExtra("flag",replys.get(index).bean.getFlag());
            intent.putExtra("Like",replys.get(index).like);
            intent.putExtra("Count",replys.get(index).replyCount);
            callback.startComment(intent);
            tempIndex = index;
        });
        viewHolder.like.setOnClickListener(v->{
            if(isNoLogin())return;
            replys.get(index).like = !replys.get(index).like;
            viewHolder.setLikeAnim(replys.get(index).like);
            updateCommentLike(index,viewHolder,replys.get(index).like);
            callback.onCommentLike(replys.get(index).bean.getId(),replys.get(index).bean.getUid(),replys.get(index).like);
        });
        viewHolder.replyCount.setOnClickListener(v-> callback.showSonEdit(replys.get(index).bean,index));
        viewHolder.header.setOnClickListener(v-> PersonalActivity.Companion.startOther((Activity) context,replys.get(index).bean.getUid()));
    }

    @Override
    public int getItemCount() {
        return replys.size();
    }

    synchronized int insertComment(Reply bean){
        replys.add(bean);
        notifyDataSetChanged();
        return replys.size()-1;
    }

    public void addComment(int position){
        replys.get(position).replyCount++;
        notifyDataSetChanged();
    }

    private void updateCommentLike(int i,ReplyHolder holder,boolean like){
        if(like)
            replys.get(i).likeCount++;
        else
            replys.get(i).likeCount--;
        notifyDataSetChanged();
    }

    interface VideoCallback{
        void showSonEdit(comment comment, int position);
        void onCommentLike(String parentId,String parentUid,boolean like);
        void startComment(Intent intent);
    }

    void updateLikeStatus(boolean like){
        if(tempIndex!=-1){
            if(like&&!replys.get(tempIndex).like){
                replys.get(tempIndex).likeCount++;
            }else if(!like&&replys.get(tempIndex).like){
                replys.get(tempIndex).likeCount--;
            }
            replys.get(tempIndex).like = like;
            notifyDataSetChanged();
        }
    }

    void updateCommentStatus(int count){
        if(tempIndex!=-1&&count!=-1){
            if(count!=tempCount){
                replys.get(tempIndex).replyCount = count;
                notifyDataSetChanged();
            }
        }
    }
}

package com.lin.meet.db_bean;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lin.meet.R;
import com.lin.meet.override.ScaleAnim;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReplyHolder extends RecyclerView.ViewHolder {
    public CircleImageView header;
    public LinearLayout item;
    public ImageView thumb;
    public TextView content;
    public TextView nickName;
    public TextView replyCount;
    public TextView thumbCount;
    public TextView time;
    public LinearLayout like;
    public ReplyHolder(@NonNull View itemView) {
        super(itemView);
        item = itemView.findViewById(R.id.comment_item);
        header = itemView.findViewById(R.id.comment_header);
        content = itemView.findViewById(R.id.comment_content);
        nickName = itemView.findViewById(R.id.comment_nickName);
        replyCount = itemView.findViewById(R.id.comment_reply);
        thumbCount = itemView.findViewById(R.id.thumb_count);
        time = itemView.findViewById(R.id.comment_time);
        thumb = itemView.findViewById(R.id.comment_thumb);
        like = item.findViewById(R.id.comment_like);
    }

    public void loadHeader(Context context,String uri){
        Glide.with(context).load(uri).into(header);
    }

    public void setLikeAnim(Boolean like){
        if(like){
            ScaleAnim.startAnim(thumb,R.mipmap.like2);
        }
        else {
            ScaleAnim.startAnim(thumb,R.mipmap.like);
        }
    }

    public void setLickCount(int count){
        if(count==0){
            thumbCount.setText("点赞");
        }
        else
            thumbCount.setText(String.valueOf(count));
    }

    public void setLike(boolean like){
        if(like){
            thumb.setImageResource(R.mipmap.like2);
        }else {
            thumb.setImageResource(R.mipmap.like);
        }
    }
}

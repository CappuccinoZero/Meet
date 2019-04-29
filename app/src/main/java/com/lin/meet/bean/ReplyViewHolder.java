package com.lin.meet.bean;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lin.meet.R;
import com.lin.meet.override.ScaleAnim;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReplyViewHolder extends RecyclerView.ViewHolder {
    public CircleImageView header;
    public LinearLayout item;
    public RelativeLayout replyLayout;
    public ImageView thumb;
    public TextView content;
    public TextView content1;
    public TextView content2;
    public TextView nickName0;
    public TextView nickName1;
    public TextView nickName2;
    public TextView replyCount;
    public TextView thumbCount;
    public TextView time;
    public TextView more;
    public LinearLayout like;
    private boolean islike = false;
    public ReplyViewHolder(@NonNull View itemView) {
        super(itemView);
        item = itemView.findViewById(R.id.comment_item);
        header = itemView.findViewById(R.id.comment_header);
        content = itemView.findViewById(R.id.comment_content);
        content1 = itemView.findViewById(R.id.comment_user_1_reply);
        content2 = itemView.findViewById(R.id.comment_user_2_reply);
        nickName0 = itemView.findViewById(R.id.comment_nickName);
        nickName1 = itemView.findViewById(R.id.comment_user_1);
        nickName2 = itemView.findViewById(R.id.comment_user_2);
        replyCount = itemView.findViewById(R.id.comment_reply);
        thumbCount = itemView.findViewById(R.id.thumb_count);
        time = itemView.findViewById(R.id.comment_time);
        replyLayout = itemView.findViewById(R.id.comment_reply_layout);
        more = itemView.findViewById(R.id.comment_more);
        thumb = itemView.findViewById(R.id.comment_thumb);
        like = item.findViewById(R.id.comment_like);
    }

    public void loadHeader(Context context,String uri){
        Glide.with(context).load(uri).into(header);
    }

    public void setReplyCount(int count){
        replyLayout.setVisibility(count==0?View.GONE:View.VISIBLE);
        nickName1.setVisibility(count>=1?View.VISIBLE:View.GONE);
        nickName2.setVisibility(count>=2?View.VISIBLE:View.GONE);
        content1.setVisibility(count>=1?View.VISIBLE:View.GONE);
        content2.setVisibility(count>=2?View.VISIBLE:View.GONE);
        more.setVisibility(count>=3?View.VISIBLE:View.GONE);
    }

    private void startThumbAnim(Boolean islike){
        if(islike){
            ScaleAnim.startAnim(thumb,R.drawable.thumb_red);
        }
        else {
            ScaleAnim.startAnim(thumb,R.drawable.thumb);
        }
    }

    public Boolean onClickLike(){
        islike = !islike;
        startThumbAnim(islike);
        return islike;
    }

    public void setLike(boolean islike){
        this.islike = islike;
        if(islike){
            thumb.setImageResource(R.drawable.thumb_red);
        }else {
            thumb.setImageResource(R.drawable.thumb);
        }
    }

    public void setLickCount(int count){
        if(count==0){
            thumbCount.setText("点赞");
        }
        else
            thumbCount.setText(String.valueOf(count));
    }

}

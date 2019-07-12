package com.lin.meet.topic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lin.meet.R;
import com.lin.meet.bean.TopicMain;
import com.lin.meet.comment.CommentActivity;
import com.lin.meet.db_bean.Reply;
import com.lin.meet.db_bean.ReplyHolder;
import com.lin.meet.db_bean.comment;
import com.lin.meet.override.ScaleAnim;
import com.lin.meet.personal.PersonalActivity;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;
import de.hdodenhof.circleimageview.CircleImageView;

public class TopicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private List<RecommendViewBean> list = new ArrayList<>();
    private List<Reply> replys = new ArrayList<>();
    private Context context;
    private TopicCallback callback;
    private int thumbCount = 0,commentCount = 0;
    private boolean like = false;
    private int tempIndex = -1;
    private int tempCount = -1;
    private String authorUid = "";
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(context==null)
            context = viewGroup.getContext();
        View view = null;
        if(i==1){
            view = LayoutInflater.from(context).inflate(R.layout.topic_view,viewGroup,false);
            return new TopicViewHoloder(view);
        }
        else {
            view = LayoutInflater.from(context).inflate(R.layout.comment_item,viewGroup,false);
            return new ReplyHolder(view);
        }
    }


    @Override
    public int getItemViewType(int position) {
        if(position<list.size())
            return 1;
        else
            return 0;
    }

    private boolean isNoLogin(){
        return !BmobUser.isLogin();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHoloder, int i) {
        if(viewHoloder instanceof TopicViewHoloder){
            RecommendViewBean bean = list.get(i);
            switch (bean.flag){
                case RecommendViewBean.FLAG_HEAD:
                    ((TopicViewHoloder)viewHoloder).showLayout(1);
                    ((TopicViewHoloder)viewHoloder).author.setText(bean.author);
                    ((TopicViewHoloder)viewHoloder).time.setText(bean.time);
                    ((TopicViewHoloder)viewHoloder).loadHeader(context,bean.uri);
                    ((TopicViewHoloder)viewHoloder).header.setOnClickListener(v-> PersonalActivity.Companion.startOther((Activity) context,authorUid));
                    break;
                case RecommendViewBean.FLAG_IMAGE:
                    ((TopicViewHoloder)viewHoloder).showLayout(3);
                    ((TopicViewHoloder)viewHoloder).loadImage(context,bean.uri);
                    break;
                case RecommendViewBean.FLAG_CONTENT:
                    ((TopicViewHoloder)viewHoloder).showLayout(2);
                    ((TopicViewHoloder)viewHoloder).text.setText(bean.content);
                    break;
                case RecommendViewBean.FLAG_CONTENT_BOTTOM:
                    ((TopicViewHoloder)viewHoloder).setCount(thumbCount,commentCount);
                    ((TopicViewHoloder)viewHoloder).showLayout(5);
                    ((TopicViewHoloder)viewHoloder).thumb.setImageResource(like?R.mipmap.like2:R.mipmap.like);
                    ((TopicViewHoloder)viewHoloder).thumb.setOnClickListener((v)->{
                        if(isNoLogin())return;
                        if(like){
                            like = false;
                            ScaleAnim.startAnim(((TopicViewHoloder)viewHoloder).thumb,R.mipmap.like);
                            callback.setLike(like);
                        }
                        else {
                            like = true;
                            ScaleAnim.startAnim(((TopicViewHoloder)viewHoloder).thumb,R.mipmap.like2);
                            callback.setLike(like);
                        }
                    });
                    break;
                case RecommendViewBean.FLAG_CONTENT_REPLY:
                    ((TopicViewHoloder)viewHoloder).showLayout(6);
                    break;
                case RecommendViewBean.FLAG_CONTENT_TITLE:
                    ((TopicViewHoloder)viewHoloder).showLayout(4);
                    ((TopicViewHoloder)viewHoloder).title.setText(bean.content);
                    break;
                case RecommendViewBean.FLAG_LOCATION:
                    ((TopicViewHoloder)viewHoloder).showLayout(7);
                    ((TopicViewHoloder)viewHoloder).location.setText(bean.content);
                    break;
            }
        }else if(viewHoloder instanceof ReplyHolder){
            int index = i - list.size();
            ((ReplyHolder)viewHoloder).content.setText(replys.get(index).bean.getContent());
            ((ReplyHolder)viewHoloder).nickName.setText(replys.get(index).nickName);
            ((ReplyHolder)viewHoloder).time.setText(replys.get(index).bean.getCreatedAt());
            ((ReplyHolder)viewHoloder).loadHeader(context,replys.get(index).headUri);
            ((ReplyHolder)viewHoloder).setLickCount(replys.get(index).likeCount);
            ((ReplyHolder)viewHoloder).setLike(replys.get(index).like);
            if(replys.get(index).replyCount>0)
                ((ReplyHolder)viewHoloder).replyCount.setText(String.valueOf(replys.get(index).replyCount)+"回复");
            else
                ((ReplyHolder)viewHoloder).replyCount.setText("回复");
            ((ReplyHolder)viewHoloder).item.setOnClickListener((v)->{
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("id",replys.get(index).bean.getId());
                intent.putExtra("flag",replys.get(index).bean.getFlag());
                intent.putExtra("Like",replys.get(index).like);
                intent.putExtra("Count",replys.get(index).replyCount);
                tempIndex = i - list.size();
                callback.startComment(intent);
            });
            ((ReplyHolder)viewHoloder).like.setOnClickListener(v->{
                if(isNoLogin())return;
                replys.get(index).like = !replys.get(index).like;
                ((ReplyHolder)viewHoloder).setLikeAnim(replys.get(index).like);
                updateCommentLike(index,((ReplyHolder)viewHoloder),replys.get(index).like);
                callback.onCommentLike(replys.get(index).bean.getId(),replys.get(index).bean.getUid(),replys.get(index).like);
            });
            ((ReplyHolder)viewHoloder).replyCount.setOnClickListener(v->{
                callback.showSonEdit(replys.get(index).bean,i);
            });
            ((ReplyHolder)viewHoloder).header.setOnClickListener(v->PersonalActivity.Companion.startOther((Activity)context,replys.get(index).bean.getUid()));
        }
    }

    @Override
    public int getItemCount() {
        return list.size()+replys.size();
    }

    public static class TopicViewHoloder extends RecyclerView.ViewHolder {
        CircleImageView header;
        TextView author;
        TextView time;
        ImageView image;
        TextView text;
        TextView title;
        RelativeLayout layout_1;
        LinearLayout layout_2_title;
        LinearLayout layout_2;
        LinearLayout layout_2_bottom;
        LinearLayout layout_3;
        LinearLayout layout_4;
        LinearLayout layout_5;
        TextView thumbCount;
        TextView commentCount;
        TextView location;
        ImageView thumb;
        public TopicViewHoloder(@NonNull View itemView) {
            super(itemView);
            header=(CircleImageView)itemView.findViewById(R.id.topic_view_header);
            author=(TextView) itemView.findViewById(R.id.topic_view_author);
            time=(TextView) itemView.findViewById(R.id.topic_view_time);
            image=(ImageView) itemView.findViewById(R.id.topic_view_image);
            text=(TextView) itemView.findViewById(R.id.topic_view_content);
            title = (TextView) itemView.findViewById(R.id.topic_view_title);
            layout_1 = (RelativeLayout)itemView.findViewById(R.id.topic_view_layout1);
            layout_2_title = (LinearLayout)itemView.findViewById(R.id.topic_view_layout2_title);
            layout_2 = (LinearLayout)itemView.findViewById(R.id.topic_view_layout2);
            layout_2_bottom = (LinearLayout)itemView.findViewById(R.id.topic_view_layout2_bottom);
            layout_3 = (LinearLayout)itemView.findViewById(R.id.topic_view_layout3);
            layout_4 = (LinearLayout)itemView.findViewById(R.id.topic_view_layout4);
            layout_5 = (LinearLayout)itemView.findViewById(R.id.topic_view_location);
            thumbCount = (TextView)itemView.findViewById(R.id.topic_thumb_count);
            commentCount = (TextView)itemView.findViewById(R.id.topic_reply_count);
            location = (TextView)itemView.findViewById(R.id.location_text);
            thumb = (ImageView)itemView.findViewById(R.id.topic_thumb);
        }

        void loadImage(Context context, String url){
            Glide.with(context).asDrawable().load(url).into(image);
        }

        public void loadHeader(Context context,String url){
            Glide.with(context).asDrawable().load(url).into(header);
        }

        void showLayout(int i){
                layout_1.setVisibility(i==1?View.VISIBLE:View.GONE);
                layout_2.setVisibility(i==2?View.VISIBLE:View.GONE);
                layout_2_title.setVisibility(i==4?View.VISIBLE:View.GONE);
                layout_2_bottom.setVisibility(i==5?View.VISIBLE:View.GONE);
                layout_3.setVisibility(i==3?View.VISIBLE:View.GONE);
                layout_4.setVisibility(i==6?View.VISIBLE:View.GONE);
                layout_5.setVisibility(i==7?View.VISIBLE:View.GONE);
        }

        public void setCount(int x,int y){
            thumbCount.setText(String.valueOf(x));
            commentCount.setText(String.valueOf(y));
        }
    }

    public static class RecommendViewBean{
        String uri;
        String content;
        String time;
        String author;
        int flag;
        public static final int FLAG_HEAD = 0;
        public static final int FLAG_IMAGE = 1;
        public static final int FLAG_CONTENT = 2;
        public static final int FLAG_CONTENT_BOTTOM = 3;
        public static final int FLAG_CONTENT_REPLY = 4;
        public static final int FLAG_CONTENT_TITLE = 5;
        static final int FLAG_LOCATION = 6;
    }

    TopicAdapter(TopicCallback callback){
        this.callback = callback;
    }

    public void initAdapter(TopicMain bean){
        if(bean==null)
            return;
        this.authorUid = bean.bean.getUid();
        RecommendViewBean reBean= new RecommendViewBean();
        reBean.author = bean.getNickName();
        reBean.time = bean.bean.getCreatedAt();
        reBean.uri = bean.getHeaderUri();
        reBean.flag = RecommendViewBean.FLAG_HEAD;
        list.add(reBean);

        reBean = new RecommendViewBean();
        reBean.content = bean.bean.getTitle();
        reBean.flag = RecommendViewBean.FLAG_CONTENT_TITLE;
        list.add(reBean);

        for(int i=0;i<6;i++){
            if(!bean.images[i].equals("@null")){
                reBean = new RecommendViewBean();
                reBean.flag = RecommendViewBean.FLAG_IMAGE;
                reBean.uri = bean.images[i];
                list.add(reBean);
            }
        }

        reBean = new RecommendViewBean();
        reBean.flag = RecommendViewBean.FLAG_CONTENT;
        reBean.content = bean.bean.getContent();
        list.add(reBean);

        if(!bean.bean.getLocation().equals("@null")){
            reBean = new RecommendViewBean();
            reBean.flag = RecommendViewBean.FLAG_LOCATION;
            reBean.content = bean.bean.getLocation();
            list.add(reBean);
        }

        reBean = new RecommendViewBean();
        reBean.flag = RecommendViewBean.FLAG_CONTENT_BOTTOM;
        list.add(reBean);

        reBean = new RecommendViewBean();
        reBean.flag = RecommendViewBean.FLAG_CONTENT_REPLY;
        list.add(reBean);
        notifyDataSetChanged();
    }

    void initThumbCount(TopicMain bean){
        thumbCount = bean.getLikeCount();
        notifyDataSetChanged();
    }

    synchronized int insertComment(int position,Reply bean){
        replys.add(position,bean);
        notifyDataSetChanged();
        return replys.size()+list.size()-1;
    }

    synchronized int insertComment(Reply bean){
        replys.add(bean);
        notifyDataSetChanged();
        return replys.size()+list.size()-1;
    }

    interface TopicCallback{
        void showSonEdit(comment comment, int position);
        void setLike(boolean like);
        void onCommentLike(String parentId,String parentUid,boolean like);
        void startComment(Intent intent);
    }

    public void setThumbCount(int count){
        this.thumbCount = count;
        notifyDataSetChanged();
    }

    public void setCommentCount(int count){
        this.commentCount = count;
        notifyDataSetChanged();
    }


    int getLikeCount() {
        return thumbCount;
    }

    private void updateCommentLike(int i,ReplyHolder holder,boolean like){
        if(like)
            replys.get(i).likeCount++;
        else
            replys.get(i).likeCount--;
        notifyDataSetChanged();
    }

    void setLike(boolean like){
        this.like = like;
        notifyDataSetChanged();
    }

    void addComment(int position){
        replys.get(position-list.size()).replyCount++;
        notifyDataSetChanged();
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

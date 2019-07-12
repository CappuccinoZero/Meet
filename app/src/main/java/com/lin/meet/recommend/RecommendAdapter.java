package com.lin.meet.recommend;

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
import com.lin.meet.comment.CommentActivity;
import com.lin.meet.db_bean.Reply;
import com.lin.meet.db_bean.ReplyHolder;
import com.lin.meet.db_bean.comment;
import com.lin.meet.jsoup.LoveNewsBean;
import com.lin.meet.override.ScaleAnim;
import com.lin.meet.personal.PersonalActivity;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;
import de.hdodenhof.circleimageview.CircleImageView;

public class RecommendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private int thumbCount = 0,commentCount = 0;
    private List<RecommendViewBean> list = new ArrayList<>();
    private List<Reply> replys = new ArrayList<>();
    private Context context;
    private RecommendCallback callback;
    private boolean isLike = false;
    private int tempIndex = -1;
    private int tempCount = -1;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(context==null)
            context = viewGroup.getContext();
        View view = null;
        if(i==1){
            view = LayoutInflater.from(context).inflate(R.layout.recommend_view,viewGroup,false);
            return new RecommendViewHoloder(view);
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
        if(viewHoloder instanceof RecommendViewHoloder){
            RecommendViewBean bean = list.get(i);
            switch (bean.flag){
                case RecommendViewBean.FLAG_HEAD:
                    ((RecommendViewHoloder)viewHoloder).showLayout(1);
                    ((RecommendViewHoloder)viewHoloder).author.setText(bean.author);
                    ((RecommendViewHoloder)viewHoloder).time.setText(bean.time);
                    break;
                case RecommendViewBean.FLAG_IMAGE:
                    ((RecommendViewHoloder)viewHoloder).showLayout(3);
                    ((RecommendViewHoloder)viewHoloder).loadImage(context,bean.uri);
                    break;
                case RecommendViewBean.FLAG_CONTENT:
                    ((RecommendViewHoloder)viewHoloder).showLayout(2);
                    if(bean.content.charAt(0)==' '||bean.content.charAt(0)=='　')
                        ((RecommendViewHoloder)viewHoloder).text.setText(bean.content);
                    else
                        ((RecommendViewHoloder)viewHoloder).text.setText("　　"+bean.content);
                    break;
                case RecommendViewBean.FLAG_CONTENT_BOTTOM:
                    ((RecommendViewHoloder)viewHoloder).setCount(thumbCount,commentCount);
                    ((RecommendViewHoloder)viewHoloder).showLayout(5);
                    ((RecommendViewHoloder)viewHoloder).thumb.setImageResource(isLike?R.mipmap.like2:R.mipmap.like);
                    ((RecommendViewHoloder)viewHoloder).thumb.setOnClickListener((v)->{
                        if(isNoLogin())return;
                        if(isLike){
                            isLike = false;
                            ScaleAnim.startAnim(((RecommendViewHoloder)viewHoloder).thumb,R.mipmap.like);
                            callback.setIsLike(isLike);
                        }
                        else {
                            isLike = true;
                            ScaleAnim.startAnim(((RecommendViewHoloder)viewHoloder).thumb,R.mipmap.like2);
                            callback.setIsLike(isLike);
                        }
                        changeThumbCount(isLike);
                    });
                    break;
                case RecommendViewBean.FLAG_CONTENT_REPLY:
                    ((RecommendViewHoloder)viewHoloder).showLayout(6);
                    break;
            }
        }else if(viewHoloder instanceof ReplyHolder) {
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
                callback.startComment(intent);
                tempIndex = index;
            });
            ((ReplyHolder)viewHoloder).like.setOnClickListener(v->{
                if(isNoLogin())return;
                replys.get(index).like = !replys.get(index).like;
                ((ReplyHolder)viewHoloder).setLikeAnim(replys.get(index).like);
                updateCommentLike(index,((ReplyHolder)viewHoloder),replys.get(index).like);
                callback.onSonLike(replys.get(index).bean.getId(),replys.get(index).bean.getUid(),replys.get(index).like);
            });
            ((ReplyHolder)viewHoloder).replyCount.setOnClickListener(v-> callback.showSonEdit(replys.get(index).bean,i));
            ((ReplyHolder)viewHoloder).header.setOnClickListener(v-> PersonalActivity.Companion.startOther((Activity)context,replys.get(index).bean.getUid()));
        }
    }

    private void updateCommentLike(int i,ReplyHolder holder,boolean like){
        if(like)
            replys.get(i).likeCount++;
        else
            replys.get(i).likeCount--;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size()+replys.size();
    }

    public static class RecommendViewHoloder extends RecyclerView.ViewHolder {
        CircleImageView header;
        TextView author;
        TextView time;
        ImageView image;
        TextView text;
        TextView thumbCount;
        TextView commentCount;
        RelativeLayout layout_1;
        LinearLayout layout_2;
        LinearLayout layout_2_bottom;
        LinearLayout layout_3;
        LinearLayout layout_4;
        ImageView thumb;
        public RecommendViewHoloder(@NonNull View itemView) {
            super(itemView);
            header=(CircleImageView)itemView.findViewById(R.id.recommend_view_header);
            author=(TextView) itemView.findViewById(R.id.recommend_view_author);
            time=(TextView) itemView.findViewById(R.id.recommend_view_time);
            image=(ImageView) itemView.findViewById(R.id.recommend_view_image);
            text=(TextView) itemView.findViewById(R.id.recommend_view_content);
            thumbCount = (TextView)itemView.findViewById(R.id.recommend_thumb_count);
            commentCount = (TextView)itemView.findViewById(R.id.recommend_reply_count);
            layout_1 = (RelativeLayout)itemView.findViewById(R.id.recommend_view_layout1);
            layout_2 = (LinearLayout)itemView.findViewById(R.id.recommend_view_layout2);
            layout_2_bottom = (LinearLayout)itemView.findViewById(R.id.recommend_view_layout2_bottom);
            layout_3 = (LinearLayout)itemView.findViewById(R.id.recommend_view_layout3);
            layout_4 = (LinearLayout)itemView.findViewById(R.id.recommend_view_layout4);
            thumb = (ImageView)itemView.findViewById(R.id.recommend_thumb);
        }

        public void loadImage(Context context,String url){
            Glide.with(context).asDrawable().load(url).into(image);
        }

        public void loadHeader(Context context,String url){
            Glide.with(context).asDrawable().load(url).into(header);
        }

        public void showLayout(int i){
                layout_1.setVisibility(i==1?View.VISIBLE:View.GONE);
                layout_2.setVisibility(i==2?View.VISIBLE:View.GONE);
                layout_2_bottom.setVisibility(i==5?View.VISIBLE:View.GONE);
                layout_3.setVisibility(i==3?View.VISIBLE:View.GONE);
                layout_4.setVisibility(i==6?View.VISIBLE:View.GONE);
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
    }

    RecommendAdapter(RecommendCallback callback){
        this.callback = callback;
    }


    public void initAdapter(LoveNewsBean bean){
        if(bean==null)
            return;
        RecommendViewBean reBean= new RecommendViewBean();
        reBean.author = bean.getAuthor();
        reBean.time = bean.getTime();
        reBean.flag = RecommendViewBean.FLAG_HEAD;
        list.add(reBean);
        int max = Math.max(bean.getContents().size()-1,bean.getImgs().size()-1);
        for(int i=0;i<max;i++){
            if(bean.getContents().size()-1>i){
                reBean = new RecommendViewBean();
                reBean.flag = RecommendViewBean.FLAG_CONTENT;
                reBean.content = bean.getContents().get(i);
                list.add(reBean);
            }
            if(bean.getImgs().size()-1>i&&i>0){
                reBean = new RecommendViewBean();
                reBean.flag = RecommendViewBean.FLAG_IMAGE;
                reBean.uri = bean.getImgs().get(i);
                list.add(reBean);
            }
        }
        reBean = new RecommendViewBean();
        reBean.flag = RecommendViewBean.FLAG_CONTENT_BOTTOM;
        list.add(reBean);
        reBean = new RecommendViewBean();
        reBean.flag = RecommendViewBean.FLAG_CONTENT_REPLY;
        list.add(reBean);
    }

    public void initAidongwuAdapter(LoveNewsBean bean){
        if(bean==null)
            return;
        RecommendViewBean reBean= new RecommendViewBean();
        reBean.author = bean.getAuthor();
        reBean.time = bean.getTime();
        reBean.flag = RecommendViewBean.FLAG_HEAD;
        list.add(reBean);
        int max = Math.max(bean.getContents().size(),bean.getImgs().size());
        for(int i=0;i<max;i++){
            if(bean.getContents().size()>i){
                reBean = new RecommendViewBean();
                reBean.flag = RecommendViewBean.FLAG_CONTENT;
                reBean.content = bean.getContents().get(i);
                list.add(reBean);
            }
            if(bean.getImgs().size()>i){
                reBean = new RecommendViewBean();
                reBean.flag = RecommendViewBean.FLAG_IMAGE;
                reBean.uri = bean.getImgs().get(i);
                list.add(reBean);
            }
        }
        reBean = new RecommendViewBean();
        reBean.flag = RecommendViewBean.FLAG_CONTENT_BOTTOM;
        list.add(reBean);
        reBean = new RecommendViewBean();
        reBean.flag = RecommendViewBean.FLAG_CONTENT_REPLY;
        list.add(reBean);
    }

    synchronized int insertComment(Reply bean){
        replys.add(bean);
        notifyDataSetChanged();
        return replys.size()+list.size()-1;
    }

    synchronized int insertComment(int position,Reply bean){
        replys.add(position,bean);
        notifyDataSetChanged();
        return replys.size()+list.size()-1;
    }

    public void setThumbCount(int x){
        thumbCount = x;
        notifyDataSetChanged();
    }

    public void changeThumbCount(boolean like){
        if (like)
            thumbCount++;
        else
            thumbCount--;
        notifyDataSetChanged();
    }

    public void setCommentCount(int x){
        commentCount = x;
        notifyDataSetChanged();
    }

    public void setCount(int thumb,int comment){
        thumbCount = thumb;
        commentCount = comment;
        notifyDataSetChanged();
    }

    public interface RecommendCallback{
        void showSonEdit(comment comment, int position);
        void setIsLike(Boolean isLike);
        void onSonLike(String parentId,String parentUid,boolean like);
        void startComment(Intent intent);
    }

    public void setLike(Boolean isLike){
        this.isLike = isLike;
        notifyDataSetChanged();
    }

    public void setSonLike(int position,int count){
        replys.get(position-list.size()).likeCount = count;
        notifyDataSetChanged();
    }

    public void setCommentLike(int position,boolean isLike){
        replys.get(position-list.size()).like = isLike;
        notifyDataSetChanged();
    }

    public void addComment(int position){
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

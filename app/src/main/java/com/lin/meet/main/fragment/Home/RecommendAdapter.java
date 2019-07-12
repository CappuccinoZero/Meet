package com.lin.meet.main.fragment.Home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.transition.Explode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lin.meet.R;
import com.lin.meet.jsoup.LoveNewsBean;
import com.lin.meet.recommend.RecommendActivity;
import com.lin.meet.recommend.RecommendConstract;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecommendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<LoveNewsBean> list = new ArrayList<>();
    private Context context;
    private Activity activity;
    private boolean isLoading = false;
    private boolean isError = false;

    RecommendAdapter(Activity activity){
        this.activity = activity;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(context==null)
            context=viewGroup.getContext();
        if(i==1){
            View view = LayoutInflater.from(context).inflate(R.layout.recommend_item,viewGroup,false);
            return new RecommendViewHolder(view,false);
        }
        else{
            View view = LayoutInflater.from(context).inflate(R.layout.loading_item_2,viewGroup,false);
            return new RecommendViewHolder(view,true);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if(!((RecommendViewHolder) viewHolder).isBottom){
            LoveNewsBean bean = list.get(i);
            ((RecommendViewHolder) viewHolder).title.setText(bean.getTitle());
            ((RecommendViewHolder) viewHolder).author.setText(bean.getAuthor());
            ((RecommendViewHolder) viewHolder).setImageUrl(context,bean.getImg());
            setOnclickListener((RecommendViewHolder) viewHolder,bean);
        }else {
            if(isError){
                ((RecommendViewHolder) viewHolder).errorView.setVisibility(View.VISIBLE);
                ((RecommendViewHolder) viewHolder).loadingView1.setVisibility(View.GONE);
            }
            else if(isLoading){
                ((RecommendViewHolder) viewHolder).errorView.setVisibility(View.GONE);
                ((RecommendViewHolder) viewHolder).loadingView1.setVisibility(View.VISIBLE);
            }else{
                ((RecommendViewHolder) viewHolder).errorView.setVisibility(View.GONE);
                ((RecommendViewHolder) viewHolder).loadingView1.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void setOnclickListener(RecommendViewHolder viewHolder,final LoveNewsBean bean){
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hideCallback!=null)hideCallback.setVisiable(false);
                activity.getWindow().setExitTransition(new Explode());
                Intent intent = new Intent(context, RecommendActivity.class);
                intent.putExtra("LoveNewsBean",bean);
                Pair<View,String> pair1 = new Pair<>((View)viewHolder.cardView, ViewCompat.getTransitionName(viewHolder.cardView));
                Pair<View,String> pair2 = new Pair<>((View)viewHolder.title, ViewCompat.getTransitionName(viewHolder.title));
                ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,pair1,pair2);
                ActivityCompat.startActivity(activity,intent,compat.toBundle());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == list.size())
            return 0;
        return 1;
    }

    static class RecommendViewHolder extends RecyclerView.ViewHolder{
        public boolean isBottom;
        CardView cardView;
        TextView title;
        TextView author;
        ImageView img;
        CircleImageView header;

        View loadingView1;
        View errorView;
        RecommendViewHolder(@NonNull View itemView, boolean isBottom) {
            super(itemView);
            this.isBottom = isBottom;
            if (!isBottom){
                cardView = itemView.findViewById(R.id.recommend_card);
                title = itemView.findViewById(R.id.recommend_item_title);
                author = itemView.findViewById(R.id.recommend_item_author);
                img = itemView.findViewById(R.id.recommend_item_img);
                header = itemView.findViewById(R.id.recommend_item_header);
            }else{
                loadingView1 = itemView.findViewById(R.id.loading_1);
                errorView =itemView.findViewById(R.id.errorView);
            }
        }


        public void setImageUrl(Context context,String url){
            Glide.with(context).asDrawable().load(url).into(img);
        }

        public void setHeaderUrl(Context context,String url){
            Glide.with(context).asDrawable().load(url).into(header);
        }
    }

    public void addRecomd(LoveNewsBean bean){
        list.add(bean);
        notifyDataSetChanged();
    }

    public void addRecomds(List<LoveNewsBean> beans){
        for(int i=0;i<beans.size();i++){
            list.add(beans.get(i));
            notifyDataSetChanged();
        }
    }

    public void addTopRecomds(List<LoveNewsBean> beans){
        for(int i=0;i<beans.size();i++){
            list.add(i,beans.get(i));
            notifyDataSetChanged();
        }
    }

    public synchronized void addRecomd(int position,LoveNewsBean bean){
        list.add(position,bean);
        notifyDataSetChanged();
    }

    public void refresh(){
        list.clear();
        isLoading = false;
        notifyDataSetChanged();
    }

    RecommendConstract.searchCallback hideCallback;
    public void setHideCallback(RecommendConstract.searchCallback hideCallback){
        this.hideCallback = hideCallback;
    }

    void setLoadingStatus(boolean isLoading){
        this.isLoading = isLoading;
        notifyDataSetChanged();
    }

    boolean isLoading(){
        return isLoading;
    }

    void setError(boolean error){
        this.isError = error;
        notifyDataSetChanged();
    }

    boolean isError(){
        return isError;
    }

}

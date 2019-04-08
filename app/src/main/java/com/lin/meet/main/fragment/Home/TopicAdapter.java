package com.lin.meet.main.fragment.Home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lin.meet.R;
import com.lin.meet.topic.TopicActivity;
import com.ufreedom.floatingview.Floating;
import com.ufreedom.floatingview.FloatingBuilder;
import com.ufreedom.floatingview.FloatingElement;

public class TopicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(context==null)
            context=viewGroup.getContext();
        View view = null;
        view = LayoutInflater.from(context).inflate(R.layout.topic_item,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        setListener((ViewHolder) viewHolder);
    }

    @Override
    public int getItemCount() {
        return 20;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        boolean awesomeStatus;
        ImageView awesome;
        CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            awesomeStatus = false;
            awesome = (ImageView)itemView.findViewById(R.id.awesome);
            cardView = (CardView)itemView.findViewById(R.id.topic_cardView);
        }
    }

    private void setListener(final ViewHolder viewHolder){
        viewHolder.awesome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!viewHolder.awesomeStatus){
                    viewHolder.awesomeStatus = true;
                    viewHolder.awesome.setImageResource(R.drawable.thumb_red);
                    FloatingElement element = new FloatingBuilder()
                            .anchorView(viewHolder.awesome)
                            .targetView(R.layout.aswsome_view)
                            .build();
                    Floating floating=new Floating((Activity) context);
                    floating.startFloating(element);
                }else {
                    viewHolder.awesomeStatus = false;
                    viewHolder.awesome.setImageResource(R.drawable.thumb);
                }
            }
        });
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TopicActivity.class);
                context.startActivity(intent);
            }
        });
    }
}

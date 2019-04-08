package com.lin.meet.override;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.lin.meet.R;

public  class  LoadingHolder extends RecyclerView.ViewHolder{
    RelativeLayout layout_1;
    RelativeLayout layout_2;
    RelativeLayout layout_3;
    LinearLayout layout_4;
    public LoadingHolder(@NonNull View itemView) {
        super(itemView);
        layout_1 = (RelativeLayout)itemView.findViewById(R.id.history_loading_1);
        layout_2 = (RelativeLayout) itemView.findViewById(R.id.history_loading_2);
        layout_3 = (RelativeLayout) itemView.findViewById(R.id.history_loading_3);
        layout_4 = (LinearLayout)itemView.findViewById(R.id.history_loading_4);
    }

    public void setData(int mState){
        switch (mState){
            case 0:
                layout_1.setVisibility(View.GONE);
                layout_2.setVisibility(View.GONE);
                layout_3.setVisibility(View.GONE);
                layout_4.setVisibility(View.VISIBLE);
                break;
            case 1:
                layout_1.setVisibility(View.VISIBLE);
                layout_2.setVisibility(View.GONE);
                layout_3.setVisibility(View.GONE);
                layout_4.setVisibility(View.GONE);
                break;
            case 2:
                layout_1.setVisibility(View.GONE);
                layout_2.setVisibility(View.VISIBLE);
                layout_3.setVisibility(View.GONE);
                layout_4.setVisibility(View.GONE);
                break;
            case 3:
                layout_1.setVisibility(View.GONE);
                layout_2.setVisibility(View.GONE);
                layout_3.setVisibility(View.VISIBLE);
                layout_4.setVisibility(View.GONE);
                break;
            case 4:
                layout_1.setVisibility(View.GONE);
                layout_2.setVisibility(View.GONE);
                layout_3.setVisibility(View.GONE);
                layout_4.setVisibility(View.GONE);
                break;
        }
    }
}

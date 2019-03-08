package com.lin.meet.history;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.lin.meet.IntroductionPage.IntroductionActivity;
import com.lin.meet.R;
import com.lin.meet.camera_demo.PhotoBean;
import com.lin.meet.demo.LoadingHolder;
import com.lin.meet.demo.SmoothCheckBox;
import com.lin.meet.main.DataBase;
import com.lin.meet.main.DataBaseModel;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static android.support.constraint.Constraints.TAG;

public class HistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements HistoryContract.Adapter{
    private Context context;
    private DataBase dataBase = new DataBaseModel();
    private List<PhotoBean> cache_list;
    private int list_count = 0;
    private Map<PhotoBean,Boolean> map = new HashMap<>();
    private boolean isDelete = false;
    private HistoryContract.View mView;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
    public int count;
    public static int mState;
    private int itemCount;
    private RequestOptions requestOptions = new RequestOptions();
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (context==null){
            context=viewGroup.getContext();
            mView=(HistoryActivity)context;
        }
        if(i==0){
            View view = LayoutInflater.from(context).inflate(R.layout.history_card_item,viewGroup,false);
            return new HistoryAdapter.ViewHolder(view);
        }else{
            View view = LayoutInflater.from(context).inflate(R.layout.loading_item,viewGroup,false);
            HistoryAdapter.LoadingHolder loadingHolder=new HistoryAdapter.LoadingHolder(view);
            loadingHolder.setData();
            return loadingHolder;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position==list_count)
            return 1;
        return 0;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder ViewHolder, int i) {
        if(ViewHolder instanceof HistoryAdapter.ViewHolder){
            HistoryAdapter.ViewHolder viewHolder =(HistoryAdapter.ViewHolder) ViewHolder;
            PhotoBean photo = cache_list.get(i);
            requestOptions.placeholder(R.drawable.default_image);
            requestOptions.error(R.drawable.load_error);
            Glide.with(context).asDrawable().load(photo.getPath()).apply(requestOptions).into(viewHolder.imageView);
            Date date = new Date(photo.getTime());
            viewHolder.time.setText(format.format(date));
            viewHolder.maybe.setText(photo.getMaybe());
            if(viewHolder.checkBox.isChecked()!=map.get(photo))
                viewHolder.checkBox.performClick();
            if(photo.getLocation()!=null){
                viewHolder.location.setText(photo.getLocation());
            }
            if(isDelete){
                viewHolder.checkBox.setVisibility(View.VISIBLE);
                viewHolder.layout_1.setVisibility(View.GONE);
                viewHolder.layout_2.setVisibility(View.VISIBLE);
            }else{
                viewHolder.checkBox.setVisibility(View.GONE);
            }
            setClistListener(viewHolder,i);
        }else if(ViewHolder instanceof HistoryAdapter.LoadingHolder){
            if(cache_list.size()<=6)
                mState = 4;
            ((LoadingHolder) ViewHolder).setData();
        }
    }



    @Override
    public int getItemCount() {
            return list_count+1;
    }


    @Override
    public void deletePhoto() {
        for(int i =0;i<cache_list.size();i++){
            if(map.get(cache_list.get(i))){
                map.remove(cache_list.get(i));
                File file = new File(cache_list.get(i).getPath());
                dataBase.deletePhotoData(cache_list.get(i).getPath());
                cache_list.remove(i);
                if(i<list_count){
                    list_count--;
                    notifyItemRemoved(i);
                }
                if(file.exists())
                    file.delete();
                i--;
            }
        }
    }

    @Override
    public void doSelectAll() {
        count = cache_list.size();
        for(int i=0;i<cache_list.size();i++){
            Log.d(TAG, "doSelectAll: 选择数量"+i);
            map.put(cache_list.get(i),true);
        }
        this.notifyDataSetChanged();
    }

    @Override
    public void doDelete(boolean delete) {
        isDelete = delete;
        this.notifyDataSetChanged();
    }

    @Override
    public void doCloseAll() {
        count = 0;
        for(int i=0;i<cache_list.size();i++){
            map.put(cache_list.get(i),false);
        }
        this.notifyDataSetChanged();
    }

    @Override
    public synchronized void insertItem(final Activity activity) {
        if (mState==2)
            return;
        mState = 1;
        notifyDataSetChanged();
        final Timer tr = new Timer();
        tr.schedule(new TimerTask() {
            @Override
            public void run() {
                tr.cancel();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(cache_list.size()==list_count)
                            mState = 2;
                        else{
                            addList();
                            mState = 0;
                        }
                        notifyDataSetChanged();
                    }
                });
            }
        },1500);
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public boolean isSelect() {
        boolean isDelete = false;
        for(int i =0;i<cache_list.size();i++){
            if(map.get(cache_list.get(i))){
                isDelete = true;
                break;
            }
        }
        return isDelete;
    }


    static class  ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView textButton;
        SmoothCheckBox checkBox;
        TextView time;
        TextView location;
        TextView maybe;
        ImageView imageView;
        ImageView up;
        ImageView down;
        RelativeLayout layout_1;
        LinearLayout layout_2;
        public ViewHolder(View view){
            super(view);
            cardView = (CardView)view;
            checkBox = (SmoothCheckBox) view.findViewById(R.id.card_box);
            textButton = (TextView) view.findViewById(R.id.card_text_button);
            time  = (TextView) view.findViewById(R.id.history_time);
            location  = (TextView) view.findViewById(R.id.history_location);
            maybe = (TextView) view.findViewById(R.id.history_maybe);
            imageView = (ImageView) view.findViewById(R.id.card_image);
            up = (ImageView) view.findViewById(R.id.up);
            down = (ImageView) view.findViewById(R.id.down);
            layout_1 = (RelativeLayout) view.findViewById(R.id.h_layout_1);
            layout_2 = (LinearLayout) view.findViewById(R.id.h_layout_2);
        }
    }

    static class  LoadingHolder extends RecyclerView.ViewHolder{
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

        public void setData(){
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

    HistoryAdapter(List<PhotoBean> list){
        cache_list=list;
        mState = 0;
        list_count = 0;
        addList();
    }

    private void addList(){
        int len=list_count+6;
        if(len>cache_list.size()){
            len=cache_list.size();
        }
        for(int i=list_count;i<len;i++){
            list_count++;
            map.put(cache_list.get(i),false);
        }
    }

    private void setClistListener(final ViewHolder viewHolder,final int i){
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isDelete){
                    viewHolder.checkBox.performClick();
                    if(viewHolder.checkBox.isChecked()){
                        count++;
                    }else{
                        count--;
                    }
                    map.put(cache_list.get(i),viewHolder.checkBox.isChecked());
                    mView.setCount(count);
                }
            }
        });
        viewHolder.cardView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        viewHolder.cardView.animate().translationZ(20f).setDuration(300).start();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    default:
                        viewHolder.cardView.animate().translationZ(1f).setDuration(400).start();
                        break;
                }
                return false;
            }
        });
        viewHolder.textButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!photoExists(cache_list.get(i).getPath())){
                    Toast.makeText(context,"源图片已丢失",Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent(context, IntroductionActivity.class);
                intent.putExtra("fromHistory",true);
                intent.putExtra("usePhoto",false);
                intent.putExtra("imagePath",cache_list.get(i).getPath());
                context.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation((Activity)context,viewHolder.imageView,"MyShare_0").toBundle());
            }
        });
        viewHolder.up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.layout_1.setVisibility(View.GONE);
                viewHolder.layout_2.setVisibility(View.VISIBLE);
            }
        });
        viewHolder.down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isDelete)
                    return;
                viewHolder.layout_1.setVisibility(View.VISIBLE);
                viewHolder.layout_2.setVisibility(View.GONE);
            }
        });
    }

    private boolean photoExists(String path){
        File file = new File(path);
        if(file.exists()||file.isFile())
            return true;
        return false;
    }

    private Bitmap getBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        //canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}

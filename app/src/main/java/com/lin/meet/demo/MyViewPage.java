package com.lin.meet.demo;

import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.lin.meet.R;
import com.lin.meet.my_util.MyUtil;

public class MyViewPage extends ViewPager {
    private float xDown;
    private float yDown;
    private double fz = Math.sqrt(3);
    private boolean setLocate = false;
    private recyclerStopScroll stopScroll = null;
    public MyViewPage(@NonNull Context context) {
        super(context);
    }

    public MyViewPage(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                setLocate = true;
                xDown=ev.getX();
                yDown=ev.getY();
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                float xMove=ev.getX()-xDown;
                float yMove=ev.getY()-yDown;
                float bz = Math.abs(xMove/yMove);
                if(bz>=fz){
                    if(Math.abs(xMove)<getResources().getDimension(R.dimen.touch_distance))
                        break;
                    if(xMove<0 &&setLocate){
                        setLocate = false;
                        getParent().requestDisallowInterceptTouchEvent(false);
                        if(stopScroll!=null)
                            stopScroll.setViewPageItem(RIGHT);
                    }else  if(xMove>0&&setLocate){
                        setLocate = false;
                        getParent().requestDisallowInterceptTouchEvent(false);
                        if(stopScroll!=null)
                            stopScroll.setViewPageItem(LEFT);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
                default:
                    break;

        }
        return super.dispatchTouchEvent(ev);
    }

    public void setRecyclerStop(recyclerStopScroll stopScroll){
        this.stopScroll = stopScroll;
    }

    public static int RIGHT = 0;
    public static int LEFT = 1;
    public interface recyclerStopScroll{
        void setViewPageItem(int direction);
    }
}

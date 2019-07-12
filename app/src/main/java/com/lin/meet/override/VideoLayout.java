package com.lin.meet.override;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.lin.meet.R;

public class VideoLayout extends FrameLayout {
    private int lastSeek = 0;
    private static final int MAX_CLICK_TIME = 200;
    private long lastTime = 0;
    private int lastX = 0;
    private int lastY = 0;
    private boolean isShow = true;
    private boolean isPause = false;
    private Callback callback = null;

    private View top,bottom;
    private int minAdjust,minSeek;
    private int adjustSpace,seekSpace;
    private boolean isSetAdjust,isSetSeek;
    private long lastShowView,lastShowAudio;
    private int lastY0;
    private int lastX0;
    public interface Callback{
        void showView();
        void hideView();
        void addAdjust();
        void lowAdjust();
        void addSeek();
        void lowSeek();
        void setSeek();
        void onPauseVideo();
        void onStartVideo();
        void showProgress();
        void hideProgress();
        void hideStateBar();
        void showStateBar();
    }

    public boolean isSetAdjust(){
        return isSetAdjust;
    }

    public boolean  isSetSeek(){
        return isSetSeek;
    }

    public boolean  isHideView(){
        if(System.currentTimeMillis() - lastShowView >= 2000){
            isShow = false;
            return true;
        }
        return false;
    }

    public boolean isHideAudio(){
        return System.currentTimeMillis() - lastShowAudio >= 2000;
    }

    private void initResource(AttributeSet attrs){
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.SmoothCheckBox);
        minAdjust = (int)array.getDimension(R.styleable.VideoLayout_minAdjust,100);
        minSeek = (int)array.getDimension(R.styleable.VideoLayout_minSeek,100);
        adjustSpace = (int)array.getDimension(R.styleable.VideoLayout_adjustSpace,10);
        seekSpace = (int)array.getDimension(R.styleable.VideoLayout_seekSpace,10);
        array.recycle();
    }

    public void setVideoListener(Callback callback,final View top,final View bottom){
        this.callback = callback;
        this.top = top;
        this.bottom = bottom;
    }

    public VideoLayout(Context context) {
        super(context);
    }

    public VideoLayout(Context context,AttributeSet attrs) {
        super(context, attrs);
        initResource(attrs);
    }


    public VideoLayout(Context context,AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initResource(attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(callback==null||!(ev.getY() > top.getBottom() && ev.getY() < bottom.getTop())) return super.dispatchTouchEvent(ev);;
        if(ev.getAction()==MotionEvent.ACTION_DOWN){
            lastX = (int)ev.getX();
            lastY = (int)ev.getY();
        }else if(ev.getAction()==MotionEvent.ACTION_MOVE){
            if(isSetSeek||Math.abs(ev.getX()-lastX)>minSeek&&!isSetAdjust){
                callback.showView();
                if(!isSetSeek){
                    isSetSeek = true;
                    lastX0 = (int)ev.getX();
                }
                else if(Math.abs(ev.getX()-lastX0)>=seekSpace){
                    if(ev.getX()>lastX0)
                        callback.addSeek();
                    else
                        callback.lowSeek();
                    lastX0 = (int)ev.getX();
                }
            }
            if(isSetAdjust||Math.abs(ev.getY()-lastY)>minAdjust&&!isSetSeek){
                if(!isSetAdjust){
                    callback.showProgress();
                    isSetAdjust = true;
                    lastY0 = (int)ev.getY();
                }
                if(Math.abs(ev.getY()-lastY0)>=adjustSpace){
                    if(ev.getY()<lastY0)
                        callback.addAdjust();
                    else
                        callback.lowAdjust();
                    lastY0 = (int)ev.getY();
                }
            }
        }
        else if(ev.getAction()==MotionEvent.ACTION_UP){
            if(isSetAdjust){
                isSetAdjust = false;
                isSetSeek = false;
                lastShowAudio = System.currentTimeMillis();
                return false;
            }
            if(isSetSeek){
                isSetAdjust = false;
                isSetSeek = false;
                callback.setSeek();
                callback.hideView();
                return false;
            }
            isSetAdjust = false;
            isSetSeek = false;
            if(ev.getPointerCount() == 1){
                isShow = !isShow;
                if(System.currentTimeMillis() - lastTime <= MAX_CLICK_TIME){
                    isPause = !isPause;
                    lastTime = 0;
                    if(isPause) isShow = true;
                    else isShow = false;
                }else{
                    lastTime = System.currentTimeMillis();
                }
                if(isShow){
                    callback.showView();
                    callback.showStateBar();
                    lastShowView = System.currentTimeMillis();
                }else{
                    callback.hideView();
                    callback.hideStateBar();
                }
                if(isPause) callback.onPauseVideo();
                else callback.onStartVideo();
            }
        }
        return true;
    }
}

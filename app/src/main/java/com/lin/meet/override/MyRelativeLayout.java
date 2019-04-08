package com.lin.meet.override;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class MyRelativeLayout extends RelativeLayout {
    private MyLayoutInterface myLayoutInterface;

    public MyRelativeLayout(Context context) {
        super(context);
    }

    public MyRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(myLayoutInterface!=null){
            myLayoutInterface.touch(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    public interface MyLayoutInterface{
        void touch(MotionEvent event);
    }

    public void setLayoutInterface(MyLayoutInterface layoutInterface){
        this.myLayoutInterface = layoutInterface;
    }
}

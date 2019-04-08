package com.lin.meet.override;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import cn.jzvd.JZVideoPlayerStandard;

public class JZVideo extends JZVideoPlayerStandard {
    public JZVideo(Context context) {
        super(context);
    }

    public JZVideo(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }


}

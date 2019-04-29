package com.lin.meet.override;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.widget.ImageView;

public class ScaleAnim {
    public static void startAnim(ImageView view, int changeId){
        ObjectAnimator startX = ObjectAnimator.ofFloat(view,"ScaleX",1f,0f);
        ObjectAnimator startY = ObjectAnimator.ofFloat(view,"ScaleY",1f,0f);
        ObjectAnimator endX = ObjectAnimator.ofFloat(view,"ScaleX",0f,1f);
        ObjectAnimator endY = ObjectAnimator.ofFloat(view,"ScaleY",0f,1f);
        startX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setImageResource(changeId);
            }
        });
        AnimatorSet set = new AnimatorSet();
        set.play(startX).with(startY).before(endX).before(endY);
        set.setDuration(100);
        set.start();
    }
}

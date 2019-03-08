package com.lin.meet.main;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.bumptech.glide.Glide;
import com.lin.meet.R;
import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;
public class HelloTest extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_test);
        JZVideoPlayerStandard jzVideoPlayerStandard = (JZVideoPlayerStandard) findViewById(R.id.videoplayer);

        jzVideoPlayerStandard.setUp("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"
                , JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL, "饺子闭眼睛");

        Glide.with(this).load("http://p.qpic.cn/videoyun/0/2449_43b6f696980311e59ed467f22794e792_1/640").into(jzVideoPlayerStandard.thumbImageView);
    }

    @Override
    public void onBackPressed() {
        if (JZVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }
    @Override
    protected void onPause() { //选择适当的声明周期释放
        super.onPause();
        JZVideoPlayer.releaseAllVideos();
    }
}

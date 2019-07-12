package com.lin.meet.video;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;

import com.danikula.videocache.HttpProxyCacheServer;
import com.lin.meet.MyApplication;
import com.lin.meet.ijk_media.media.IjkVideoView;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class VideoViewManager {
    private Context context;
    private boolean playVideo = false;
    private AudioManager audio;
    private String proxyUrl = "";
    private VideoCallback callback;
    public interface VideoCallback{
        void onStart();
        void onError();
        void onEnd();
    }
    private IjkVideoView player;
    public VideoViewManager(IjkVideoView player,Context context) {
        this.player = player;
        this.context = context;
        audio=(AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        IjkMediaPlayer mediaPlayer = new IjkMediaPlayer();
    }

    public void setUri(String uri){
        if(!uri.equals("")){
            HttpProxyCacheServer proxy = MyApplication.getProxy(context);
            proxyUrl = proxy.getProxyUrl(uri);
        }
        player.setVideoURI(Uri.parse(proxyUrl));
    }

    public void play(String uri){
        setUri(uri);
        player.start();
    }

    public void play(){
        player.start();
        if(!playVideo)
            start();
    }

    public void stop(){
        player.stopPlayback();
    }

    public void pause(){
        player.pause();
    }

    public int getDuration(){
        return player.getDuration();
    }

    public int getPosition(){
        int x = (int)(100*player.getCurrentPosition()/(float)player.getDuration());
        return x;
    }

    public String getTime(){
        int position = player.getCurrentPosition()/1000;
        int h = position/60;
        int min = position%60;
        String strh;
        String strmin;
        if(h<10)
            strh = "0"+ String.valueOf(h);
        else
            strh = String.valueOf(h);
        if(min<10)
            strmin = "0"+ String.valueOf(min);
        else
            strmin = String.valueOf(min);
        String str = strh+":"+strmin+"/"+getDurationStr();
        return str;
    }

    public String getTime(int position){
        position = (int)(player.getDuration()*(position/100f)/1000);
        int h = position/60;
        int min = position%60;
        String strh;
        String strmin;
        if(h<10)
            strh = "0"+ String.valueOf(h);
        else
            strh = String.valueOf(h);
        if(min<10)
            strmin = "0"+ String.valueOf(min);
        else
            strmin = String.valueOf(min);
        String str = strh+":"+strmin+"/"+getDurationStr();
        return str;
    }

    public void setVideoListener(Activity activity,VideoCallback callback){
        if(callback!=null)
            this.callback = callback;
        player.setOnCompletionListener(x->{
            playVideo = false;
            this.callback.onEnd();
        });
        player.setOnErrorListener((x,y,z)->{
            playVideo = false;
            this.callback.onError();
            return false;
        });
        player.setOnPreparedListener(x->{
            start();
        });
    }

    public void seekTo(int to){
        if(to>100) to = 100;
        if(to<0) to = 0;
        int duration = getDuration();
        if(duration>0){
            int msec = (int)(duration * (to)/100f);
            player.seekTo(msec);
        }
    }

    public boolean isPlaying(){
        return playVideo;
    }

    public void addAdjust(){
        audio.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_RAISE,AudioManager.FLAG_PLAY_SOUND);
    }

    public void lowAdjust(){
        audio.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_LOWER,AudioManager.FLAG_PLAY_SOUND);
    }

    public void setAdjust(float x){
        if(x>1) x=1;
        if(x<0) x=0;
        int max = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int set = (int)x *max;
        audio.setStreamVolume(AudioManager.STREAM_MUSIC,set,AudioManager.FLAG_PLAY_SOUND);
    }

    public int getAdjust(){
        int max = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int current = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
        return (int)(100 * current/(float)max);
    }

    private String getDurationStr(){
        int position = getDuration()/1000;
        int h = position/60;
        int min = position%60;
        String strh;
        String strmin;
        if(h<10)
            strh = "0"+ String.valueOf(h);
        else
            strh = String.valueOf(h);
        if(min<10)
            strmin = "0"+ String.valueOf(min);
        else
            strmin = String.valueOf(min);
        String str = strh+":"+strmin;
        return str;
    }

    public void start(){
        callback.onStart();
        playVideo = true;
    }

    public int getCurrentPosition(){
        return (int)(100*(player.getCurrentPosition()/(float)player.getDuration()));
    }
}

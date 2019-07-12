package com.lin.meet.video;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lin.meet.R;
import com.lin.meet.db_bean.Reply;
import com.lin.meet.db_bean.comment;
import com.lin.meet.ijk_media.media.IRenderView;
import com.lin.meet.ijk_media.media.IjkVideoView;
import com.lin.meet.override.EmojiAdapter;
import com.lin.meet.override.MyRelativeLayout;
import com.lin.meet.override.ScaleAnim;
import com.lin.meet.override.VideoLayout;
import com.lin.meet.personal.PersonalActivity;
import com.youngfeng.snake.annotations.EnableDragToClose;

import cn.bmob.v3.BmobUser;
import de.hdodenhof.circleimageview.CircleImageView;

@EnableDragToClose
public class VideoActivity extends AppCompatActivity implements View.OnClickListener, MyRelativeLayout.MyLayoutInterface,EmojiAdapter.EmojiCallback,VideoContract.View, CommentAdapter.VideoCallback, VideoLayout.Callback {
    private String url;
    private String title;
    private TextView nameView;
    private ImageView useEmoji;
    private boolean isUseEmoji = false;
    private GridView emojiView;
    private EmojiAdapter emojiAdapter;
    private int useEdit = 0;//1->defalut 2->last = now 3->other
    private boolean like = false;
    private comment currentComment;
    private boolean isStar = false;
    RecyclerView commentRecyclerView;
    private CommentAdapter adapter;
    private boolean showEdit = false;
    private boolean isLife = true;
    private ImageView thumb;
    private ImageView star;
    private TextView send;
    private CircleImageView header;
    private TextView nickName;
    private VideoContract.presenter presenter;
    private LinearLayoutManager manager;
    private LinearLayout replyLayout;
    private EditText replyEdit;
    private RelativeLayout replyNormal;
    private LinearLayout replyWrite;
    private int lastPosition = -1;
    private String tempReply = "";
    private String tempSon = "";
    private String uid = "";
    private VideoLayout videoView;
    private IjkVideoView player;
    private VideoViewManager videoManager;
    private View topView;
    private View bottomView;
    private SeekBar seekBar;
    private ImageView play,fill;
    private boolean playStatus = true;
    private TextView time;
    private ProgressBar progressBar;
    private View ProgressLayout;
    private Thread daemThread;
    private View loadingView;
    private ImageView back;
    private boolean isFill = false;
    private ImageView audio;
    private int current = -1;
    private View nullComment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        setContentView(R.layout.activity_video);
        init();
        initVideo();
        Intent intent = getIntent();
        uid = intent.getStringExtra("UID");
        presenter.initData(intent.getStringExtra("VIDEO"),uid);
        if(savedInstanceState!=null&&savedInstanceState.getInt("current")>0){
            current = savedInstanceState.getInt("current");
            url = savedInstanceState.getString("url");
            title = savedInstanceState.getString("title");
        }

        Configuration mConfiguration = this.getResources().getConfiguration(); //获取设置的配置信息
        int ori = mConfiguration.orientation; //获取屏幕方向
        if (ori == mConfiguration.ORIENTATION_LANDSCAPE) {
            initScreenStatus(true);
        } else if (ori == mConfiguration.ORIENTATION_PORTRAIT) {
            initScreenStatus(false);
        }
    }

    private void initVideo(){
        nameView = (TextView)findViewById(R.id.title);
        videoView = (VideoLayout) findViewById(R.id.videoView);
        topView = (View)findViewById(R.id.topLayout);
        bottomView = (View)findViewById(R.id.bottomLayout);
        player = (IjkVideoView)findViewById(R.id.player);
        seekBar = (SeekBar)findViewById(R.id.seekBar);
        play = (ImageView)findViewById(R.id.play);
        back = (ImageView)findViewById(R.id.back);
        fill = (ImageView)findViewById(R.id.fill);
        time = (TextView)findViewById(R.id.duration);
        loadingView = (View)findViewById(R.id.loadingLayout);
        ProgressLayout = (View)findViewById(R.id.audioLayout);
        progressBar = (ProgressBar)findViewById(R.id.progress);
        audio = (ImageView)findViewById(R.id.audio);
        player.setAspectRatio(IRenderView.AR_ASPECT_FIT_PARENT);
        seekBar.setClickable(false);
        seekBar.setEnabled(false);
        seekBar.setSelected(false);
        seekBar.setFocusable(false);
        player.setEnabled(false);
        videoManager = new VideoViewManager(player,this);
        progressBar.setProgress(videoManager.getAdjust());
        videoView.setVideoListener(this,topView,bottomView);
        play.setOnClickListener(this);
        fill.setOnClickListener(this);
        back.setOnClickListener(v->onBackPressed());
        videoManager.setVideoListener(this, new VideoViewManager.VideoCallback() {
            @Override
            public void onStart() {
                loadingView.setVisibility(View.GONE);
                seekBar.setClickable(true);
                seekBar.setEnabled(true);
                seekBar.setSelected(true);
                seekBar.setFocusable(true);
                play.setEnabled(true);
                play.setImageResource(R.mipmap.pause);
                playStatus = true;
            }

            @Override
            public void onError() {
                seekBar.setEnabled(false);
                play.setEnabled(false);
            }

            @Override
            public void onEnd() {
                seekBar.setProgress(100);
                play.setEnabled(true);
                play.setImageResource(R.mipmap.video);
                playStatus = false;
            }

        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                time.setText(videoManager.getTime());
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                setSeek();
            }
        });
        daemThread = new Thread(this::onDaemon);
        daemThread.setDaemon(true);
        daemThread.start();
    }

        @Override
    public void onBackPressed() {
        if(!isFill)
            super.onBackPressed();
        else
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
    @Override
    protected void onPause() {
        videoManager.pause();
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        videoManager.stop();
        super.onDestroy();
    }

    private void init(){
        presenter = new VideoPresenter(this);
        emojiView = (GridView)findViewById(R.id.emoji_View);
        useEmoji = (ImageView)findViewById(R.id.use_emoji);
        nullComment = (View)findViewById(R.id.nullComment);
        useEmoji.setOnClickListener(this);
        emojiAdapter = new EmojiAdapter(this);
        emojiView.setAdapter(emojiAdapter);
        closeEmoji();

        star = (ImageView)findViewById(R.id.star);
        thumb = (ImageView)findViewById(R.id.video_thumb);
        send = (TextView)findViewById(R.id.video_reply_text);
        header = (CircleImageView)findViewById(R.id.video_header);
        nickName = (TextView)findViewById(R.id.nickName);
        commentRecyclerView = (RecyclerView)findViewById(R.id.video_recyclerView);
        adapter = new CommentAdapter(this);
        manager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        commentRecyclerView.setAdapter(adapter);
        commentRecyclerView.setLayoutManager(manager);
        replyEdit = (EditText)findViewById(R.id.video_reply_edit);
        replyLayout = (LinearLayout)findViewById(R.id.video_reply_layout);
        replyNormal = (RelativeLayout)findViewById(R.id.video_reply_normal);
        replyWrite = (LinearLayout) findViewById(R.id.video_reply_write);
        replyLayout.setOnClickListener(this);
        replyEdit.setOnClickListener(this);
        send.setOnClickListener(this);
        thumb.setOnClickListener(this);
        star.setOnClickListener(this);
        header.setOnClickListener(v-> PersonalActivity.Companion.startOther(this,uid));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.video_reply_layout:
                useEdit = 1;
                showEdit(0);
                break;
            case R.id.use_emoji:
                emojiClick();
                break;
            case R.id.video_reply_edit:
                closeEmoji();
                break;
            case R.id.video_reply_text:
                if(useEdit==1)
                    presenter.senComment(replyEdit.getText().toString());
                else
                    presenter.onSendSonMessage(currentComment,replyEdit.getText().toString(),lastPosition);
                break;
            case R.id.video_thumb:
                if(isNoLogin())return;
                Log.d(TAG, "onClick: 测试 Like ");
                like();
                break;
            case R.id.star:
                if(isNoLogin())return;
                onClickStar();
                break;
            case R.id.play:
                playStatus = !playStatus;
                if(playStatus){
                    play.setImageResource(R.mipmap.pause);
                    videoManager.play();
                }else{
                    play.setImageResource(R.mipmap.video);
                    videoManager.pause();
                }
                break;
            case R.id.fill:
                if(isFill)
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                else
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    private void onClickStar(){
        isStar = !isStar;
        if(isStar){
            ScaleAnim.startAnim(star,R.mipmap.love2);
        }else {
            ScaleAnim.startAnim(star,R.mipmap.love);
        }
        presenter.onStar();
    }

    public void showEdit(int postion){
        if(useEdit==2){
            replyEdit.setText(tempSon);
            lastPosition = postion;
        }
        else if(useEdit==1){
            replyEdit.setText(tempReply);
        }
        else{
            replyEdit.setText("");
            lastPosition = postion;
        }
        replyEdit.setSelection(replyEdit.getText().toString().length());
        closeEmoji();
        replyEdit.requestFocus();
        emojiView.setVisibility(View.GONE);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(replyEdit,0);
        showEdit = true;
        replyWrite.setVisibility(View.VISIBLE);
        replyNormal.setVisibility(View.GONE);
    }

    private void hideEdit(){
        showEdit = false;
        hideInputMethod();
        replyWrite.setVisibility(View.GONE);
        replyNormal.setVisibility(View.VISIBLE);
        if(useEdit==2)
            tempSon = replyEdit.getText().toString();
        else if(useEdit==1)
            tempReply = replyEdit.getText().toString();
        useEdit = 0;
    }

    private static final String TAG = "测试";

    @Override
    public void touch(MotionEvent event) {
        if(event.getY()<replyWrite.getTop()){
            hideEdit();
        }
    }

    @Override
    public void getEmoji(String emoji) {
        int index = replyEdit.getSelectionStart();
        Editable editable = replyEdit.getText();
        editable.insert(index,emoji);
    }

    private void closeEmoji(){
        emojiView.setVisibility(View.GONE);
        isUseEmoji = false;
    }

    private void emojiClick(){
        if(!isUseEmoji){
            hideInputMethod();
            new Thread(this::openEmoji).start();
        }else {
            closeEmoji();
        }
    }

    private synchronized void openEmoji(){
        try {
            Thread.sleep(100);
            runOnUiThread(()->emojiView.setVisibility(View.VISIBLE));
            isUseEmoji = true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void hideInputMethod(){
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View v = getWindow().peekDecorView();
        if (null != v) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    @Override
    public void setNullCommentVisiable(boolean show) {
        if(nullComment!=null)
            nullComment.setVisibility(show?View.VISIBLE:View.GONE);
    }

    @Override
    public void playVideo(String uri,String title) {
        if(!uri.isEmpty()&&!uri.equals("@null")){
            this.url = uri;
            this.title = title;
        }
        if(url.isEmpty()||url.equals("@null")) return;
        videoManager.play(url);
        nameView.setText(title);
    }

    @Override
    public void setHeader(String uri) {
        Glide.with(this).load(uri).into(header);
    }

    @Override
    public void setNickName(String nickName) {
        this.nickName.setText(nickName);
    }

    @Override
    public void toast(String msg) {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public int insertComment(Reply reply, Boolean isRoll) {
        return adapter.insertComment(reply);
    }


    @Override
    public void likeResult(int resultCode, boolean like) {
        if(resultCode == 1){
            this.like = like;
            if(like)
                thumb.setImageResource(R.mipmap.like2);
            else
                thumb.setImageResource(R.mipmap.like);
        }
    }

    @Override
    public void onStarResult(int resultCode, boolean star) {
        if(resultCode == 1){
            isStar = star;
            if(star)
                this.star.setImageResource(R.mipmap.love2);
            else
                this.star.setImageResource(R.mipmap.love);
        }
    }


    private void like(){
        like = !like;
        if(like){
            ScaleAnim.startAnim(thumb,R.mipmap.like2);
        }else {
            ScaleAnim.startAnim(thumb,R.mipmap.like);
        }
        presenter.onClickLike(like);
    }

    private boolean isNoLogin(){
        return !BmobUser.isLogin();
    }

    @Override
    public void showSonEdit(comment comment, int position) {
        useEdit = 2;
        currentComment = comment;
        if(lastPosition!=position&&lastPosition!=-1){
            useEdit = 3;
            replyEdit.setText("");
        }
        showEdit(position);
    }

    @Override
    public void onCommentLike(String parentId, String parentUid, boolean like) {
        presenter.onClickCommentLike(parentId,parentUid,like);
    }

    @Override
    public void startComment(Intent intent) {
        startActivityForResult(intent,2000);
        overridePendingTransition(R.anim.bottom_in,R.anim.bottom_out);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==2000&&resultCode==2001){
            adapter.updateLikeStatus(data.getBooleanExtra("Like",false));
            adapter.updateCommentStatus(data.getIntExtra("Count",-1));
        }
    }

    @Override
    public void senMessageResult(int resultCode, Reply msg) {
        if(resultCode == 1){
            int position = adapter.insertComment(msg);
            moveToPosition(position);
            toast("发送成功");
            hideEdit();
            tempReply = "";
            replyEdit.setText("");
        }else if(resultCode == 0){
            toast("回复错误,请检查网络");
        }
    }

    public void moveToPosition(int position) {
        manager.scrollToPosition(position);
    }

    @Override
    public void sonSendResult(int resultCode,int position) {
        if(resultCode==1){
            adapter.addComment(position);
        }
        hideEdit();
        replyEdit.setText("");
        tempSon = "";
    }

    @Override
    public void showView() {
        topView.setVisibility(View.VISIBLE);
        bottomView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideView() {
        topView.setVisibility(View.GONE);
        bottomView.setVisibility(View.GONE);
    }

    @Override
    public void addAdjust() {
        videoManager.addAdjust();
        progressBar.setProgress(videoManager.getAdjust());
        audio.setImageResource(R.mipmap.audio);
    }

    @Override
    public void lowAdjust() {
        videoManager.lowAdjust();
        progressBar.setProgress(videoManager.getAdjust());
        if(progressBar.getProgress()==0)
            audio.setImageResource(R.mipmap.audio2);

    }

    @Override
    public void addSeek() {
        if(videoManager.isPlaying()){
            int x = seekBar.getProgress();
            x+=1;
            if(x>=100)x=100;
            time.setText(videoManager.getTime(x));
            seekBar.setProgress(x);
        }
    }

    @Override
    public void lowSeek() {
        if(videoManager.isPlaying()){
            int x = seekBar.getProgress();
            x-=1;
            if(x<=0)x=0;
            time.setText(videoManager.getTime(x));
            seekBar.setProgress(x);
        }
    }

    @Override
    public void setSeek() {
        if(videoManager.isPlaying()){
            int x = seekBar.getProgress();
            videoManager.seekTo(x);
        }
    }

    @Override
    public void onPauseVideo() {
        videoManager.pause();
        play.setImageResource(R.mipmap.video);
        playStatus = false;
    }

    @Override
    public void onStartVideo() {
        videoManager.play();
        play.setImageResource(R.mipmap.pause);
        playStatus = true;
    }

    @Override
    public void showProgress() {
        ProgressLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        ProgressLayout.setVisibility(View.GONE);
    }

    private void onDaemon(){
        while(isLife){
            try{
                Thread.sleep(100);
            }catch (InterruptedException e) {
                e.printStackTrace();
            }
            runOnUiThread(()->{
                if(!videoView.isSetSeek()&&player.isPlaying()){
                    seekBar.setProgress(videoManager.getPosition());
                    time.setText(videoManager.getTime());
                }
                if(videoView.isHideView()&&topView.getVisibility()==View.VISIBLE){
                    hideView();
                }
                if(videoView.isHideAudio()&&progressBar.getVisibility()==View.VISIBLE){
                    hideProgress();
                }
                if(videoManager.isPlaying()&&player.isPlaying()&&current!=-1){
                    videoManager.seekTo(current);
                    current = -1;
                }
            });
        }
    }

    @Override
    public void hideStateBar(){
        if(isFill)return;
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
    }

    @Override
    public void showStateBar(){
        if(isFill)return;
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
    }

    private void initScreenStatus(Boolean isFill){
        this.isFill = isFill;
        if(isFill){
            WindowManager.LayoutParams attrs = getWindow().getAttributes();
            attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(attrs);
        }else {
            showStateBar();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("current",videoManager.getCurrentPosition());
        outState.putString("url",url);
        outState.putString("title",title);
    }
}

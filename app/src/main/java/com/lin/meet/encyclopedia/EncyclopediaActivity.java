package com.lin.meet.encyclopedia;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.lin.meet.R;
import com.lin.meet.main.WebActivity;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class EncyclopediaActivity extends AppCompatActivity implements View.OnClickListener,EncyclopediaContract.View {
    private static final String TAG = "百科";
    private int x=0;
    private boolean useCloseAnimation = false;
    private WebView webView;
    private TextToSpeech texttospeech;
    private CardView cardView;
    private TextView text_0,text_1,text_2,text_3,text_4,text_5;
    private TextView title_c,title_e,title_baike,title_name;
    private ImageView horn_c,horn_e,back;
    private ImageView img,img_1,img_2;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout appBarLayout;
    private int img_h,appbar_h;
    private boolean ishideStatusBar = false,isRead=false,startRoll=false;
    private NestedScrollView scrollView;
    private TextView baidu;
    private RequestOptions requestOptions = new RequestOptions();
    private String datas[],name_chinese,name_english;
    private String myUrl;

    public static String[] getDatas(String img_path_1,String img_path_2,String img_path_3,
                                    String chinieseName,String englishName,String url){
        return new String[]{img_path_1,img_path_2,img_path_3,chinieseName,englishName,url};
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        Transition explode = TransitionInflater.from(this).inflateTransition(android.R.transition.explode);
        getWindow().setEnterTransition(explode);
        setContentView(R.layout.activity_encyclopedia);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        initView();
        initData();
    }

    public static void openEncyclopedia(Activity activity, String datas[]){
        Intent intent = new Intent(activity, EncyclopediaActivity.class);
        intent.putExtra("Datas",datas);
        activity.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle());
    }


    private void setImageTranslation(int len){
        img.setTranslationY(len);
    }

    private void initView(){
        baidu = (TextView)findViewById(R.id.baidu);
        text_0 = (TextView)findViewById(R.id.ency_content_0);
        text_1 = (TextView)findViewById(R.id.ency_content_1);
        text_2 = (TextView)findViewById(R.id.ency_content_2);
        text_3 = (TextView)findViewById(R.id.ency_content_3);
        text_4 = (TextView)findViewById(R.id.ency_content_4);
        text_5 = (TextView)findViewById(R.id.ency_content_5);
        cardView = (CardView)findViewById(R.id.ency_card);
        img = (ImageView)findViewById(R.id.ency_img);
        img_1 = (ImageView)findViewById(R.id.ency_ima_1);
        img_2 = (ImageView)findViewById(R.id.ency_ima_2);
        back = (ImageView)findViewById(R.id.ency_back);
        horn_c = (ImageView)findViewById(R.id.ency_horn_0);
        horn_e = (ImageView)findViewById(R.id.ency_horn_1);
        title_c = (TextView)findViewById(R.id.ency_title_0);
        title_e = (TextView)findViewById(R.id.ency_title_1);
        scrollView = (NestedScrollView)findViewById(R.id.sroll_view);
        title_baike = (TextView)findViewById(R.id.ency_title_baike);
        title_name = (TextView)findViewById(R.id.ency_title_name);
        appBarLayout = (AppBarLayout)findViewById(R.id.ency_appbar);
        img = (ImageView)findViewById(R.id.ency_img);
        appbar_h = (int)getResources().getDimension(R.dimen.ency_appbar);
        img_h = (int)getResources().getDimension(R.dimen.ency_head);

        baidu.setOnClickListener(this);
        back.setOnClickListener(this);
        horn_c.setOnClickListener(this);
        horn_e.setOnClickListener(this);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.BaseOnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                x = appbar_h*i/appbar_h;
                if(!startRoll)
                    setImageTranslation(x);
                float a =1f-Math.abs(1f *i/appbar_h);
                float b = 0.7f-Math.abs(0.7f *i/appbar_h);
                title_baike.setAlpha(b);
                title_name.setAlpha(a);
            }
        });
        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView nestedScrollView, int i, int i1, int i2, int i3) {
                if(i1==0&&startRoll)
                    startRoll=false;
                else if(!startRoll)
                    startRoll=true;
                setImageTranslation(x-i1);
                if(i1>i3&&!ishideStatusBar){
                    setStatusBar(true);
                }else if(i3>i1&&ishideStatusBar){
                    setStatusBar(false);
                }
            }
        });
        requestOptions.placeholder(R.mipmap.load);
        requestOptions.error(R.mipmap.error);
    }

    private void initData(){
        Intent intent = getIntent();
        datas = intent.getStringArrayExtra("Datas");
        setImageHead(datas[0]);
        setImage_1(datas[1]);
        setImage_2(datas[2]);
        setTitleName(datas[3]);
        setChineseTitle(datas[3]);
        setEnglishTitle(datas[4]);
        myUrl=datas[5];
    }

    private synchronized void setStatusBar(boolean isHide){
        final Timer t=new Timer();
        ishideStatusBar=isHide;
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(ishideStatusBar){
                            WindowManager.LayoutParams attrs = getWindow().getAttributes();
                            attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                            getWindow().setAttributes(attrs);
                            back.setVisibility(View.GONE);
                        }
                        else{
                            WindowManager.LayoutParams attrs = getWindow().getAttributes();
                            attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
                            getWindow().setAttributes(attrs);
                            back.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        },300);
    }

    private void initTextToSpeech(){
        texttospeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                isRead=true;
                int result = texttospeech.setLanguage(Locale.CHINA);
                if(result != TextToSpeech.LANG_COUNTRY_AVAILABLE
                        && result != TextToSpeech.LANG_AVAILABLE){
                    isRead=false;
                }
            }
        }) ;
    }

    @Override
    public void speackText(TextView text, final ImageView horn){
        if(!isRead)
            return;
        texttospeech.speak(text.getText().toString(),TextToSpeech.QUEUE_FLUSH,null,"UniqueID");
        texttospeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                openHorn(horn);
            }

            @Override
            public void onDone(String utteranceId) {
                openHorn(null);
            }

            @Override
            public void onError(String utteranceId) {
                openHorn(null);
            }
        });
    }

    @Override
    public void setTitleName(String str) {
        title_name.setText(str);
    }

    @Override
    public void setChineseTitle(String str) {
        title_c.setText(str);
    }

    @Override
    public void setEnglishTitle(String str) {
        title_e.setText(str);
    }

    @Override
    public void setContent_0(String str) {
        text_0.setVisibility(View.VISIBLE);
        text_0.setText(str);
    }

    @Override
    public void setContent_1(String str) {
        text_1.setVisibility(View.VISIBLE);
        text_1.setText(str);
    }

    @Override
    public void setContent_2(String str) {
        text_2.setVisibility(View.VISIBLE);
        text_2.setText(str);
    }

    @Override
    public void setContent_3(String str) {
        text_3.setVisibility(View.VISIBLE);
        text_3.setText(str);
    }

    @Override
    public void setContent_4(String str) {
        text_4.setVisibility(View.VISIBLE);
        text_4.setText(str);
    }

    @Override
    public void setContent_5(String str) {
        text_5.setVisibility(View.VISIBLE);
        text_5.setText(str);
    }

    @Override
    public void setImageHead(String path) {
        Glide.with(this).asDrawable().load(path).apply(requestOptions).into(img);
    }

    @Override
    public void setImage_1(String path) {
        Glide.with(this).asDrawable().load(path).apply(requestOptions).into(img_1);
    }

    @Override
    public void setImage_2(String path) {
        Glide.with(this).asDrawable().load(path).apply(requestOptions).into(img_2);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ency_back:
                finish();
                break;
            case R.id.ency_horn_0:
                speackText(title_c,horn_c);
                break;
            case R.id.ency_horn_1:
                speackText(title_e,horn_e);
                break;
            case R.id.baidu:
                Intent intent=new Intent(this, WebActivity.class);
                intent.putExtra("url",myUrl);
                startActivity(intent);
        }
    }

    @Override
    public void openHorn(ImageView horn){
        if(horn!=horn_c)
            horn_c.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.horn));
        else
            horn_c.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.horn_r));
        if(horn!=horn_e)
            horn_e.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.horn));
        else
            horn_e.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.horn_r));
    }

    @Override
    protected void onStop() {
        super.onStop();
        texttospeech.stop();
        texttospeech.shutdown();
    }

    @Override
    protected void onResume() {
        initTextToSpeech();
        super.onResume();
    }

    @Override
    public void finish(){
        super.finish();
    }
}

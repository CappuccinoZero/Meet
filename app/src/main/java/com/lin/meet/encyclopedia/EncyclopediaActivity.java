package com.lin.meet.encyclopedia;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.TransitionSet;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hw.ycshareelement.transition.ChangeTextTransition;
import com.lin.meet.R;
import com.lin.meet.jsoup.AnimalBaike;
import com.lin.meet.jsoup.BaikeBean;
import com.lin.meet.main.WebActivity;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class EncyclopediaActivity extends AppCompatActivity implements View.OnClickListener,EncyclopediaContract.View {
    private int x=0;
    private boolean useCloseAnimation = false;
    private WebView webView;
    private TextToSpeech texttospeech;
    private CardView cardView;
    private TextView title_c,title_e,title_baike,title_name;
    private ImageView horn_c,horn_e,back;
    private ImageView img;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout appBarLayout;
    private int img_h,appbar_h;
    private boolean ishideStatusBar = false,isRead=false,startRoll=false;
    private NestedScrollView scrollView;
    private TextView baidu;
    private RequestOptions requestOptions = new RequestOptions();
    private String datas[],name_chinese,name_english;
    private String myUrl;
    private EncyclopediaContract.Presenter presenter;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == AnimalBaike.BAIKE_WHAT){
                BaikeBean bean = (BaikeBean) msg.getData().getSerializable("Baike");
                presenter.initBaike(bean);
            }
        }
    };

    private TextView summary;
    private View baike1,baike2,baike3,baike4,baike5,baike6,baike7,baike8,baike9,baike10,baike11,baike12,baike13,baike14,baike15,baike16;

    public static String[] getDatas(String img_path_1,String img_path_2,String img_path_3,
                                    String chinieseName,String englishName,String url){
        return new String[]{img_path_1,img_path_2,img_path_3,chinieseName,englishName,url};
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encyclopedia);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        initView();
        initData();
        initTransitionAnimation();
    }

    private void initTransitionAnimation(){
        ViewCompat.setTransitionName(img,"baike_img");
        ViewCompat.setTransitionName(title_c,"baike_cnName");
        ViewCompat.setTransitionName(title_e,"baike_enName");
        ViewCompat.setTransitionName(title_name,"cnTitle");

        TransitionSet set = new TransitionSet();
        set.addTransition(new ChangeBounds());
        set.addTransition(new ChangeImageTransform());
        set.addTransition(new ChangeTransform());
        set.addTransition(new ChangeTextTransition());
        set.addTarget(img);
        set.addTarget(title_c);
        set.addTarget(title_e);
        set.addTarget(title_name);
        getWindow().setSharedElementEnterTransition(set);
        getWindow().setSharedElementExitTransition(set);
    }

    public static void openEncyclopedia(Activity activity, String datas[], String newurl, String type, ActivityOptionsCompat compat){
        Intent intent = new Intent(activity, EncyclopediaActivity.class);
        intent.putExtra("Baike",true);
        intent.putExtra("cnName",datas[3]);
        intent.putExtra("enName",datas[4]);
        intent.putExtra("imageUri",datas[0]);
        intent.putExtra("url",newurl);
        intent.putExtra("type",type);
        ActivityCompat.startActivity(activity,intent,compat.toBundle());
    }


    private void setImageTranslation(int len){
        img.setTranslationY(len);
    }

    private void initView(){
        baidu = (TextView)findViewById(R.id.baidu);
        cardView = (CardView)findViewById(R.id.ency_card);
        img = (ImageView)findViewById(R.id.ency_img);
        back = (ImageView)findViewById(R.id.ency_back);
        horn_c = (ImageView)findViewById(R.id.ency_horn_0);
        horn_e = (ImageView)findViewById(R.id.ency_horn_1);
        title_c = (TextView)findViewById(R.id.ency_title_0);
        title_e = (TextView)findViewById(R.id.ency_title_1);
        summary = (TextView)findViewById(R.id.baike_summary);
        scrollView = (NestedScrollView)findViewById(R.id.sroll_view);
        title_baike = (TextView)findViewById(R.id.ency_title_baike);
        title_name = (TextView)findViewById(R.id.ency_title_name);
        appBarLayout = (AppBarLayout)findViewById(R.id.ency_appbar);
        presenter =new EncyPresenter(this);
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

        baike1 = (View)findViewById(R.id.baike1);
        baike2 = (View)findViewById(R.id.baike2);
        baike3 = (View)findViewById(R.id.baike3);
        baike4 = (View)findViewById(R.id.baike4);
        baike5 = (View)findViewById(R.id.baike5);
        baike6 = (View)findViewById(R.id.baike6);
        baike7 = (View)findViewById(R.id.baike7);
        baike8 = (View)findViewById(R.id.baike8);
        baike9 = (View)findViewById(R.id.baike9);
        baike10 = (View)findViewById(R.id.baike10);
        baike11 = (View)findViewById(R.id.baike11);
        baike12 = (View)findViewById(R.id.baike12);
        baike13 = (View)findViewById(R.id.baike13);
        baike14 = (View)findViewById(R.id.baike14);
        baike15 = (View)findViewById(R.id.baike15);
        baike16 = (View)findViewById(R.id.baike16);
    }

    private void initData(){
        Intent intent = getIntent();
        if(intent.getBooleanExtra("Baike",false)){
            setImageHead(intent.getStringExtra("imageUri"));
            setTitleName(intent.getStringExtra("cnName"));
            setChineseTitle(intent.getStringExtra("cnName"));
            setEnglishTitle(intent.getStringExtra("enName"));
            String type = intent.getStringExtra("type");
            myUrl = intent.getStringExtra("url");
            if(type==null) type = "@null";
            if(type.equals("Chong_Wu")){
                AnimalBaike.getChongWuBean(handler,myUrl);
            }else{
                AnimalBaike.getBaikeBean(handler,myUrl);
            }
        }
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
    public void setImageHead(String path) {
        Glide.with(this).asDrawable().load(path).apply(requestOptions).into(img);
    }

    @Override
    public void setSummary(String str) {
        summary.setText(str);
    }

    @Override
    public void setBaike(int flag, String title, String content, String image) {
        View view = null;
        switch (flag){
            case 1:
                view = baike1;
                break;
            case 2:
                view = baike2;
                break;
            case 3:
                view = baike3;
                break;
            case 4:
                view = baike4;
                break;
            case 5:
                view = baike5;
                break;
            case 6:
                view = baike6;
                break;
            case 7:
                view = baike7;
                break;
            case 8:
                view = baike8;
                break;
            case 9:
                view = baike9;
                break;
            case 10:
                view = baike10;
                break;
            case 11:
                view = baike1;
                break;
            case 12:
                view = baike12;
                break;
            case 13:
                view = baike13;
                break;
            case 14:
                view = baike14;
                break;
            case 15:
                view = baike15;
                break;
            case 16:
                view = baike16;
                break;
        }
        if(title!=null&&content!=null)
            initBaike(title,content,image,view);
        else
            initBaike(image,view);
    }

    @Override
    public void setImage(int flag, String img) {
        setBaike(flag,null,null,img);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ency_back:
                onBackPressed();
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

    private void initBaike(String title,String content,String img,View view){
        view.setVisibility(View.VISIBLE);
        TextView baikeTitle = (TextView)view.findViewById(R.id.baike_title);
        TextView baikeContent = (TextView)view.findViewById(R.id.baike_content);
        baikeTitle.setText(title);
        baikeContent.setText(content);
        if(img!=null){
            CardView imgLayout = (CardView)view.findViewById(R.id.baike_image_layout);
            imgLayout.setVisibility(View.VISIBLE);
            ImageView imgView = (ImageView)view.findViewById(R.id.baike_image);
            Glide.with(this).load(img).into(imgView);
        }
    }

    private void initBaike(String img,View view){
        view.setVisibility(View.VISIBLE);
        TextView baikeTitle = (TextView)view.findViewById(R.id.baike_title);
        TextView baikeContent = (TextView)view.findViewById(R.id.baike_content);
        baikeTitle.setVisibility(View.GONE);
        baikeContent.setVisibility(View.GONE);
        if(img!=null){
            CardView imgLayout = (CardView)view.findViewById(R.id.baike_image_layout);
            imgLayout.setVisibility(View.VISIBLE);
            ImageView imgView = (ImageView)view.findViewById(R.id.baike_image);
            Glide.with(this).load(img).into(imgView);
        }
    }
}

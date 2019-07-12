package com.lin.meet.IntroductionPage;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.lin.meet.R;
import com.lin.meet.my_util.MyUtil;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.PictureFileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;
import comulez.github.droplibrary.DropIndicator;
import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;


public class IntroductionActivity extends AppCompatActivity implements View.OnClickListener ,IntorductionContract.View{
    private static final String TAG = "简介活动";
    public static final int INIT_OPEN_PHOTO = 10;
    public static final int INTORDUCTION_OPEN_PHOTO = 11;
    private boolean useCloseAnimation = false;
    private ViewPageAdapter adapter;
    private List<View> list = new ArrayList<>();
    private View view_1,view_2,view_3;
    private ImageView imageView;
    private ImageView imageView_1,imageView_2,imageView_3;
    private TextView animal_1,animal_2,animal_3;
    private TextView content_1,content_2,content_3;
    private TextView probability_1,probability_2,probability_3;
    private TextView more_1,more_2,more_3;
    private ViewPager pager;
    private TabLayout tab;
    private ImageView back,photo;
    private String image_path;
    private int id1,id2,id3;
    private String type1="@null",type2="@null",type3="@null";
    private String uri1 = "@null",uri2 = "@null",uri3 = "@null";
    private long timeId = 0;
    private IntorductionContract.Presenter presenter;
    private RelativeLayout result_1,result_2,result_3;
    private TextView text11,text12,text21,text22,text31,text32;
    private boolean fixResult = false;
    private DropIndicator indicator;
    private RequestOptions requestOptions = new RequestOptions();
    private AlertDialog alertDialog;
    private LocationClient locationClient;
    private String name,maybe;
    private int okId = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        Transition explode = TransitionInflater.from(this).inflateTransition(android.R.transition.explode);
        getWindow().setEnterTransition(explode);
        setContentView(R.layout.activity_introduction);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        init();
        initLocation();
        Intent intent = getIntent();
        int page = intent.getIntExtra("page",0);
        if(intent.getBooleanExtra("usePhoto",true)){//来自相册
            openPhoto(INIT_OPEN_PHOTO);
            useCloseAnimation = true;
        }else {
            String path = intent.getStringExtra("imagePath");
            image_path = path;
            setImageView(path);
            if(intent.getBooleanExtra("fromHistory",false)){//来自历史页
                presenter.doIdentification(path,path);
                ImageView tempImg = intent.getParcelableExtra("image");
            }else{//来自拍摄
                timeId = intent.getLongExtra("time",0);
                useCloseAnimation = true;
                int id[]=intent.getIntArrayExtra("id");
                float maybe[]=intent.getFloatArrayExtra("maybe");
                presenter.doIdentification(id,maybe,page);
            }
        }
    }

    private double latitude,longitude;
    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        option.setCoorType("bd09ll");
        option.setScanSpan(1000);
        locationClient = new LocationClient(this);
        locationClient.setLocOption(option);
        locationClient.registerLocationListener(new MylocationListener());
        locationClient.start();
    }
    public class MylocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null){
                return;
            }
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    }

    @Override
    public double getMapX() {
        return latitude;
    }

    @Override
    public double getMapY() {
        return longitude;
    }

    @Override
    public void saveError(String e) {
        Toast.makeText(this,e,Toast.LENGTH_SHORT).show();
        if(alertDialog!=null){
            alertDialog.dismiss();
        }
    }

    private void FlagOk(){
        if(okId==1){
            cancelResultView(result_1,text12);
            text12.setText("标记成功");
        }else if(okId==2){
            cancelResultView(result_2,text22);
            text22.setText("标记成功");
        }else if(okId==3){
            cancelResultView(result_3,text32);
            text32.setText("标记成功");
        }
    }

    @Override
    protected void onDestroy() {
        PictureFileUtils.deleteExternalCacheDirFile(this);
        super.onDestroy();
    }

    private void init(){
        requestOptions.placeholder(R.mipmap.load);
        requestOptions.error(R.mipmap.error);
        back = (ImageView)findViewById(R.id.introduce_back);
        photo = (ImageView)findViewById(R.id.introduce_photo);
        pager = (ViewPager)findViewById(R.id.viewpage);
        indicator = (DropIndicator)findViewById(R.id.circleIndicator);
        presenter = new IntroductionPresenter(this,this);
        view_1 = getLayoutInflater().inflate(R.layout.intorduce_view, null);
        imageView_1 = (ImageView) view_1.findViewById(R.id.introduction_image);
        animal_1 =(TextView) view_1.findViewById(R.id.introduction_text_animal);
        content_1=(TextView) view_1.findViewById(R.id.introduction_content);
        probability_1=(TextView) view_1.findViewById(R.id.introduction_text_probability);
        more_1=(TextView) view_1.findViewById(R.id.introduction_text_baike);
        more_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pair<View,String> pair1 = new Pair<>(animal_1, ViewCompat.getTransitionName(animal_1));
                Pair<View,String> pair2 = new Pair<>(imageView_1, ViewCompat.getTransitionName(imageView_1));
                Pair<View,String> pair3 = new Pair<>(animal_1, "cnTitle");
                ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(IntroductionActivity.this,pair1,pair2,pair3);
                presenter.intoEncy(IntroductionActivity.this,id1,uri1,type1,compat);
            }
        });
        text11 = (TextView)view_1.findViewById(R.id.introduction_text_left1);
        text12 = (TextView)view_1.findViewById(R.id.introduction_text_left2);
        result_1=(RelativeLayout)view_1.findViewById(R.id.introduction_result);
        result_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!fixResult){
                    Toast.makeText(IntroductionActivity.this,"确认成功",Toast.LENGTH_SHORT).show();
                    fixResults(text11,text12,result_1);
                    cancelResultView(result_2,text21);
                    cancelResultView(result_3,text31);
                    presenter.updateResult(timeId,animal_1.getText().toString());
                }else{
                    name = animal_1.getText().toString();
                    maybe = probability_1.getText().toString();
                    showMapDialog(0);
                    okId = 1;
                }
            }
        });

        view_2 = getLayoutInflater().inflate(R.layout.intorduce_view, null);
        imageView_2 = (ImageView) view_2.findViewById(R.id.introduction_image);
        animal_2 =(TextView) view_2.findViewById(R.id.introduction_text_animal);
        content_2=(TextView) view_2.findViewById(R.id.introduction_content);
        probability_2=(TextView) view_2.findViewById(R.id.introduction_text_probability);
        more_2=(TextView) view_2.findViewById(R.id.introduction_text_baike);
        more_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pair<View,String> pair1 = new Pair<>(animal_2, ViewCompat.getTransitionName(animal_2));
                Pair<View,String> pair2 = new Pair<>(imageView_2, ViewCompat.getTransitionName(imageView_2));
                Pair<View,String> pair3 = new Pair<>(animal_2, "cnTitle");
                ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(IntroductionActivity.this,pair1,pair2,pair3);
                presenter.intoEncy(IntroductionActivity.this,id2,uri2,type2,compat);
            }
        });
        text21 = (TextView)view_2.findViewById(R.id.introduction_text_left1);
        text22 = (TextView)view_2.findViewById(R.id.introduction_text_left2);
        result_2=(RelativeLayout)view_2.findViewById(R.id.introduction_result);
        result_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!fixResult){
                    Toast.makeText(IntroductionActivity.this,"确认成功",Toast.LENGTH_SHORT).show();
                    fixResults(text21,text22,result_2);
                    cancelResultView(result_1,text11);
                    cancelResultView(result_3,text31);
                    presenter.updateResult(timeId,animal_2.getText().toString());
                }
                else{
                    name = animal_2.getText().toString();
                    maybe = probability_2.getText().toString();
                    showMapDialog(0);
                    okId = 2;
                }
            }
        });

        view_3 = getLayoutInflater().inflate(R.layout.intorduce_view, null);
        imageView_3 = (ImageView) view_3.findViewById(R.id.introduction_image);
        animal_3 =(TextView) view_3.findViewById(R.id.introduction_text_animal);
        content_3=(TextView) view_3.findViewById(R.id.introduction_content);
        probability_3=(TextView) view_3.findViewById(R.id.introduction_text_probability);
        more_3=(TextView) view_3.findViewById(R.id.introduction_text_baike);
        more_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pair<View,String> pair1 = new Pair<>(animal_3, ViewCompat.getTransitionName(animal_3));
                Pair<View,String> pair2 = new Pair<>(imageView_3, ViewCompat.getTransitionName(imageView_3));
                Pair<View,String> pair3 = new Pair<>(animal_3, "cnTitle");
                ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(IntroductionActivity.this,pair1,pair2,pair3);
                presenter.intoEncy(IntroductionActivity.this,id3,uri3,type3,compat);
            }
        });
        text31 = (TextView)view_3.findViewById(R.id.introduction_text_left1);
        text32 = (TextView)view_3.findViewById(R.id.introduction_text_left2);
        result_3=(RelativeLayout)view_3.findViewById(R.id.introduction_result);
        result_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!fixResult){
                    Toast.makeText(IntroductionActivity.this,"确认成功",Toast.LENGTH_SHORT).show();
                    fixResults(text31,text32,result_3);
                    cancelResultView(result_1,text11);
                    cancelResultView(result_2,text21);
                    presenter.updateResult(timeId,animal_3.getText().toString());
                }else{
                    name = animal_3.getText().toString();
                    maybe = probability_3.getText().toString();
                    showMapDialog(0);
                    okId = 3;
                }
            }
        });

        imageView = (ImageView) findViewById(R.id.introduction_default);
        list.add(view_1);
        list.add(view_2);
        list.add(view_3);
        back.setOnClickListener(this);
        photo.setOnClickListener(this);
        ViewPageAdapter adapter = new ViewPageAdapter(list);
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(4);
        pager.setPageTransformer(true,new DepthPageTransformer());
        indicator.setViewPager(pager);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.introduce_back:
                finish();
                break;
            case R.id.introduce_photo:
                openPhoto(INTORDUCTION_OPEN_PHOTO);
                break;
                default:
                    break;
        }
    }

    @Override
    public void setImageView(Uri uri) {
        RequestOptions requestOptions=new RequestOptions();
        requestOptions.placeholder(R.mipmap.load);
        requestOptions.error(R.mipmap.error);
        Glide.with(this).load(uri).apply(requestOptions).thumbnail(0.05f).into(imageView);
    }

    @Override
    public void setImageView(String path) {
        RequestOptions requestOptions=new RequestOptions();
        requestOptions.placeholder(R.mipmap.load);
        requestOptions.error(R.mipmap.error);
        Glide.with(this).load(path).apply(requestOptions).thumbnail(0.05f).into(imageView);
    }

    @Override
    public void setImageView_1(String path) {
        Glide.with(this).asDrawable().load(path).apply(requestOptions).into(imageView_1);
    }

    @Override
    public void setImageView_2(String path) {
        Glide.with(this).asDrawable().load(path).apply(requestOptions).into(imageView_2);
    }

    @Override
    public void setImageView_3(String path) {
        Glide.with(this).asDrawable().load(path).apply(requestOptions).into(imageView_3);
    }

    @Override
    public void setAnimal_1(String text) {
        animal_1.setText(text);
    }

    @Override
    public void setAnimal_2(String text) {
        animal_2.setText(text);
    }

    @Override
    public void setAnimal_3(String text) {
        animal_3.setText(text);
    }

    @Override
    public void setProbability_1(String text) {
        probability_1.setText(text);
    }

    @Override
    public void setProbability_2(String text) {
        probability_2.setText(text);
    }

    @Override
    public void setProbability_3(String text) {
        probability_3.setText(text);
    }

    @Override
    public void setContent_1(String text) {
        content_1.setText(text);
    }

    @Override
    public void setContent_2(String text) {
        content_2.setText(text);
    }

    @Override
    public void setContent_3(String text) {
        content_3.setText(text);
    }

    @Override
    public void setId(int a, int b, int c) {
        id1=a;
        id2=b;
        id3=c;
    }

    @Override
    public void openPhoto(int requestCode) {
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
                .selectionMode(PictureConfig.SINGLE)
                .enableCrop(true)
                .withAspectRatio(1,1)
                .isGif(false)
                .openClickSound(true)
                .isDragFrame(true)
                .isCamera(false)
                .compress(true)
                .minimumCompressSize(300)
                .forResult(requestCode);
    }

    @Override
    public void showMapDialog(int id){
        View mView = getLayoutInflater().inflate(R.layout.map_dialog_view,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if(id==0)
            (mView.findViewById(R.id.map_dialog_1)).setVisibility(View.VISIBLE);
        else if(id==1)
            (mView.findViewById(R.id.map_dialog_2)).setVisibility(View.VISIBLE);
        else if(id==2){
            (mView.findViewById(R.id.map_dialog_3)).setVisibility(View.VISIBLE);
            FlagOk();
            alertDialog.dismiss();
        }
        if(id==0){
            builder.setNegativeButton("取消",null);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(!BmobUser.isLogin()){
                        Toast.makeText(IntroductionActivity.this,"需要登录",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    showMapDialog(1);
                }
            });
        }else if(id==1){
            File file = new File(image_path);
            long size = 0;
            try {
                size = MyUtil.getFileSize(file);
                size/=1024;
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(size<=300){
                presenter.onFlagMap(image_path,name,maybe);
            }else{
                Luban.with(this)
                        .load(image_path)
                        .ignoreBy(300)
                        .setTargetDir(Environment.getExternalStorageDirectory().getAbsoluteFile().toString() + File.separator + "Mybitmap"+File.separator)
                        .filter(new CompressionPredicate() {
                            @Override
                            public boolean apply(String path) {
                                return !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif"));
                            }
                        })
                        .setCompressListener(new OnCompressListener() {
                            @Override
                            public void onStart() {
                            }
                            @Override
                            public void onSuccess(File file) {
                                presenter.onFlagMap(file.getAbsolutePath(),name,maybe);
                            }
                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(IntroductionActivity.this,"图片压缩失败",Toast.LENGTH_SHORT).show();
                            }
                        }).launch();
            }
        }else{
            builder.setPositiveButton("确定",null);
        }
        builder.setView(mView);
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void setUri1(String uri,String type) {
        uri1 = uri;
        type1 = type;
    }

    @Override
    public void setUri2(String uri,String type) {
        uri2 = uri;
        type2 = type;
    }

    @Override
    public void setUri3(String uri,String type) {
        uri3 = uri;
        type3 = type;
    }

    @Override
    public void setContent(String content, int position) {
        if(content==null)
            return;
        if(content.isEmpty())
            return;
        if(position==1){
            ((TextView)view_1.findViewById(R.id.introduction_content)).setText(content);
        }else if(position==2){
            ((TextView)view_2.findViewById(R.id.introduction_content)).setText(content);
        }else if(position==3){
            ((TextView)view_3.findViewById(R.id.introduction_content)).setText(content);
        }
    }

    @Override
    public void updateFromNetwork() {
        presenter.updateBaike(id1,1);
        presenter.updateBaike(id2,2);
        presenter.updateBaike(id3,3);
    }

    @Override
    public void swap(int flag) {
        int temp = id1;
        String tempStr;
        if(flag==1) {
            id1 = id2;
            id2 = temp;
            tempStr = uri1;
            uri1 = uri2;
            uri2 = tempStr;
            tempStr = type1;
            type1 = type2;
            type2 = tempStr;
        }
        else {
            id1 = id3;
            id3 = temp;
            tempStr = uri1;
            uri1 = uri3;
            uri3 = tempStr;
            tempStr = type1;
            type1 = type3;
            type3 = tempStr;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LocalMedia media = null;
        switch (requestCode){
            case INTORDUCTION_OPEN_PHOTO:
                if(data==null){
                    return;
                }
                initAllButton();
                media = PictureSelector.obtainMultipleResult(data).get(0);
                if(media!=null&&media.isCompressed()){
                    image_path = media.getCompressPath();
                }
                else if(media!=null&&media.isCut()){
                    image_path = media.getCutPath();
                }else if(media!=null){
                    image_path = media.getPath();
                }else return;
                timeId=presenter.doIdentification(image_path,media.getPath());
                break;
            case INIT_OPEN_PHOTO:
                if(data==null){
                    finish();
                    return;
                }
                media = PictureSelector.obtainMultipleResult(data).get(0);
                if(media!=null&&media.isCompressed()){
                    image_path = media.getCompressPath();
                }
                else if(media!=null&&media.isCut()){
                    image_path = media.getCutPath();
                }else if(media!=null){
                    image_path = media.getPath();
                }else return;
                setImageView(image_path);
                timeId=presenter.doIdentification(image_path,media.getPath());
                break;
        }
    }

    @Override
    public void finish(){
        super.finish();
        if(useCloseAnimation)
            overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
    }

    private void cancelResultView(RelativeLayout result,TextView text){
        result.setBackground(getDrawable(R.drawable.bg_ripple_rrect_frame_3));
        result.setClickable(false);
        text.setTextColor(getResources().getColor(R.color.text_back_0));
    }

    private void initAllButton(){
        initButton(result_1,text11,text12);
        initButton(result_2,text21,text22);
        initButton(result_3,text31,text32);
        fixResult = false;
    }

    private void initButton(RelativeLayout result,TextView text1,TextView text2){
        result.setBackground(getDrawable(R.drawable.bg_ripple_rrect_frame_2));
        result.setClickable(true);
        text1.setTextColor(getResources().getColor(R.color.pink_A700));
        text2.setTextColor(getResources().getColor(R.color.pink_A700));
        text1.setTranslationY(0);
        text2.setTranslationY(0);
        text2.setVisibility(View.GONE);
    }

    private void fixResults(TextView text1,TextView text2,RelativeLayout button){
        text2.setVisibility(View.VISIBLE);
        fixResult = true;
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(text1,"translationY",0f,button.getWidth());
        animator1.setDuration(300);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(text2,"translationY",-button.getWidth(),0);
        animator2.setDuration(300);
        AnimatorSet set = new AnimatorSet();
        set.play(animator1).with(animator2);
        set.start();
    }



    public static class DepthPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.75f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 0) { // [-1,0]
                // Use the default_image slide transition when moving to the left page
                view.setAlpha(1);
                view.setTranslationX(0);
                view.setScaleX(1);
                view.setScaleY(1);

            } else if (position <= 1) { // (0,1]
                // Fade the page out.
                view.setAlpha(1 - position);

                // Counteract the default_image slide transition
                view.setTranslationX(pageWidth * -position);

                // Scale the page down (between MIN_SCALE and 1)
                float scaleFactor = MIN_SCALE
                        + (1 - MIN_SCALE) * (1 - Math.abs(position));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }
}

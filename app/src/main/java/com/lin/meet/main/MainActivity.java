package com.lin.meet.main;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.bottomnavigation.LabelVisibilityMode;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.lin.meet.Know.SendKnowActivity;
import com.lin.meet.R;
import com.lin.meet.bean.User;
import com.lin.meet.camera_demo.CameraActivity;
import com.lin.meet.login.StartActivity;
import com.lin.meet.main.fragment.Book.Book;
import com.lin.meet.main.fragment.Find.Find;
import com.lin.meet.main.fragment.Home.Home;
import com.lin.meet.main.fragment.Home.RecommendFragment;
import com.lin.meet.personal.PersonalActivity;
import com.lin.meet.picture_observer.SendPitureActivity;
import com.lin.meet.topic.SendTopic;
import com.lin.meet.video.SendVideo;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,MainConstract.View,MainConstract.MainDrawerCallback {
    public static String savePath = Environment.getExternalStorageDirectory().getAbsoluteFile()+ File.separator+"Mybitmap"+File.separator+"Cache"+File.separator;
    private BottomNavigationView bv;
    private NavigationView nv;
    private Fragment fragments[];
    private FrameLayout animator_layout;
    private int lastShow;
    private DrawerLayout drawer;
    private CircleImageView header;
    private TextView name;
    private FloatingActionButton actionButton;
    private User user;
    private RequestOptions options;
    private MainConstract.Presenter presenter;
    private RelativeLayout headLayout;
    private ImageView headBackground;
    private TextView exit;
    private View roundView;
    private RelativeLayout itemLayout;
    private Book book ;
    private Find find ;
    private Home home ;
    private RecommendFragment recod;
    private View formView;
    private boolean drawIsOpen = false;
    private boolean showStatusBar = true;
    private FloatingActionButton sender1,sender2,sender3,sender4;
    private TextView text1,text2,text3,text4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);
        View decorView = getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        decorView.setSystemUiVisibility(option);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_main);
        initView();
        initFragment();
        initLoadUserView();
    }

    private void initView(){
        presenter = new MainPresenter(this);
        request_permissions();
        formView = (View)findViewById(R.id.formView);
        sender1 = (FloatingActionButton)formView.findViewById(R.id.topicSender);
        sender2 = (FloatingActionButton)formView.findViewById(R.id.knowSender);
        sender3 = (FloatingActionButton)formView.findViewById(R.id.videoSender);
        sender4 = (FloatingActionButton)formView.findViewById(R.id.pictureSender);
        text1 = (TextView)formView.findViewById(R.id.topicText);
        text2 = (TextView)formView.findViewById(R.id.knowText);
        text3 = (TextView)formView.findViewById(R.id.videoText);
        text4 = (TextView)formView.findViewById(R.id.pictureText);
        animator_layout = (FrameLayout)findViewById(R.id.animator_layout);
        actionButton = (FloatingActionButton)findViewById(R.id.open_camera_activity);
        actionButton.setOnClickListener(this);
        options = new RequestOptions();
        options.error(R.color.bank_FF6C6C6C);
        bv = (BottomNavigationView)findViewById(R.id.main_bnv);
        bv.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
        bv.setItemIconTintList(null);
        bv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                startFabAnimation(menuItem.getItemId());
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                switch (menuItem.getItemId()){

                    case R.id.item_home:
                        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                        if(lastShow==0){
                            recod.scrollAndRefresh();
                            return true;
                        }
                        switchFragment(0);
                        break;
                    case R.id.item_book:
                        if(lastShow==1){
                            book.scrollAndRefresh();
                            return true;
                        }
                        switchFragment(1);
                        break;
                    case R.id.item_find:
                        if(lastShow==2){
                            find.refreshMap();
                            return true;
                        }
                        switchFragment(2);
                        break;
                    case R.id.item_know:
                        if(lastShow==3){
                            home.scrollAndRefresh();
                            return true;
                        }
                        switchFragment(3);
                        break;
                }
                return true;
            }
        });
        itemLayout = (RelativeLayout)findViewById(R.id.draw_item_layout);
        exit = (TextView) findViewById(R.id.exit);
        drawer = (DrawerLayout) findViewById(R.id.main_drawer);
        nv = (NavigationView) findViewById(R.id.main_nv);
        headLayout = (RelativeLayout) nv.getHeaderView(0);
        headBackground = (ImageView) headLayout.findViewById(R.id.user_background);
        header = (CircleImageView)headLayout.findViewById(R.id.user_header);
        name = (TextView)headLayout.findViewById(R.id.user_name);
        roundView = (View)headLayout.findViewById(R.id.user_view);
        sender1.setOnClickListener(this);
        sender2.setOnClickListener(this);
        sender3.setOnClickListener(this);
        sender4.setOnClickListener(this);
        headLayout.setOnClickListener(this);
        exit.setOnClickListener(this);
        checkCacheFile();
        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View view, float v) {

            }

            @Override
            public void onDrawerOpened(@NonNull View view) {
                drawIsOpen = true;
            }

            @Override
            public void onDrawerClosed(@NonNull View view) {
                drawIsOpen = false;
            }

            @Override
            public void onDrawerStateChanged(int i) {

            }
        });
    }


    private static final String TAG = "MainActivity";

    private void initFragment(){
        book = new Book();
        find = new Find();
        home = new Home();
        recod = new RecommendFragment();
        recod.setDrawerCallback(this);
        fragments = new Fragment[]{recod,book,find,home};
        lastShow = 0;
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.main_content,recod)
                .show(recod)
                .commit();
    }

    private void switchFragment(int index){
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.hide(fragments[lastShow]);
        if(!fragments[index].isAdded())
            transaction.add(R.id.main_content,fragments[index]);
        transaction.show(fragments[index]).commitAllowingStateLoss();
        lastShow = index;
    }

    private void request_permissions() {
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.CAMERA);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]), 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        int grantResult = grantResults[i];
                        if (grantResult == PackageManager.PERMISSION_DENIED) {
                            String s = permissions[i];
                            Toast.makeText(this, s + " permission was denied", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode==1){
            initLoadUserView();
        }
    }



    private void startCamera(){
        int rx = (actionButton.getLeft()+actionButton.getRight())/2;
        int ry = (actionButton.getTop()+actionButton.getBottom())/4;
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(actionButton,"translationY",0,-ry);
        animator1.setDuration(125);
        Animator animator2 = ViewAnimationUtils.createCircularReveal(animator_layout,rx,ry,0,actionButton.getBottom());
        animator2.setDuration(500);
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(actionButton,"scaleX",1,0);
        ObjectAnimator animator4 = ObjectAnimator.ofFloat(actionButton,"scaleY",1,0);
        animator3.setDuration(200);
        animator4.setDuration(200);
        AnimatorSet set = new AnimatorSet();
        set.play(animator1).before(animator2).before(animator3).before(animator4);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                CameraActivity.startCameraActivity(MainActivity.this);
                overridePendingTransition(R.anim.anim_in,R.anim.anim_out);
                actionButton.setTranslationY(0);
                hideStateBar();
                super.onAnimationEnd(animation);
            }
        });
        animator2.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                animator_layout.getBackground().setAlpha(255);
                super.onAnimationStart(animation);
            }
        });
        set.start();
    }
    @Override
    protected void onResume() {
        super.onResume();
        animator_layout.getBackground().setAlpha(0);
        actionButton.setScaleX(1);
        actionButton.setScaleY(1);
        showStateBar();
    }

    private void startFabAnimation(int id){
        if(id==R.id.item_know&&lastShow!=3){
            actionButton.setImageResource(R.drawable.camera_test);
            AnimatorSet set = new AnimatorSet();
            ObjectAnimator rotation_0 = ObjectAnimator.ofFloat(actionButton,"rotation",-45,90);
            rotation_0.setDuration(150);
            ObjectAnimator rotation_1 = ObjectAnimator.ofFloat(actionButton,"rotation",90,110);
            rotation_1.setDuration(100);
            ObjectAnimator rotation_2 = ObjectAnimator.ofFloat(actionButton,"rotation",110,90);
            rotation_1.setDuration(25);
            rotation_1.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    actionButton.setImageResource(R.drawable.delete);
                    super.onAnimationStart(animation);
                }
            });
            set.play(rotation_1).before(rotation_2).after(rotation_0);
            set.start();
        }
        if(id!=R.id.item_know&&lastShow==3){
            actionButton.setImageResource(R.drawable.delete);
            AnimatorSet set = new AnimatorSet();
            ObjectAnimator rotation_0 = ObjectAnimator.ofFloat(actionButton,"rotation",135,0);
            rotation_0.setDuration(150);
            ObjectAnimator rotation_1 = ObjectAnimator.ofFloat(actionButton,"rotation",0,-20);
            rotation_1.setDuration(100);
            ObjectAnimator rotation_2 = ObjectAnimator.ofFloat(actionButton,"rotation",-20,0);
            rotation_1.setDuration(25);
            rotation_1.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    actionButton.setImageResource(R.drawable.camera_test);
                    super.onAnimationStart(animation);
                }
            });
            set.play(rotation_1).before(rotation_2).after(rotation_0);
            set.start();
        }else {
            ObjectAnimator animator = ObjectAnimator.ofFloat(actionButton,"rotation",0,-20,20,0);
            animator.setDuration(200);
            animator.start();
        }
    }

    private void hideStateBar(){
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
    }

    private void showStateBar(){
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
    }

    @Override
    public void openDrawer(){
        drawer.openDrawer(GravityCompat.START);
    }

    @Override
    public void onBackPressed() {
        if(drawIsOpen)
            drawer.closeDrawers();
        else
            super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mainHeadLayout:
                if(BmobUser.isLogin()){
                    getWindow().setExitTransition(new Fade());
                    Intent intent = new Intent(this, PersonalActivity.class);
                    Pair<View,String> pair1 = new Pair<>(headBackground,headBackground.getTransitionName());
                    Pair<View,String> pair2 = new Pair<>(header,header.getTransitionName());
                    Pair<View,String> pair3 = new Pair<>(name,name.getTransitionName());
                    Pair<View,String> pair4 = new Pair<>(roundView,roundView.getTransitionName());
                    Pair<View,String> pair5 = new Pair<>(itemLayout,itemLayout.getTransitionName());
                    ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(this,pair1,pair2,pair3,pair4,pair5);
                    startActivityForResult(intent,1,compat.toBundle());
                }else {
                    startActivity(new Intent(this, StartActivity.class));
                }
                break;
            case R.id.open_camera_activity:
                if(lastShow!=3){
                    startCamera();
                }else {
                    initSender();
                    showStatusBar = !showStatusBar;
                    if(showStatusBar){
                        showStateBar();
                        hideSenderAnimation();
                    }else {
                        formView.setVisibility(showStatusBar?View.GONE:View.VISIBLE);
                        hideStateBar();
                        openSenderAnimation();
                    }
                }
                break;
            case R.id.exit:
                finish();
                BmobUser.logOut();
                break;
            case R.id.topicSender:
                startActivity(new Intent(this, SendTopic.class));
                break;
            case R.id.knowSender:
                startActivity(new Intent(this, SendKnowActivity.class));
                break;
            case R.id.videoSender:
                startActivity(new Intent(this, SendVideo.class));
                break;
            case R.id.pictureSender:
                startActivity(new Intent(this, SendPitureActivity.class));
                break;
        }
        if(v.getId()==R.id.topicSender||v.getId()==R.id.knowSender|v.getId()==R.id.videoSender|v.getId()==R.id.pictureSender){
            showStatusBar = !showStatusBar;
            formView.setVisibility(showStatusBar?View.GONE:View.VISIBLE);
            hideStateBar();
        }
    }
    int a = 1;

    @Override
    public void setHeader(@NotNull String str) {
        Glide.with(this).load(str).apply(options).into(header);
    }

    @Override
    public void setName(@NotNull String str) {
        name.setText(str);
    }

    @NotNull
    @Override
    public String getName(@NotNull String str) {
        return null;
    }

    private void checkCacheFile(){
        savePath = Environment.getExternalStorageDirectory().getAbsoluteFile()+ File.separator+"Mybitmap"+File.separator+"Cache"+File.separator;
        File file = new File(savePath);
        if(file.exists())
            file.mkdirs();
    }

    private void initLoadUserView(){
        if(BmobUser.isLogin()){
            user = BmobUser.getCurrentUser(User.class);
        }else{
            return;
        }
        setHeader(user.getHeaderUri());
        setHeaderBackground(user.getBackgroundUri());
        setName(user.getNickName());
    }

    @Override
    public void updateImageView(int id, @NotNull String path) {
        switch (id){
            case 1://侧边头像
                setHeader(path);
                break;
            case 2:
                setHeaderBackground(path);
                break;
        }
    }

    private void openSenderAnimation(){
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(sender1,"TranslationY",0,-120,0);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(sender2,"TranslationY",0,-120,0);
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(sender3,"TranslationY",0,-120,0);
        ObjectAnimator animator4 = ObjectAnimator.ofFloat(sender4,"TranslationY",0,-120,0);
        ObjectAnimator animator5 = ObjectAnimator.ofFloat(text1,"Alpha",0,1);
        ObjectAnimator animator6 = ObjectAnimator.ofFloat(text2,"Alpha",0,1);
        ObjectAnimator animator7 = ObjectAnimator.ofFloat(text3,"Alpha",0,1);
        ObjectAnimator animator8 = ObjectAnimator.ofFloat(text4,"Alpha",0,1);
        ObjectAnimator animator9 = ObjectAnimator.ofFloat(actionButton,"Rotation",0,155,135);
        AnimatorSet set = new AnimatorSet();
        set.play(animator1)
                .with(animator2)
                .with(animator3)
                .with(animator4)
                .with(animator5)
                .with(animator6)
                .with(animator7)
                .with(animator8)
                .with(animator9);
        set.setDuration(300);
        set.start();
    }

    private void hideSenderAnimation(){
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(formView,"Alpha",1,0);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(actionButton,"Rotation",135,-20,0);
        AnimatorSet set = new AnimatorSet();
        set.play(animator1)
                .with(animator2);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                formView.setVisibility(showStatusBar?View.GONE:View.VISIBLE);
                super.onAnimationEnd(animation);
            }
        });
        set.setDuration(300);
        set.start();
    }

    private void initSender(){
        formView.setAlpha(1f);
        sender1.setAlpha(1f);
        sender2.setAlpha(1f);
        sender3.setAlpha(1f);
        sender4.setAlpha(1f);
        text1.setAlpha(1f);
        text2.setAlpha(1f);
        text3.setAlpha(1f);
        text4.setAlpha(1f);
    }

    @Override
    public void setHeaderBackground(@NotNull String str) {
        Glide.with(this).load(str).apply(options).into(headBackground);
    }


    @Override
    public void closeDrawer() {
        drawer.closeDrawers();
    }
}

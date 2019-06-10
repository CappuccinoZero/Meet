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
import android.os.Handler;
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
import com.lin.meet.R;
import com.lin.meet.bean.User;
import com.lin.meet.camera_demo.CameraActivity;
import com.lin.meet.login.StartActivity;
import com.lin.meet.main.fragment.Book.Book;
import com.lin.meet.main.fragment.Find.Find;
import com.lin.meet.main.fragment.Home.Home;
import com.lin.meet.main.fragment.Know.Know;
import com.lin.meet.personal.PersonalActivity;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,MainConstract.View {
    public static String savePath = Environment.getExternalStorageDirectory().getAbsoluteFile()+ File.separator+"Mybitmap"+File.separator+"Cache"+File.separator;
    private boolean isLogin = false;//是否处于登录状态
    private DataBase dataBase;
    private ImageView imageView;
    private BottomNavigationView bv;
    private NavigationView nv;
    private Fragment fragments[];
    private FloatingActionButton faButton;
    private FrameLayout animator_layout;
    private Handler handler;
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
    private Book book = new Book();
    private Find find = new Find();
    private Home home = new Home();
    private Know know = new Know();
    private boolean drawIsOpen = false;

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
        initLoadUserView();
    }

    private void initView(){
        presenter = new MainPresenter(this);
        request_permissions();
        handler = new Handler();
        faButton = (FloatingActionButton)findViewById(R.id.open_camera_activity);
        animator_layout = (FrameLayout)findViewById(R.id.animator_layout);
        actionButton = (FloatingActionButton)findViewById(R.id.open_camera_activity);
        actionButton.setOnClickListener(this);
        options = new RequestOptions();
        options.error(R.color.bank_FF6C6C6C);
        initFragment();
        dataBase = new DataBaseModel();
        bv = (BottomNavigationView)findViewById(R.id.main_bnv);
        bv.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
        bv.setItemIconTintList(null);
        bv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                startFabAnimation();
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                switch (menuItem.getItemId()){

                    case R.id.item_home:
                        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                        if(lastShow==0){
                            home.scrollAndRefresh();
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
                            know.scrollAndRefresh();
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
        know = new Know();
        fragments = new Fragment[]{home,book,find,know};
        lastShow = 0;
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.main_content,home)
                .show(home)
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
        int rx = (faButton.getLeft()+faButton.getRight())/2;
        int ry = (faButton.getTop()+faButton.getBottom())/4;
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(faButton,"translationY",0,-ry);
        animator1.setDuration(125);
        Animator animator2 = ViewAnimationUtils.createCircularReveal(animator_layout,rx,ry,0,faButton.getBottom());
        animator2.setDuration(500);
        AnimatorSet set = new AnimatorSet();
        set.play(animator1).before(animator2);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                CameraActivity.startCameraActivity(MainActivity.this);
                overridePendingTransition(R.anim.anim_in,R.anim.anim_out);
                faButton.setTranslationY(0);
                hideStateBar();
                super.onAnimationEnd(animation);
            }
        });
        animator2.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                animator_layout.getBackground().setAlpha(255);
                faButton.hide();
                super.onAnimationStart(animation);
            }
        });
        set.start();
    }

    @Override
    protected void onResume() {
        animator_layout.getBackground().setAlpha(0);
        faButton.show();
        showStateBar();
        super.onResume();
    }

    private void startFabAnimation(){
        ObjectAnimator animator = ObjectAnimator.ofFloat(faButton,"rotation",0,-20,20,0);
        animator.setDuration(200);
        animator.start();
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
                startCamera();
                break;
            case R.id.exit:
                finish();
                BmobUser.logOut();
                break;
        }
    }

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

    @Override
    public void setHeaderBackground(@NotNull String str) {
        Glide.with(this).load(str).apply(options).into(headBackground);
    }


}

package com.lin.meet.main;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.design.bottomnavigation.LabelVisibilityMode;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;


import com.lin.meet.R;
import com.lin.meet.camera_demo.CameraActivity;
import com.lin.meet.main.fragment.Book.Book;
import com.lin.meet.main.fragment.Find.Find;
import com.lin.meet.main.fragment.Home.Home;
import com.lin.meet.main.fragment.Know.Know;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window window = getWindow();
        request_permissions();
        handler = new Handler();
        faButton = (FloatingActionButton)findViewById(R.id.open_camera_activity);
        animator_layout = (FrameLayout)findViewById(R.id.animator_layout);
        ((FloatingActionButton) findViewById(R.id.open_camera_activity)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCamera();
                //startActivity(new Intent(MainActivity.this,HelloTest.class));
            }
        });
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
                        if(lastShow==0)
                            return true;
                        switchFragment(0);
                        break;
                    case R.id.item_book:
                        if(lastShow==1)
                            return true;
                        switchFragment(1);
                        break;
                    case R.id.item_know:
                        if(lastShow==2)
                            return true;
                        switchFragment(2);
                        break;
                    case R.id.item_find:
                        if(lastShow==3)
                            return true;
                        switchFragment(3);
                        break;
                }
                return true;
            }
        });
        drawer = (DrawerLayout) findViewById(R.id.main_drawer);
        nv = (NavigationView) findViewById(R.id.main_nv);

    }

    private static final String TAG = "MainActivity";

    private void initFragment(){
        Book book = new Book();
        Find find = new Find();
        Home home = new Home();
        Know know = new Know();
        fragments = new Fragment[]{home,book,know,find};
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
}

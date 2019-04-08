package com.lin.meet.setting;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.lin.meet.R;
import com.lin.meet.override.SmoothCheckBox;

public class CameraSetting extends AppCompatActivity implements View.OnClickListener,CSettingContract.View {
    private Toolbar toolbar;
    private RelativeLayout layout_0;
    private RelativeLayout layout_1;
    private RelativeLayout layout_2;
    private RelativeLayout layout_3;
    private RelativeLayout layout_4;
    private RelativeLayout layout_5;
    private SmoothCheckBox box_0;
    private SmoothCheckBox box_1;
    private SmoothCheckBox box_2;
    private SmoothCheckBox box_3;
    private SmoothCheckBox box_4;
    private CSettingContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.camera_setting_color));
        setContentView(R.layout.camera_setting_layout);
        init();
        presenter.initSetting(this);
    }

    private void init(){
        presenter = new CameraPresenter(this);
        toolbar = (Toolbar)findViewById(R.id.camera_setting_toobar);
        layout_0 = (RelativeLayout)findViewById(R.id.c_layout_0);
        layout_1 = (RelativeLayout)findViewById(R.id.c_layout_1);
        layout_2 = (RelativeLayout)findViewById(R.id.c_layout_2);
        layout_3 = (RelativeLayout)findViewById(R.id.c_layout_3);
        layout_4 = (RelativeLayout)findViewById(R.id.c_layout_4);
        layout_5 = (RelativeLayout)findViewById(R.id.c_layout_5);
        box_0 = (SmoothCheckBox)findViewById(R.id.c_box_0);
        box_1 = (SmoothCheckBox)findViewById(R.id.c_box_1);
        box_2 = (SmoothCheckBox)findViewById(R.id.c_box_2);
        box_3 = (SmoothCheckBox)findViewById(R.id.c_box_3);
        box_4 = (SmoothCheckBox)findViewById(R.id.c_box_4);
        box_3.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked) {
                if(box_3.isChecked()){
                    if (ActivityCompat.checkSelfPermission(CameraSetting.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(CameraSetting.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(CameraSetting.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION} , 1);
                    }
                }
            }
        });

        layout_0.setOnClickListener(this);
        layout_1.setOnClickListener(this);
        layout_2.setOnClickListener(this);
        layout_3.setOnClickListener(this);
        layout_4.setOnClickListener(this);
        layout_5.setOnClickListener(this);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.back_x);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void finish(){
        Intent intent = new Intent();
        intent.putExtra("box_1",box_0.isChecked());
        intent.putExtra("box_2",box_4.isChecked());
        intent.putExtra("box_3",box_1.isChecked());
        intent.putExtra("box_4",box_2.isChecked());
        intent.putExtra("box_5",box_3.isChecked());
        setResult(10001, intent);
        super.finish();
        overridePendingTransition(R.anim.anim_in,R.anim.anim_out);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.c_layout_0:
                box_0.performClick();
                break;
            case R.id.c_layout_1:
                box_1.performClick();
                break;
            case R.id.c_layout_2:
                box_2.performClick();
                setLayout_3();
                break;
            case R.id.c_layout_3:
                box_3.performClick();
                break;
            case R.id.c_layout_5:
                box_4.performClick();
                break;
            case R.id.c_layout_4:
                Toast.makeText(this, "联系我们", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void setLayout_3(){
        if(box_2.isChecked()){
            layout_3.setVisibility(View.VISIBLE);
        }else{
            layout_3.setVisibility(View.GONE);
        }
    }

    @Override
    public void setCheckBox_1(boolean check) {
        box_0.setChecked(check);
    }

    @Override
    public void setCheckBox_2(boolean check) {
        box_4.setChecked(check);
    }

    @Override
    public void setCheckBox_3(boolean check) {
        box_1.setChecked(check);
    }

    @Override
    public void setCheckBox_4(boolean check) {
        box_2.setChecked(check);
        setLayoutVisible(check);
    }

    @Override
    public void setCheckBox_5(boolean check) {
        box_3.setChecked(check);
    }



    @Override
    public void setLayoutVisible(boolean visible) {
        if(visible)
            layout_3.setVisibility(View.VISIBLE);
        else
            layout_3.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        presenter.saveSetting(box_0.isChecked(),box_4.isChecked(),box_1.isChecked(),box_2.isChecked(),box_3.isChecked(),this);
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(!(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED))
                    box_3.setChecked(false);
        }
    }

}

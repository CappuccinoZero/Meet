package com.lin.meet.login;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.lin.meet.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private RelativeLayout flowLayout;
    private ImageView flowBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        init();
    }

    private void init(){
        flowLayout = (RelativeLayout)findViewById(R.id.flowLayout);
        flowBack = (ImageView)findViewById(R.id.flowBack);
        flowBack.setOnClickListener(this);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.anim_in,R.anim.anim_out);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.flowBack:
                finish();
                break;
        }
    }
}

package com.lin.meet.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.lin.meet.R;

import pers.lin.linanimations.animations.WaveView;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private RelativeLayout waveLayout;
    private ImageView waveBack;
    private Button next;
    private WaveView waveView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);
        init();
    }

    void init(){
        waveLayout = (RelativeLayout)findViewById(R.id.wave_layout);
        waveBack = (ImageView)findViewById(R.id.waveBack);
        next = (Button)findViewById(R.id.register_next);
        waveView = (WaveView)findViewById(R.id.login_waveView);
        waveBack.setOnClickListener(this);
        next.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.waveBack:
                finish();
                break;
            case R.id.register_next:
                startActivity(new Intent(this,NoMaybe.class));
                break;
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.anim_in,R.anim.anim_out);
    }
}

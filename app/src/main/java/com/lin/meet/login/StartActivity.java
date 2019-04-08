package com.lin.meet.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lin.meet.R;

import pers.lin.linanimations.animations.RippleView;

public class StartActivity extends AppCompatActivity implements View.OnClickListener {
    private RelativeLayout rippleLayout;
    private Button checkLogin;
    private TextView checkRegister;
    private RippleView rippleView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_start);
        init();
    }

    private void init(){
        rippleLayout = (RelativeLayout)findViewById(R.id.ripple_layout);
        checkLogin = (Button)findViewById(R.id.check_login);
        checkRegister = (TextView)findViewById(R.id.check_register);
        rippleView = (RippleView)findViewById(R.id.login_rippleView);
        rippleView.setAsBackground(rippleView.getBgColor());
        checkLogin.setOnClickListener(this);
        checkRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.check_login:
                startActivity(new Intent(this,LoginActivity.class));
                overridePendingTransition(R.anim.anim_in,R.anim.anim_out);
                break;
            case R.id.check_register:
                startActivity(new Intent(this,RegisterActivity.class));
                overridePendingTransition(R.anim.anim_in,R.anim.anim_out);
                break;
        }
    }
}

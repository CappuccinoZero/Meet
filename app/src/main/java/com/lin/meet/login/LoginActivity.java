package com.lin.meet.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.lin.meet.R;
import com.lin.meet.main.MainActivity;
import com.lin.meet.my_util.MyUtil;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener,LoginConstract.LoginView {
    private LoginConstract.LoginPresenter presenter;
    private RelativeLayout flowLayout;
    private ImageView flowBack;
    private EditText phoneEdit;
    private EditText passwordEdit;
    private Button loginButton;
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
        phoneEdit = (EditText)findViewById(R.id.login_number_edit);
        passwordEdit = (EditText)findViewById(R.id.login_password_edit);
        loginButton = (Button)findViewById(R.id.login_Button);
        presenter = LoginPresenter.getLoginPresenter(this);
        loginButton.setOnClickListener(this);
        flowBack.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.flowBack:
                startActivity(new Intent(this,StartActivity.class));
                overridePendingTransition(R.anim.anim_in,R.anim.anim_out);
                finish();
                break;
            case R.id.login_Button:
                if(checkEdit()){
                   presenter.onLogin(phoneEdit.getText().toString(),passwordEdit.getText().toString());
                }
        }
    }

    @Override
    public void loginResult(int resultCode) {
        if(resultCode==1){
            toast("登录成功");
            HashMap<String,String> map = new HashMap<>();
            map.put("username",phoneEdit.getText().toString());
            map.put("token",MyUtil.getMD5String(passwordEdit.getText().toString()));
            MyUtil.saveSharedPreferences(this,"LoginToken",map);
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        else{
            toast("登录失败，账号或密码错误");
        }
    }

    @Override
    public void toast(String str) {
        Toast.makeText(this,str,Toast.LENGTH_SHORT).show();
    }

    private boolean checkEdit(){
        if(passwordEdit.getText().toString().length()<=0||phoneEdit.getText().toString().length()<=0){
        toast("账号和密码不能为空");
        return false;
    }
        if(phoneEdit.getText().toString().length()!=11){
            toast("手机号码错误");
            return false;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this,StartActivity.class));
        overridePendingTransition(R.anim.anim_in,R.anim.anim_out);
        super.onBackPressed();
    }
}

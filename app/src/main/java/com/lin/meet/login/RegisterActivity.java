package com.lin.meet.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lin.meet.R;
import com.lin.meet.main.MainActivity;
import com.lin.meet.my_util.MyUtil;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import pers.lin.linanimations.animations.WaveView;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener,LoginConstract.registerView, TextWatcher {
    private RelativeLayout waveLayout;
    private int status = 0;
    private ImageView waveBack;
    private Button next;
    private WaveView waveView;
    private EditText usernameEdit;
    private EditText passwordEdit;
    private LoginConstract.RegisterPresenter presenter;
    private TextView reSendSms;
    private TextView countdown;
    private TextView phoneText;
    private EditText veriEdit;
    private RelativeLayout smsLayout;
    private Timer timer;
    private TimerTask timerTask;
    private int countTime = 60;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);
        init();
    }

    void init(){
        presenter = LoginPresenter.getRegisterPresenter(this);
        waveLayout = (RelativeLayout)findViewById(R.id.wave_layout);
        waveBack = (ImageView)findViewById(R.id.waveBack);
        next = (Button)findViewById(R.id.register_next);
        usernameEdit = (EditText)findViewById(R.id.register_username_edit);
        passwordEdit = (EditText)findViewById(R.id.register_password_edit);
        reSendSms = (TextView)findViewById(R.id.register_reSend);
        countdown = (TextView)findViewById(R.id.register_counter);
        phoneText = (TextView)findViewById(R.id.register_phone_message);
        veriEdit = (EditText)findViewById(R.id.register_veri_edit);
        smsLayout = (RelativeLayout)findViewById(R.id.register_sms_layout);
        reSendSms.setOnClickListener(this);
        veriEdit.addTextChangedListener(this);
        waveBack.setOnClickListener(this);
        next.setOnClickListener(this);
        usernameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(usernameEdit.getText().toString().isEmpty()){
                    usernameEdit.setGravity(Gravity.CENTER);
                    usernameEdit.setCursorVisible(false);
                }else {
                    usernameEdit.setGravity(Gravity.START);
                    usernameEdit.setCursorVisible(true);
                }
            }
        });
        passwordEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(passwordEdit.getText().toString().isEmpty()){
                    passwordEdit.setGravity(Gravity.CENTER);
                    passwordEdit.setCursorVisible(false);
                }else {
                    passwordEdit.setGravity(Gravity.START);
                    passwordEdit.setCursorVisible(true);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.waveBack:
                if(status==0){
                    startActivity(new Intent(this,StartActivity.class));
                    overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                    finish();
                }
                else
                    hideSmsLayout();
                break;
            case R.id.register_next:
                if(checkUser(usernameEdit.getText().toString(),passwordEdit.getText().toString())){
                    presenter.selectPhone(usernameEdit.getText().toString());
                }
                break;
            case R.id.register_reSend:
                veriEdit.setText("");
                Toast.makeText(this,"已重新发送短信",Toast.LENGTH_SHORT).show();
                presenter.sendMSM(usernameEdit.getText().toString());
                break;
        }
    }


    @Override
    public void onBackPressed() {
        if(status==0){
            startActivity(new Intent(this,StartActivity.class));
            overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            super.onBackPressed();
        }
        else
            hideSmsLayout();
    }

    @Override
    public void registerResult(int resultCode) {
        if(resultCode==0){
            Toast.makeText(this,"注册失败",Toast.LENGTH_SHORT).show();
            HashMap<String,String> map = new HashMap<>();
            map.put("username",usernameEdit.getText().toString());
            map.put("token", com.lin.meet.my_util.MyUtil.getMD5String(passwordEdit.getText().toString()));
            MyUtil.saveSharedPreferences(this,"LoginToken",map);
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }else if(resultCode==1){
            Toast.makeText(this,"注册成功",Toast.LENGTH_SHORT).show();
            HashMap<String,String> map = new HashMap<>();
            map.put("username",usernameEdit.getText().toString());
            map.put("token",MyUtil.getMD5String(passwordEdit.getText().toString()));
            MyUtil.saveSharedPreferences(this,"LoginToken",map);
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @Override
    public void showSmsLayout() {
        status = 1;
        smsLayout.setVisibility(View.VISIBLE);
        usernameEdit.setVisibility(View.GONE);
        passwordEdit.setVisibility(View.GONE);
        next.setVisibility(View.GONE);
        veriEdit.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(veriEdit,0);
        hideReSendSms();
    }

    @Override
    public void hideSmsLayout() {
        status = 0;
        smsLayout.setVisibility(View.GONE);
        usernameEdit.setVisibility(View.VISIBLE);
        passwordEdit.setVisibility(View.VISIBLE);
        next.setVisibility(View.VISIBLE);
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View v = getWindow().peekDecorView();
        if (null != v) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
        veriEdit.setText("");
    }

    @Override
    public void setCounter(int time) {
        countdown.setText(""+time);
    }

    @Override
    public void showReSendSms() {
        reSendSms.setVisibility(View.VISIBLE);
        countdown.setVisibility(View.GONE);
    }

    @Override
    public void hideReSendSms() {
        reSendSms.setVisibility(View.GONE);
        countdown.setVisibility(View.VISIBLE);
    }

    @Override
    public void setPhoneText(String phone) {
        char[] phoneChars = phone.toCharArray();
        for(int i=3;i<phoneChars.length-4;i++){
            phoneChars[i] = '*';
        }
        phoneText.setText(new String(phoneChars));
    }

    @Override
    public void startCounter() {
        clearTimer();
        timer = new Timer();
        countTime = 60;
        timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(()->{
                    setCounter(countTime);
                    if(countTime==0){
                        timerTask.cancel();
                        showReSendSms();
                    }
                    countTime--;
                });
            }
        };
        timer.schedule(timerTask,0,1000L);
    }

    @Override
    public void showSendSMSError() {
        status = 1;
        smsLayout.setVisibility(View.VISIBLE);
        usernameEdit.setVisibility(View.GONE);
        passwordEdit.setVisibility(View.GONE);
        next.setVisibility(View.GONE);
        reSendSms.setVisibility(View.GONE);
        countdown.setVisibility(View.GONE);
    }

    @Override
    public void onRegister() {
        presenter.onRegister(usernameEdit.getText().toString(),passwordEdit.getText().toString());
    }

    @Override
    public void selectPhoneResult(int resultCode) {
        if(resultCode==1){
            presenter.sendMSM(usernameEdit.getText().toString());

        }else {
            Toast.makeText(this,"手机号码已被注册",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void toast(String str) {
        Toast.makeText(this,str,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if(presenter.isSendMsm()&&veriEdit.getText().toString().length()==6){
            presenter.checkVerifySmsCode(usernameEdit.getText().toString(),veriEdit.getText().toString());
        }
    }

    private void clearTimer(){
        if(timer!=null||timerTask!=null){
            timer = null;
            timerTask.cancel();
            timerTask = null;
        }
    }

    private boolean checkUser(String phone,String password){
        if(phone.length()!=11){
            Toast.makeText(this,"请输入正确的手机号码",Toast.LENGTH_SHORT).show();
            return false;
        }
        return checkPassword(password);
    }

    private boolean checkPassword(String password){
        if(password.length()<6){
            Toast.makeText(this,"密码不能少于6位",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}

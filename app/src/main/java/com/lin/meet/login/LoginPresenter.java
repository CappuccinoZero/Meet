package com.lin.meet.login;

import com.lin.meet.bean.User;
import com.lin.meet.my_util.MyUtil;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class LoginPresenter implements LoginConstract.RegisterPresenter,LoginConstract.LoginPresenter {
    private LoginConstract.registerView registerView;
    private LoginConstract.LoginView loginView;
    private Integer smsId = 0;
    private boolean isSendSms = false;
    private LoginPresenter(LoginConstract.registerView registerView){
        this.registerView = registerView;
    };

    private LoginPresenter(LoginConstract.LoginView loginView){
        this.loginView = loginView;
    };


    public static LoginConstract.RegisterPresenter getRegisterPresenter(LoginConstract.registerView registerView) {
        return new LoginPresenter(registerView);
    }

    public static LoginConstract.LoginPresenter getLoginPresenter(LoginConstract.LoginView loginView) {
        return new LoginPresenter(loginView);
    }

    @Override
    public void onRegister(String user, String password) {
        char []nickChar = user.toCharArray();
        nickChar = new char[]{nickChar[nickChar.length-4],nickChar[nickChar.length-3],nickChar[nickChar.length-2],nickChar[nickChar.length-1]};
        String nickName = "用户"+new String(nickChar);
        User userBean = new User();
        userBean.setUsername(user);
        userBean.setNickName(nickName);
        userBean.setPassword(MyUtil.getMD5String(password));
        userBean.signUp(new SaveListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if(e==null){
                    registerView.registerResult(1);
                }else{
                    registerView.registerResult(0);
                }
            }
        });
    }

    @Override
    public void sendMSM(String phone) {
        registerView.showSmsLayout();
        registerView.setPhoneText(phone);
        registerView.startCounter();
        BmobSMS.requestSMSCode(phone, "Meet", new QueryListener<Integer>() {
            @Override
            public void done(Integer integer, BmobException e) {
                if(e==null){
                    smsId = integer;
                    isSendSms = true;
                }else {
                    registerView.showSendSMSError();
                    isSendSms = false;
                }
            }
        });
    }

    @Override
    public void checkVerifySmsCode(String phone,String code) {
        BmobSMS.verifySmsCode(phone, code, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    registerView.onRegister();
                }else {
                    registerView.toast("发送短信失败，请重试");
                }
            }
        });
    }

    @Override
    public void selectPhone(String phone) {
        BmobQuery<User> query = new BmobQuery<>();
        query.addWhereEqualTo("username",phone);
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if(e==null&&list.size()==0){
                    registerView.selectPhoneResult(1);
                }else {
                    registerView.selectPhoneResult(0);
                }
            }
        });
    }

    @Override
    public boolean isSendMsm() {
        return isSendSms;
    }

    @Override
    public void onLogin(String user, String password) {
        User userBean = new User();
        userBean.setUsername(user);
        userBean.setPassword(MyUtil.getMD5String(password));
        userBean.login(new SaveListener<User>(){
            @Override
            public void done(User user, BmobException e) {
                if(e==null){
                    loginView.loginResult(1);
                }else{
                    loginView.loginResult(0);
                }
            }
        });
    }
}

package com.lin.meet.login;

public interface LoginConstract {
    public interface LoginView{
        void loginResult(int resultCode);
        void toast(String str);
    };
    public interface registerView{
        void registerResult(int resultCode);
        void showSmsLayout();
        void hideSmsLayout();
        void setCounter(int time);
        void showReSendSms();
        void hideReSendSms();
        void setPhoneText(String phone);
        void startCounter();
        void showSendSMSError();
        void onRegister();
        void selectPhoneResult(int resultCode);
        void toast(String str);
    };
    public interface LoginPresenter{
        void onLogin(String user,String password);
    };
    public interface RegisterPresenter{
        void onRegister(String user,String password);
        void sendMSM(String phone);
        void checkVerifySmsCode(String phone,String code);
        void selectPhone(String phone);
        boolean isSendMsm();
    };

    public interface Model{
    };
}

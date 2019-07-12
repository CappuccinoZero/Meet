package com.lin.meet.db_bean;

import com.lin.meet.bean.User;

import cn.bmob.v3.BmobUser;

public class CreateID {
    public static String getId(){
        if(BmobUser.isLogin()){
            return String.valueOf(System.currentTimeMillis()+BmobUser.getCurrentUser(User.class).getUid());
        }
        return null;
    }
}

package com.lin.meet.db_bean;

import cn.bmob.v3.BmobObject;

public class friends extends BmobObject {
    private String uidA;//关注者
    private String uidB;//被关注者

    public String getUidA() {
        return uidA;
    }

    public String getUidB() {
        return uidB;
    }

    public void setUidA(String uidA) {
        this.uidA = uidA;
    }

    public void setUidB(String uidB) {
        this.uidB = uidB;
    }
}

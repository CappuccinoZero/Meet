package com.lin.meet.db_bean;

import com.lin.meet.bean.User;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;

//赞以及相关类
public class comment_like extends BmobObject {
    private String parentId;//上一级ID
    private String parentUid;//上一级UID
    private boolean isMain;//是否是主人
    private String mainId;//主题ID
    private String uid;//主人UID
    private String id;//主人ID
    private int flag;//标志0 - 3

    public String getId() {
        return id;
    }

    public String getUid() {
        return uid;
    }


    public comment_like setId(String id) {
        this.id = id;
        return this;
    }

    public comment_like setUid(String uid) {
        this.uid = uid;
        return this;
    }


    public int getFlag() {
        return flag;
    }

    public comment_like setFlag(int flag) {
        this.flag = flag;
        return this;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getParentUid() {
        return parentUid;
    }

    public void setParentUid(String parentUid) {
        this.parentUid = parentUid;
    }

    public String getParentId() {
        return parentId;
    }

    public String getMainId() {
        return mainId;
    }

    public void setMainId(String mainId) {
        this.mainId = mainId;
    }

    public boolean isMain() {
        return isMain;
    }

    public void setMain(boolean main) {
        isMain = main;
    }

    public static comment_like createMainLike(String parentUid,String parentId,int flag){
        if(!BmobUser.isLogin())
            return null;
        User user = BmobUser.getCurrentUser(User.class);
        comment_like like = new comment_like();
        like.flag = flag;
        like.isMain = true;
        like.id = CreateID.getId();
        like.uid = user.getUid();
        like.parentId = parentId;
        like.parentUid = parentUid;
        like.mainId = parentId;
        return like;
    }

    public static comment_like createSonLike(String parentUid,String parentId,String mainId,int flag){
        if(!BmobUser.isLogin())
            return null;
        User user = BmobUser.getCurrentUser(User.class);
        comment_like like = new comment_like();
        like.flag = flag;
        like.isMain = false;
        like.id = CreateID.getId();
        like.uid = user.getUid();
        like.parentId = parentId;
        like.parentUid = parentUid;
        like.mainId = mainId;
        return like;
    }
}

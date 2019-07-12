package com.lin.meet.db_bean;

import com.lin.meet.bean.User;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;

//评论 ->
public class comment extends BmobObject {
    private String parentId;//上一级ID
    private String parentUid;//上一级UID
    private String mainId;//主题ID
    private boolean main;//是否是主
    private int flag;//0 - 3
    private String uid;//主人UID
    private String id;//主人ID
    private String content;//内容

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public int getFlag() {
        return flag;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }



    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }


    public String getParentId() {
        return parentId;
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

    public void setMainId(String mainId) {
        this.mainId = mainId;
    }

    public String getMainId() {
        return mainId;
    }

    public void setMain(boolean main) {
        this.main = main;
    }

    public boolean getMain() {
        return main;
    }

    public comment createSonComment(String content,int flag){
        if(!BmobUser.isLogin())
            return null;
        User user = BmobUser.getCurrentUser(User.class);
        comment comment = new comment();
        comment.parentId = this.id;
        comment.parentUid = this.uid;
        comment.mainId = this.mainId;
        comment.uid = user.getUid();
        comment.id = CreateID.getId();
        comment.main = false;
        comment.content = content;
        comment.flag = flag;
        return comment;
    }

    public static comment createMainComment(String content,String parentUid,String parentId,int flag){
        if(!BmobUser.isLogin())
            return null;
        User user = BmobUser.getCurrentUser(User.class);
        comment comment = new comment();
        comment.parentId = parentId;
        comment.mainId = parentId;
        comment.parentUid = parentUid;
        comment.uid = user.getUid();
        comment.id = CreateID.getId();
        comment.main = true;
        comment.content = content;
        comment.flag = flag;
        return comment;
    }
}

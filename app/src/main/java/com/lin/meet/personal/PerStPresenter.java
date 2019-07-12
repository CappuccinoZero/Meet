package com.lin.meet.personal;

import android.content.Context;
import android.os.Environment;

import com.lin.meet.bean.User;
import com.lin.meet.my_util.MyUtil;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class PerStPresenter implements PerStContract.Presenter {
    private String savePath = Environment.getExternalStorageDirectory().getAbsoluteFile()+ File.separator+"Mybitmap"+File.separator+"Cache"+File.separator;
    private PerStContract.View view;
    private Context context;
    private String path = "";
    private String headPath = "";
    private User user = null;
    private BmobFile uploadFile = null;

    PerStPresenter(PerStContract.View view){
        this.view = view;
        context = (Context)view;
    }
    @Override
    public void updateBackground(@NotNull String phone, @NotNull String token,String path) {
        this.path = path;
        if(BmobUser.isLogin()){
            user = BmobUser.getCurrentUser(User.class);
            deleteBackground();
        }else {
            user = new User();
            user.setPassword(token);
            user.setUsername(phone);
            user.login(new SaveListener<User>() {
                @Override
                public void done(User o, BmobException e) {
                    if(e==null){
                        deleteBackground();
                    }else {

                    }
                }
            });
        }
    }

    private void deleteBackground(){
        if(user!=null){
            BmobFile file = new BmobFile();
            file.setUrl(user.getBackgroundUri());
            file.delete(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    uploadBackground();
                }
            });
        }
    }

    private void uploadBackground(){
        File file = new File(path);
        if(file.exists()){
            view.createProgressDialog();
            uploadFile = new BmobFile(file);
            uploadFile.uploadblock(new UploadFileListener() {
                @Override
                public void done(BmobException e) {
                    if(e==null){
                        user.setBackgroundUri(uploadFile.getFileUrl());
                        updateUser("上传成功","",0);
                        view.setBackground(uploadFile.getFileUrl());
                    }else{

                    }
                    view.closeProgressDialog();
                }
            });
        }
    }

    private void updateUser(String msg,String str,int id){
        user.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    view.toast(msg);
                    if(id>0){
                        view.updateData(str,id);
                    }
                    if(id==-1)
                        view.updateData(str,0);
                }else{
                }
            }
        });
    }

    @Override
    public void canelUpload(){
        if(uploadFile!=null){
            uploadFile.cancel();
            uploadFile = null;
        }
    }

    private String saveFile(String key,String path){
        HashMap<String,String> map = new HashMap<>();
        String fileName = String.valueOf(System.currentTimeMillis());
        map.put(key,fileName);
        MyUtil.saveSharedPreferences(context,"Cache"+BmobUser.getCurrentUser(User.class).getUid(),map);
        copyFile(path,fileName);
        return fileName;
    }

    private void copyFile(String oldPath,String name){
        try {
            int bytesum = 0;
            int byteread = 0;
            File old = new File(oldPath);
            if(old.exists()){
                InputStream in = new FileInputStream(oldPath);
                FileOutputStream ou = new FileOutputStream(savePath+name);
                byte[] bytes = new byte[1024];
                int lenth;
                while((byteread = in.read(bytes))!=-1){
                    bytesum += byteread;
                    ou.write(bytes,0,byteread);
                }
                in.close();
                ou.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            view.closeProgressDialog();
        }
    }

    @Override
    public void updateHeader(@NotNull String phone, @NotNull String token, @NotNull String path) {
        this.headPath = path;
        if(BmobUser.isLogin()){
            user = BmobUser.getCurrentUser(User.class);
            deleteHeader();
        }else {
            user = new User();
            user.setPassword(token);
            user.setUsername(phone);
            user.login(new SaveListener<User>() {
                @Override
                public void done(User o, BmobException e) {
                    if(e==null){
                        deleteHeader();
                    }else {

                    }
                }
            });
        }
    }

    private void deleteHeader(){
        if(user!=null){
            BmobFile file = new BmobFile();
            file.setUrl(user.getHeaderUri());
            file.delete(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    uploadHeader();
                }
            });
        }
    }

    private void uploadHeader(){
        File file = new File(headPath);
        if(file.exists()){
            view.createProgressDialog();
            uploadFile = new BmobFile(file);
            uploadFile.uploadblock(new UploadFileListener() {
                @Override
                public void done(BmobException e) {
                    if(e==null){
                        user.setHeaderUri(uploadFile.getFileUrl());
                        updateUser("上传成功","",0);
                        view.setHeader(uploadFile.getFileUrl());
                    }else{

                    }
                    view.closeProgressDialog();
                }
            });
        }
    }

    @Override
    public void onSettingNickName(@NotNull String name) {
        if(initUser()){
            user.setNickName(name);
            updateUser("修改成功",name,1);
        }
    }

    @Override
    public void onSettingSex(@NotNull String name) {
        if(initUser()){
            user.setSex(name);
            updateUser("修改成功",name,2);
        }
    }

    @Override
    public void onSettingBirth(@NotNull String name) {
        if(initUser()){
            user.setBrith(name);
            updateUser("修改成功",name,3);
        }
    }

    @Override
    public void onSettingWork(@NotNull String name) {
        if(initUser()){
            user.setWork(name);
            updateUser("修改成功",name,4);
        }
    }

    @Override
    public void onSettingEmail(@NotNull String name) {
        if(initUser()){
            user.setE_mail(name);
            updateUser("修改成功",name,5);
        }
    }


    @Override
    public void onSettingArea(@NotNull String name) {
        if(initUser()){
            user.setArea(name);
            updateUser("修改成功",name,6);
        }
    }

    @Override
    public void onSettingSignature(@NotNull String name) {
        if(initUser()){
            user.setSignature(name);
            updateUser("修改成功",name,7);
        }
    }

    @Override
    public void onSettingIntroduce(@NotNull String name) {
        if(initUser()){
            user.setIntroduce(name);
            updateUser("修改成功",name,8);
        }
    }

    private boolean initUser(){
        if(BmobUser.isLogin()){
            if(user==null){
                user = BmobUser.getCurrentUser(User.class);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onSettingUID(@NotNull String id) {
        char[] ids = id.toCharArray();
        if(id.equals("")){
            view.toast("UID不能为空");
            return;
        }
        for(char ch:ids){
            if(!
                    ((ch>='0'&&ch<='9')||
                    (ch>='a'&&ch<='z')||
                    (ch>='A'&&ch<='Z'))
            ){
                view.toast("UID只能包含字母和数字");
                return;
            }
        }
        if(initUser()){
            BmobQuery<User> query = new BmobQuery<>();
            query.addWhereEqualTo("uid",id);
            query.findObjects(new FindListener<User>() {
                @Override
                public void done(List<User> list, BmobException e) {
                    if(e==null&&list.size()==0){
                        user.setUid(id);
                        updateUser("设置成功",id,-1);
                    }else if(list.size()>0)
                        view.toast("UID已被使用");
                    else
                        view.toast("错误,请检查网络");
                }
            });
        }
    }
}

package com.lin.meet.main.fragment.Home;

import com.lin.meet.bean.User;
import com.lin.meet.bean.topic_islike;
import com.lin.meet.bean.topic_main;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class TopicPresenter implements HomeConstract.TopicPresenter{
    private int allPage = 0;//已经加载数量
    private int page = 10;//每次加载的数量
    private HomeConstract.TopicView view;
    private boolean loading = false;
    private final int FLAG_INIT = 0,FLAG_INSERT = 1;
    TopicPresenter(HomeConstract.TopicView view){
        this.view = view;
    }

    @Override
    public void onInitTopics(int flag) {
        allPage = 0;
        view.initTopicAdapter();
        onLoadTopics(flag);
    }

    @Override
    public void onInsertTopics() {
        onLoadTopics(0);
    }

    @Override
    public void onInsertTopic(String id) {
        BmobQuery<topic_main> query = new BmobQuery<>();
        query.addWhereEqualTo("id",id);
        query.findObjects(new FindListener<topic_main>() {
            @Override
            public void done(List<topic_main> list, BmobException e) {
                if(e == null&&list.size()>0){
                    TopicAdapter.TopicBean bean = new TopicAdapter.TopicBean(list.get(0));
                    onLoadUser(bean,1);
                }
            }
        });
    }

    @Override
    public void onClickLike(int position,String id) {
        if(!BmobUser.isLogin()){
            return;
        }
        BmobQuery<topic_islike> query = new BmobQuery<>();
        query.addWhereEqualTo("id",id);
        query.addWhereEqualTo("uid", BmobUser.getCurrentUser(User.class).getUid());
        query.findObjects(new FindListener<topic_islike>() {
            @Override
            public void done(List<topic_islike> list, BmobException e) {
                if(e==null&&list.size()==0){
                    insertLike(id,position);
                }
                else if(e==null){
                    deleteLike(list.get(0),position);
                }
                else {

                }
            }
        });
    }

    @Override
    public void onInsertToTop() {
        if(loading)return;
        loading = true;
        BmobQuery<topic_main> query = new BmobQuery<>();
        query.order("-updatedAt");
        query.setLimit(page);
        query.setSkip(allPage);
        query.findObjects(new FindListener<topic_main>() {
            @Override
            public void done(List<topic_main> list, BmobException e) {
                if(e == null&&list.size()>0){
                    allPage += list.size();
                    for(int i=0;i<list.size();i++){
                        TopicAdapter.TopicBean bean = new TopicAdapter.TopicBean(list.get(i));
                        onLoadUserTop(bean,i);
                    }
                }
                view.endRefresh();
                loading = false;
            }
        });
    }

    private void onLoadTopics(int flag){
        if(loading)return;
        loading = true;
        BmobQuery<topic_main> query = new BmobQuery<>();
        query.order("-updatedAt");
        query.setLimit(page);
        query.setSkip(allPage);
        query.findObjects(new FindListener<topic_main>() {
            @Override
            public void done(List<topic_main> list, BmobException e) {
                if(e == null&&list.size()>0){
                    allPage += list.size();
                    for(int i=0;i<list.size();i++){
                        TopicAdapter.TopicBean bean = new TopicAdapter.TopicBean(list.get(i));
                        onLoadUser(bean,flag);
                    }
                    if(flag == 2)
                        view.endRefresh();
                }
                loading = false;
            }
        });
    }


    private void onLoadUser(TopicAdapter.TopicBean bean,int flag){
        BmobQuery<User> query = new BmobQuery<>();
        query.addWhereEqualTo("uid",bean.bean.getUid());
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if(e == null&&list.size()>0){
                    bean.nickName = list.get(0).getNickName();
                    bean.header = list.get(0).getHeaderUri();
                    if(flag==1){
                        view.insertTopic(0,bean);
                    }else{
                        int position = view.insertTopic(bean);
                        onGetUserLike(position,bean.bean.getId());
                    }
                }
            }
        });
    }

    private void onLoadUserTop(TopicAdapter.TopicBean bean,int position){
        BmobQuery<User> query = new BmobQuery<>();
        query.addWhereEqualTo("uid",bean.bean.getUid());
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if(e == null&&list.size()>0){
                    bean.nickName = list.get(0).getNickName();
                    bean.header = list.get(0).getHeaderUri();
                    view.insertTopic(position,bean);
                    onGetUserLike(position,bean.bean.getId());

                }
            }
        });
    }

    private void onGetUserLike(int position,String id){
        if(!BmobUser.isLogin())
            return;
        BmobQuery<topic_islike> query = new BmobQuery<>();
        query.addWhereEqualTo("id",id);
        query.addWhereEqualTo("uid", BmobUser.getCurrentUser(User.class).getUid());
        query.findObjects(new FindListener<topic_islike>() {
            @Override
            public void done(List<topic_islike> list, BmobException e) {
                if(list.size()>0&&e==null){
                    view.setLike(position,true);
                }
            }
        });
    }



    private void deleteLike(topic_islike like,int position){
        like.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    view.likeResult(1,position,false);
                }
            }
        });
    }

    private void insertLike(String id,int position){
        topic_islike like = new topic_islike();
        like.setId(id);
        like.setIslike(true);
        like.setUid(BmobUser.getCurrentUser(User.class).getUid());
        like.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(e==null){
                    view.likeResult(1,position,true);
                }
            }
        });
    }
}

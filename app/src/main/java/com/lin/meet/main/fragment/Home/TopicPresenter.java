package com.lin.meet.main.fragment.Home;

import com.lin.meet.bean.User;
import com.lin.meet.db_bean.comment_like;
import com.lin.meet.db_bean.topic_main;

import java.util.ArrayList;
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
                }else{
                    view.endRefresh();
                }
            }
        });
    }


    @Override
    public void onClickLike(boolean like,String id,String uid,int position){
        if(!BmobUser.isLogin())
            return;
        List queryList =new ArrayList<BmobQuery<comment_like>>();
        BmobQuery query1 = new BmobQuery<comment_like>();
        query1.addWhereEqualTo("uid",BmobUser.getCurrentUser(User.class).getUid());
        queryList.add(query1);
        BmobQuery query2 =new BmobQuery<comment_like>();
        query2.addWhereEqualTo("parentId",id);
        queryList.add(query2);
        BmobQuery query4 = new BmobQuery<comment_like>();
        query4.addWhereEqualTo("mainId",id);
        queryList.add(query4);
        BmobQuery query5 = new BmobQuery<comment_like>();
        query5.addWhereEqualTo("isMain",true);
        queryList.add(query5);
        BmobQuery query6 = new BmobQuery<comment_like>();
        query6.addWhereEqualTo("flag",1);
        queryList.add(query6);
        BmobQuery query = new BmobQuery<comment_like>();
        query.and(queryList);
        query.findObjects(new FindListener<comment_like>() {
            @Override
            public void done(List<comment_like> list, BmobException e) {
                if(list != null&&list.size()>0&&e==null&&!like){
                    deleteLike(list.get(0),position);
                }else if(list != null&&list.size()==0&&e==null&&like){
                    insertLike(id,uid,position);
                }
            }
        });
    }

    @Override
    public void onInsertToTop() {
        if(loading)return;
        loading = true;
        view.isNetError(false);
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
                view.endLoadMore();
                view.endRefresh();
                loading = false;
            }
        });
    }

    private void onLoadTopics(int flag){
        if(loading)return;
        loading = true;
        view.isNetError(false);
        BmobQuery<topic_main> query = new BmobQuery<>();
        query.order("-updatedAt");
        query.setLimit(page);
        query.setSkip(allPage);
        query.findObjects(new FindListener<topic_main>() {
            @Override
            public void done(List<topic_main> list, BmobException e) {
                if(e == null&&list.size()>0){
                    view.isNetError(false);
                    allPage += list.size();
                    for(int i=0;i<list.size();i++){
                        TopicAdapter.TopicBean bean = new TopicAdapter.TopicBean(list.get(i));
                        onLoadUser(bean,flag);
                    }
                }
                if(e!=null&&flag==2)
                    view.isNetError(true);
                view.endLoadMore();
                view.endRefresh();
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
        List queryList =new ArrayList<BmobQuery<comment_like>>();
        BmobQuery query1 = new BmobQuery<comment_like>();
        query1.addWhereEqualTo("uid",BmobUser.getCurrentUser(User.class).getUid());
        queryList.add(query1);
        BmobQuery query2 =new BmobQuery<comment_like>();
        query2.addWhereEqualTo("parentId",id);
        queryList.add(query2);
        BmobQuery query4 = new BmobQuery<comment_like>();
        query4.addWhereEqualTo("mainId",id);
        queryList.add(query4);
        BmobQuery query5 = new BmobQuery<comment_like>();
        query5.addWhereEqualTo("isMain",true);
        queryList.add(query5);
        BmobQuery query6 = new BmobQuery<comment_like>();
        query6.addWhereEqualTo("flag",1);
        queryList.add(query6);
        BmobQuery query = new BmobQuery<comment_like>();
        query.and(queryList);
        query.findObjects(new FindListener<comment_like>() {
            @Override
            public void done(List<comment_like> list, BmobException e) {
                if(list.size()>0&&e==null){
                    view.setLike(position,true);
                }
            }
        });
    }



    private void deleteLike(comment_like like,int position){
        like.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    view.likeResult(1,position,false);
                }
            }
        });
    }

    private void insertLike(String id,String uid,int position){
        comment_like like = comment_like.createMainLike(uid,id,1);
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

package com.lin.meet.main;

import com.lin.meet.camera_demo.PhotoBean;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.util.List;

public class DataBaseModel implements DataBase {

    @Override
    public void init() {
        LitePal.getDatabase();
    }

    @Override
    public void addData(PhotoBean bean) {
        bean.save();
    }

    @Override
    public void deletePhotoData(String fileName) {
        DataSupport.deleteAll(PhotoBean.class,"path = ?",fileName);
    }

    @Override
    public void deletePhotoAll() {
        DataSupport.deleteAll(PhotoBean.class);
    }

    @Override
    public List<PhotoBean> findAllPhoto() {
        List<PhotoBean> list = DataSupport.order("time desc").find(PhotoBean.class);
        return list;
    }

    @Override
    public PhotoBean findPhoto(String fileName) {
        List<PhotoBean> list = DataSupport.where("path = ?",fileName).find(PhotoBean.class);
        return list.get(0);
    }


}

package com.lin.meet.main;

import com.lin.meet.camera_demo.PhotoBean;

import java.util.List;

public interface DataBase {
    void init();
    void addData(PhotoBean bean);
    void deletePhotoData(String fileName);
    void deletePhotoAll();
    List<PhotoBean> findAllPhoto();
    PhotoBean findPhoto(String fileName);
}

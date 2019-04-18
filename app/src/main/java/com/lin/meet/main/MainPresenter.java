package com.lin.meet.main;

import org.jetbrains.annotations.NotNull;

import java.io.File;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;

public class MainPresenter implements MainConstract.Presenter {
    private MainConstract.View view;

    MainPresenter(MainConstract.View view){
        this.view = view;
    }

    @Override
    public void downloadToCache(@NotNull String uri, @NotNull String fileName, int result) {
        File file = new File(MainActivity.savePath,fileName);
        BmobFile bmobFile = new BmobFile(fileName, "", uri);
        bmobFile.download(new DownloadFileListener() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    view.updateImageView(result,s);
                } else {

                }
            }

            @Override
            public void onProgress(Integer integer, long l) {

            }
        });
    }
}

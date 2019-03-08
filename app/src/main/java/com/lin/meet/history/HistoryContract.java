package com.lin.meet.history;

import android.app.Activity;

public interface HistoryContract {
    public interface View{
        void showDeleteDialog();
        void showLoadingDialog();
        void setCount(int count);
        void showActionButtonDelete();
        void showActionButtonClose();
        void clickFB();
        void showDialog(int id);
    }

    public interface Presenter{
        void deletePhoto();
        void openDelete();
        void closeDelete();
        void doSelectAll();
        void doCloseAll();
    }

    public interface Model{
    }

    public interface Adapter{
        void deletePhoto();
        void doSelectAll();
        void doDelete(boolean select);
        void doCloseAll();
        void insertItem(final Activity activity);
        int getCount();
        boolean isSelect();
    }
}

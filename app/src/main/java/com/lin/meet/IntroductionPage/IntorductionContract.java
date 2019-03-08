package com.lin.meet.IntroductionPage;

import android.app.Activity;
import android.net.Uri;

public interface IntorductionContract {
    public interface View{
        void setImageView(Uri uri);
        void setImageView(String path);
        void setImageView_1(String path);
        void setImageView_2(String path);
        void setImageView_3(String path);
        void setAnimal_1(String text);
        void setAnimal_2(String text);
        void setAnimal_3(String text);
        void setProbability_1(String text);
        void setProbability_2(String text);
        void setProbability_3(String text);
        void setContent_1(String text);
        void setContent_2(String text);
        void setContent_3(String text);
        void setId(int a,int b ,int c);
        void openPhoto(int requestCode);
        void showMapDialog(int id);
    }

    public interface Presenter{
        void openPhoto();
        void intoEncy(Activity activity, int id);
        long doIdentification(String path);
        void doIdentification(int id[],float maybe[]);
        void doIdentification(int id[],float maybe[],int swap);
        void updateResult(long time,String newName);
        void swapCard(int item,int id[],float[] maybe,int random);
    }

    public interface Model{
        void updateResult(long time,String newName);
        long savePhoto(String path,String meybe);
    }

}

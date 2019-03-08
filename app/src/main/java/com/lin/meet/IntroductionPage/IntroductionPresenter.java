package com.lin.meet.IntroductionPage;

import android.app.Activity;
import android.content.Context;

import com.lin.meet.encyclopedia.EncyclopediaActivity;
import com.lin.meet.my_util.TFLiteUtil;

import java.util.Random;

public class IntroductionPresenter implements IntorductionContract.Presenter {
    IntorductionContract.View view;
    IntorductionContract.Model model;
    TFLiteUtil tflite;
    public IntroductionPresenter(IntorductionContract.View view, Context context){
        this.view = view;
        model = new IntroductionModel(context);
        tflite=new TFLiteUtil(context);
        tflite.init();
        tflite.readCacheLabelFromUrl();
    }
    @Override
    public void openPhoto() {

    }

    @Override
    public void intoEncy(Activity activity, int id) {
        EncyclopediaActivity.openEncyclopedia(activity,EncyclopediaActivity.getDatas
                (tflite.myUrl[id*3-2],tflite.myUrl[id*3-1],tflite.myUrl[id*3],tflite.mylabel[id],tflite.elabel[id],"https://baike.baidu.com/"));
    }

    @Override
    public long doIdentification(String path) {
        Random r = new Random();
        int x = r.nextInt(3);
        float temp[][]=tflite.predict_image(path);
        view.setAnimal_1(tflite.mylabel[(int)temp[0][1]]);
        view.setContent_1(tflite.mylabel[(int)temp[0][1]]);
        view.setProbability_1(String.format("%.2f",(temp[1][1]*100))+"%");
        view.setImageView_1(tflite.myUrl[((int)temp[0][1])*3-x]);

        view.setAnimal_2(tflite.mylabel[(int)temp[0][2]]);
        view.setContent_2(tflite.mylabel[(int)temp[0][2]]);
        view.setProbability_2(String.format("%.2f",(temp[1][2]*100))+"%");
        view.setImageView_2(tflite.myUrl[((int)temp[0][2])*3-x]);

        view.setAnimal_3(tflite.mylabel[(int)temp[0][3]]);
        view.setContent_3(tflite.mylabel[(int)temp[0][3]]);
        view.setProbability_3(String.format("%.2f",(temp[1][3]*100))+"%");
        view.setImageView_3(tflite.myUrl[((int)temp[0][3])*3-x]);
        view.setId((int)temp[0][1],(int)temp[0][2],(int)temp[0][3]);
        return model.savePhoto(path,tflite.mylabel[(int)temp[0][1]]);
    }

    @Override
    public void doIdentification(int id[],float maybe[]) {
        doIdentification(id,maybe,-1);
    }

    @Override
    public void doIdentification(int[] id, float[] maybe, int swap) {
        Random r = new Random();
        int x = r.nextInt(3);
        view.setAnimal_1(tflite.mylabel[id[0]]);
        view.setContent_1(tflite.mylabel[id[0]]);
        view.setProbability_1(String.format("%.2f",(maybe[0]*100))+"%");
        view.setImageView_1(tflite.myUrl[id[0]*3-x]);

        view.setAnimal_2(tflite.mylabel[id[1]]);
        view.setContent_2(tflite.mylabel[id[1]]);
        view.setProbability_2(String.format("%.2f",(maybe[1]*100))+"%");
        view.setImageView_2(tflite.myUrl[id[1]*3-x]);

        view.setAnimal_3(tflite.mylabel[id[2]]);
        view.setContent_3(tflite.mylabel[id[2]]);
        view.setProbability_3(String.format("%.2f",(maybe[2]*100))+"%");
        view.setImageView_3(tflite.myUrl[id[2]*3-x]);
        view.setId(id[0],id[1],id[2]);

        if(swap>0)
            swapCard(swap,id,maybe,x);

    }

    @Override
    public void updateResult(long time, String newName) {
        model.updateResult(time,newName);
    }

    @Override
    public void swapCard(int item,int id[],float[] maybe,int random) {
        if (item==0)
            return;
        String tempName = tflite.mylabel[id[0]];
        String tempUrl = tflite.myUrl[id[0]*3-random];
        String tempMaybe = String.format("%.2f",(maybe[0]*100))+"%";
        view.setAnimal_1(tflite.mylabel[id[item]]);
        view.setContent_1(tflite.mylabel[id[item]]);
        view.setProbability_1(String.format("%.2f",(maybe[item]*100))+"%");
        view.setImageView_1(tflite.myUrl[id[item]*3-random]);

        if(item==1){
            view.setAnimal_2(tempName);
            view.setContent_2(tempName);
            view.setProbability_2(tempMaybe);
            view.setImageView_2(tempUrl);
        }else {
            view.setAnimal_3(tempName);
            view.setContent_3(tempName);
            view.setProbability_3(tempMaybe);
            view.setImageView_3(tempUrl);
        }
    }
}

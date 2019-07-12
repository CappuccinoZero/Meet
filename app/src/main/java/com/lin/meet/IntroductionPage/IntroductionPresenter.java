package com.lin.meet.IntroductionPage;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.ActivityOptionsCompat;

import com.lin.meet.bean.Baike;
import com.lin.meet.bean.MapFlag;
import com.lin.meet.bean.User;
import com.lin.meet.encyclopedia.EncyclopediaActivity;
import com.lin.meet.my_util.TFLiteUtil;

import java.io.File;
import java.util.List;
import java.util.Random;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

import static com.lin.meet.db_bean.CreateID.getId;

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
    public void intoEncy(Activity activity, int id,String uri,String type,ActivityOptionsCompat compat) {
        EncyclopediaActivity.openEncyclopedia(activity,EncyclopediaActivity.getDatas
                (tflite.myUrl[id*3-2],tflite.myUrl[id*3-1],tflite.myUrl[id*3],tflite.mylabel[id],tflite.elabel[id],"https://baike.baidu.com/")
        ,uri,type,compat);
    }

    @Override
    public long doIdentification(String path,String path2) {
        Random r = new Random();
        int x = r.nextInt(3);
        float temp[][]=tflite.predict_image(path);
        view.setAnimal_1(tflite.mylabel[(int)temp[0][1]]);
        view.setProbability_1(String.format("%.2f",(temp[1][1]*100))+"%");
        view.setImageView_1(tflite.myUrl[((int)temp[0][1])*3-x]);

        view.setAnimal_2(tflite.mylabel[(int)temp[0][2]]);
        view.setProbability_2(String.format("%.2f",(temp[1][2]*100))+"%");
        view.setImageView_2(tflite.myUrl[((int)temp[0][2])*3-x]);

        view.setAnimal_3(tflite.mylabel[(int)temp[0][3]]);
        view.setProbability_3(String.format("%.2f",(temp[1][3]*100))+"%");
        view.setImageView_3(tflite.myUrl[((int)temp[0][3])*3-x]);
        view.setId((int)temp[0][1],(int)temp[0][2],(int)temp[0][3]);
        view.updateFromNetwork();
        return model.savePhoto(path2,tflite.mylabel[(int)temp[0][1]]);
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
        view.setProbability_1(String.format("%.2f",(maybe[0]*100))+"%");
        view.setImageView_1(tflite.myUrl[id[0]*3-x]);

        view.setAnimal_2(tflite.mylabel[id[1]]);
        view.setProbability_2(String.format("%.2f",(maybe[1]*100))+"%");
        view.setImageView_2(tflite.myUrl[id[1]*3-x]);

        view.setAnimal_3(tflite.mylabel[id[2]]);
        view.setProbability_3(String.format("%.2f",(maybe[2]*100))+"%");
        view.setImageView_3(tflite.myUrl[id[2]*3-x]);
        view.setId(id[0],id[1],id[2]);

        if(swap>0)
            swapCard(swap,id,maybe,x);
        view.updateFromNetwork();

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
            view.setProbability_2(tempMaybe);
            view.setImageView_2(tempUrl);
            view.swap(1);
        }else {
            view.setAnimal_3(tempName);
            view.setProbability_3(tempMaybe);
            view.setImageView_3(tempUrl);
            view.swap(2);
        }
    }

    @Override
    public void updateBaike(int id,int position) {
        BmobQuery<Baike> query = new BmobQuery<>();
        query.addWhereEqualTo("flag",id);
        query.findObjects(new FindListener<Baike>() {
            @Override
            public void done(List<Baike> list, BmobException e) {
                if(e == null&&list.size()>0){
                    if(position==1){
                        view.setUri1(list.get(0).getUri(),list.get(0).getType());
                        view.setContent(list.get(0).getBrief(),1);
                    }
                    else if(position==2){
                        view.setUri2(list.get(0).getUri(),list.get(0).getType());
                        view.setContent(list.get(0).getBrief(),2);
                    }else if(position==3){
                        view.setUri3(list.get(0).getUri(),list.get(0).getType());
                        view.setContent(list.get(0).getBrief(),3);
                    }
                }
            }
        });
    }

    @Override
    public void onFlagMap(String path, String name,String maybe) {
        if(!BmobUser.isLogin()){
            view.saveError("登录后才能标记。");
            return;
        }
        StringBuilder builder = new StringBuilder();
        builder.append("我使用识图功能识别了一种动物\"").append(name).append("\",识别相似度有").append(maybe).append("。");

        File file = new File(path);
        BmobFile bmobFile =new BmobFile(file);
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    saveFlag(bmobFile.getFileUrl(),builder.toString());
                }else {
                    view.saveError("标记失败,请检查网络。");
                }
            }
        });
    }

    public void saveFlag(String image, String content){
        User user = BmobUser.getCurrentUser(User.class);
        MapFlag bean =new MapFlag();
        bean.setX(view.getMapX());
        bean.setY(view.getMapY());
        bean.setContent(content);
        bean.setImage(image);
        bean.setUid(user.getUid());
        bean.setId(getId());
        bean.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(e==null){
                    view.showMapDialog(2);
                }else{
                    view.saveError("标记失败,请检查网络。");
                }
            }
        });
    }
}

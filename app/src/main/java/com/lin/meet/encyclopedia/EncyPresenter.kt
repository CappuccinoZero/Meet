package com.lin.meet.encyclopedia

import com.lin.meet.jsoup.BaikeBean

class EncyPresenter(view:EncyclopediaContract.View):EncyclopediaContract.Presenter {
    override fun initBaike(bean: BaikeBean?) {
        var position = -1
        if(bean!!.flavus!=null){
            position = bean.map[bean.flavus]!!
        }
        view.setSummary(bean.summary)
        var lenth = bean.titles.size
        var flag:Int = -1
        for(index in 0 until lenth){
            if(index < bean.images.size||index == position||flag >= 0){
                var img = ""
                if(index == position && index < bean.images.size){
                    flag = index
                    img = bean.flavus
                }
                else if(index == position){
                    img = bean.flavus
                }
                else if(flag>0 && index > bean.images.size ){
                    //Log.d("测",index)
                    img = bean.images[flag]
                    flag = -1
                }
                else{
                    if(index<bean.images.size)
                        img = bean.images[index]
                }
                view.setBaike(index+1,bean.titles[index],bean.content[index],img)
            }
            else{
                view.setBaike(index+1,bean.titles[index],bean.content[index],null)
            }
        }

        if(bean.images.size > lenth){
            var star = lenth
            if(position != -1)
                star -= 1
            for(index in star until bean.images.size){
                view.setImage(lenth++,bean.images[index])
            }
        }
    }

    private val view = view
}
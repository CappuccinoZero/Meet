package com.lin.meet.picture_observer
class ObserverPresenter(view:ObserverContract.View):ObserverContract.Presenter {
    val view = view
    override fun initAuthorMessage(uid: String) {
        if(uid == "@Meet")
            view.updateAhthor(null,false)
    }

    override fun downloadPicture() {

    }
}
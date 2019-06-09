package com.lin.meet.personal

class PersonalPresenter(view:PersonalContract.View):PersonalContract.Presenter{
    private var view:PersonalContract.View
    init{
        this.view = view
    }
}
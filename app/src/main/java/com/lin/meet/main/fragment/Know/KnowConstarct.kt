package com.lin.meet.main.fragment.Know

interface KnowConstarct {
    interface View{
        fun refreshAdapter()
        fun insertKnow(bean:KnowAndUser)
        fun insertKnow(position:Int,bean:KnowAndUser)
        fun endRefresh()
    }

    interface Presenter{
        fun refreshKnows()
        fun insetKnow()
        fun insertKnow(id:String)
    }
}
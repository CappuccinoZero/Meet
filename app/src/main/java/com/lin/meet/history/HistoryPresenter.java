package com.lin.meet.history;

public class HistoryPresenter implements HistoryContract.Presenter {
    private HistoryContract.View mView;
    private HistoryContract.Adapter mAdapter;
    public HistoryPresenter(HistoryContract.View view,HistoryContract.Adapter adapter){
        this.mView=view;
        this.mAdapter=adapter;
    }

    @Override
    public void deletePhoto() {
        if(mAdapter.isSelect())
            mView.showDeleteDialog();
    }

    @Override
    public void openDelete() {
        mView.showActionButtonDelete();
        mAdapter.doDelete(true);
    }

    @Override
    public void closeDelete() {
        mView.showActionButtonClose();
        mAdapter.doDelete(false);
    }

    @Override
    public void doSelectAll() {
        mAdapter.doSelectAll();
        mView.setCount(mAdapter.getCount());
    }

    @Override
    public void doCloseAll() {
        mAdapter.doCloseAll();
        mView.setCount(mAdapter.getCount());
    }
}

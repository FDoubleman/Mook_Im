package com.cloud.factory.presenter.message;

import android.support.v7.util.DiffUtil;

import com.cloud.factory.data.massage.SessionDataSource;
import com.cloud.factory.data.massage.SessionRepository;
import com.cloud.factory.model.db.Session;
import com.cloud.factory.presenter.BaseSourcePresenter;
import com.cloud.factory.util.DiffUiDataCallback;

import java.util.List;

/**
 * Created by fmm on 2018/1/2.
 */

public class SessionPresenter
        extends BaseSourcePresenter<Session,Session,SessionDataSource,SessionContract.View>
        implements SessionContract.Presenter {

    public SessionPresenter(SessionContract.View view) {
        super(new SessionRepository(), view);
    }

    @Override
    public void onDataLoaded(List<Session> sessions) {
        SessionContract.View view =getView();
        if(view ==null){
            return;
        }
        //差异对比
        List<Session> oldList = view.getRecycleAdapter().getItems();
        DiffUiDataCallback callback = new DiffUiDataCallback<>(oldList,sessions);
        DiffUtil.DiffResult result =DiffUtil.calculateDiff(callback);
        //更新界面
        refreshData(result,sessions);
    }
}

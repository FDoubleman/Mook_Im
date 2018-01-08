package com.cloud.factory.presenter.group;

import android.support.v7.util.DiffUtil;

import com.cloud.factory.data.helper.GroupHelper;
import com.cloud.factory.model.api.group.GroupsDataSource;
import com.cloud.factory.model.api.group.GroupsRepository;
import com.cloud.factory.model.db.Group;
import com.cloud.factory.presenter.BaseSourcePresenter;
import com.cloud.factory.util.DiffUiDataCallback;

import java.util.List;

/**
 * Created by fmm on 2018/1/8.
 *
 */

public class GroupsPresenter extends
        BaseSourcePresenter<Group,Group,GroupsDataSource,GroupsContract.View>
        implements GroupsContract.Presenter {

    public GroupsPresenter( GroupsContract.View view) {
        super(new GroupsRepository(), view);
    }

    @Override
    public void start() {
        super.start();

        // 加载网络数据, 以后可以优化到下拉刷新中
        // 只有用户下拉进行网络请求刷新
        GroupHelper.refreshGroups();
    }


    @Override
    public void onDataLoaded(List<Group> groups) {
       GroupsContract.View view = getView();
       if(view ==null){
           return;
       }

        // 对比差异
        List<Group> old = view.getRecycleAdapter().getItems();
        DiffUiDataCallback<Group> callback = new DiffUiDataCallback<>(old, groups);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);

        // 界面刷新
        refreshData(result, groups);
    }
}

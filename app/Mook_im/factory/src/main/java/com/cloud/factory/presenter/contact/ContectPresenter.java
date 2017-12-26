package com.cloud.factory.presenter.contact;

import android.support.v7.util.DiffUtil;

import com.cloud.common.widget.recycler.RecyclerAdapter;
import com.cloud.factory.data.DataSource;
import com.cloud.factory.data.helper.UserHelper;
import com.cloud.factory.data.user.ContactDataSource;
import com.cloud.factory.data.user.ContactRepository;
import com.cloud.factory.model.db.User;
import com.cloud.factory.presenter.BaseSourcePresenter;
import com.cloud.factory.util.DiffUiDataCallback;

import java.util.List;

/**
 * Created by fmm on 2017/10/11.
 *
 */

public class ContectPresenter extends BaseSourcePresenter<User, User, ContactDataSource, ContactContract.View>
        implements ContactContract.Presenter, DataSource.SucceedCallback<List<User>> {


    public ContectPresenter(ContactContract.View view) {
        super(new ContactRepository(), view);
    }

    @Override
    public void start() {
        super.start();
        // 加载网络数据
        UserHelper.refreshContacts();
    }


    @Override
    public void onDataLoaded(List<User> users) {
    // 无论怎么操作，数据变更，最终都会通知到这里来
        final ContactContract.View view = getView();
        if (view == null)
            return;

        RecyclerAdapter<User> adapter = view.getRecycleAdapter();
        List<User> old = adapter.getItems();

        // 进行数据对比
        DiffUtil.Callback callback = new DiffUiDataCallback<>(old, users);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);

        // 调用基类方法进行界面刷新
        refreshData(result, users);
    }
}

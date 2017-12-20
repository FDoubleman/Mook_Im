package com.cloud.factory.presenter.contact;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.util.DiffUtil;

import com.cloud.factory.data.DataSource;
import com.cloud.factory.data.helper.UserHelper;
import com.cloud.factory.model.card.UserCard;
import com.cloud.factory.model.db.AppDatabase;
import com.cloud.factory.model.db.User;
import com.cloud.factory.model.db.User_Table;
import com.cloud.factory.persistence.Account;
import com.cloud.factory.presenter.BasePresenter;
import com.cloud.factory.util.DiffUiDataCallback;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fmm on 2017/10/11.
 *
 */

public class ContectPresenter extends BasePresenter<ContectContract.View>
        implements ContectContract.Presenter {


    public ContectPresenter(ContectContract.View view) {
        super(view);
    }

    @Override
    public void start() {
        super.start();
        //1、加载本地数据
        SQLite.select()
                .from(User.class)
                .where(User_Table.isFollow.eq(true))
                .and(User_Table.id.notEq(Account.getUserId()))
                .orderBy(User_Table.name,true)
                .limit(100)
                .async()
                .queryListResultCallback(new QueryTransaction.QueryResultListCallback<User>() {
                    @Override
                    public void onListQueryResult(QueryTransaction transaction,
                                                  @NonNull List<User> tResult) {
                        getView().getRecycleAdapter().replace(tResult);
                        getView().onAdapterDataChanged();
                    }
                }).execute();

        //2、从网络获得联系人数据
        UserHelper.refreshContacts(new DataSource.CallBack<List<UserCard>>() {
            @Override
            public void onDataNotLoad(@StringRes int strRes) {
                // 网络失败，因为本地有数据，不管错误
            }

            @Override
            public void onDataLoad(List<UserCard> user) {
                //1、将Usecard 保存至数据库
                final List<User> userList = new ArrayList<User>();
                for (UserCard userCard : user) {
                    userList.add(userCard.build());
                }
                DatabaseDefinition definition = FlowManager.getDatabase(AppDatabase.class);
                definition.beginTransactionAsync(new ITransaction() {
                    @Override
                    public void execute(DatabaseWrapper databaseWrapper) {
                        FlowManager.getModelAdapter(User.class)
                                .saveAll(userList);

                    }
                }).build().execute();

                //2、刷新UI
                List<User> old = getView().getRecycleAdapter().getItems();

                diff(old,userList);
                // TODO 问题：
                // 1.关注后虽然存储数据库，但是没有刷新联系人
                // 2.如果刷新数据库，或者从网络刷新，最终刷新的时候是全局刷新
                // 3.本地刷新和网络刷新，在添加到界面的时候会有可能冲突；导致数据显示异常
                // 4.如何识别已经在数据库中有这样的数据了
            }
        });
    }

    private void diff(List<User> oldList ,List<User> newList){
        // 进行数据对比
        DiffUiDataCallback callback = new DiffUiDataCallback(oldList,newList);
        DiffUtil.DiffResult result =  DiffUtil.calculateDiff(callback);

            // 在对比完成后进行数据的赋值
        getView().getRecycleAdapter().replace(newList);
        // 尝试刷新界面
        result.dispatchUpdatesTo(getView().getRecycleAdapter());
        getView().onAdapterDataChanged();
    }
}

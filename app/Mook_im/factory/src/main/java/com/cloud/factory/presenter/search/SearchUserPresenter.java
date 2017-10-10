package com.cloud.factory.presenter.search;

import android.support.annotation.StringRes;

import com.cloud.factory.data.DataSource;
import com.cloud.factory.data.helper.UserHelper;
import com.cloud.factory.model.card.UserCard;
import com.cloud.factory.presenter.BasePresenter;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import java.util.List;

import retrofit2.Call;

/**
 * Created by fmm on 2017/10/5.
 */

public class SearchUserPresenter extends BasePresenter<SearchContract.UserView>
        implements SearchContract.SearchPresenter,DataSource.CallBack<List<UserCard>> {
    private Call searchCall;

    public SearchUserPresenter(SearchContract.UserView view) {
        super(view);
    }

    @Override
    public void search(String content) {
        //开始请求
        start();

        Call call=searchCall;
        if(call !=null && !call.isCanceled()){
            // 如果有上一次的请求，并且没有取消，
            // 则调用取消请求操作
            call.cancel();
        }
        searchCall = UserHelper.search(content,this);
    }

    @Override
    public void onDataLoad(final List<UserCard> user) {
        //搜索成功
        final SearchContract.UserView userView = getView();
        if(userView!=null){
            Run.onUiAsync(new Action() {
                @Override
                public void call() {
                    userView.onSearchDone(user);
                }
            });
        }

    }

    @Override
    public void onDataNotLoad(@StringRes final int strRes) {
        //搜索失败
        final SearchContract.UserView userView = getView();
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                userView.showError(strRes);
            }
        });
    }
}

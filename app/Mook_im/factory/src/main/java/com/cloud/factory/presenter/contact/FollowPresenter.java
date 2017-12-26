package com.cloud.factory.presenter.contact;

import com.cloud.factory.data.DataSource;
import com.cloud.factory.data.helper.UserHelper;
import com.cloud.factory.model.card.UserCard;
import com.cloud.factory.presenter.BasePresenter;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

/**
 * Created by fmm on 2017/10/11.
 */

public class FollowPresenter extends BasePresenter<FollowContract.FollowView>
        implements FollowContract.FollowPresenter ,DataSource.Callback<UserCard> {


    public FollowPresenter(FollowContract.FollowView view) {
        super(view);
    }

    @Override
    public void followUser(String followId) {
        start();
        UserHelper.follow(followId,this);
    }


    @Override
    public void onDataLoaded(final UserCard userCard) {
        final FollowContract.FollowView view = getView();
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                view.followSuccess(userCard);
            }
        });
    }

    @Override
    public void onDataNotAvailable(final int strRes) {
        final FollowContract.FollowView view = getView();
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                view.showError(strRes);
            }
        });
    }
}

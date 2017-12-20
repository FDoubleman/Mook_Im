package com.cloud.factory.presenter.contact;

import com.cloud.factory.model.card.UserCard;
import com.cloud.factory.presenter.BaseContract;

/**
 * Created by fmm on 2017/10/11.
 */

public interface FollowContract {

    //执行网络请求 关注
    public interface FollowPresenter extends BaseContract.Presenter{
        void followUser(String followId);
    }

    //网络请求成功 具体回调
    interface FollowView extends BaseContract.View<FollowPresenter>{
        void followSuccess(UserCard userCard);
    }
}

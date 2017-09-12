package com.cloud.factory.presenter.user;

import com.cloud.factory.presenter.BaseContract;

/**
 * Created by fmm on 2017/9/8.
 */

public interface UpdateInfoContract {

    interface View extends BaseContract.View<Presenter>{
        void updateSeccess();
    }

    interface Presenter extends BaseContract.Presenter{
        void update(String portrait,String desc,boolean isMan);
    }
}

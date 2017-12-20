package com.cloud.factory.presenter.contact;

import com.cloud.factory.model.db.User;
import com.cloud.factory.presenter.BaseContract;

/**
 * Created by fmm on 2017/10/12.
 */

public interface PersonalContract {

    //请求网络层
    interface Presenter extends BaseContract.Presenter{
        //获得个人信息
        User getUserPersonal();
    }

    //获得网络数据后 回调
    interface View extends BaseContract.View<Presenter>{
        //获取ID
        String getUserId();
        //数据加载完成
        void onLoadDone(User user);

        // 是否发起聊天
        void allowSayHello(boolean isAllow);

        // 设置关注状态
        void setFollowStatus(boolean isFollow);
    }
}

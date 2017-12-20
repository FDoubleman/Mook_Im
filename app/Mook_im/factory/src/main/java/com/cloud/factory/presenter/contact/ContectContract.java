package com.cloud.factory.presenter.contact;

import com.cloud.factory.model.db.User;
import com.cloud.factory.presenter.BaseContract;

/**
 * Created by fmm on 2017/10/11.
 */

public interface ContectContract extends BaseContract {

    // 什么都不需要额外定义，开始就是调用start即可
     interface Presenter extends BaseContract.Presenter{

    }

    // 都在基类完成了
     interface View extends BaseContract.RecyclerView<Presenter,User>{

    }
}

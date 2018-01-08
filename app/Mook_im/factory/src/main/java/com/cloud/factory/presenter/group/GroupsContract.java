package com.cloud.factory.presenter.group;

import com.cloud.factory.model.db.Group;
import com.cloud.factory.presenter.BaseContract;

/**
 * Created by fmm on 2018/1/8.
 * 我的群列表契约
 */

public interface GroupsContract {
    // 什么都不需要额外定义，开始就是调用start即可
    interface Presenter extends BaseContract.Presenter{

    }
    // 都在基类完成了
    interface View extends BaseContract.RecyclerView<Presenter,Group>{

    }

}

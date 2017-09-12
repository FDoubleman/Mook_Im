package com.cloud.im;

import com.cloud.common.base.BaseApplication;
import com.cloud.factory.Factory;
import com.igexin.sdk.PushManager;

/**
 * Created by fmm on 2017/8/21.
 */

public class App extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        //调用 factory 进行初始化
        Factory.setUp();
        //初始化推送服务
        PushManager.getInstance().initialize(this);

    }
}

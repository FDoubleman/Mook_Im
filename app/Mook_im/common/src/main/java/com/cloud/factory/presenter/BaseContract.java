package com.cloud.factory.presenter;

import android.support.annotation.StringRes;

import com.cloud.common.widget.recycler.RecyclerAdapter;

/**
 * Created by fmm on 2017/9/4.
 */

public interface BaseContract {

     interface View< T extends Presenter>{

        //公共 显示错误提示
        void showError(@StringRes int msg);

        //公共 显示加载中
        void onLoading();

        //公共 设置presenter
        void setPresenter(T presenter);
    }

     interface Presenter{
         // 共用的开始触发
         void start();

         // 共用的销毁触发
         void destroy();
    }

    interface RecyclerView<T extends Presenter ,ViewMode> extends View<T>{

        // 界面端只能刷新整个数据集合，不能精确到每一条数据更新
        // void onDone(List<User> users);

        // 拿到一个适配器，然后自己自主的进行刷新
        //获得一个适配器
        RecyclerAdapter<ViewMode> getRecycleAdapter();

        // 当适配器数据更改了的时候触发
        void onAdapterDataChanged();
    }

}

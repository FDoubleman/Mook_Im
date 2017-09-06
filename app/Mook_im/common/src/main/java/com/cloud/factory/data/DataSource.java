package com.cloud.factory.data;

import android.support.annotation.StringRes;

/**
 * Created by fmm on 2017/9/5.
 */

public interface DataSource {


    interface CallBack<T> extends SuccseeCallBack<T> ,FaileCallBack{

    }

    interface SuccseeCallBack<T>{
        void onDataLoad(T user);
    }

    interface FaileCallBack<T>{
        void onDataNotLoad(@StringRes int strRes);
    }
}

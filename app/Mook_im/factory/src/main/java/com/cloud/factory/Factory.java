package com.cloud.factory;

import com.cloud.common.base.BaseApplication;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by fmm on 2017/8/24.
 */

public class Factory {

    private static Factory instance;
    static {
        instance = new Factory();
    }

    private final ExecutorService executor;

    private Factory(){
        // 新建一个4个线程的线程池
        executor = Executors.newFixedThreadPool(4);
    }

    public static BaseApplication getApp(){
        return BaseApplication.getInstance();
    }

    public static void runOnAsync(Runnable runnable){
        // 拿到单例，拿到线程池，然后异步执行
        instance.executor.submit(runnable);
    }
}

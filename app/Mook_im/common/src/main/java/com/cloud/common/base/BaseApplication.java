package com.cloud.common.base;

import android.app.Application;
import android.os.SystemClock;
import android.support.annotation.StringRes;
import android.widget.Toast;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import java.io.File;

/**
 * Created by fmm on 2017/8/21.
 *
 */

public class BaseApplication extends Application {

    private static BaseApplication instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance =this;
    }

    /**
     * 外部获取单例
     *
     * @return BaseApplication
     */
    public static BaseApplication getInstance(){
        return instance;
    }

    /**
     * 获得缓存路径
     * @return File
     */
    public static File getCacheDirFile(){

        return instance.getCacheDir();
    }

    public static File getPortraitTmpFile(){
        //1、创建文件夹
        File dirfile= getCacheDirFile();
        dirfile.mkdirs();

        //2、删除之前缓存的 文件
        File[] files = dirfile.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                //noinspection ResultOfMethodCallIgnored
                file.delete();
            }
        }

        //3、创建文件
        File portraitFile = new File(dirfile, SystemClock.uptimeMillis()+".jpg");
        System.currentTimeMillis();
        return portraitFile.getAbsoluteFile();
    }

    /**
     * 显示一个Toast
     *
     * @param msg 字符串
     */
    public static void showToast(final String msg) {
        // Toast 只能在主线程中显示，所有需要进行线程转换，
        // 保证一定是在主线程进行的show操作
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                // 这里进行回调的时候一定就是主线程状态了
                Toast.makeText(instance, msg, Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * 显示一个Toast
     *
     * @param msgId 传递的是字符串的资源
     */
    public static void showToast(@StringRes int msgId) {
        showToast(instance.getString(msgId));
    }

}

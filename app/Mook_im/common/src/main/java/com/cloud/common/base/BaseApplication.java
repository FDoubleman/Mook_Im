package com.cloud.common.base;

import android.app.Application;
import android.os.SystemClock;

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
     * 弹出吐司
     * @param msg 字符串
     */
    public static void showToast(String msg){

    }

    /**
     * 弹出吐司
     * @param msID 字符串 id
     */
    public static void showToast(int msID){

    }
}

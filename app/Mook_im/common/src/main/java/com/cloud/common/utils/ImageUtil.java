package com.cloud.common.utils;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;
import com.cloud.common.R;

/**
 * Created by fmm on 2017/7/23.
 */

public class ImageUtil {


    /**
     * 对view 设置背景
     * @param context
     * @param drawRes
     * @param view
     */
    public static void loadViewBg(Context context ,int drawRes,View view){
        Glide.with(context)
                .load(drawRes)
                .centerCrop()
                .into(new ViewTarget<View,GlideDrawable>(view) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        this.view.setBackground(resource);
                    }
                });
    }

    public static void loadView(Context context, String path , ImageView view){
        Glide.with(context)
                .load(path) //路径
                .centerCrop() // 居中剪切
                .diskCacheStrategy(DiskCacheStrategy.NONE) // 不使用缓存，直接从原图加载
                .placeholder(R.color.grey_200) // 默认颜色
                .into(view);
    }

    public static void loadView(Context context, Uri uri , ImageView view){
        Glide.with(context)
                .load(uri) //路径
                .centerCrop() // 居中剪切
                .diskCacheStrategy(DiskCacheStrategy.NONE) // 不使用缓存，直接从原图加载
                .into(view);
    }
}

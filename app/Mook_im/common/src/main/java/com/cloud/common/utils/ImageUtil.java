package com.cloud.common.utils;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;
import com.cloud.common.R;

import net.qiujuer.genius.ui.compat.UiCompat;

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

    public static void loadBgView(final Context context, final int draRes, ImageView imageView, final int colorRes){
        Glide.with(context)
                .load(draRes)
                .centerCrop()
                .into(new ViewTarget<ImageView,GlideDrawable>(imageView) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        //获得 glid 的drawable
                        Drawable drawable = resource.getCurrent();
                        // 使用适配类进行包装
                        drawable = DrawableCompat.wrap(drawable);
                        // 设置给ImageView
                        drawable.setColorFilter(UiCompat.getColor(context.getResources(),
                                colorRes),
                                PorterDuff.Mode.SCREEN);
                        this.view.setImageDrawable(drawable);
                    }
                });
    }
}

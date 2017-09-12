package com.cloud.im;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.Property;
import android.view.View;

import com.cloud.common.base.BaseActivity;
import com.cloud.factory.persistence.Account;
import com.cloud.im.activity.AccountActivity;
import com.cloud.im.activity.MainActivity;
import com.cloud.im.fragments.assist.PermissionsFragment;

import net.qiujuer.genius.res.Resource;
import net.qiujuer.genius.ui.compat.UiCompat;

public class LaunchActivity extends BaseActivity {
    private ColorDrawable mBgDrawable;


    @Override
    public int getContentLayoutId() {
        return R.layout.activity_launch;
    }


    @Override
    protected void initWidget() {
        super.initWidget();
        //获得根布局
       View view =findViewById(R.id.ll_root);
       //获得颜色
       int color = UiCompat.getColor(getResources(),R.color.colorAccent);
        //通过颜色创建draw
        ColorDrawable drawable = new ColorDrawable(color);
        //设置bg
        view.setBackground(drawable);
        mBgDrawable =drawable;

    }

    @Override
    protected void initData() {
        super.initData();
        startAnim(0.5f, new Runnable() {
            @Override
            public void run() {
                //等待获得 pushID
                waitPushReceiverId();
            }
        });
    }

    private void waitPushReceiverId() {
        //1、是否登录
        //2、是否绑定设备
        if(Account.isLogin()){
            // 已经登录情况下，判断是否绑定
            // 如果没有绑定则等待广播接收器进行绑定
            if(Account.isBind()){
                skip();
                return;
            }
        }else{
            //未登录 跳转登录
            //首先获得 设备的pushID
            // TODO: 2017/9/7
            if(!TextUtils.isEmpty(Account.getPushId())){
                skip();
            }
            return;
        }

        //未满足跳转逻辑 继续获取
        // 循环等待
        getWindow().getDecorView()
                .postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        waitPushReceiverId();
                    }
                }, 500);

    }

    private void skip() {
        startAnim(1f, new Runnable() {
            @Override
            public void run() {
                //跳转
                reallSkip();
            }
        });
    }

    private void reallSkip() {
        if (PermissionsFragment.haveAll(this, getSupportFragmentManager())) {
            if(Account.isLogin()){
                MainActivity.show(this);
            }else{
                AccountActivity.show(this);
            }
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 开始一个动画
     * @param endProgress 结束时的进度
     * @param runnable 接口回调
     */
    private void startAnim(float endProgress, final Runnable runnable){
        // 获取一个最终的颜色
        int finalColor = Resource.Color.WHITE; // UiCompat.getColor(getResources(), R.color.white);
        // 运算当前进度的颜色
        ArgbEvaluator evaluator = new ArgbEvaluator();
        int endColor = (int) evaluator.evaluate(endProgress, mBgDrawable.getColor(), finalColor);

        ValueAnimator valueAnimator =ObjectAnimator.ofObject(this,mProperty,evaluator,endColor);
        valueAnimator.setDuration(1500);
        valueAnimator.setIntValues(mBgDrawable.getColor(),finalColor);
        valueAnimator.addListener(new AnimatorListenerAdapter(){
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                runnable.run();
            }
        });
        valueAnimator.start();
    }

    private final Property<LaunchActivity, Object> mProperty
            =new Property<LaunchActivity, Object>(Object.class,"color") {
        @Override
        public Object get(LaunchActivity object) {

            return object.mBgDrawable.getColor();
        }

        @Override
        public void set(LaunchActivity object, Object value) {

            object.mBgDrawable.setColor((Integer)value);
        }
    };

}

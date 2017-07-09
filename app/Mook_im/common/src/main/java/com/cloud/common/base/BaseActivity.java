package com.cloud.common.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by fmm on 2017/7/9.
 */

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWidows();
        if (initArgs(getIntent().getExtras())) {
            // 得到界面Id并设置到Activity界面中
            int layId = getContentLayoutId();
            setContentView(layId);
            initWidget();
            initData();
        } else {
            finish();
        }

    }


    /**
     * 初始化控件
     *
     * @return
     */
    public abstract int getContentLayoutId();

    /**
     * 初始化窗口
     */
    protected void initWidows() {

    }

    /**
     * 初始化相关参数
     *
     * @param bundle 参数Bundle
     * @return 如果参数正确返回True，错误返回False
     */
    protected boolean initArgs(Bundle bundle) {
        return true;
    }

    /**
     * 初始化 控件
     */
    protected void initWidget() {
        ButterKnife.bind(this);
    }

    /**
     * 初始化 数据
     */
    protected void initData() {

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    /**
     * 重写返回键 activity 中要是有fragemnt 返回键回调 fragment 的 onBackPressed
     */
    @Override
    public void onBackPressed() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null && fragments.size() > 0) {
            for (Fragment fragment : fragments) {
                if (fragment instanceof BaseFragment) {
                    if (((BaseFragment) fragment).onBackPressed()) {
                        return;
                    }
                }
            }
        }

        super.onBackPressed();
        finish();
    }
}

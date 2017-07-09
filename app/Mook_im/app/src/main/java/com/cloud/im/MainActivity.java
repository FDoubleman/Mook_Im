package com.cloud.im;

import android.widget.TextView;

import com.cloud.common.base.BaseActivity;

import butterknife.BindView;

public class MainActivity extends BaseActivity {


    @BindView(R.id.tv_test)
    TextView tvTest;

    @Override
    public int getContentLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        tvTest.setText(" 双击 666 ！");
    }

    @Override
    protected void initData() {
        super.initData();

    }
}

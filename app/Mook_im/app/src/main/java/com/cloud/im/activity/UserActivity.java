package com.cloud.im.activity;

import android.content.Context;
import android.content.Intent;
import android.widget.ImageView;

import com.cloud.common.base.BaseActivity;
import com.cloud.common.base.BaseFragment;
import com.cloud.common.utils.ImageUtil;
import com.cloud.im.R;
import com.cloud.im.fragments.user.UpdateInfoFragment;

import butterknife.BindView;

/**
 * Created by fmm on 2017/9/4.
 *
 */

public class UserActivity extends BaseActivity {

    private BaseFragment mCurFragment;
    @BindView(R.id.iv_bg)
    ImageView ivBg;

    public static void show(Context context){
        context.startActivity(new Intent(context,UserActivity.class));
    }

    @Override
    public int getContentLayoutId() {
        return R.layout.activity_user;

    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mCurFragment = new UpdateInfoFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fl_container,
                        mCurFragment,UpdateInfoFragment.Tag)
                .commit();
        // 初始化背景
        ImageUtil.loadBgView(this,R.drawable.bg_src_tianjin,
                ivBg,R.color.colorAccent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCurFragment.onActivityResult(requestCode, resultCode, data);
    }
}

package com.cloud.im;

import com.cloud.common.base.BaseActivity;
import com.cloud.im.fragments.assist.PermissionsFragment;

public class LaunchActivity extends BaseActivity {



    @Override
    public int getContentLayoutId() {
        return R.layout.activity_launch;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (PermissionsFragment.haveAll(this, getSupportFragmentManager())) {
            MainActivity.show(this);
            finish();
        }

    }
}

package com.cloud.common.base;

import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.cloud.common.R;

/**
 * Created by fmm on 2017/10/5.
 */

public abstract class ToolbarActivity extends BaseActivity {
    private Toolbar mToolbar;

    @Override
    protected void initWidget() {
        super.initWidget();
        initToolbar((Toolbar) findViewById(R.id.toolbar));
    }

    public  void initToolbar(Toolbar toolbar){
        mToolbar =toolbar;
        if(mToolbar!=null){
            setSupportActionBar(mToolbar);
        }

        initTitleNeedBack();
    }

    protected  void initTitleNeedBack(){
    ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }


}

package com.cloud.im.fragments.message;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.cloud.common.base.BaseFragment;
import com.cloud.common.widget.adapter.TextWatcherAdapter;
import com.cloud.im.R;
import com.cloud.im.activity.MessageActivity;

import butterknife.BindView;

/**
 * Created by fmm on 2017/12/28.
 * 聊天基类
 */

public abstract class ChatFragment extends BaseFragment implements AppBarLayout.OnOffsetChangedListener {

    protected String mReceiverId;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;

    @BindView(R.id.appbar)
    AppBarLayout mAppBarLayout;

    @BindView(R.id.collapsingToolbarLayout)
    CollapsingToolbarLayout mCollapsingLayout;

    @BindView(R.id.edit_content)
    EditText mContent;

    @BindView(R.id.btn_submit)
    View mSubmit;

    @Override
    protected void initArgs(Bundle bundle) {
        super.initArgs(bundle);
        mReceiverId = bundle.getString(MessageActivity.KEY_RECEIVER_ID);
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        initToolbar();
        initAppBar();
        initEditContent();
    }


    //设置toolbar 返回键
    protected  void initToolbar(){
        Toolbar toolbar =mToolbar;
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }

    //设置appbar 折叠监听
    protected  void initAppBar(){
        mAppBarLayout.addOnOffsetChangedListener(this);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        //交给子类实现
    }

    //输入文字内容监听
    public void initEditContent(){
        mContent.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                //发送按钮监听
               String content = s.toString().trim();
               boolean needSubmit = !TextUtils.isEmpty(content);
                mSubmit.setActivated(needSubmit);
            }
        });
    }
}

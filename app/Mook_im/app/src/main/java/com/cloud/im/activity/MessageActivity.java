package com.cloud.im.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

import com.cloud.common.base.BaseActivity;
import com.cloud.common.base.BaseFragment;
import com.cloud.factory.model.Author;
import com.cloud.im.R;
import com.cloud.im.fragments.message.ChatGroupFragment;
import com.cloud.im.fragments.message.ChatUserFragment;

/**
 * Created by fmm on 2017/12/28.
 */

public class MessageActivity extends BaseActivity {
    // 接收者Id，可以是群，也可以是人的Id
    public static final String KEY_RECEIVER_ID = "KEY_RECEIVER_ID";
    // 是否是群
    private static final String KEY_RECEIVER_IS_GROUP = "KEY_RECEIVER_IS_GROUP";
    private String mReceiverId;
    private boolean mIsGroup;

    /**
     * 显示人的聊天界面
     *
     * @param context 上下文
     * @param author  人的信息
     */
    public static void show(Context context, Author author) {
        if (context == null || author == null || TextUtils.isEmpty(author.getId())) {
            return;
        }
        Intent intent = new Intent(context, MessageActivity.class);
        intent.putExtra(KEY_RECEIVER_ID, author.getId());
        intent.putExtra(KEY_RECEIVER_IS_GROUP, false);
        context.startActivity(intent);
    }

    @Override
    public int getContentLayoutId() {
        return R.layout.activity_message;
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        mReceiverId = bundle.getString(KEY_RECEIVER_ID);
        mIsGroup = bundle.getBoolean(KEY_RECEIVER_IS_GROUP);
        return !TextUtils.isEmpty(mReceiverId);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setTitle("");
        BaseFragment fragment;
        if (mIsGroup) {
            fragment = new ChatGroupFragment();
        } else {
            fragment = new ChatUserFragment();
        }

        // 从Activity传递参数到Fragment中去
        Bundle bundle = new Bundle();
        bundle.putString(KEY_RECEIVER_ID, mReceiverId);
        fragment.setArguments(bundle);

        //显示fragment
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.lay_container, fragment).commit();
    }
}
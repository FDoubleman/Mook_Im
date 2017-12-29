package com.cloud.im.fragments.message;

import com.cloud.factory.model.db.Group;
import com.cloud.factory.presenter.message.ChatContract;

/**
 * Created by fmm on 2017/12/28.
 * 群组聊天页面
 */

public class ChatGroupFragment extends ChatFragment<Group> implements ChatContract.GroupView {


    @Override
    public int getContentLayoutId() {
        return 0;
    }


    @Override
    protected ChatContract.Presenter initPresenter() {
        return null;
    }

    @Override
    public void onInit(Group group) {

    }
}

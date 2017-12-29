package com.cloud.factory.presenter.message;

import com.cloud.factory.data.helper.UserHelper;
import com.cloud.factory.data.massage.MessageRepository;
import com.cloud.factory.model.db.Message;
import com.cloud.factory.model.db.User;

/**
 * Created by fmm on 2017/12/29.
 */

public class ChatUserPresenter extends ChatPresenter<ChatContract.UserView>
        implements ChatContract.Presenter {

    public ChatUserPresenter(ChatContract.UserView view, String receiverId) {
        // 数据源，View，接收者，接收者的类型
        super(new MessageRepository(receiverId), view, receiverId, Message.RECEIVER_TYPE_NONE);
    }

    @Override
    public void start() {
        super.start();
        // 从本地拿这个人的信息
        User receiver = UserHelper.searchLoacl(mReceiverId);
        getView().onInit(receiver);
    }
}

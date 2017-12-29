package com.cloud.factory.presenter.message;

import android.support.v7.util.DiffUtil;

import com.cloud.factory.data.helper.MessageHelper;
import com.cloud.factory.data.massage.MessageDataSource;
import com.cloud.factory.model.api.message.MsgCreateModel;
import com.cloud.factory.model.db.Message;
import com.cloud.factory.persistence.Account;
import com.cloud.factory.presenter.BaseSourcePresenter;
import com.cloud.factory.util.DiffUiDataCallback;

import java.util.List;

/**
 * Created by fmm on 2017/12/29.
 */

public class ChatPresenter<View extends ChatContract.View>
        extends BaseSourcePresenter<Message,Message,MessageDataSource,View>
        implements ChatContract.Presenter{
    // 接收者Id，可能是群，或者人的ID
    protected String mReceiverId;
    // 区分是人还是群Id
    protected int mReceiverType;

    public ChatPresenter(MessageDataSource source, View view, String receiverId, int receiverType) {
        super(source, view);
        this.mReceiverId = receiverId;
        this.mReceiverType = receiverType;
    }

    @Override
    public void pushText(String content) {
        //创建一个新的消息
        MsgCreateModel model = new MsgCreateModel.Builder()
                .receiver(mReceiverId,mReceiverType)
                .content(content,Message.TYPE_STR)
                .build();
        MessageHelper.push(model);
    }

    @Override
    public void pushAudio(String path) {

    }

    @Override
    public void pushImages(String[] paths) {

    }

    @Override
    public boolean rePush(Message message) {
        //发送者是当前的登录用户 而且消息是发送失败状态
        if(Account.getUserId().equalsIgnoreCase(message.getSender().getId())
                ||message.getStatus() ==Message.STATUS_FAILED){
            //构建消息
            // 更改状态
            message.setStatus(Message.STATUS_CREATED);
            MsgCreateModel model =  MsgCreateModel.buildWithMessage(message);
            //发送消息
            MessageHelper.push(model);
            return true;
        }

        return false;
    }

    @Override
    public void onDataLoaded(List<Message> messages) {
        ChatContract.View view = getView();
        if(view ==null){
            return;
        }

        //获得旧的数据
       List<Message> oldList = view.getRecycleAdapter().getItems();

        //差异计算
        DiffUiDataCallback<Message> callback = new DiffUiDataCallback<>(oldList,messages);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);

        //进行界面刷新
        refreshData(result,messages);
    }
}

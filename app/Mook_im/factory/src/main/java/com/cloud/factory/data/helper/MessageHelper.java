package com.cloud.factory.data.helper;


import com.cloud.factory.Factory;
import com.cloud.factory.model.api.RspModel;
import com.cloud.factory.model.api.message.MsgCreateModel;
import com.cloud.factory.model.card.MessageCard;
import com.cloud.factory.model.db.Message;
import com.cloud.factory.model.db.Message_Table;
import com.cloud.factory.net.Network;
import com.cloud.factory.net.RemoteService;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 消息工具类
 *
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */
public class MessageHelper {
    // 从本地找消息
    public static Message findFromLocal(String id) {
        return SQLite.select()
                .from(Message.class)
                .where(Message_Table.id.eq(id))
                .querySingle();
    }

    // 发送是异步进行的
    public static void push(final MsgCreateModel model) {
        Factory.runOnAsync(new Runnable() {
            @Override
            public void run() {
                // 成功状态：如果是一个已经发送过的消息，则不能重新发送
                // 正在发送状态：如果是一个消息正在发送，则不能重新发送
                Message message = findFromLocal(model.getId());
                if (message != null && message.getStatus() != Message.STATUS_FAILED) {
                    return;
                }

                // TODO: 2017/12/29 发送语音 图片 文件

                //1、发送的时候需要通知界面更新 card
                final MessageCard card = model.buildCard();
                Factory.getMessageCanter().dispatch(card);
                RemoteService service = Network.getInstance().getService();
                service.msgPush(model).enqueue(new Callback<RspModel<MessageCard>>() {
                    @Override
                    public void onResponse(Call<RspModel<MessageCard>> call, Response<RspModel<MessageCard>> response) {

                        RspModel<MessageCard> rspMode = response.body();
                        if (rspMode.success()) {
                            MessageCard result = rspMode.getResult();
                            if (result != null) {
                                //成功调度
                                Factory.getMessageCanter().dispatch(result);
                            }
                        } else {
                            // 检查是否是账户异常
                            Factory.decodeRspCode(rspMode, null);
                            // 走失败流程
                            onFailure(call, null);
                        }
                    }

                    @Override
                    public void onFailure(Call<RspModel<MessageCard>> call, Throwable t) {
                        // 通知失败
                        card.setStatus(Message.STATUS_FAILED);
                        Factory.getMessageCanter().dispatch(card);
                    }
                });


            }
        });

    }
}

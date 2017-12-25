package com.cloud.factory.data.massage;

import android.text.TextUtils;

import com.cloud.factory.data.helper.DBHelper;
import com.cloud.factory.data.helper.GroupHelper;
import com.cloud.factory.data.helper.MessageHelper;
import com.cloud.factory.data.helper.UserHelper;
import com.cloud.factory.model.card.MessageCard;
import com.cloud.factory.model.db.Group;
import com.cloud.factory.model.db.Message;
import com.cloud.factory.model.db.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by fmm on 2017/12/25.
 *
 */

public class MessageDispatch implements MessageCanter {

    // 单线程池；处理卡片一个个的消息进行处理
    private final Executor executor = Executors.newSingleThreadExecutor();
    private static MessageDispatch instance;

    public static MessageDispatch getInstance() {
        if (instance == null) {
            synchronized (MessageDispatch.class) {
                if (instance == null) {
                    instance = new MessageDispatch();
                }
            }
        }
        return instance;
    }


    @Override
    public void dispatch(MessageCard... messageCards) {
        if(messageCards ==null || messageCards.length ==0){
            return;
        }
        executor.execute(new MassageHandle(messageCards));
    }

    private class MassageHandle implements Runnable{

        private final MessageCard[] cards;

        public MassageHandle(MessageCard[] cards) {
            this.cards = cards;
        }

        @Override
        public void run() {
            List<Message> messages = new ArrayList<>();
            for (MessageCard card : cards) {
                // 卡片基础信息过滤，错误卡片直接过滤
                if (card == null || TextUtils.isEmpty(card.getSenderId())
                        || TextUtils.isEmpty(card.getId())
                        || (TextUtils.isEmpty(card.getReceiverId())
                        && TextUtils.isEmpty(card.getGroupId())))
                    continue;
                // 消息卡片有可能是推送过来的，也有可能是直接造的
                // 推送来的代表服务器一定有，我们可以查询到（本地有可能有，有可能没有）
                // 如果是直接造的，那么先存储本地，后发送网络
                // 发送消息流程：写消息->存储本地->发送网络->网络返回->刷新本地状态
                Message message = MessageHelper.findFromLocal(card.getId());
                if (message != null) {
                    // 消息本身字段从发送后就不变化了，如果收到了消息，
                    // 本地有，同时本地显示消息状态为完成状态，则不必处理，
                    // 因为此时回来的消息和本地一定一摸一样

                    // 如果已经完成则不做处理
                    if (message.getStatus() == Message.STATUS_DONE)
                        continue;
                    // 新状态为完成才更新服务器时间，不然不做更新
                    if (card.getStatus() == Message.STATUS_DONE){
                        // 代表网络发送成功，此时需要修改时间为服务器的时间

                        message.setCreateAt(card.getCreateAt());

                        // 如果没有进入判断，则代表这个消息是发送失败了，
                        // 重新进行数据库更新而而已
                    }

                    // 更新一些会变化的内容
                    message.setContent(card.getContent());
                    message.setAttach(card.getAttach());
                    // 更新状态
                    message.setStatus(card.getStatus());
                } else {
                    // 没找到本地消息，初次在数据库存储
                    User sender = UserHelper.search(card.getSenderId());
                    User receiver = null;
                    Group group = null;
                    if (!TextUtils.isEmpty(card.getReceiverId())) {
                        receiver = UserHelper.search(card.getReceiverId());
                    } else if (!TextUtils.isEmpty(card.getGroupId())) {
                        group = GroupHelper.findFromLocal(card.getGroupId());
                    }

                    // 接收者总有一个
                    if (receiver == null && group == null)
                        continue;

                    message = card.build(sender, receiver, group);
                }
                messages.add(message);
            }
            if (messages.size() > 0)
                DBHelper.save(Message.class, messages.toArray(new Message[0]));
        }
    }
}

package net.qiujuer.web.italker.push.factory;

import net.qiujuer.web.italker.push.bean.api.message.MessageCreateModel;
import net.qiujuer.web.italker.push.bean.db.Group;
import net.qiujuer.web.italker.push.bean.db.Message;
import net.qiujuer.web.italker.push.bean.db.User;
import net.qiujuer.web.italker.push.utils.Hib;

/**
 * Created by fmm on 2017/12/27.
 */
public class MessageFactory {

    /**
     * 通过ID  查找到一条信息
     * @param messageId 信息id
     * @return 信息
     */
    public static Message findbyId(String messageId){

        return Hib.query(session -> session.get(Message.class,messageId));
    }

    /**
     * 添加一条发送给人的消息
     * @param sender 发送者
     * @param receiver 接收者
     * @param model 消息
     */
    public static Message add(User sender, User receiver, MessageCreateModel model){

        Message message = new Message(sender,receiver,model);
        return save(message);
    }

    /**
     * 添加一条发送给群的消息
     * @param sender 发送者
     * @param receiver 接收的群
     * @param model 消息
     */
    public static Message add(User sender, Group receiver, MessageCreateModel model){

        Message message = new Message(sender,receiver,model);
        return save(message);
    }

    private static Message save( Message message) {
        return Hib.query(session -> {
            //保存
            session.save(message);

            //刷新到数据库
            session.flush();
            // 紧接着从数据库中查询出来
            session.refresh(message);
            return message;
        });
    }
}

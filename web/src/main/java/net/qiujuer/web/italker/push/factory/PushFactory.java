package net.qiujuer.web.italker.push.factory;

import com.google.common.base.Strings;
import net.qiujuer.web.italker.push.bean.api.base.PushModel;
import net.qiujuer.web.italker.push.bean.card.MessageCard;
import net.qiujuer.web.italker.push.bean.db.*;
import net.qiujuer.web.italker.push.utils.Hib;
import net.qiujuer.web.italker.push.utils.PushDispatcher;
import net.qiujuer.web.italker.push.utils.TextUtil;
import org.jvnet.hk2.internal.Collector;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by fmm on 2017/12/27.
 * 统一消息推送工具类 消息存储与处理的工具类
 */
public class PushFactory {


    //推送一条消息  并将推送信息保存 推送历史表
    public static void pushNewMessage(User sender, Message message) {

        //1、入参过滤
        if (sender == null || message == null) {
            return;
        }


        //2、保存至推送历史数据库
        //2.1、推送的内容 -->要推送的字符串
        MessageCard card = new MessageCard(message);
        String entity = TextUtil.toJson(card);

        // 3、初始化消息推送 发送者
        PushDispatcher dispatcher = new PushDispatcher();

        //2.2、
        if (message.getGroup() == null || Strings.isNullOrEmpty(message.getGroupId())) {
            //推送给人
            //2.3、获得接收者
            User receiver = UserFactory.findUserById(message.getReceiverId());
            if (receiver == null) {
                return;
            }
            //2.4、创建历史记录
            PushHistory pushHistory = new PushHistory();
            //推送类型
            pushHistory.setEntityType(PushModel.ENTITY_TYPE_MESSAGE);
            //推送内容
            pushHistory.setEntity(entity);
            pushHistory.setReceiver(receiver);
            // 接收者当前的设备推送Id
            pushHistory.setReceiverPushId(receiver.getPushId());

            //pushHistory.setSenderId("1213");
            //3.1、创建一个pushmodle
            PushModel pushModel = new PushModel();
            // 每一条历史记录都是独立的，可以单独的发送
            pushModel.add(pushHistory.getEntityType(), pushHistory.getEntity());
            // 把需要发送的数据，添加到发送者
            dispatcher.add(receiver, pushModel);

            //2.5、保存数据
            Hib.queryOnly(session -> {
                session.save(pushHistory);
            });

        } else {
            //推送给群
            Group group = message.getGroup();
            // 因为延迟加载情况可能为null，需要通过Id查询
            if (group == null) {
                group = GroupFactory.findById(message.getGroupId());
            }
            // 如果群真的没有，则返回
            if (group == null)
                return;
            //获的所有群成员
            Set<GroupMember> members = GroupFactory.getMembers(group);
            if(members ==null ||members.size()==0){
                return;
            }
            //过滤自己
            members = members.stream().filter(groupMember ->
                    !groupMember.getUserId().equalsIgnoreCase(sender.getId()))
                    .collect(Collectors.toSet());
            if(members.size() ==0 ){
                return;
            }
            // 一个历史记录列表
            List<PushHistory> histories = new ArrayList<>();
            addGroupMembersPushModel(dispatcher, // 推送的发送者
                    histories, // 数据库要存储的列表
                    members,    // 所有的成员
                    entity, // 要发送的数据
                    PushModel.ENTITY_TYPE_MESSAGE); // 发送的类型
            //将推送历史记录表保存
            Hib.queryOnly(session -> {
                for (PushHistory history : histories) {
                    session.saveOrUpdate(history);
                }
            });
        }
        //3、推送消息
        dispatcher.submit();
    }
    /**
     * 给群成员构建一个消息，
     * 把消息存储到数据库的历史记录中，每个人，每条消息都是一个记录
     *
     * 1、添加历史记录
     * 2、添加推送消息
     */
    private static void addGroupMembersPushModel(PushDispatcher dispatcher,
                                                 List<PushHistory> histories,
                                                 Set<GroupMember> members,
                                                 String entity,
                                                 int entityTypeMessage) {
        for (GroupMember member : members) {
            //无须通过Id再去找用户
            User receiver = member.getUser();
            if (receiver == null)
                return;

            //1、创建推送历史记录表
            PushHistory history = new PushHistory();
            history.setEntityType(entityTypeMessage);
            history.setEntity(entity);
            history.setReceiver(receiver);
            history.setReceiverPushId(receiver.getPushId());
            //添加
            histories.add(history);

            //2、构建一个消息modle
            PushModel pushModel = new PushModel();
            pushModel.add(history.getEntityType(),history.getEntity());
            dispatcher.add(receiver,pushModel);
        }

    }
}

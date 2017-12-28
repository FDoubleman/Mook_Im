package net.qiujuer.web.italker.push.service;

import net.qiujuer.web.italker.push.bean.api.base.ResponseModel;
import net.qiujuer.web.italker.push.bean.api.message.MessageCreateModel;
import net.qiujuer.web.italker.push.bean.api.user.UpdateInfoModel;
import net.qiujuer.web.italker.push.bean.card.MessageCard;
import net.qiujuer.web.italker.push.bean.card.UserCard;
import net.qiujuer.web.italker.push.bean.db.Group;
import net.qiujuer.web.italker.push.bean.db.Message;
import net.qiujuer.web.italker.push.bean.db.User;
import net.qiujuer.web.italker.push.factory.GroupFactory;
import net.qiujuer.web.italker.push.factory.MessageFactory;
import net.qiujuer.web.italker.push.factory.PushFactory;
import net.qiujuer.web.italker.push.factory.UserFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by fmm on 2017/12/27.
 */
@Path("msg")
public class MessageService extends BaseService {

    // 发送一条消息到服务器
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<MessageCard> pushMessage(MessageCreateModel createModel) {

        if (!MessageCreateModel.check(createModel)) {
            return ResponseModel.buildParameterError();
        }
        //查询这条消息是否添加过 查询是否已经在数据库中有了
        Message message = MessageFactory.findbyId(createModel.getId());
        if (message != null) {
            // 正常返回
            return ResponseModel.buildOk(new MessageCard(message));
        }

        User self = getSelf();
        //判断消息的发送类型
        if (createModel.getReceiverType() == Message.RECEIVER_TYPE_GROUP) {
            //发送给群组
            return pushToGroup(self, createModel);
        } else {
            //发送给个人
            return pushToUser(self, createModel);
        }

    }

    private ResponseModel<MessageCard> pushToUser(User sender, MessageCreateModel createModel) {
        //1、找到接收者
        User receiver = UserFactory.findUserById(createModel.getReceiverId());
        //2、过滤接收者 和自己是否是同一个人
        if (receiver == null) {
            // 没有找到接收者
            return ResponseModel.buildNotFoundUserError("Con't find receiver user");
        }
        if (receiver.getId().equalsIgnoreCase(sender.getId())) {
            // 发送者接收者是同一个人就返回创建消息失败
            return ResponseModel.buildCreateError(ResponseModel.ERROR_CREATE_MESSAGE);
        }

        //3、存储数据库
        Message message = MessageFactory.add(sender, receiver, createModel);

        return buildAndPushResponse(sender, message);
    }

    private ResponseModel<MessageCard> pushToGroup(User sender, MessageCreateModel createModel) {
        // 找群是有权限性质的找
        Group group = GroupFactory.findById(sender, createModel.getReceiverId());
        if (group == null) {
            // 没有找到接收者群，有可能是你不是群的成员
            return ResponseModel.buildNotFoundUserError("Con't find receiver group");
        }
        // 添加到数据库
        Message message = MessageFactory.add(sender, group, createModel);
        // 走通用的推送逻辑
        return buildAndPushResponse(sender, message);
    }


    // 推送并构建一个返回信息
    private ResponseModel<MessageCard> buildAndPushResponse(User sender, Message message) {
        //过滤消息
        if (message == null) {
            // 存储数据库失败
            return ResponseModel.buildCreateError(ResponseModel.ERROR_CREATE_MESSAGE);
        }
        // 进行推送
        PushFactory.pushNewMessage(sender, message);

        return ResponseModel.buildOk(new MessageCard(message));
    }
}

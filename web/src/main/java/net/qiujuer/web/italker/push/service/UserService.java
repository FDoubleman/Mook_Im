package net.qiujuer.web.italker.push.service;


import com.google.common.base.Strings;
import net.qiujuer.web.italker.push.bean.api.base.PushModel;
import net.qiujuer.web.italker.push.bean.api.base.ResponseModel;
import net.qiujuer.web.italker.push.bean.api.user.UpdateInfoModel;
import net.qiujuer.web.italker.push.bean.card.UserCard;
import net.qiujuer.web.italker.push.bean.db.User;

import net.qiujuer.web.italker.push.factory.UserFactory;
import net.qiujuer.web.italker.push.utils.PushDispatcher;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by fmm on 2017/9/2.
 *
 */
@Path("/user")
public class UserService extends BaseService {

    @PUT
    //@Path("") //127.0.0.1/api/user 不需要写，就是当前目录
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<UserCard> updata(@HeaderParam("token") String token,
                                          UpdateInfoModel model) {
        if (!UpdateInfoModel.check(model)) {
            return ResponseModel.buildParameterError();
        }
        //0、获得用户
//       User self = UserFactory.findUserByToken(token);
        User self = getSelf();

        //1、更新用户信息
        self = model.updateToUser(self);
        //2、更新数据库
        self = UserFactory.updataUser(self);
        //3、返回跟新后的User

        UserCard card = new UserCard(self, true);
        return ResponseModel.buildOk(card);
    }


    // 拉取联系人
    @GET
    @Path("/contact")//127.0.0.1/api/user/contact
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<List<UserCard>> contact() {
        //1、获得自己
        User self = getSelf();

        PushDispatcher dispatcher = new PushDispatcher();
        PushModel pushModel =new PushModel();
        pushModel.add(new PushModel.Entity(0,"hello 推送消息"));
        dispatcher.add(self,pushModel);
        dispatcher.submit();


        //2、通过自己获得联系人
        List<User> users = UserFactory.contacts(self);
        //3、将联系人 转化为UserCard
        List<UserCard> userCards = users.stream()
                //将User 转换为 UserCard
                .map(user -> new UserCard(user, true))
                .collect(Collectors.toList());
        //4、返回结果
        return ResponseModel.buildOk(userCards);
    }

    //关注某人
    //就是修改数据库中的某条对应关系 使用put
    @PUT
    @Path("/follow/{followId}")//127.0.0.1/api/user/follow/{followId}
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<UserCard> follow(@PathParam("followId") String followId) {
        //1、获得自己
        User self = getSelf();
        //2、follwId 校验 非空 或者不是自己
        if (Strings.isNullOrEmpty(followId) || self.getId().equalsIgnoreCase(followId)) {
            //参数异常
            return ResponseModel.buildParameterError();
        }
        //3、获得followUser
        User followUser = UserFactory.findUserById(followId);
        if (followUser == null) {
            // 未找到人
            return ResponseModel.buildNotFoundUserError(null);
        }


        //3、关注某人具体操作
        //3.1、通过id 找到关注人的
        followUser = UserFactory.follow(self, followUser, "");
        if (followUser == null) {
            // 关注失败，返回服务器异常
            return ResponseModel.buildServiceError();

        }
        // TODO 通知我关注的人我关注他


        //4、将返回关注人的信息
        // 返回关注的人的信息
        UserCard card = new UserCard(followUser, true);
        return ResponseModel.buildOk(card);
    }

    @GET
    @Path("{userId}") // http://127.0.0.1/api/user/{id}
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<UserCard> getUserInfo(@PathParam("userId") String userId) {
        User self = getSelf();
        //1、参数校验

        if (Strings.isNullOrEmpty(userId)) {
            //参数异常
            return ResponseModel.buildParameterError();
        }

        //2、是否是当前的自己
        if (userId.equalsIgnoreCase(self.getId())) {
            //返回当前的自己
            return ResponseModel.buildOk(new UserCard(self, true));
        }
        //3、通过userID 查询user
        User user = UserFactory.findUserById(userId);
        if (user == null) {
            //未找到当前用户
            return ResponseModel.buildNotFoundUserError("");
        }
        //4、返回
        // 如果我们直接有关注的记录，则我已关注需要查询信息的用户
        boolean isFollow = UserFactory.getUserFollow(self, user) != null;//判断两个是否关注
        return ResponseModel.buildOk(new UserCard(user, isFollow));
    }

    // 搜索人的接口实现
    // 为了简化分页：只返回20条数据
    @GET // 搜索人，不涉及数据更改，只是查询，则为GET
    // http://127.0.0.1/api/user/search/
    @Path("/search/{name:(.*)?}") // 名字为任意字符，可以为空
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<List<UserCard>> search(@DefaultValue("") @PathParam("name") String name) {
        //1、获得自己
        User self = getSelf();
        //2、获得搜索的list
        List<User> searchList = UserFactory.search(name);
        //3、获得自己联系人list
        List<User> contactList = UserFactory.contacts(self);
        //4、重构UserCardList

        // 把User->UserCard
        List<UserCard> userCards = searchList.stream().map(user -> {
            // 判断这个人是否是我自己，或者是我的联系人中的人
            boolean isFollow = user.getId().equalsIgnoreCase(self.getId())
                    // 进行联系人的任意匹配，匹配其中的Id字段
                    || contactList.stream().anyMatch(
                    contactUser -> contactUser.getId()
                            .equalsIgnoreCase(user.getId())
            );
            return new UserCard(user, isFollow);
        }).collect(Collectors.toList());


        //5、返回数据

        return ResponseModel.buildOk(userCards);
    }

}

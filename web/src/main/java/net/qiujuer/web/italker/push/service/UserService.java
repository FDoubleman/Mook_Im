package net.qiujuer.web.italker.push.service;


import net.qiujuer.web.italker.push.bean.api.base.ResponseModel;
import net.qiujuer.web.italker.push.bean.api.user.UpdateInfoModel;
import net.qiujuer.web.italker.push.bean.card.UserCard;
import net.qiujuer.web.italker.push.bean.db.User;

import net.qiujuer.web.italker.push.factory.UserFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by fmm on 2017/9/2.
 *
 */
@Path("/user")
public class UserService extends BaseService{

    @PUT
    //@Path("") //127.0.0.1/api/user 不需要写，就是当前目录
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<UserCard> updata(@HeaderParam("token")String token,
                                          UpdateInfoModel model){
        if(!UpdateInfoModel.check(model)){
            return ResponseModel.buildParameterError();
        }
        //0、获得用户
//       User self = UserFactory.findUserByToken(token);
        User self =getSelf();

        //1、更新用户信息
        self = model.updateToUser(self);
        //2、更新数据库
        self =UserFactory.updataUser(self);
        //3、返回跟新后的User

        UserCard card = new UserCard(self,true);
        return ResponseModel.buildOk(card);
    }
}

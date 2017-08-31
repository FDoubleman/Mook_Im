package net.qiujuer.web.italker.push.service;

import net.qiujuer.web.italker.push.bean.db.User;
import net.qiujuer.web.italker.push.bean.db.api.account.RegisterModel;
import net.qiujuer.web.italker.push.bean.db.card.UserCard;
import net.qiujuer.web.italker.push.factory.UserFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by fmm on 2017/7/8.
 *
 */
//127.0.0.0/api/account
@Path("/account")
public class AccountService {

    // 注册
    @POST
    @Path("/register")
    // 指定请求与返回的相应体为JSON
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public UserCard register(RegisterModel registerModel){
        User user =  UserFactory.register(registerModel.getAccount(),
                registerModel.getPassword(),
                registerModel.getName());
        if(user!=null){
            UserCard userCard = new UserCard();
            userCard.setName(registerModel.getName());
            userCard.setFollow(true);
            return userCard;
        }

        return null;
    }
}

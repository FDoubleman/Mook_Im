package net.qiujuer.web.italker.push.service;

import com.google.common.base.Strings;
import net.qiujuer.web.italker.push.bean.api.account.AccountRspModel;
import net.qiujuer.web.italker.push.bean.api.account.LoginModel;
import net.qiujuer.web.italker.push.bean.api.account.RegisterModel;
import net.qiujuer.web.italker.push.bean.api.base.ResponseModel;
import net.qiujuer.web.italker.push.bean.db.User;
import net.qiujuer.web.italker.push.factory.UserFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by fmm on 2017/7/8.
 *
 */
//127.0.0.0/api/account
@Path("/account")
public class AccountService extends BaseService{

    // 登录
    @POST
    @Path("/login")//127.0.0.0/api/account/login
    // 指定请求与返回的相应体为JSON
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<AccountRspModel> login(LoginModel model){

        if(!LoginModel.cheack(model)){
            return ResponseModel.buildParameterError();
        }

        User user = UserFactory.login(model.getAccount(),model.getPassword());
        if(user!=null){

            if(!Strings.isNullOrEmpty(model.getPushId())){
                return bindPushId(user,model.getPushId());
            }

            AccountRspModel rspModel = new AccountRspModel(user);
            return ResponseModel.buildOk(rspModel);
        }else{
            return ResponseModel.buildLoginError();
        }

    }


    // 注册
    @POST
    @Path("/register")
    // 指定请求与返回的相应体为JSON
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<AccountRspModel> register(RegisterModel registerModel){

        //检查入参是否错误
        if(!RegisterModel.cheack(registerModel)){
            return ResponseModel.buildParameterError();
        }
        //通过手机号 查找是否注册过
        User user = UserFactory.findUserByPhone(registerModel.getAccount().trim());
        if(user!=null){
            return ResponseModel.buildHaveAccountError();
        }
        //通过用户名 查找是否注册过
         user= UserFactory.findUserByName(registerModel.getName());
        if(user!=null){
            return ResponseModel.buildHaveNameError();
        }
        //未注册过  开始创建用户 注册
         user =  UserFactory.register(registerModel.getAccount(),
                registerModel.getPassword(),
                registerModel.getName());

        if(user!=null){

            if(!Strings.isNullOrEmpty(registerModel.getPushId())){
                return bindPushId(user,registerModel.getPushId());
            }

            //注册成功
            AccountRspModel accountRspModel = new AccountRspModel(user);
            return ResponseModel.buildOk(accountRspModel);
        }else{
            return ResponseModel.buildRegisterError();
        }
    }


    @POST
    @Path("/bind/{pushId}")//127.0.0.0/api/account/bind
    // 指定请求与返回的相应体为JSON
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    // 从请求头中获取token字段
    // pushId从url地址中获取
    public ResponseModel<AccountRspModel> bind(@HeaderParam("token")String token,
                                               @PathParam("pushId")String pushId){
        if (Strings.isNullOrEmpty(token) ||
                Strings.isNullOrEmpty(pushId)) {
            // 返回参数异常
            return ResponseModel.buildParameterError();
        }
        // 进行设备Id绑定的操作
//        User user =UserFactory.findUserByToken(token);
        User user =getSelf();
        return bindPushId(user,pushId);

    }

    public static ResponseModel<AccountRspModel> bindPushId(User user ,String pushId){


        user = UserFactory.bindPushId(user,pushId);
        if(user==null){
            // 绑定失败则是服务器异常
            return ResponseModel.buildServiceError();
        }
        // 返回当前的账户, 并且已经绑定了
        AccountRspModel rspModel = new AccountRspModel(user,true);

        return ResponseModel.buildOk(rspModel);
    }

}

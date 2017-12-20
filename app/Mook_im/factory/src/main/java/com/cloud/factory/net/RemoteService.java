package com.cloud.factory.net;

import com.cloud.factory.model.api.RspModel;
import com.cloud.factory.model.api.account.AccountRspModel;
import com.cloud.factory.model.api.account.LoginModel;
import com.cloud.factory.model.api.account.RegisterModel;
import com.cloud.factory.model.api.user.UserUpdateModel;
import com.cloud.factory.model.card.UserCard;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by fmm on 2017/9/6.
 */

public interface RemoteService {


    /**
     * 注册接口
     *
     * @param model 传入的是RegisterModel
     * @return 返回的是RspModel<AccountRspModel>
     */
    @POST("account/register")
    Call<RspModel<AccountRspModel>> accountRegister(@Body RegisterModel model);

    /**
     * 登录接口
     *
     */
    @POST("account/login")
    Call<RspModel<AccountRspModel>> accountLogin(@Body LoginModel model);

    /**
     * 注册接口
     *
     * @param pushid 推送id
     * @return 返回的是RspModel<AccountRspModel>
     */
    @POST("account/bind/{pushId}")
    Call<RspModel<AccountRspModel>> accountBind(@Path(encoded = true, value = "pushId") String pushid);


    /**
     * 更新用户信息接口
     * @param user
     * @return
     */
    @PUT("user")
    Call<RspModel<UserCard>> updataInfo(@Body UserUpdateModel user);

    /**
     * 搜索用户接口
     * @param name 用户名称
     * @return 搜索结果
     */
    @GET("user/search/{name}")
    Call<RspModel<List<UserCard>>> userSearch(@Path("name")String name);

    /**
     * 关注某人
     * @param userId 被关注人的id
     * @return 被关注人的信息
     */
    @PUT("user/follow/{userId}")
    Call<RspModel<UserCard>> followUser(@Path("userId")String userId);

    // 获取联系人列表
    @GET("user/contact")
    Call<RspModel<List<UserCard>>> userContacts();

    //获得个人信息
    @GET("user/{userId}")
    Call<RspModel<UserCard>> findUser(@Path("userId")String userId);
}

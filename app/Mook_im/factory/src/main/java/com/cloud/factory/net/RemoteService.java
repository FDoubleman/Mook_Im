package com.cloud.factory.net;

import com.cloud.factory.model.api.RspModel;
import com.cloud.factory.model.api.account.AccountRspModel;
import com.cloud.factory.model.api.account.RegisterModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

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


}

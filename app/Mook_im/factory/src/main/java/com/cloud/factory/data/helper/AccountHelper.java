package com.cloud.factory.data.helper;

import com.cloud.factory.Factory;
import com.cloud.factory.R;
import com.cloud.factory.data.DataSource;
import com.cloud.factory.model.api.RspModel;
import com.cloud.factory.model.api.account.AccountRspModel;
import com.cloud.factory.model.api.account.RegisterModel;
import com.cloud.factory.model.db.User;
import com.cloud.factory.net.Network;
import com.cloud.factory.net.RemoteService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 进行网络请求 注册 登录
 * Created by fmm on 2017/9/5.
 */

public class AccountHelper {


    /**
     * 注册接口异步调用
     * @param registerModel 注册实体body
     * @param callBack 注册接口回调
     */
    public static void register(RegisterModel registerModel, final DataSource.CallBack<User> callBack){

        RemoteService service = Network.getInstance().getService();
        Call<RspModel<AccountRspModel>> call =service.accountRegister(registerModel);

        call.enqueue(new Callback<RspModel<AccountRspModel>>() {
            @Override
            public void onResponse(Call<RspModel<AccountRspModel>> call,
                                   Response<RspModel<AccountRspModel>> response) {
                //请求成功的操作
                //1、判断是否成code
                //2、成功 保存至数据库
                //3、判断是否绑定设置
                //3.1、未绑定 绑定操作
                //4、
                RspModel<AccountRspModel> modelResponse =response.body();
                if(modelResponse.success()){
                    AccountRspModel model = modelResponse.getResult();
                    //是否绑定过
                    if(model.isBind()){
                        if(callBack!=null){
                            callBack.onDataLoad(model.getUser());
                        }
                    }else{
                        //未被绑定
                        //bindPush(callBack);
                        callBack.onDataLoad(model.getUser());
                    }

                }else{
                    //不成功 返回对应错误提示
                    Factory.decodeRspCode(modelResponse,callBack);
                }
            }

            @Override
            public void onFailure(Call<RspModel<AccountRspModel>> call, Throwable t) {
                callBack.onDataNotLoad(R.string.data_network_error);
            }
        });
    }

    /**
     * 绑定推送设备
     * @param callBack 绑定回调
     */
    private static void bindPush(DataSource.CallBack<User> callBack) {


    }
}

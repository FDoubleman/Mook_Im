package com.cloud.factory.data.helper;

import android.text.TextUtils;

import com.cloud.factory.Factory;
import com.cloud.factory.R;
import com.cloud.factory.data.DataSource;
import com.cloud.factory.model.api.RspModel;
import com.cloud.factory.model.api.account.AccountRspModel;
import com.cloud.factory.model.api.account.LoginModel;
import com.cloud.factory.model.api.account.RegisterModel;
import com.cloud.factory.model.db.User;
import com.cloud.factory.net.Network;
import com.cloud.factory.net.RemoteService;
import com.cloud.factory.persistence.Account;

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
    public static void register(RegisterModel registerModel,  DataSource.CallBack<User> callBack){

        RemoteService service = Network.getInstance().getService();
        Call<RspModel<AccountRspModel>> call =service.accountRegister(registerModel);

        call.enqueue(new AccountRspCallback(callBack));
    }

    /**
     * 登录接口
     * @param loginModel
     * @param callBack
     */
    public static void login(LoginModel loginModel, DataSource.CallBack<User> callBack){

        RemoteService service = Network.getInstance().getService();
        //获得一个call
        Call<RspModel<AccountRspModel>> call =service.accountLogin(loginModel);
        //异步请求
        call.enqueue(new AccountRspCallback(callBack));
    }

    /**
     * 绑定推送设备
     * @param callBack 绑定回调
     */
    public static void bindPush(DataSource.CallBack<User> callBack) {
        String pushID  = Account.getPushId();
        if(TextUtils.isEmpty(pushID)){
            return;
        }
        RemoteService service = Network.getInstance().getService();
        Call<RspModel<AccountRspModel>> call =service.accountBind(pushID);

        call.enqueue(new AccountRspCallback(callBack));

    }


    private static class AccountRspCallback implements Callback<RspModel<AccountRspModel>>{
        private DataSource.CallBack<User> callBack;
        public AccountRspCallback( DataSource.CallBack<User> callBack) {
            this.callBack =callBack;
        }

        @Override
        public void onResponse(Call<RspModel<AccountRspModel>> call, Response<RspModel<AccountRspModel>> response) {
            //请求成功的操作
            //1、判断是否成code
            //2、成功 保存至数据库
            //3、判断是否绑定设置
            //3.1、未绑定 绑定操作
            //4、
            RspModel<AccountRspModel> modelResponse =response.body();
            if(modelResponse.success()){
                AccountRspModel model = modelResponse.getResult();
                User user = model.getUser();
                DBHelper.save(User.class,user);
                //将获得的用户信息保存至数据库
                //方式一、
//                user.save();
//                    //方式二、
//                    FlowManager.getModelAdapter(User.class)
//                            .save(user);
//                    // 第三种，事务中
//                    DatabaseDefinition definition = FlowManager.getDatabase(AppDatabase.class);
//                    definition.beginTransactionAsync(new ITransaction() {
//                        @Override
//                        public void execute(DatabaseWrapper databaseWrapper) {
//                            FlowManager.getModelAdapter(User.class)
//                                    .save(user);
//                        }
//                    }).build().execute();

                Account.login(model);
                //是否绑定过
                if(model.isBind()){
                    // 设置绑定状态为True
                    Account.setBind(true);
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
            if(callBack==null){
                return;
            }
            callBack.onDataNotLoad(R.string.data_network_error);
        }
    }
}

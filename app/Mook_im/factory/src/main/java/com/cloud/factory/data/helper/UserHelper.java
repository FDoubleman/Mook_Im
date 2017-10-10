package com.cloud.factory.data.helper;

import com.cloud.factory.Factory;
import com.cloud.factory.R;
import com.cloud.factory.data.DataSource;
import com.cloud.factory.model.api.RspModel;
import com.cloud.factory.model.api.user.UserUpdateModel;
import com.cloud.factory.model.card.UserCard;
import com.cloud.factory.model.db.User;
import com.cloud.factory.net.Network;
import com.cloud.factory.net.RemoteService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by fmm on 2017/9/11.
 * 更新个人信息接口
 */

public class UserHelper {

    public static void updateUserInfo(UserUpdateModel model, DataSource.CallBack<UserCard> callBack) {
        RemoteService service = Network.getInstance().getService();
        Call<RspModel<UserCard>> call = service.updataInfo(model);
        call.enqueue(new UpdataInfoCallBack(callBack));

    }


    public static class UpdataInfoCallBack implements Callback<RspModel<UserCard>> {
        private DataSource.CallBack<UserCard> callBack;

        public UpdataInfoCallBack(DataSource.CallBack<UserCard> callBack) {
            this.callBack = callBack;
        }

        @Override
        public void onResponse(Call<RspModel<UserCard>> call, Response<RspModel<UserCard>> response) {
            RspModel<UserCard> rspModel = response.body();
            if (rspModel.success()) {
                //获得Usecar
                UserCard userCard = rspModel.getResult();
                User user = userCard.build();
                //更新数据库 中的数据
                user.save();
                callBack.onDataLoad(userCard);
            } else {
                // 错误情况下进行错误分配
                Factory.decodeRspCode(rspModel, callBack);
            }
        }

        @Override
        public void onFailure(Call<RspModel<UserCard>> call, Throwable t) {
            callBack.onDataNotLoad(R.string.data_network_error);
        }
    }


    public static Call search(String content, DataSource.CallBack<List<UserCard>> callBack){
        //获得searvice
        RemoteService service = Network.getInstance().getService();
        //获得call
        Call call =service.userSearch(content);
        //enqueue 加入队列
        call.enqueue(new SearchUserCallBack(callBack));

        return call;
    }

    public static class SearchUserCallBack implements Callback<RspModel<List<UserCard>>>{
        private DataSource.CallBack<List<UserCard>> mCallBack;
        public SearchUserCallBack(DataSource.CallBack<List<UserCard>> callBack){
            mCallBack =callBack;
        }

        @Override
        public void onResponse(Call<RspModel<List<UserCard>>> call, Response<RspModel<List<UserCard>>> response) {

            RspModel<List<UserCard>> rspModel =  response.body();
            if(response.isSuccessful()){
               mCallBack.onDataLoad(rspModel.getResult());
            }else{
                Factory.decodeRspCode(rspModel,mCallBack);
            }
        }

        @Override
        public void onFailure(Call<RspModel<List<UserCard>>> call, Throwable t) {
            mCallBack.onDataNotLoad(R.string.data_network_error);
        }
    }
}

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
        call.enqueue(new UpdataInfo(callBack));

    }


    public static class UpdataInfo implements Callback<RspModel<UserCard>> {
        private DataSource.CallBack<UserCard> callBack;

        public UpdataInfo(DataSource.CallBack<UserCard> callBack) {
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
}

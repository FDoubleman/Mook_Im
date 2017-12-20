package com.cloud.factory.data.helper;

import com.cloud.factory.Factory;
import com.cloud.factory.R;
import com.cloud.factory.data.DataSource;
import com.cloud.factory.model.api.RspModel;
import com.cloud.factory.model.api.user.UserUpdateModel;
import com.cloud.factory.model.card.UserCard;
import com.cloud.factory.model.db.User;
import com.cloud.factory.model.db.User_Table;
import com.cloud.factory.net.Network;
import com.cloud.factory.net.RemoteService;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by fmm on 2017/9/11.
 * 更新个人信息接口
 */

public class UserHelper {
    //更新用户信息
    public static void updateUserInfo(UserUpdateModel model, DataSource.CallBack<UserCard> callBack) {
        RemoteService service = Network.getInstance().getService();
        Call<RspModel<UserCard>> call = service.updataInfo(model);
        call.enqueue(new UpdataInfoCallBack(callBack));

    }
    //搜索某人
    public static Call search(String content, DataSource.CallBack<List<UserCard>> callBack){
        //获得searvice
        RemoteService service = Network.getInstance().getService();
        //获得call
        Call call =service.userSearch(content);
        //enqueue 加入队列
        call.enqueue(new SearchUserCallBack(callBack));

        return call;
    }

    //关注某人
    public static Call follow(String followId,DataSource.CallBack<UserCard> callBack){
        RemoteService service = Network.getInstance().getService();
        Call call = service.followUser(followId);
        call.enqueue(new followCallback(callBack));
        return call;
    }

    //刷新联系人列表
    public static void refreshContacts(DataSource.CallBack<List<UserCard>> callBack){
        RemoteService service = Network.getInstance().getService();
        Call call = service.userContacts();

        call.enqueue(new ContactsCallback(callBack));
    }

    //从本地先从本地 获得个人信息
    public static User search(String id){
        User user =searchLoacl(id);
        if(user==null){
            user =searchNet(id);
        }
        return user;
    }

    //先从网络获得个人信息
    public static User searchFirstNet(String id){
        User user =searchNet(id);
        if(user==null){
            user =searchLoacl(id);
        }
        return user;
    }


    /**
     * 从本地获得个人信息
     * @param id 用户id
     * @return 用户信息
     */
    private static User searchLoacl(String id){
       return SQLite.select()
                .from(User.class)
                .where(User_Table.id.eq(id))
                .querySingle();
    }

    /**
     * 从网络获得个人信息
     * @param id 用户id
     * @return 用户信息
     */
    private static User searchNet(String id){
       RemoteService service =  Network.getInstance().getService();
        try {
            Response<RspModel<UserCard>> response = service.findUser(id).execute();
            UserCard userCard  =  response.body().getResult();
           if(userCard!=null){
              User user= userCard.build();
               DBHelper.getInstance().save(User.class,user);
               return user;
           }

            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class UpdataInfoCallBack implements Callback<RspModel<UserCard>> {
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
                DBHelper.getInstance().save(User.class,user);
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

    private static class SearchUserCallBack implements Callback<RspModel<List<UserCard>>>{
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

    private static class followCallback implements Callback<RspModel<UserCard>>{
        private DataSource.CallBack<UserCard> mCallBack;
        public followCallback(DataSource.CallBack<UserCard> callBack){
            mCallBack =callBack;
        }

        @Override
        public void onResponse(Call<RspModel<UserCard>> call, Response<RspModel<UserCard>> response) {
            RspModel<UserCard> rspModel = response.body();
            if(response.isSuccessful()){
               UserCard userCard = rspModel.getResult();

                //保存数据库 ---???? 为啥要保存到本地
                User user = userCard.build();
                //保存并通知联系人列表刷新
                DBHelper.getInstance().save(User.class,user);

                mCallBack.onDataLoad(userCard);
            }else{
                Factory.decodeRspCode(rspModel,mCallBack);
            }
        }

        @Override
        public void onFailure(Call<RspModel<UserCard>> call, Throwable t) {
            mCallBack.onDataNotLoad(R.string.data_network_error);
        }
    }

    private static class ContactsCallback implements Callback<RspModel<List<UserCard>>>{
        private DataSource.CallBack<List<UserCard>> mCallBack;

        public ContactsCallback(DataSource.CallBack<List<UserCard>> callBack){
            mCallBack =callBack;
        }

        @Override
        public void onResponse(Call<RspModel<List<UserCard>>> call,
                               Response<RspModel<List<UserCard>>> response) {
            RspModel<List<UserCard>> rspModel = response.body();
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

package com.cloud.factory;

import android.support.annotation.StringRes;
import android.util.Log;

import com.cloud.common.base.BaseApplication;
import com.cloud.factory.data.DataSource;
import com.cloud.factory.data.group.GroupCanter;
import com.cloud.factory.data.group.GroupDispatch;
import com.cloud.factory.data.massage.MessageCanter;
import com.cloud.factory.data.massage.MessageDispatch;
import com.cloud.factory.data.user.UserCanter;
import com.cloud.factory.data.user.UserDispatch;
import com.cloud.factory.model.api.PushModel;
import com.cloud.factory.model.api.RspModel;
import com.cloud.factory.model.card.GroupCard;
import com.cloud.factory.model.card.GroupMemberCard;
import com.cloud.factory.model.card.MessageCard;
import com.cloud.factory.model.card.UserCard;
import com.cloud.factory.persistence.Account;
import com.cloud.factory.util.DBFlowExclusionStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by fmm on 2017/8/24.
 */

public class Factory {
    private static final String TAG = Factory.class.getSimpleName();
    private static Factory instance;
    static {
        instance = new Factory();
    }

    private final ExecutorService executor;
    private  static  Gson mGson;

    private Factory(){
        // 新建一个4个线程的线程池
        executor = Executors.newFixedThreadPool(4);

        mGson =     new GsonBuilder()
                // 设置时间格式
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
                // 设置一个过滤器，数据库级别的Model不进行Json转换
                .setExclusionStrategies(new DBFlowExclusionStrategy())
                .create();


    }

    public static BaseApplication app(){
        return BaseApplication.getInstance();
    }

    public static void setUp(){

        //数据库初始化
        FlowManager.init(new FlowConfig.Builder(app())
                .openDatabasesOnInit(true)// 数据库初始化的时候就开始打开
                .build());

        //数据持久化操作
        Account.load(app());

    }
    public static void runOnAsync(Runnable runnable){
        // 拿到单例，拿到线程池，然后异步执行
        instance.executor.submit(runnable);
    }

    public static Gson getGson(){
        return instance.mGson;
    }

    /**
     * 进行错误Code的解析，
     * 把网络返回的Code值进行统一的规划并返回为一个String资源
     *
     * @param model    RspModel
     * @param callback DataSource.FailedCallback 用于返回一个错误的资源Id
     */
    public static void decodeRspCode(RspModel model, DataSource.FailedCallback callback) {
        if (model == null)
            return;

        // 进行Code区分
        switch (model.getCode()) {
            case RspModel.SUCCEED:
                return;
            case RspModel.ERROR_SERVICE:
                decodeRspCode(R.string.data_rsp_error_service, callback);
                break;
            case RspModel.ERROR_NOT_FOUND_USER:
                decodeRspCode(R.string.data_rsp_error_not_found_user, callback);
                break;
            case RspModel.ERROR_NOT_FOUND_GROUP:
                decodeRspCode(R.string.data_rsp_error_not_found_group, callback);
                break;
            case RspModel.ERROR_NOT_FOUND_GROUP_MEMBER:
                decodeRspCode(R.string.data_rsp_error_not_found_group_member, callback);
                break;
            case RspModel.ERROR_CREATE_USER:
                decodeRspCode(R.string.data_rsp_error_create_user, callback);
                break;
            case RspModel.ERROR_CREATE_GROUP:
                decodeRspCode(R.string.data_rsp_error_create_group, callback);
                break;
            case RspModel.ERROR_CREATE_MESSAGE:
                decodeRspCode(R.string.data_rsp_error_create_message, callback);
                break;
            case RspModel.ERROR_PARAMETERS:
                decodeRspCode(R.string.data_rsp_error_parameters, callback);
                break;
            case RspModel.ERROR_PARAMETERS_EXIST_ACCOUNT:
                decodeRspCode(R.string.data_rsp_error_parameters_exist_account, callback);
                break;
            case RspModel.ERROR_PARAMETERS_EXIST_NAME:
                decodeRspCode(R.string.data_rsp_error_parameters_exist_name, callback);
                break;
            case RspModel.ERROR_ACCOUNT_TOKEN:
                BaseApplication.showToast(R.string.data_rsp_error_account_token);
                instance.logout();
                break;
            case RspModel.ERROR_ACCOUNT_LOGIN:
                decodeRspCode(R.string.data_rsp_error_account_login, callback);
                break;
            case RspModel.ERROR_ACCOUNT_REGISTER:
                decodeRspCode(R.string.data_rsp_error_account_register, callback);
                break;
            case RspModel.ERROR_ACCOUNT_NO_PERMISSION:
                decodeRspCode(R.string.data_rsp_error_account_no_permission, callback);
                break;
            case RspModel.ERROR_UNKNOWN:
            default:
                decodeRspCode(R.string.data_rsp_error_unknown, callback);
                break;
        }
    }

    private static void decodeRspCode(@StringRes final int resId,
                                      final DataSource.FailedCallback callback) {
        if (callback != null)
            callback.onDataNotAvailable(resId);
    }

    /**
     * 收到账户退出的消息需要进行账户退出重新登录
     */
    private void logout() {

    }

    public static void dispatchPush(String str){
        // 首先检查登录状态
        if (!Account.isLogin())
            return;

        PushModel model = PushModel.decode(str);
        if (model == null)
            return;

        Log.e(TAG, model.toString());
        // 对推送集合进行遍历
        for (PushModel.Entity entity : model.getEntities()) {
            switch (entity.type) {
                case PushModel.ENTITY_TYPE_LOGOUT:
                    instance.logout();
                    // 退出情况下，直接返回，并且不可继续
                    return;

                case PushModel.ENTITY_TYPE_MESSAGE: {
                    // 普通消息
                    MessageCard card = getGson().fromJson(entity.content, MessageCard.class);
                    getMessageCanter().dispatch(card);
                    break;
                }

                case PushModel.ENTITY_TYPE_ADD_FRIEND: {
                    // 好友添加
                    UserCard card = getGson().fromJson(entity.content, UserCard.class);
                    getUserCanter().dispatch(card);
                    break;
                }

                case PushModel.ENTITY_TYPE_ADD_GROUP: {
                    // 添加群
                    GroupCard card = getGson().fromJson(entity.content, GroupCard.class);
                    getGroupCanter().dispatch(card);
                    break;
                }

                case PushModel.ENTITY_TYPE_ADD_GROUP_MEMBERS:
                case PushModel.ENTITY_TYPE_MODIFY_GROUP_MEMBERS: {
                    // 群成员变更, 回来的是一个群成员的列表
                    Type type = new TypeToken<List<GroupMemberCard>>() {
                    }.getType();
                    List<GroupMemberCard> card = getGson().fromJson(entity.content, type);
                    // 把数据集合丢到数据中心处理
                    getGroupCanter().dispatch(card.toArray(new GroupMemberCard[0]));
                    break;
                }
                case PushModel.ENTITY_TYPE_EXIT_GROUP_MEMBERS: {
                    // TODO 成员退出的推送
                }

            }
        }
    }

    /**
     * 获取用户中心的实现类
     * @return
     */
    public static UserCanter getUserCanter(){
        return UserDispatch.getInstance();
    }

    /**
     * 获取消息中心的实现类
     * @return
     */
    public static MessageCanter getMessageCanter(){
        return MessageDispatch.getInstance();
    }

    /**
     * 获取群中心的实现类
     * @return
     */
    public static GroupCanter getGroupCanter(){
        return GroupDispatch.getInstance();
    }
}

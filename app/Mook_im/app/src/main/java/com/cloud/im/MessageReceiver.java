package com.cloud.im;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.cloud.common.utils.logger.Logger;
import com.cloud.factory.Factory;
import com.cloud.factory.data.helper.AccountHelper;
import com.cloud.factory.persistence.Account;
import com.igexin.sdk.PushConsts;

/**
 * Created by fmm on 2017/9/7.
 * 个推 接收者
 */

public class MessageReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent==null){
            return;
        }
        Bundle bundle = intent.getExtras();
        switch (bundle.getInt(PushConsts.CMD_ACTION)){
            case PushConsts.GET_CLIENTID:
                Logger.getLogger().d("GET_CLIENTID:" + bundle.toString());
                //获得pushID
                onClientInit(bundle.getString("clientid"));
                break;
            case PushConsts.GET_MSG_DATA:
                // 常规消息送达
                byte[] payload = bundle.getByteArray("payload");
                if (payload != null) {
                    String message = new String(payload);

                    Logger.getLogger().d("GET_CLIENTID:" + message);
                    onMessageArrived(message);
                }
            default:
                Logger.getLogger().d("OTHER:" + bundle.toString());
                break;
        }
    }

    /**
     * 当Id初始化的时候
     *
     * @param cid 设备Id
     */
    private void onClientInit(String cid){
        // 设置设备Id
        Account.setPushId(cid);
        if(Account.isLogin()){
            // 账户登录状态，进行一次PushId绑定
            // 没有登录是不能绑定PushId的
            AccountHelper.bindPush(null);
        }

    }

    /**
     * 消息达到时
     *
     * @param message 新消息
     */
    private void onMessageArrived(String message) {
        // 交给Factory处理
        Factory.dispatchPush(message);
    }
}

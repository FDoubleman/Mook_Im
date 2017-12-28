package net.qiujuer.web.italker.push.utils;

import com.gexin.rp.sdk.base.IBatch;
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import com.google.common.base.Strings;
import net.qiujuer.web.italker.push.bean.api.base.PushModel;
import net.qiujuer.web.italker.push.bean.db.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by fmm on 2017/12/27.
 * 消息推送工具类
 *  1、添加消息
 *  2、发送消息
 */
public class PushDispatcher {
    //定义常量, appId、appKey、masterSecret 采用本文档 "第二步 获取访问凭证 "中获得的应用配置
    private static String appId = "bkx8laFWaxAdRGJjrsZN73";
    private static String appKey = "rfmX3wLHXu5tUEnKi1IMy9";
    private static String masterSecret = "5hBuiF8DBD91GULN85X9L9";
    private static String url = "http://sdk.open.api.igexin.com/apiex.htm";
    private final IGtPush pusher;

    // 要收到消息的人和内容的列表
    private final List<BatchBean> beans = new ArrayList<>();
    public PushDispatcher() {
        pusher = new IGtPush(url, appKey, masterSecret);
    }

    /**
     * 添加消息
     * @param receiver 接收者的设备Id
     * @param pushModel 消息内容
     * @return 是否添加成功
     */
    public boolean add(User receiver , PushModel pushModel){
        //过滤接收者
        if(receiver ==null || pushModel ==null ||
                Strings.isNullOrEmpty(receiver.getPushId())){
            return false;
        }
        //过滤推送信息
        String pushString  = pushModel.getPushString();
        if(Strings.isNullOrEmpty(pushString)){
            return false;
        }

        // 创建消息封装体 构建一个目标+内容
        BatchBean batchBean = buildMessage(receiver.getPushId(),pushString);
        beans.add(batchBean);
        return true;
    }

    /**
     * 最终发送添加后的消息
     * @return 发送是否成功
     */
    public boolean submit(){

        //1、获得发送者
        IBatch batch = pusher.getBatch();

        // 是否有数据需要发送
        boolean haveData = false;
        //2、循环添加消息
        for (BatchBean bean : beans) {
            try {
                batch.add(bean.message,bean.target);
                haveData =true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 没有数据就直接返回
        if (!haveData)
            return false;

        //3、发送消息
        IPushResult result = null;
        try {
            result = batch.submit();
        } catch (IOException e) {
            e.printStackTrace();

            //4、消息重发 失败情况下尝试重复发送一次
            try {
                batch.retry();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        //5、发送结果
        if (result != null) {
            try {
                Logger.getLogger("PushDispatcher")
                        .log(Level.INFO, (String) result.getResponse().get("result"));
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Logger.getLogger("PushDispatcher")
                .log(Level.WARNING, "推送服务器响应异常！！！");
        return false;
    }



    /**
     * 对要发送的数据进行格式化封装
     *
     * @param clientId 接收者的设备Id
     * @param text     要接收的数据
     * @return BatchBean
     */
    private BatchBean buildMessage(String clientId, String text) {
        // 透传消息，不是通知栏显示，而是在MessageReceiver收到
        TransmissionTemplate template = new TransmissionTemplate();
        template.setAppId(appId);
        template.setAppkey(appKey);
        template.setTransmissionContent(text);
        template.setTransmissionType(0); //这个Type为int型，填写1则自动启动app

        SingleMessage message = new SingleMessage();
        message.setData(template); // 把透传消息设置到单消息模版中
        message.setOffline(true); // 是否运行离线发送
        message.setOfflineExpireTime(24 * 3600 * 1000); // 离线消息时常

        // 设置推送目标，填入appid和clientId
        Target target = new Target();
        target.setAppId(appId);
        target.setClientId(clientId);

        // 返回一个封装
        return new BatchBean(message, target);
    }



    // 给每个人发送消息的一个Bean封装
    private static class BatchBean{
        SingleMessage message;
        Target target;

        public BatchBean(SingleMessage message, Target target) {
            this.message = message;
            this.target = target;
        }
    }

}

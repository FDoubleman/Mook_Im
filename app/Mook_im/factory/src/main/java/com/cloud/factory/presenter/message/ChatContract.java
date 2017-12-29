package com.cloud.factory.presenter.message;

import com.cloud.factory.model.db.Group;
import com.cloud.factory.model.db.Message;
import com.cloud.factory.model.db.User;
import com.cloud.factory.presenter.BaseContract;

/**
 * Created by fmm on 2017/12/29.
 * 聊天契约
 */

public interface ChatContract {

    interface Presenter extends BaseContract.Presenter{
        //发送文字
        void pushText(String content);
        //发送语音
        void pushAudio(String path);
        //发送图片
        void pushImages(String[] paths);
        //重新发送一个消息
        boolean rePush(Message message);
    }

    //界面的基类
    interface View<InitModel> extends BaseContract.RecyclerView<Presenter,Message>{
        //初始化model
        void onInit(InitModel model);
    }

    // 人聊天的界面
    interface UserView extends View<User> {

    }

    // 群聊天的界面
    interface GroupView extends View<Group> {

    }
}

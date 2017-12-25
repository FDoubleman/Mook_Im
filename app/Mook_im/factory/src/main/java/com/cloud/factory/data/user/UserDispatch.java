package com.cloud.factory.data.user;

import android.text.TextUtils;

import com.cloud.factory.data.helper.DBHelper;
import com.cloud.factory.model.card.UserCard;
import com.cloud.factory.model.db.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by fmm on 2017/12/25.
 */

public class UserDispatch implements UserCanter {

    // 单线程池；处理卡片一个个的消息进行处理
    private final Executor executor = Executors.newSingleThreadExecutor();
    private static UserDispatch instance;

    public static UserDispatch getInstance() {
        if (instance == null) {
            synchronized (UserDispatch.class) {
                if (instance == null) {
                    instance = new UserDispatch();
                }
            }
        }
        return instance;
    }

    @Override
    public void dispatch(UserCard... userCards) {
        if (userCards == null || userCards.length == 0) {
            return;
        }
        // TODO 为什么呢？？？ 丢到单线程池中
        executor.execute(new UserCardHandler(userCards));
    }

    private class UserCardHandler implements Runnable{
        private final UserCard[] cards;

        UserCardHandler (UserCard[] userCards){
            cards =userCards;
        }
        @Override
        public void run() {
            List<User> cardList =new ArrayList<>();
            //过滤 数据
            for (UserCard card : cards) {
                // 进行过滤操作
                if(card ==null || TextUtils.isEmpty(card.getId())){
                    continue;
                }
                //添加集合
                cardList.add(card.build());
            }
            // 进行数据库存储，并分发通知, 异步的操作
            DBHelper.save(User.class,cardList.toArray(new User[0]));
        }
    }

}

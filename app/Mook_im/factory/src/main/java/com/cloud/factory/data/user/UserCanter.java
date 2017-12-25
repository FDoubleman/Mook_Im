package com.cloud.factory.data.user;

import com.cloud.factory.model.card.UserCard;

/**
 * Created by fmm on 2017/12/25.
 * 用户中心基本定义
 */

public interface UserCanter {
    //分发处理一堆用户卡片的信息，并更新到数据库
    void dispatch(UserCard... userCards);
}

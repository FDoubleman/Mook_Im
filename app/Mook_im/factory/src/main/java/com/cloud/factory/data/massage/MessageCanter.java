package com.cloud.factory.data.massage;

import com.cloud.factory.model.card.MessageCard;

/**
 * Created by fmm on 2017/12/25.
 * 消息中心基本定义
 */

public interface MessageCanter {
    //分发处理一堆用户卡片的信息，并更新到数据库
    void dispatch(MessageCard... userCards);
}

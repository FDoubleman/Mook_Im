package com.cloud.factory.data.massage;

import com.cloud.factory.data.DbDataSource;
import com.cloud.factory.model.db.Message;

/**
 * Created by fmm on 2017/12/29.
 * 消息的数据源定义，他的实现是：MessageRepository
 * 关注的对象是Message表
 */

public interface MessageDataSource extends DbDataSource<Message> {
}

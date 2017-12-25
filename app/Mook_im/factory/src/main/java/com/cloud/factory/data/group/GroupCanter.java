package com.cloud.factory.data.group;

import com.cloud.factory.model.card.GroupCard;
import com.cloud.factory.model.card.GroupMemberCard;

/**
 * Created by fmm on 2017/12/25.
 */

public interface GroupCanter {

    // 群卡片的处理
    void dispatch(GroupCard... cards);

    // 群成员的处理
    void dispatch(GroupMemberCard... cards);
}

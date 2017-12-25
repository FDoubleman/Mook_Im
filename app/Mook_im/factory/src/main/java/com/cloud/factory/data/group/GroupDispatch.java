package com.cloud.factory.data.group;

import com.cloud.factory.data.helper.DBHelper;
import com.cloud.factory.data.helper.GroupHelper;
import com.cloud.factory.data.helper.UserHelper;
import com.cloud.factory.model.card.GroupCard;
import com.cloud.factory.model.card.GroupMemberCard;
import com.cloud.factory.model.db.Group;
import com.cloud.factory.model.db.GroupMember;
import com.cloud.factory.model.db.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by fmm on 2017/12/25.
 */

public class GroupDispatch implements GroupCanter{

    private static GroupDispatch instance;
    private Executor executor = Executors.newSingleThreadExecutor();
    private GroupDispatch(){

    }
    public static GroupDispatch getInstance(){
        if(instance ==null){
            synchronized (GroupDispatch.class){
                instance =new GroupDispatch();
            }
        }
        return instance;
    }

    @Override
    public void dispatch(GroupCard... cards) {
        if (cards == null || cards.length == 0)
            return;
        executor.execute(new GroupHandler(cards));
    }

    @Override
    public void dispatch(GroupMemberCard... cards) {
        if (cards == null || cards.length == 0)
            return;
        executor.execute(new GroupMemberRspHandler(cards));
    }

    private class GroupMemberRspHandler implements Runnable {
        private final GroupMemberCard[] cards;

        GroupMemberRspHandler(GroupMemberCard[] cards) {
            this.cards = cards;
        }

        @Override
        public void run() {
            List<GroupMember> members = new ArrayList<>();
            for (GroupMemberCard model : cards) {
                // 成员对应的人的信息
                User user = UserHelper.search(model.getUserId());
                // 成员对应的群的信息
                Group group = GroupHelper.find(model.getGroupId());
                if (user != null && group != null) {
                    GroupMember member = model.build(group, user);
                    members.add(member);
                }
            }
            if (members.size() > 0)
                DBHelper.save(GroupMember.class, members.toArray(new GroupMember[0]));
        }
    }

    /**
     * 把群Card处理为群DB类
     */
    private class GroupHandler implements Runnable {
        private final GroupCard[] cards;

        GroupHandler(GroupCard[] cards) {
            this.cards = cards;
        }

        @Override
        public void run() {
            List<Group> groups = new ArrayList<>();
            for (GroupCard card : cards) {
                // 搜索管理员
                User owner = UserHelper.search(card.getOwnerId());
                if (owner != null) {
                    Group group = card.build(owner);
                    groups.add(group);
                }
            }
            if (groups.size() > 0)
                DBHelper.save(Group.class, groups.toArray(new Group[0]));
        }
    }
}

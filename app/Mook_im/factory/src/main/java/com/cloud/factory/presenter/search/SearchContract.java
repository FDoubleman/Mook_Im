package com.cloud.factory.presenter.search;

import com.cloud.factory.model.card.GroupCard;
import com.cloud.factory.model.card.UserCard;
import com.cloud.factory.presenter.BaseContract;

import java.util.List;

/**
 * Created by fmm on 2017/10/5.
 */

public interface SearchContract extends BaseContract {

    interface SearchPresenter extends BaseContract.Presenter{
        void search(String content);
    }

    interface UserView extends BaseContract.View<SearchPresenter>{
        void onSearchDone(List<UserCard> userCards);
    }

    interface GroupView extends BaseContract.View<SearchPresenter>{
        void onSearchDone(List<GroupCard> userCards);
    }
}

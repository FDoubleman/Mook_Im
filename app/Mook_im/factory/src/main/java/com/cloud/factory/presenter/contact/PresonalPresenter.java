package com.cloud.factory.presenter.contact;

import com.cloud.factory.Factory;
import com.cloud.factory.data.helper.UserHelper;
import com.cloud.factory.model.db.User;
import com.cloud.factory.persistence.Account;
import com.cloud.factory.presenter.BasePresenter;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

/**
 * Created by fmm on 2017/10/12.
 */

public class PresonalPresenter extends BasePresenter<PersonalContract.View> implements PersonalContract.Presenter {
    private User mUser;
    public PresonalPresenter(PersonalContract.View view) {
        super(view);
    }

    @Override
    public void start() {
        super.start();
        Factory.runOnAsync(new Runnable() {
            @Override
            public void run() {
             PersonalContract.View view=   getView();
                if(view!=null){
                    String id  = view.getUserId();
                    User user = UserHelper.searchFirstNet(id);
                    onLoaded(user);
                }
            }
        });
    }

    private void onLoaded( final User user) {

        //是否是自己
        boolean isSelf = Account.getUserId().equalsIgnoreCase(user.getId());
        //是否已经关注
        final boolean isFollow = isSelf ||user.isFollow();
        //已经关注同时不是自己才能聊天
        final boolean isSayHello = !isSelf && isFollow;

        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                PersonalContract.View view =  getView();
                if(view ==null){
                    return;
                }
                view.onLoadDone(user);
                view.allowSayHello(isSayHello);
                view.setFollowStatus(isFollow);

            }
        });

    }

    @Override
    public User getUserPersonal() {
        return mUser;
    }


}

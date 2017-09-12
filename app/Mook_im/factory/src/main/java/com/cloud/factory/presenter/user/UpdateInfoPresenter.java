package com.cloud.factory.presenter.user;

import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.util.Log;

import com.cloud.factory.Factory;
import com.cloud.factory.R;
import com.cloud.factory.data.DataSource;
import com.cloud.factory.data.helper.UserHelper;
import com.cloud.factory.model.api.user.UserUpdateModel;
import com.cloud.factory.model.card.UserCard;
import com.cloud.factory.model.db.User;
import com.cloud.factory.net.UploadHelp;
import com.cloud.factory.presenter.BasePresenter;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

/**
 * Created by fmm on 2017/9/8.
 */

public class UpdateInfoPresenter extends BasePresenter<UpdateInfoContract.View>
        implements UpdateInfoContract.Presenter, DataSource.CallBack<UserCard> {


    public UpdateInfoPresenter(UpdateInfoContract.View view) {
        super(view);
    }

    @Override
    public void update(final String portrait, final String desc, final boolean isMan) {
        //1、上传头像
        start();
        //2、字段校验
        final UpdateInfoContract.View view = getView();
        if (TextUtils.isEmpty(portrait) || TextUtils.isEmpty(desc)) {
            view.showError(R.string.data_account_update_invalid_parameter);
        } else {
            Log.e("TAG", "localPath:" + portrait);
            Factory.runOnAsync(new Runnable() {
                @Override
                public void run() {
                    //2、获得头像的地址
                    String url = UploadHelp.uploadPortrait(portrait);
                    Log.e("TAG", "url:" + url);
                    if (TextUtils.isEmpty(url)) {
                        view.showError(R.string.data_upload_error);
                    } else {
                        //3、更新个人信息
                        UserUpdateModel model = new UserUpdateModel("", url,
                                desc, isMan ? User.SEX_MAN : User.SEX_WOMAN);
                        //4、更新本地个人信息
                        UserHelper.updateUserInfo(model, UpdateInfoPresenter.this);

                    }
                }
            });
        }
    }

    @Override
    public void onDataLoad(UserCard user) {
        //5、更新UI
        final UpdateInfoContract.View view = getView();
        if (view == null)
            return;
        // 强制执行在主线程中
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                view.updateSeccess();
            }
        });
    }

    @Override
    public void onDataNotLoad(@StringRes final int strRes) {
        final UpdateInfoContract.View view = getView();
        if (view == null)
            return;
        // 强制执行在主线程中
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                view.showError(strRes);
            }
        });

    }
}

package com.cloud.factory.presenter.group;

import android.text.TextUtils;

import com.cloud.factory.Factory;
import com.cloud.factory.R;
import com.cloud.factory.data.DataSource;
import com.cloud.factory.data.helper.GroupHelper;
import com.cloud.factory.data.helper.UserHelper;
import com.cloud.factory.model.api.group.GroupCreateModel;
import com.cloud.factory.model.card.GroupCard;
import com.cloud.factory.model.db.view.UserSampleModel;
import com.cloud.factory.net.UploadHelp;
import com.cloud.factory.presenter.BaseRecyclerPresenter;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by fmm on 2018/1/4.
 */

public class GroupCreatePresenter
        extends BaseRecyclerPresenter<GroupCreateContract.ViewModel, GroupCreateContract.View>
        implements GroupCreateContract.Presenter, DataSource.Callback<GroupCard> {
    //要加入群的人
    private Set<String> users = new HashSet<>();

    public GroupCreatePresenter(GroupCreateContract.View view) {
        super(view);
    }

    @Override
    public void start() {
        super.start();
        // 加载联系人列表
        Factory.runOnAsync(loader);
    }

    @Override
    public void create(final String name, final String desc, final String picture) {
        GroupCreateContract.View view = getView();
        view.onLoading();
        // 1、判断参数
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(desc) ||
                TextUtils.isEmpty(picture) || users.size() == 0) {
            view.showError(R.string.label_group_create_invalid);
            return;
        }
        // 2、上传图片
        Factory.runOnAsync(new Runnable() {
            @Override
            public void run() {
                String url = uploadPicture(picture);
                if (TextUtils.isEmpty(url))
                    return;
                // 进行网络请求
                GroupCreateModel model = new GroupCreateModel(name, desc, url, users);
                GroupHelper.create(model, GroupCreatePresenter.this);
            }
        });
        //3、请求接口
        //4、处理回调

    }

    // 同步上传操作
    private String uploadPicture(String path) {
        String url = UploadHelp.uploadPortrait(path);
        if (TextUtils.isEmpty(url)) {
            // 切换到UI线程 提示信息
            Run.onUiAsync(new Action() {
                @Override
                public void call() {
                    GroupCreateContract.View view = getView();
                    if (view != null) {
                        view.showError(R.string.data_upload_error);
                    }
                }
            });
        }
        return url;
    }

    @Override
    public void changeSelect(GroupCreateContract.ViewModel model, boolean isSelected) {
        if (isSelected) {
            users.add(model.author.getId());
        } else {
            users.remove(model.author.getId());
        }

    }

    @Override
    public void onDataLoaded(GroupCard groupCard) {
        // 成功
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                GroupCreateContract.View view = getView();
                if (view != null) {
                    view.onCreateSucceed();
                }
            }
        });
    }

    @Override
    public void onDataNotAvailable(final int strRes) {
        // 失败情况
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                GroupCreateContract.View view = getView();
                if (view != null) {
                    view.showError(strRes);
                }
            }
        });
    }


    private Runnable loader = new Runnable() {
        @Override
        public void run() {
            List<UserSampleModel> UserSampleModel = UserHelper.getSampleContact();
            List<GroupCreateContract.ViewModel> viewModelList = new ArrayList<>();
            for (UserSampleModel sampleModel : UserSampleModel) {
                GroupCreateContract.ViewModel viewModel = new GroupCreateContract.ViewModel();
                viewModel.author = sampleModel;

                viewModelList.add(viewModel);
            }
            //刷新UI
            refreshData(viewModelList);
        }
    };
}

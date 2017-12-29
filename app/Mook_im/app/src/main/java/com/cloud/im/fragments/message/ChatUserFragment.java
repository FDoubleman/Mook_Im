package com.cloud.im.fragments.message;


import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;
import com.cloud.common.widget.PortraitView;
import com.cloud.factory.model.db.User;
import com.cloud.factory.presenter.message.ChatContract;
import com.cloud.factory.presenter.message.ChatUserPresenter;
import com.cloud.im.R;
import com.cloud.im.activity.PersonalActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by fmm on 2017/12/28.
 * 用户聊天页面
 */

public class ChatUserFragment extends ChatFragment<User> implements ChatContract.UserView {


    private MenuItem mUserInfoMenuItem;
    @BindView(R.id.im_portrait)
    PortraitView mPortrait;
    @Override
    public int getContentLayoutId() {
        return R.layout.fragment_chat_user;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        //ImageUtil.loadViewBg(getContext(),R.drawable.default_banner_chat,mCollapsingLayout);

        Glide.with(this)
                .load(R.drawable.default_banner_chat)
                .centerCrop()
                .into(new ViewTarget<CollapsingToolbarLayout, GlideDrawable>(mCollapsingLayout) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        this.view.setContentScrim(resource.getCurrent());
                    }
                });
    }

    @Override
    protected void initToolbar() {
        super.initToolbar();
        Toolbar toolbar =mToolbar;
        toolbar.inflateMenu(R.menu.chat_user);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
               int id = item.getItemId();
                if(id ==R.id.action_person){
                    onPortraitClick();
                }
                return false;
            }
        });
        // 拿到菜单Icon
        mUserInfoMenuItem = toolbar.getMenu().findItem(R.id.action_person);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        super.onOffsetChanged(appBarLayout, verticalOffset);
        View view = mPortrait;//头像
        MenuItem menuItem = mUserInfoMenuItem;//菜单选项

        if (view == null || menuItem == null)
            return;

        if (verticalOffset == 0) {
            // 完全展开
            view.setVisibility(View.VISIBLE);
            view.setScaleX(1);
            view.setScaleY(1);
            view.setAlpha(1);

            // 隐藏菜单
            menuItem.setVisible(false);
            menuItem.getIcon().setAlpha(0);
        } else {
            // abs 运算
            verticalOffset = Math.abs(verticalOffset);
            final int totalScrollRange = appBarLayout.getTotalScrollRange();
            if (verticalOffset >= totalScrollRange) {
                // 关闭状态
                view.setVisibility(View.INVISIBLE);
                view.setScaleX(0);
                view.setScaleY(0);
                view.setAlpha(0);

                // 显示菜单
                menuItem.setVisible(true);
                menuItem.getIcon().setAlpha(255);

            } else {
                // 中间状态
                float progress = 1 - verticalOffset / (float) totalScrollRange;
                view.setVisibility(View.VISIBLE);
                view.setScaleX(progress);
                view.setScaleY(progress);
                view.setAlpha(progress);
                // 和头像恰好相反
                menuItem.setVisible(true);
                menuItem.getIcon().setAlpha(255 - (int) (255 * progress));
            }
        }
    }

    @OnClick(R.id.im_portrait)
    void onPortraitClick() {
        PersonalActivity.show(getContext(), mReceiverId);
    }

    @Override
    protected ChatContract.Presenter initPresenter() {
        return new ChatUserPresenter(this,mReceiverId);
    }

    @Override
    public void onInit(User user) {
        // 对和你聊天的朋友的信息进行初始化操作
        mPortrait.setup(Glide.with(this), user.getPortrait());
        mCollapsingLayout.setTitle(user.getName());
    }
}

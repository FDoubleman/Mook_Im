package com.cloud.im.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.TextView;

import com.cloud.common.base.BaseActivity;
import com.cloud.common.utils.ImageUtil;
import com.cloud.common.utils.NavHelper;
import com.cloud.common.widget.PortraitView;
import com.cloud.im.R;
import com.cloud.im.fragments.ConnectFragment;
import com.cloud.im.fragments.GroupFragment;
import com.cloud.im.fragments.HomeFragment;

import net.qiujuer.genius.ui.Ui;
import net.qiujuer.genius.ui.widget.FloatActionButton;

import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener,
        NavHelper.OnTabChangedListener<Integer> {

    @BindView(R.id.appbar)
    View mAppbar;

    @BindView(R.id.tv_title)
    TextView mTvTitle;

    @BindView(R.id.iv_portrait)
    PortraitView ivPortrait;

    @BindView(R.id.navigation)
    BottomNavigationView mNavigation;

    @BindView(R.id.btn_action)
    FloatActionButton fatAction;
    private NavHelper<Integer> mNavHelper;


    public static void show(Context context){
        context.startActivity(new Intent(context, MainActivity.class));
    }

    @Override
    public int getContentLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        mNavHelper = new NavHelper<>(this,getSupportFragmentManager(),R.id.fl_conter,this);
        mNavHelper.add(R.id.action_home,new NavHelper.Tab<Integer>(HomeFragment.class,R.string.action_home))
                .add(R.id.action_group,new NavHelper.Tab<Integer>(GroupFragment.class,R.string.action_group))
                .add(R.id.action_contact,new NavHelper.Tab<Integer>(ConnectFragment.class,R.string.action_contact));

        mNavigation.setOnNavigationItemSelectedListener(this);
        ImageUtil.loadViewBg(this,R.drawable.bg_src_morning,mAppbar);
    }

    @Override
    protected void initData() {
        super.initData();
        Menu menu =mNavigation.getMenu();
        menu.performIdentifierAction(R.id.action_home,0);
    }

    @OnClick(R.id.iv_search)
    void onSearch(){

    }

    @OnClick(R.id.btn_action)
    void onActionClick() {
        AccountActivity.show(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        return mNavHelper.performClickMenu(item.getItemId());//返回false 消费事件 item切换效果不变
    }

    @Override
    public void onTabChanged(NavHelper.Tab<Integer> newTab, NavHelper.Tab<Integer> oldTab) {
        mTvTitle.setText(newTab.extra);

        // 对浮动按钮进行隐藏与显示的动画
        float transY = 0;
        float rotation = 0;
        if (Objects.equals(newTab.extra, R.string.action_home)) {
            // 主界面时隐藏
            transY = Ui.dipToPx(getResources(), 76);
        } else {
            // transY 默认为0 则显示
            if (Objects.equals(newTab.extra, R.string.action_group)) {
                // 群
                fatAction.setImageResource(R.drawable.ic_group_add);
                rotation = -360;
            } else {
                // 联系人
                fatAction.setImageResource(R.drawable.ic_contact_add);
                rotation = 360;
            }
        }

        // 开始动画
        // 旋转，Y轴位移，弹性差值器，时间
        fatAction.animate()
                .rotation(rotation)
                .translationY(transY)
                .setInterpolator(new AnticipateOvershootInterpolator(1))
                .setDuration(480)
                .start();
    }
}

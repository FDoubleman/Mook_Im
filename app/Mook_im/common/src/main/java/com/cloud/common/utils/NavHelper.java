package com.cloud.common.utils;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.SparseArray;

import com.cloud.common.base.BaseFragment;

/**
 * Created by fmm on 2017/7/23.
 * 底部 导航栏 控制fragment 显示隐藏
 */

public class NavHelper<T> {
    // 所有的Tab集合
    private final SparseArray<Tab<T>> tabs = new SparseArray<>();

    private Context mContext;
    private FragmentManager mFragmentManager;
    private int mContentID;//显示fragment layout 的容器
    private OnTabChangedListener mOnTabChangedListener;//tab item 点击监听
    private Tab<T> mCurrentTab;

    public NavHelper(Context context, FragmentManager fragmentManager,
                     int contentID, OnTabChangedListener onTabChangedListener) {
        mContext = context;
        mFragmentManager = fragmentManager;
        mContentID = contentID;
        mOnTabChangedListener = onTabChangedListener;
    }


    /**
     * 添加 tab
     * @param menuID menuID
     * @param tab tab
     * @return NavHelper
     */
    public NavHelper<T> add(int menuID, Tab<T> tab) {
        tabs.put(menuID, tab);
        return this;
    }


    /**
     * 获取当前的显示的Tab
     *
     * @return 当前的Tab
     */
    public Tab<T> getCurrentTab() {
        return mCurrentTab;
    }

    /**
     * 接受点击事件
     * @param menuItemId 点的item id
     * @return 点击处理是否成功
     */
    public boolean performClickMenu(int menuItemId) {
        //第一次点击
        Tab<T> tTab = tabs.get(menuItemId);
        if (tTab != null) {
            doSelect(tTab);
            return true;
        }
        return false;//异常情况
    }

    /**
     * 处理选择事件 并过滤重复点击
     * @param tTab
     */
    private void doSelect(Tab<T> tTab) {
        Tab<T> oldTab = null;
        if (mCurrentTab != null) {
            oldTab = mCurrentTab;
            if (oldTab == tTab) {
                //连续点击
                notifyTabReSelect(tTab);

                return;
            }
        }
        mCurrentTab = tTab;
        doTabChanged(mCurrentTab, oldTab);
    }

    /**
     * 第一次点击 添加界面 ，复用已添加处理
     * @param newTab
     * @param oldTab
     */
    private void doTabChanged(Tab<T> newTab, Tab<T> oldTab) {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        if (oldTab != null) {
            if (oldTab.mFragment != null) {
                // 从界面移除，但是还在Fragment的缓存空间中
                ft.detach(oldTab.mFragment);
            }
        }
        if (newTab != null) {
            if (newTab.mFragment == null) {
                Fragment fragment = BaseFragment.instantiate(mContext, newTab.cls.getName(), null);
                newTab.mFragment = fragment;
                //添加
                ft.add(mContentID, fragment, newTab.cls.getName());
            } else {
                //已添加过 既存在缓存  显示缓存
                ft.attach(newTab.mFragment);
            }

        }
        ft.commit();
        // 通知回调
        notifyTabSelect(newTab, oldTab);
    }

    /**
     * tab 重复点击
     */
    private void notifyTabReSelect(Tab<T> tTab) {

    }

    /**
     * tab 点击操作
     *
     * @param newTab
     * @param oldTab
     */
    private void notifyTabSelect(Tab<T> newTab, Tab<T> oldTab) {
        if (mOnTabChangedListener != null) {
            mOnTabChangedListener.onTabChanged(newTab, oldTab);
        }
    }


    public static class Tab<T> {

        public Tab(Class<?> cls, T extra) {
            this.cls = cls;
            this.extra = extra;
        }

        public Class<?> cls;
        // tab传递的额外的值
        public T extra;


        //tab 内部缓存的 fragment
        Fragment mFragment;
    }

    public interface OnTabChangedListener<T> {
        void onTabChanged(Tab<T> newTab, Tab<T> oldTab);
    }
}

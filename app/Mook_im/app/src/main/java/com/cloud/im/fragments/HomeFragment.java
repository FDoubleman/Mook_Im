package com.cloud.im.fragments;

import android.view.View;

import com.cloud.common.base.BaseFragment;
import com.cloud.common.widget.GalleryView;
import com.cloud.im.R;

import butterknife.BindView;

/**
 * Created by fmm on 2017/7/23.
 */

public class HomeFragment extends BaseFragment {
    @BindView(R.id.gv_gallery)
    GalleryView gvGallery;

    @Override
    public int getContentLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);

    }

    @Override
    protected void initData() {
        super.initData();
        gvGallery.setUp(getLoaderManager(), new GalleryView.SelectedChangeListener() {
            @Override
            public void onSelectedChange(int count) {

            }
        });
    }
}

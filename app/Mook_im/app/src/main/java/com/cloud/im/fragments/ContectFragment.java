package com.cloud.im.fragments;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cloud.common.base.BasePresenterFragment;
import com.cloud.common.widget.EmptyView;
import com.cloud.common.widget.PortraitView;
import com.cloud.common.widget.recycler.RecyclerAdapter;
import com.cloud.factory.model.db.User;
import com.cloud.factory.presenter.contact.ContactContract;
import com.cloud.factory.presenter.contact.ContectPresenter;
import com.cloud.im.R;
import com.cloud.im.activity.PersonalActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by fmm on 2017/7/23.
 */

public class ContectFragment extends BasePresenterFragment<ContactContract.Presenter>
         implements ContactContract.View {

    @BindView(R.id.recycler)
    RecyclerView mRecycler;

    @BindView(R.id.empty)
    EmptyView mEmptyView;

    private RecyclerAdapter mAdapter;
    @Override
    public int getContentLayoutId() {
        return R.layout.fragment_contact;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
       mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
         mRecycler.setAdapter(mAdapter = new RecyclerAdapter<User>() {

             @Override
             protected int getItemViewType(int position, User user) {
                 return R.layout.cell_contact_list;
             }

             @Override
             protected ViewHolder<User> onCreateViewHolder(View root, int viewType) {
                 return new ContectFragment.ViewHolder(root);
             }
         });
        mAdapter.setListener(new RecyclerAdapter.AdapterListenerImp<User>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, User user) {
                super.onItemClick(holder, user);
                //进入个人详情
                PersonalActivity.show(getContext(),user.getId());
            }
        });

        //设置占位符
        mEmptyView.bind(mRecycler);
        setPlaceHolderView(mEmptyView);

    }

    @Override
    protected ContectPresenter initPresenter() {
        return new ContectPresenter(this);
    }


    @Override
    public RecyclerAdapter<User> getRecycleAdapter() {
        return mAdapter;
    }

    @Override
    public void onAdapterDataChanged() {
        mEmptyView.triggerOkOrEmpty(mAdapter.getItemCount()>0);
    }

    @Override
    protected void onFirstInit() {
        super.onFirstInit();
        mPresenter.start();
    }

    public class  ViewHolder extends RecyclerAdapter.ViewHolder<User>{
        @BindView(R.id.im_portrait)
        PortraitView mPortraitView;

        @BindView(R.id.txt_name)
        TextView mName;

        @BindView(R.id.txt_desc)
        TextView mDesc;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(User user) {
            mPortraitView.setup(Glide.with(getContext()),mData.getPortrait());
            mName.setText(mData.getName());
            mDesc.setText(mData.getDesc());
        }

        @OnClick(R.id.im_portrait)
        void onPortraitClick(){
            //进入个人详情
            PersonalActivity.show(getContext(),mData.getId());
        }

    }
}

package com.cloud.im.fragments.search;

import android.support.annotation.StringRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cloud.common.base.BasePresenterFragment;
import com.cloud.common.widget.EmptyView;
import com.cloud.common.widget.PortraitView;
import com.cloud.common.widget.recycler.RecyclerAdapter;
import com.cloud.factory.model.card.UserCard;
import com.cloud.factory.presenter.contact.FollowContract;
import com.cloud.factory.presenter.contact.FollowPresenter;
import com.cloud.factory.presenter.search.SearchContract;
import com.cloud.factory.presenter.search.SearchUserPresenter;
import com.cloud.im.R;
import com.cloud.im.activity.PersonalActivity;
import com.cloud.im.activity.SearchActivity;

import net.qiujuer.genius.ui.Ui;
import net.qiujuer.genius.ui.compat.UiCompat;
import net.qiujuer.genius.ui.drawable.LoadingCircleDrawable;
import net.qiujuer.genius.ui.drawable.LoadingDrawable;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by fmm on 2017/10/5.
 *
 */

public class SearchUserFragment extends BasePresenterFragment<SearchContract.SearchPresenter>
        implements SearchActivity.SearchListener ,SearchContract.UserView{

    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.empty)
    EmptyView empty;
    private RecyclerAdapter mRecyclerAdapter;
    @Override
    public int getContentLayoutId() {
        return R.layout.fragment_search_user;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setAdapter(mRecyclerAdapter= new RecyclerAdapter<UserCard>(){

            @Override
            protected int getItemViewType(int position, UserCard userCard) {
                return R.layout.cell_search_list;
            }

            @Override
            protected ViewHolder<UserCard> onCreateViewHolder(View root, int viewType) {
                return new SearchUserFragment.SearchListViewHold(root);
            }
        });
        // 初始化占位布局
        empty.bind(recycler);
        setPlaceHolderView(empty);
    }

    @Override
    protected void initData() {
        super.initData();
        search("");
    }

    @Override
    public void search(String content) {
        mPresenter.search(content);
    }

    @Override
    protected SearchContract.SearchPresenter initPresenter() {
        return new SearchUserPresenter(this);
    }

    @Override
    public void onSearchDone(List<UserCard> userCards) {
        //填充数据
        mRecyclerAdapter.replace(userCards);
        //如果有数据，则是OK，没有数据就显示空布局
        empty.triggerOkOrEmpty(userCards.size()>0);
    }


     class SearchListViewHold extends
             RecyclerAdapter.ViewHolder<UserCard> implements FollowContract.FollowView{

        @BindView(R.id.im_portrait)
        PortraitView mPortraitView;

        @BindView(R.id.txt_name)
        TextView mName;

        @BindView(R.id.im_follow)
        ImageView mFollow;
         FollowContract.FollowPresenter mPresenter;
        public SearchListViewHold(View itemView) {
            super(itemView);
            new FollowPresenter(this);

        }

        @Override
        protected void onBind(UserCard userCard) {
            mPortraitView.setup(Glide.with(SearchUserFragment.this), userCard);
            mName.setText(userCard.getName());
            mFollow.setEnabled(!userCard.isFollow());
        }

         /**
          * 添加关注
          */
        @OnClick(R.id.im_follow)
         void follow(){
            mPresenter.followUser(mData.getId());
        }

        @OnClick(R.id.im_portrait)
        void onPortraitclick(){
            PersonalActivity.show(getContext(),mData.getId());
        }

         @Override
         public void showError(@StringRes int msg) {
            // 更改当前界面状态
             if (mFollow.getDrawable() instanceof LoadingDrawable) {
                 // 失败则停止动画，并且显示一个圆圈
                 LoadingDrawable drawable = (LoadingDrawable) mFollow.getDrawable();
                 drawable.setProgress(1);
                 drawable.stop();
             }
         }

         @Override
         public void onLoading() {
             int minSize = (int) Ui.dipToPx(getResources(), 22);
             int maxSize = (int) Ui.dipToPx(getResources(), 30);
             // 初始化一个圆形的动画的Drawable
             LoadingDrawable drawable = new LoadingCircleDrawable(minSize, maxSize);
             drawable.setBackgroundColor(0);

             int[] color = new int[]{UiCompat.getColor(getResources(), R.color.white_alpha_208)};
             drawable.setForegroundColor(color);
             // 设置进去
             mFollow.setImageDrawable(drawable);
             // 启动动画
             drawable.start();
         }

         @Override
         public void setPresenter(FollowContract.FollowPresenter presenter) {
             mPresenter =presenter;
         }

         @Override
         public void followSuccess(UserCard userCard) {
             // 更改当前界面状态
             if (mFollow.getDrawable() instanceof LoadingDrawable) {
                 ((LoadingDrawable) mFollow.getDrawable()).stop();
                 // 设置为默认的
                 mFollow.setImageResource(R.drawable.sel_opt_done_add);
             }
             // 发起更新
             updateData(userCard);
         }
     }
}

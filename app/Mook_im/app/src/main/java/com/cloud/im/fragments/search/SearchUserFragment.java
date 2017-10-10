package com.cloud.im.fragments.search;

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
import com.cloud.factory.presenter.search.SearchContract;
import com.cloud.factory.presenter.search.SearchUserPresenter;
import com.cloud.im.R;
import com.cloud.im.activity.SearchActivity;

import java.util.List;

import butterknife.BindView;

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


     class SearchListViewHold extends RecyclerAdapter.ViewHolder<UserCard>{

        @BindView(R.id.im_portrait)
        PortraitView mPortraitView;

        @BindView(R.id.txt_name)
        TextView mName;

        @BindView(R.id.im_follow)
        ImageView mFollow;

        public SearchListViewHold(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(UserCard userCard) {
            mPortraitView.setup(Glide.with(SearchUserFragment.this), userCard);
            mName.setText(userCard.getName());
            mFollow.setEnabled(!userCard.isFollow());
        }


    }
}

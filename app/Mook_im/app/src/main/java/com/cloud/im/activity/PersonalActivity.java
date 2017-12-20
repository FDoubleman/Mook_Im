package com.cloud.im.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cloud.common.base.PresenterToolbarActivity;
import com.cloud.common.widget.PortraitView;
import com.cloud.factory.model.db.User;
import com.cloud.factory.presenter.contact.PersonalContract;
import com.cloud.factory.presenter.contact.PresonalPresenter;
import com.cloud.im.R;

import net.qiujuer.genius.res.Resource;

import butterknife.BindView;

/**
 * Created by fmm on 2017/10/11.
 */

public class PersonalActivity extends PresenterToolbarActivity<PersonalContract.Presenter>
        implements PersonalContract.View {
    private static final String BOUND_KEY_ID = "BOUND_KEY_ID";
    private String mUserId;

    @BindView(R.id.im_header)
    ImageView mHeader;
    @BindView(R.id.im_portrait)
    PortraitView mPortrait;
    @BindView(R.id.txt_name)
    TextView mName;
    @BindView(R.id.txt_desc)
    TextView mDesc;
    @BindView(R.id.txt_follows)
    TextView mFollows;
    @BindView(R.id.txt_following)
    TextView mFollowing;
    @BindView(R.id.btn_say_hello)
    Button mSayHello;
    private boolean mIsFollowUser;
    private MenuItem mFollowItem;

    public static void show(Context context,String userId){
        Intent intent = new Intent(context,PersonalActivity.class);
        intent.putExtra(BOUND_KEY_ID,userId);
        context.startActivity(intent);
    }

    @Override
    public int getContentLayoutId() {
        return R.layout.activity_personal;
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        mUserId = bundle.getString(BOUND_KEY_ID);
        return !TextUtils.isEmpty(mUserId);
    }

    @Override
    protected void initData() {
        super.initData();
        mPresenter.start();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setTitle("");
    }


    @Override
    protected PersonalContract.Presenter initPresenter() {
        return new PresonalPresenter(this);
    }

    @Override
    public String getUserId() {
        return mUserId;
    }

    @Override
    public void onLoadDone(User user) {
        if (user == null)
            return;
        mPortrait.setup(Glide.with(this), user);
        mName.setText(user.getName());
        mDesc.setText(user.getDesc());
        mFollows.setText(String.format(getString(R.string.label_follows), user.getFollows()));
        mFollowing.setText(String.format(getString(R.string.label_following), user.getFollowing()));
        hideLoading();
    }

    @Override
    public void allowSayHello(boolean isAllow) {
        mSayHello.setVisibility(isAllow ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setFollowStatus(boolean isFollow) {
        mIsFollowUser = isFollow;
        changeFollowItemStatus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.personal, menu);
        mFollowItem = menu.findItem(R.id.action_follow);
        return true;
    }

    private void changeFollowItemStatus() {

        if (mFollowItem == null)
            return;

        // 根据状态设置颜色
        Drawable drawable = mIsFollowUser ? getResources()
                .getDrawable(R.drawable.ic_favorite) :
                getResources().getDrawable(R.drawable.ic_favorite_border);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, Resource.Color.WHITE);
        mFollowItem.setIcon(drawable);
    }
}

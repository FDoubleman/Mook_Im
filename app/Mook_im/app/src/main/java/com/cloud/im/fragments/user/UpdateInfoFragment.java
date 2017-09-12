package com.cloud.im.fragments.user;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.cloud.common.base.BasePresenterFragment;
import com.cloud.common.utils.ImageUtil;
import com.cloud.common.widget.PortraitView;
import com.cloud.factory.presenter.user.UpdateInfoContract;
import com.cloud.factory.presenter.user.UpdateInfoPresenter;
import com.cloud.im.App;
import com.cloud.im.R;
import com.cloud.im.activity.MainActivity;
import com.cloud.im.fragments.media.GalleyFragment;
import com.yalantis.ucrop.UCrop;

import net.qiujuer.genius.ui.widget.Loading;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;


public class UpdateInfoFragment extends BasePresenterFragment<UpdateInfoContract.Presenter>
        implements UpdateInfoContract.View {
    public static String Tag = UpdateInfoFragment.class.getName();
    private boolean isMan = true;
    @BindView(R.id.im_sex)
    ImageView mSex;

    @BindView(R.id.edit_desc)
    EditText mDesc;

    @BindView(R.id.im_portrait)
    PortraitView mPortrait;

    @BindView(R.id.loading)
    Loading mLoading;

    @BindView(R.id.btn_submit)
    Button mSubmit;
    private String mPortraitUrl = "";

    @Override
    public int getContentLayoutId() {
        return R.layout.fragment_update_info;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
    }

    @OnClick(R.id.im_portrait)
    void onPortraitClick() {
        //显示底部图片选择器
        new GalleyFragment().setListener(new GalleyFragment.OnSelectedListener() {
            @Override
            public void onSelectedImage(String path) {
                UCrop.Options options = new UCrop.Options();
                // 设置图片处理的格式JPEG
                options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
                // 设置压缩后的图片精度
                options.setCompressionQuality(96);

                // 得到头像的缓存地址
                File dPath = App.getPortraitTmpFile();

                // 发起剪切
                UCrop.of(Uri.fromFile(new File(path)), Uri.fromFile(dPath))
                        .withAspectRatio(1, 1) // 1比1比例
                        .withMaxResultSize(520, 520) // 返回最大的尺寸
                        .withOptions(options) // 相关参数
                        .start(getActivity());

            }
        }).show(getFragmentManager(), GalleyFragment.Tag);

    }

    @Override
    protected void initData() {
        super.initData();
    }

    @OnClick(R.id.im_sex)
    void changeSex() {
        isMan = !isMan;
        mSex.setImageResource(isMan
                ? R.drawable.ic_sex_man
                : R.drawable.ic_sex_woman);
        // 设置背景的层级，切换颜色
        mSex.getBackground().setLevel(isMan ? 0 : 1);
    }

    @OnClick(R.id.btn_submit)
    void submit() {
        String desc = mDesc.getText().toString();
        mPresenter.update(mPortraitUrl, desc,isMan );
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 收到从Activity传递过来的回调，然后取出其中的值进行图片加载
        // 如果是我能够处理的类型
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            // 通过UCrop得到对应的Uri
            final Uri resultUri = UCrop.getOutput(data);
            if (resultUri != null) {
                mPortraitUrl = resultUri.getPath();
                loadPortrait(resultUri);
            }

        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        }
    }

    /**
     * 加载Uri到当前的头像中
     *
     * @param uri Uri
     */
    private void loadPortrait(Uri uri) {
        ImageUtil.loadView(this.getContext(), uri, mPortrait);
    }


    @Override
    public void updateSeccess() {
        // 更新成功跳转到主界面
        MainActivity.show(getContext());
        getActivity().finish();
    }


    @Override
    public void showError(int msg) {
        super.showError(msg);
        // 当需要显示错误的时候触发，一定是结束了

        // 停止Loading
        mLoading.stop();
        // 让控件可以输入
        mDesc.setEnabled(true);
        mPortrait.setEnabled(true);
        mSex.setEnabled(true);
        // 提交按钮可以继续点击
        mSubmit.setEnabled(true);

    }

    @Override
    public void onLoading() {
        super.onLoading();
        // 正在进行时，正在进行注册，界面不可操作
        // 开始Loading
        mLoading.start();
        // 让控件不可以输入
        mDesc.setEnabled(false);
        mPortrait.setEnabled(false);
        mSex.setEnabled(false);
        // 提交按钮不可以继续点击
        mSubmit.setEnabled(false);
    }

    @Override
    protected UpdateInfoContract.Presenter initPresenter() {
        return new UpdateInfoPresenter(this);
    }
}

package com.cloud.im.fragments.user;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import com.cloud.common.base.BaseFragment;
import com.cloud.common.utils.ImageUtil;
import com.cloud.common.widget.PortraitView;
import com.cloud.factory.Factory;
import com.cloud.factory.net.UploadHelp;
import com.cloud.im.App;
import com.cloud.im.R;
import com.cloud.im.fragments.media.GalleyFragment;
import com.yalantis.ucrop.UCrop;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;


public class UpdateInfoFragment extends BaseFragment {
    public static String Tag = UpdateInfoFragment.class.getName();

    @BindView(R.id.pv_avatar)
    PortraitView mPortrait;
    @Override
    public int getContentLayoutId() {
        return R.layout.fragment_update_info;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
    }

    @OnClick(R.id.pv_avatar)
    void onPortraitClick(){
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
        }).show(getFragmentManager(),GalleyFragment.Tag);

    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 收到从Activity传递过来的回调，然后取出其中的值进行图片加载
        // 如果是我能够处理的类型
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            // 通过UCrop得到对应的Uri
            final Uri resultUri = UCrop.getOutput(data);
            if (resultUri != null) {
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
        ImageUtil.loadView(this.getContext(),uri,mPortrait);

        // 拿到本地文件的地址
        final String localPath = uri.getPath();
        Log.e("TAG", "localPath:" + localPath);

        Factory.runOnAsync(new Runnable() {
            @Override
            public void run() {
                String url = UploadHelp.uploadPortrait(localPath);
                Log.e("TAG", "url:" + url);
            }
        });
    }
}

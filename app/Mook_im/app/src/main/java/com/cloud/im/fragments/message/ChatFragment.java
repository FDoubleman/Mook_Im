package com.cloud.im.fragments.message;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cloud.common.base.BaseFragment;
import com.cloud.common.widget.PortraitView;
import com.cloud.common.widget.adapter.TextWatcherAdapter;
import com.cloud.common.widget.recycler.RecyclerAdapter;
import com.cloud.factory.model.db.Message;
import com.cloud.factory.model.db.User;
import com.cloud.factory.persistence.Account;
import com.cloud.im.R;
import com.cloud.im.activity.MessageActivity;

import net.qiujuer.genius.ui.compat.UiCompat;
import net.qiujuer.genius.ui.widget.Loading;

import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by fmm on 2017/12/28.
 * 聊天基类
 */

public abstract class ChatFragment extends BaseFragment implements AppBarLayout.OnOffsetChangedListener {

    protected String mReceiverId;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;

    @BindView(R.id.appbar)
    AppBarLayout mAppBarLayout;

    @BindView(R.id.collapsingToolbarLayout)
    CollapsingToolbarLayout mCollapsingLayout;

    @BindView(R.id.edit_content)
    EditText mContent;

    @BindView(R.id.btn_submit)
    View mSubmit;

    @Override
    protected void initArgs(Bundle bundle) {
        super.initArgs(bundle);
        mReceiverId = bundle.getString(MessageActivity.KEY_RECEIVER_ID);
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        initToolbar();
        initAppBar();
        initEditContent();
    }


    //设置toolbar 返回键
    protected  void initToolbar(){
        Toolbar toolbar =mToolbar;
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }

    //设置appbar 折叠监听
    protected  void initAppBar(){
        mAppBarLayout.addOnOffsetChangedListener(this);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        //交给子类实现
    }

    //输入文字内容监听
    public void initEditContent(){
        mContent.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                //发送按钮监听
               String content = s.toString().trim();
               boolean needSubmit = !TextUtils.isEmpty(content);
                mSubmit.setActivated(needSubmit);
            }
        });
    }


    //聊天recycleview 的适配器
    public class Adapter extends RecyclerAdapter<Message>{

        @Override
        protected int getItemViewType(int position, Message message) {
            //自己发送的在右边  收到的在左边
            boolean isRight = Objects.equals(message.getSender().getId(), Account.getUserId());
            switch (message.getType()){
                // 文字内容
                case Message.TYPE_STR:
                    return isRight ? R.layout.cell_chat_text_right : R.layout.cell_chat_text_left;

                // 语音内容
                case Message.TYPE_AUDIO:
                    return isRight ? R.layout.cell_chat_audio_right : R.layout.cell_chat_audio_left;

                // 图片内容
                case Message.TYPE_PIC:
                    return isRight ? R.layout.cell_chat_pic_right : R.layout.cell_chat_pic_left;

                // 其他内容：文件
                default:
                    return isRight ? R.layout.cell_chat_text_right : R.layout.cell_chat_text_left;
            }
        }

        @Override
        protected ViewHolder<Message> onCreateViewHolder(View root, int viewType) {
            switch (viewType) {
                // 左右都是同一个
                case R.layout.cell_chat_text_right:
                case R.layout.cell_chat_text_left:
                    return new TextHolder(root);

                case R.layout.cell_chat_audio_right:
                case R.layout.cell_chat_audio_left:
                    return new AudioHolder(root);

                case R.layout.cell_chat_pic_right:
                case R.layout.cell_chat_pic_left:
                    return new PicHolder(root);

                // 默认情况下，返回的就是Text类型的Holder进行处理
                // 文件的一些实现
                default:
                    return new TextHolder(root);
            }
        }
    }

    //Holder的基类
    private class BaseHolder extends RecyclerAdapter.ViewHolder<Message>{

        //每个call都要有用户头像
        @BindView(R.id.im_portrait)
        PortraitView mPortrait;

        //发送进度只有发消息的时候才有
        @Nullable
        @BindView(R.id.loading)
        Loading mLoading;

        public BaseHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Message message) {
            //获得发送者
            User sender = message.getSender();
            //进行数据加载 -->懒加载需要load
            sender.load();
            //设置头像
            mPortrait.setup(Glide.with(ChatFragment.this),sender);

            //消息状态 显示
            if(mLoading !=null){
                //当前布局显示右边
                int status =message.getStatus();
                if(status ==Message.STATUS_DONE){//1、消息完成
                    // 正常状态, 隐藏Loading
                    mLoading.stop();
                    mLoading.setVisibility(View.GONE);
                }else if(status ==Message.STATUS_CREATED){//2、正在发送中...
                    // 正在发送中的状态
                    mLoading.setVisibility(View.VISIBLE);
                    mLoading.setProgress(0);
                    mLoading.setForegroundColor(UiCompat.getColor(getResources(), R.color.colorAccent));
                    mLoading.start();
                }else if(status == Message.STATUS_FAILED){//3、发送失败 允许重新发送
                    // 发送失败状态, 允许重新发送
                    mLoading.setVisibility(View.VISIBLE);
                    mLoading.stop();
                    mLoading.setProgress(1);
                    mLoading.setForegroundColor(UiCompat.getColor(getResources(), R.color.alertImportant));
                }
                // 当状态是错误状态时才允许点击
                mPortrait.setEnabled(status == Message.STATUS_FAILED);
            }

        }

        @OnClick(R.id.im_portrait)
        void onRePushClick(){
            //重新发送
            if(mLoading !=null){
                // 必须是右边的才有可能需要重新发送
                // 状态改变需要重新刷新界面当前的信息
                // TODO: 2017/12/28 重新发送待完善

            }
        }
    }
    // 文字的Holder
    class TextHolder extends BaseHolder{
        @BindView(R.id.txt_content)
        TextView mContent;
        public TextHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Message message) {
            super.onBind(message);
            // 把内容设置到布局上
            mContent.setText(message.getContent());
        }
    }

    // 语音的Holder
    class AudioHolder extends BaseHolder {

        public AudioHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Message message) {
            super.onBind(message);
            // TODO
        }
    }

    // 图片的Holder
    class PicHolder extends BaseHolder {

        public PicHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Message message) {
            super.onBind(message);
            // TODO
        }
    }
}

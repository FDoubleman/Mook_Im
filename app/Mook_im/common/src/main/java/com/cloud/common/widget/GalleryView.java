package com.cloud.common.widget;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.cloud.common.R;
import com.cloud.common.utils.ImageUtil;
import com.cloud.common.widget.recycler.RecyclerAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GalleryView extends RecyclerView {

    private GalleryAdapter mAdapter = new GalleryAdapter();
    private List<ImageItem> mSelectedImages = new LinkedList<>();//所选中图片的集合
    private static int MAX_SELECT_COUNT = 9;//最大选中个数
    private static int MIN_IMAGE_FILE_SIZE = 10 * 1024;//最小图片大小 10K
    private static int LOAD_ID = 0x123;//load id
    private SelectedChangeListener mSelectedChangeListener;
    private Loadercallback loadcallback = new Loadercallback();

    public GalleryView(Context context) {
        super(context);
        init();
    }

    public GalleryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GalleryView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        //1、设置每列照片个数
        this.setLayoutManager(new GridLayoutManager(getContext(), 4));
        //2、设置adapter
        this.setAdapter(mAdapter);
        //3、设置item点击事件
        mAdapter.setListener(new RecyclerAdapter.AdapterListenerImp<ImageItem>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, ImageItem imageItem) {
                // Cell点击操作，如果说我们的点击是允许的，那么更新对应的Cell的状态
                // 然后更新界面，同理；如果说不能允许点击（已经达到最大的选中数量）那么就不刷新界面
                if (onItemSelectClick(imageItem)) {
                    //noinspection unchecked
                    holder.updateData(imageItem);
                }
            }
        });
    }

    /**
     * 初始化方法
     *
     * @param loaderManager Loader管理器
     * @return 任何一个LOADER_ID，可用于销毁Loader
     */
    public int setUp(LoaderManager loaderManager, SelectedChangeListener listener) {
        mSelectedChangeListener = listener;
        loaderManager.initLoader(LOAD_ID, null, loadcallback);
        return LOAD_ID;
    }


    private class Loadercallback implements LoaderManager.LoaderCallbacks<Cursor> {

        private final String[] IMAGE_PROJECTION = new String[]{MediaStore.Images.Media._ID
                , MediaStore.Images.Media.DATA
                , MediaStore.Images.Media.DATE_ADDED};

        @Override
        public Loader onCreateLoader(int id, Bundle args) {
            if (id == LOAD_ID) {
                return new CursorLoader(getContext(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        IMAGE_PROJECTION,//
                        null,
                        null,
                        IMAGE_PROJECTION[2] + " DESC"); // 倒序查询
            }
            return null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            List<ImageItem> itemList = new ArrayList<>();
            if (data != null && data.getCount() > 0) {
                data.moveToFirst();

                int indexId = data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]);
                int indexData = data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]);
                int indexDateAdd = data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]);
                do {
                    ImageItem item = new ImageItem();
                    int id = data.getInt(indexId);
                    String path = data.getString(indexData);
                    long addDate = data.getLong(indexDateAdd);

                    File file = new File(path);
                    if (!file.exists() || file.length() < MIN_IMAGE_FILE_SIZE) {
                        continue;
                    }


                    item.ImageID = id;
                    item.ImagePath = path;
                    item.CreatTime = addDate;

                    itemList.add(item);
                } while (data.moveToNext());
            }
            notifiyDataChange(itemList);
        }

        @Override
        public void onLoaderReset(Loader loader) {
            notifiyDataChange(null);
        }
    }


    /**
     * 图片点击事件
     *
     * @param imageItem 点击的图片
     * @return 是否通知刷新uI
     */
    private boolean onItemSelectClick(ImageItem imageItem) {
        Log.d("onbind clickpath:","------------>"+imageItem.ImagePath);
        boolean notifyRefresh;
        if (mSelectedImages.contains(imageItem)) {//如果已经选中
            mSelectedImages.remove(imageItem);//移除
            imageItem.isSelect = false; //不选中
            notifyRefresh = true; //刷新
        } else {
            //没有选中
            if (mSelectedImages.size() >= MAX_SELECT_COUNT) { //大于最多选中
                // 得到提示文字
                String str = getResources().getString(R.string.label_gallery_select_max_size);
                // 格式化填充
                str = String.format(str, "" + MAX_SELECT_COUNT);
                Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();
                notifyRefresh = false;
            } else {
                mSelectedImages.add(imageItem);
                imageItem.isSelect = true;
                notifyRefresh = true;
            }
        }

        if (notifyRefresh)//选中数量发生变化是  通知外部
            notifySelectChanged();
        return true;
    }

    /**
     * 通知选中状态改变
     */
    private void notifySelectChanged() {
        // 得到监听者，并判断是否有监听者，然后进行回调数量变化
        SelectedChangeListener listener = mSelectedChangeListener;
        if (listener != null) {
            listener.onSelectedChange(mSelectedImages.size());
        }
    }

    /**
     * 清空选中图片数组
     */
    public void clear() {
        if (mSelectedImages.size() == 0) {
            return;
        }
        for (ImageItem image : mSelectedImages) {
            image.isSelect = false;
        }
        mSelectedImages.clear();
        mAdapter.notifyDataSetChanged();
    }


    public void notifiyDataChange(List<ImageItem> itemList) {
        mAdapter.replace(itemList);
    }

    /**
     * 获得 当前选中的图片路径集合
     *
     * @return 图片路径数组
     */
    public String[] getSelectPath() {
        String[] pathArr = new String[mSelectedImages.size()];
        for (int i = 0; i < mSelectedImages.size(); i++) {
            pathArr[i] = mSelectedImages.get(i).ImagePath;
        }
        return pathArr;
    }

    class ImageItem {
        int ImageID; //图片id
        String ImagePath; //图片路径
        boolean isSelect; //是否选中
        long CreatTime; //创建时间

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ImageItem imageItem = (ImageItem) o;
            return ImagePath != null ? ImagePath.equals(imageItem.ImagePath) : imageItem.ImagePath == null;

        }

        @Override
        public int hashCode() {
            return ImagePath != null ? ImagePath.hashCode() : 0;
        }
    }

    class GalleryAdapter extends RecyclerAdapter<ImageItem> {

        @Override
        protected int getItemViewType(int position, ImageItem imageItem) {
            return R.layout.item_gallery_view;
        }

        @Override
        protected ViewHolder<ImageItem> onCreateViewHolder(View root, int viewType) {
            return new GalleryViewHolder(root);
        }

        class GalleryViewHolder extends RecyclerAdapter.ViewHolder<ImageItem> {

            private ImageView ivImage;
            private View viewShade;
            private CheckBox cbSelect;

            public GalleryViewHolder(View itemView) {
                super(itemView);
                ivImage = (ImageView) itemView.findViewById(R.id.iv_image);
                viewShade = itemView.findViewById(R.id.view_shade);
                cbSelect = (CheckBox) itemView.findViewById(R.id.cb_select);
            }

            @Override
            protected void onBind(ImageItem imageItem) {
                //Log.d("onbind data -->",""+imageItem.ImagePath);
                ImageUtil.loadView(getContext(), imageItem.ImagePath, ivImage);
                viewShade.setVisibility(imageItem.isSelect ? VISIBLE : INVISIBLE);
                cbSelect.setChecked(imageItem.isSelect);
                cbSelect.setVisibility(VISIBLE);

            }
        }
    }


    //选择改变时通知 外部
    public interface SelectedChangeListener {
        void onSelectedChange(int count);
    }

    public void setOnSelectedChangeListener(SelectedChangeListener selectedChange) {
        mSelectedChangeListener = selectedChange;
    }
}

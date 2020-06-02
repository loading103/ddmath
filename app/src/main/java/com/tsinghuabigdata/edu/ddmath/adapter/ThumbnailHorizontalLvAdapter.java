package com.tsinghuabigdata.edu.ddmath.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.module.robotqa.FlagBitmap;
import com.tsinghuabigdata.edu.ddmath.util.WindowUtils;


public class ThumbnailHorizontalLvAdapter extends MyBaseAdapter<FlagBitmap> {
    private Context mContext;
    private LayoutInflater mInflater;
    private int pre_position;

    public ThumbnailHorizontalLvAdapter(Context context) {
        super();
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.listitem_thumbnail_horizontal, null);
        }
        ImageView thumbnailImgView = ViewHolder.getView(convertView, R.id.iv_thumbnail_img);
        LinearLayout container = ViewHolder.getView(convertView, R.id.ll_item_container);

        FlagBitmap srcBmp = getItem(position);

        thumbnailImgView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        thumbnailImgView.setImageBitmap(srcBmp.getBitmap());

        if (srcBmp.isSelected()){
            thumbnailImgView.setSelected(true);
            setViewHeightAndWidth(thumbnailImgView, WindowUtils.dpToPixels(mContext,40),WindowUtils.dpToPixels(mContext,40));
            setPadding(container,WindowUtils.dpToPixels(mContext,6), WindowUtils.dpToPixels(mContext,6));
        }else {
            thumbnailImgView.setSelected(false);
            setViewHeightAndWidth(thumbnailImgView, WindowUtils.dpToPixels(mContext,30),WindowUtils.dpToPixels(mContext,30));
            setPadding(container,WindowUtils.dpToPixels(mContext,3), WindowUtils.dpToPixels(mContext,11));
        }
        return convertView;
    }

    public void setSelectImg(int pos) {
        if (pos != pre_position){
            getItem(pos).setSelected(true);
            getItem(pre_position).setSelected(false);
            pre_position = pos;
            notifyDataSetChanged();
        }
    }

    public void initSelectImgPosition(int pos){
        pre_position = pos;
    }
    private void setViewHeightAndWidth(View v, float w, float h){
        ViewGroup.LayoutParams lp = v.getLayoutParams();
        lp.width = (int) w;
        lp.height = (int) h;
        v.setLayoutParams(lp);
    }

    private void setPadding(View v, float left, float top){
        v.setPadding((int)left,(int)top,(int)left,(int)top);
    }

    private Bitmap getPropThumnail(Bitmap bm) {
//      Bitmap bb = BitmapUtil.getRoundedCornerBitmap(b, 100);
//        int w = mContext.getResources().getDimensionPixelOffset(R.dimen.thumnail_default_width);
//        int h = mContext.getResources().getDimensionPixelSize(R.dimen.thumnail_default_height);

//        Bitmap thumBitmap = ThumbnailUtils.extractThumbnail(b, (int) WindowUtils.dpToPixels(mContext, 100),
//                (int) WindowUtils.dpToPixels(mContext, 100));

        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 设置想要的大小
        int newWidth = 100;
        int newHeight = 100;
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        float scale = scaleHeight > scaleWidth ? scaleHeight : scaleWidth;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        Bitmap thumBitmap = ThumbnailUtils.extractThumbnail(newbm, (int) WindowUtils.dpToPixels(mContext, 100),
                (int) WindowUtils.dpToPixels(mContext, 100));

        return newbm;
    }

}

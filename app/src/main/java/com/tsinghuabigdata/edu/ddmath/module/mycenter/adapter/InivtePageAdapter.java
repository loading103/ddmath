package com.tsinghuabigdata.edu.ddmath.module.mycenter.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.PicassoUtil;
import com.tsinghuabigdata.edu.ddmath.util.QRCodeUtil;
import com.tsinghuabigdata.edu.ddmath.util.ShareUtils;

import java.util.List;


/**
 * Created by fish on 2016/10/24.
 */

public class InivtePageAdapter extends PagerAdapter {


    private Context       context;
    private List<String>  mList;
    private ShareListener mShareListener;

    public InivtePageAdapter(Context context, List<String> list) {
        this.context = context;
        mList = list;
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, int position) {
        final String url = mList.get(position % mList.size());
        View root;
        if (GlobalData.isPad()) {
            root = LayoutInflater.from(context).inflate(R.layout.inivte_page, null);
        } else {
            root = LayoutInflater.from(context).inflate(R.layout.inivte_page_phone, null);
        }
        ImageView ivshare =  root.findViewById(R.id.iv_share);
        ImageView ivcode =  root.findViewById(R.id.iv_code);
        PicassoUtil.displayImageIndetUrl(url, ivshare);
        Bitmap logoBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
        Bitmap codeBitmap = QRCodeUtil.createQRCodeBitmap(ShareUtils.getsharePicUrl(context), 800, logoBitmap, 0.3f);
        ivcode.setImageBitmap(codeBitmap);
        root.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mShareListener != null) {

                    mShareListener.share(url);
                }
                return false;
            }
        });
        container.addView(root);
        return root;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public interface ShareListener {
        void share(String url);
    }

    public void setShareListener(ShareListener shareListener) {
        mShareListener = shareListener;
    }
}

package com.tsinghuabigdata.edu.ddmath.module.mycenter.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Callback;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.PicassoUtil;
import com.tsinghuabigdata.edu.ddmath.util.QRCodeUtil;
import com.tsinghuabigdata.edu.ddmath.util.ShareUtils;

import java.util.List;

/**
 * Created by Administrator on 2018/4/27.
 */

public class InviteBaseAdapter extends BaseAdapter {

    private Context       mContext;
    private List<String>  mList;
    private ShareListener mShareListener;
    private int           itemHeight;

    public InviteBaseAdapter(Context context, List<String> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {

            if (GlobalData.isPad()) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.inivte_page, parent, false);
            } else {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.inivte_page_phone, parent, false);
            }
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final String url = mList.get(position);
        Bitmap logoBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_launcher);
        Bitmap codeBitmap = QRCodeUtil.createQRCodeBitmap(ShareUtils.getsharePicUrl(mContext), itemHeight / 2, logoBitmap, 0.3f);
        holder.ivcode.setImageBitmap(codeBitmap);
        PicassoUtil.displayUrlWithCallback(url, holder.ivshare, new Callback() {
            @Override
            public void onSuccess() {
                holder.ivcode.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError() {

            }
        });
        /*holder.rlContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mShareListener != null) {

                    mShareListener.share(url);
                }
                return false;
            }
        });*/
        return convertView;
    }

    public interface ShareListener {
        void share(String url);
    }

    /*public void setShareListener(ShareListener shareListener) {
        mShareListener = shareListener;
    }*/

    public void setItemHeight(int itemHeight) {
        this.itemHeight = itemHeight;
    }

    class ViewHolder {
        RelativeLayout rlContainer;
        ImageView      ivshare;
        ImageView      ivcode;

        public ViewHolder(View root) {
            rlContainer =  root.findViewById(R.id.rl_container);
            ivshare =  root.findViewById(R.id.iv_share);
            ivcode =  root.findViewById(R.id.iv_code);
            ViewGroup.LayoutParams containerParams = rlContainer.getLayoutParams();
            int middleWidth = itemHeight * 2 / 3;
            containerParams.width = middleWidth;
            rlContainer.setLayoutParams(containerParams);

            int codeWidth = (int) (middleWidth * AppConst.WIDTH_RATIO);
            RelativeLayout.LayoutParams codeParams = (RelativeLayout.LayoutParams) ivcode.getLayoutParams();
            codeParams.width = codeWidth;
            codeParams.height = codeWidth;
            codeParams.bottomMargin = (int) (itemHeight * AppConst.DIS_RATIO - codeWidth / 2);
            ivcode.setLayoutParams(codeParams);
        }


    }
}

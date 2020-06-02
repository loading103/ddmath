package com.tsinghuabigdata.edu.ddmath.module.mylearn.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.PicturePreviewActivity;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.module.mylearn.bean.WorkBean;
import com.tsinghuabigdata.edu.ddmath.module.mylearn.bean.WorkImageBean;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.PicassoUtil;
import com.tsinghuabigdata.edu.ddmath.view.CaptureView;

import java.util.ArrayList;

/**
 *
 */
public class ImageAdapter extends BaseAdapter {



    private ArrayList<WorkImageBean> mList = new ArrayList<>();

    private Context mContext;
    private WorkBean mWorkBean;

    private String mSeverAddr = "";
    //private String accessToken;

    public ImageAdapter(Context context){
        mContext = context;
        LoginInfo loginInfo = AccountUtils.getLoginUser();
        if( loginInfo!=null ){
            mSeverAddr = loginInfo.getFileServer();
//            try {
//                accessToken = URLEncoder.encode( loginInfo.getAccessToken(), "utf-8");
//            }catch (Exception e){
//                AppLog.i( "", e );
//            }
        }
    }

    public void setData( ArrayList<WorkImageBean> list, WorkBean bean ){
        mWorkBean = bean;
        mList.clear();
        mList.addAll( list );
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
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(GlobalData.isPad()?R.layout.item_mylearn_workimage:R.layout.item_mylearn_workimage_phone, parent, false);
            convertView.setTag(new MultiViewHolder(convertView));
        }
        MultiViewHolder viewHolder = ((MultiViewHolder) convertView.getTag());
        viewHolder.position = position;
        viewHolder.indexView.setText(String.valueOf(position+1) );

        String url = mSeverAddr + mList.get(position).getPath();
        try{
            if( url.endsWith(".jpg") ){
                url = url.replace(".jpg","") + ",360x360.jpg";
            }else if( url.endsWith(".jpeg") ){
                url = url.replace(".jpeg","") + ",360x360.jpeg";
            }
            //url += "?access_token=" + accessToken;
        }catch (Exception e){AppLog.i( "", e );}
        PicassoUtil.getPicasso(mContext).load( url ).error( R.drawable.ic_broken_image ).placeholder(R.drawable.ic_temporary_image).into( viewHolder.imageView );
        return convertView;
    }

    //-------------------------------------------------------
    class MultiViewHolder implements View.OnClickListener {

        private int position;
        private TextView indexView;
        private ImageView imageView;
        private CaptureView coverView;


        public MultiViewHolder(View convertView) {
            indexView = (TextView) convertView.findViewById(R.id.item_workimage_index);
            imageView = (ImageView) convertView.findViewById(R.id.item_workimage_image);
            imageView.setOnClickListener( this );

            coverView = (CaptureView)convertView.findViewById( R.id.item_workimage_rectcoverView );
            coverView.setRectCoverMode();
        }

        @Override
        public void onClick(View v) {
            if( v.getId() == R.id.item_workimage_image ){
                PicturePreviewActivity.startPicturePreviewActivity( mContext, mWorkBean.getImagesPath(mSeverAddr), mWorkBean.getCorrectList(), position );
            }
        }
    }

}
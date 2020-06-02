package com.tsinghuabigdata.edu.ddmath.module.entrance.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.PicturePreviewActivity;
import com.tsinghuabigdata.edu.ddmath.adapter.DragGridBaseAdapter;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.module.mylearn.bean.UploadImage;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.PicassoUtil;
import com.tsinghuabigdata.edu.ddmath.view.CaptureView;

import java.util.ArrayList;

/**
 *
 */
public class EntranceImageAdapter extends BaseAdapter implements DragGridBaseAdapter {



    private ArrayList<UploadImage> mList = new ArrayList<>();

    private Context mContext;

    private String mSeverAddr = "http://172.16.5.2/";
//    private String accessToken;

    public EntranceImageAdapter(Context context){
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

    public void setData( ArrayList<UploadImage> list ){
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

        String url = mSeverAddr + mList.get(position).getUrl();
        try{
            if( url.endsWith(".jpg") ){
                url = url.replace(".jpg","") + ",360x360.jpg";
            }else if( url.endsWith(".jpeg") ){
                url = url.replace(".jpeg","") + ",360x360.jpeg";
            }
        }catch (Exception e){AppLog.i( "", e );}
        PicassoUtil.getPicasso(mContext).load( url ).error( R.drawable.ic_broken_image ).placeholder(R.drawable.ic_temporary_image).into( viewHolder.imageView );
        return convertView;
    }

    @Override
    public void reorderItems(int oldPosition, int newPosition) {
        //AppLog.d("dfdgfgfgg old = "+oldPosition + ",,,new = " + newPosition );
//        UploadImage temp = mList.get(oldPosition);
//        if(oldPosition < newPosition){
//            for(int i=oldPosition; i<newPosition; i++){
//                Collections.swap(mList, i, i+1);
//            }
//        }else if(oldPosition > newPosition){
//            for(int i=oldPosition; i>newPosition; i--){
//                Collections.swap(mList, i, i-1);
//            }
//        }
//        mList.set(newPosition, temp);
    }

    @Override
    public void setHideItem(int hidePosition) {
        //this.mHidePosition = hidePosition;
        //notifyDataSetChanged();
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
                PicturePreviewActivity.startPicturePreviewActivity( mContext, getImagesPath(mSeverAddr), position);
            }
        }
    }

    @NonNull
    private ArrayList<String> getImagesPath(String domain ){
        ArrayList<String> arrayList = new ArrayList<String>();
        for( UploadImage image : mList ){
            arrayList.add(domain + image.getUrl());
        }
        return arrayList;
        /*ArrayList<String> arrayList = new ArrayList<String>();
        for (int i = 0; i < mList.size(); i++) {
            UploadImage uploadImage = mList.get(i);
            arrayList.add(mList + uploadImage.getUrl());
        }
        return arrayList;*/
    }

}
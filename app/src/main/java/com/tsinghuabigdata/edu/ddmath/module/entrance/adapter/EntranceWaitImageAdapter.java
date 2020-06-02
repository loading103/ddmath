package com.tsinghuabigdata.edu.ddmath.module.entrance.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.PicturePreviewActivity;
import com.tsinghuabigdata.edu.ddmath.adapter.DragGridBaseAdapter;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.module.entrance.EntranceListActivity;
import com.tsinghuabigdata.edu.ddmath.module.entrance.LocalImageManager;
import com.tsinghuabigdata.edu.ddmath.module.entrance.RefreshImageListener;
import com.tsinghuabigdata.edu.ddmath.module.entrance.bean.UploadStatusBean;
import com.tsinghuabigdata.edu.ddmath.module.mylearn.ScWorkCameraActivity;
import com.tsinghuabigdata.edu.ddmath.module.mylearn.ScWorkUtils;
import com.tsinghuabigdata.edu.ddmath.module.mylearn.bean.UploadImage;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.PicassoUtil;
import com.tsinghuabigdata.edu.ddmath.util.WindowUtils;
import com.tsinghuabigdata.edu.ddmath.view.CaptureView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 */
public class EntranceWaitImageAdapter extends BaseAdapter implements DragGridBaseAdapter {


    private ArrayList<UploadImage> mList = new ArrayList<>();
    private UploadStatusBean uploadStatusBean;
    private RefreshImageListener mListener;

    private Context mContext;
    private int mHidePosition = -1;
    private int imageWidth;

    private String picDomain;

    public EntranceWaitImageAdapter( Context context ){
        mContext = context;
        imageWidth = WindowUtils.dpToPixels( mContext, GlobalData.isPad()?180:120 );

        picDomain = "";
        LoginInfo loginInfo = AccountUtils.getLoginUser();
        if( loginInfo!=null ){
            picDomain = loginInfo.getFileServer();
        }
    }

    public void setData( ArrayList<UploadImage> list, UploadStatusBean bean, RefreshImageListener listener ){
        mList = list;
        uploadStatusBean = bean;
        mListener = listener;
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
        //AppLog.d("dsdsdssdsdd iiii position = " + position );
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate( GlobalData.isPad()?R.layout.item_mylearn_waitworkimage:R.layout.item_mylearn_waitworkimage_phone, parent, false);
            convertView.setTag(new MultiViewHolder(convertView));
        }

        MultiViewHolder viewHolder = ((MultiViewHolder) convertView.getTag());
        viewHolder.position = position;
        UploadImage image = mList.get(position);

        if( UploadImage.TYPE_ADDIMAGE == image.getImagetype() ){
            viewHolder.mainLayout.setVisibility( View.GONE );
            viewHolder.addImageLayout.setVisibility( View.VISIBLE );

            viewHolder.indexView.setVisibility( View.GONE );
        }else{
            viewHolder.mainLayout.setVisibility( View.VISIBLE );
            viewHolder.addImageLayout.setVisibility( View.GONE );
            viewHolder.indexView.setVisibility( View.VISIBLE );

            viewHolder.indexView.setText(String.valueOf(position+1) );

            if( image.isEditMode() ){
                viewHolder.deleteImageView.setVisibility( View.VISIBLE );
            }else{
                viewHolder.deleteImageView.setVisibility( View.GONE );
            }
            int uploadstatus = uploadStatusBean.getUploadStatus();
            int status = image.getUploadStatus();
            if( image.isLastUploadPage() ){
                if( uploadStatusBean == null || UploadStatusBean.ST_UNUPLOAD == uploadstatus || UploadStatusBean.ST_UPLOADED == uploadstatus )
                    status = image.getUploadStatus();
                else if( UploadStatusBean.ST_UPLOADING == uploadstatus ){   //上传中
                    status = image.getUploadStatusNew();    //上传中
                }else if( UploadStatusBean.ST_UPLOADFAIL == uploadstatus || UploadStatusBean.ST_UPLOADEDIT == uploadstatus ){      //
                    status = UploadImage.ST_ERROR;
                }
            }

            if( (1 == uploadStatusBean.getViewStatus() && TextUtils.isEmpty(image.getLocalpath()) && !TextUtils.isEmpty(image.getUrl()))
              || status == UploadImage.ST_SUCC && (uploadstatus==UploadStatusBean.ST_UPLOADFAIL || uploadstatus == UploadStatusBean.ST_UPLOADED || uploadstatus == UploadStatusBean.ST_UPLOADING) ){
                viewHolder.finishImageView.setImageResource( R.drawable.ic_upload_success_green);
                viewHolder.finishImageView.setVisibility( View.VISIBLE );
            }else if( status == UploadImage.ST_ERROR && (uploadstatus==UploadStatusBean.ST_UPLOADFAIL || uploadstatus == UploadStatusBean.ST_UPLOADED || uploadstatus == UploadStatusBean.ST_UPLOADING) ) {
                viewHolder.finishImageView.setImageResource( R.drawable.ic_failure);
                viewHolder.finishImageView.setVisibility( View.VISIBLE );
            } else{
                viewHolder.finishImageView.setVisibility( View.GONE );
            }

            if( status == UploadImage.ST_SUCC || uploadstatus==UploadStatusBean.ST_UPLOADED ){
                viewHolder.coverView.setVisibility( View.GONE );
            }else{
                viewHolder.coverView.setVisibility( View.VISIBLE );
            }

            if( (status == UploadImage.ST_NONE || status == UploadImage.ST_UING) && uploadstatus==UploadStatusBean.ST_UPLOADING ){
                viewHolder.uploadImageView.setVisibility( View.VISIBLE );
            }else{
                viewHolder.uploadImageView.setVisibility( View.GONE );
            }

            String localpath = image.getLocalpath();
            if( !TextUtils.isEmpty(localpath) ){
                PicassoUtil.getPicasso( mContext ).load( new File(localpath) ).error(R.drawable.ic_broken_image).placeholder(R.drawable.ic_temporary_image).resize(imageWidth, imageWidth).into(viewHolder.imageView);
            }else{
                PicassoUtil.getPicasso( mContext ).load( picDomain+image.getUrl() ).error(R.drawable.ic_broken_image).placeholder(R.drawable.ic_temporary_image).resize(imageWidth, imageWidth).into(viewHolder.imageView);
            }
        }

        //
        if(position == mHidePosition){
            convertView.setVisibility(View.INVISIBLE);
        }else{
            convertView.setVisibility( View.VISIBLE );
        }
        return convertView;
    }

    @Override
    public void reorderItems(int oldPosition, int newPosition) {
        //AppLog.d("dfdgfgfgg old = "+oldPosition + ",,,new = " + newPosition );
        UploadImage temp = mList.get(oldPosition);
        if(oldPosition < newPosition){
            for(int i=oldPosition; i<newPosition; i++){
                Collections.swap(mList, i, i+1);
            }
        }else if(oldPosition > newPosition){
            for(int i=oldPosition; i>newPosition; i--){
                Collections.swap(mList, i, i-1);
            }
        }
        mList.set(newPosition, temp);
    }

    @Override
    public void setHideItem(int hidePosition) {
        this.mHidePosition = hidePosition;
        notifyDataSetChanged();
    }

    //-----------------------------------------------------------------------
    class MultiViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private int position;

        private FrameLayout mainLayout;
        private TextView indexView;
        private ImageView imageView;
        private ImageView deleteImageView;
        private ImageView finishImageView;
        private ImageView uploadImageView;
        private RelativeLayout addImageLayout;
        private View coverView;

        private CaptureView rectCoverView;

        public MultiViewHolder(View convertView) {
            mainLayout = (FrameLayout)convertView.findViewById( R.id.item_workimage_layout );
            indexView = (TextView) convertView.findViewById(R.id.item_workimage_index);
            imageView = (ImageView) convertView.findViewById(R.id.item_workimage_image);
            imageView.setOnClickListener( this );
            imageView.setOnLongClickListener( this );
            deleteImageView = (ImageView)convertView.findViewById( R.id.item_workimage_delbtn );
            deleteImageView.setOnClickListener( this );
            finishImageView = (ImageView)convertView.findViewById( R.id.item_workimage_finish );
            uploadImageView = (ImageView)convertView.findViewById( R.id.item_workimage_uploadgif );
            AnimationDrawable animationDrawable = (AnimationDrawable)uploadImageView.getDrawable();
            if( animationDrawable!=null )
                animationDrawable.start();

            addImageLayout  = (RelativeLayout)convertView.findViewById( R.id.item_workimage_addimage );
            addImageLayout.setOnClickListener( this );
            coverView = convertView.findViewById( R.id.item_workimage_coverview );

            rectCoverView = (CaptureView)convertView.findViewById( R.id.item_workimage_rectcoverView );
            rectCoverView.setRectCoverMode();
        }

        @Override
        public void onClick(View v) {
            if( v.getId() == R.id.item_workimage_addimage ){
                //进入新的拍照界面
                Intent intent = new Intent( mContext, ScWorkCameraActivity.class );
                intent.setFlags(  Intent.FLAG_ACTIVITY_NEW_TASK );
                intent.putExtra( ScWorkUtils.IS_NEXT_IMAGE, true );
                intent.putExtra( ScWorkUtils.PARAM_BROADCAST, true );
                intent.putExtra( ScWorkUtils.PARAM_FROM, AppConst.TYPE_FROM_ENTRANCE );
                mContext.startActivity( intent );
            }else if( v.getId() == R.id.item_workimage_image ){

                //Date date = new Date();
                //date.setTime( UploadStatusBean.getUploadTime() );
                PicturePreviewActivity.startPicturePreviewActivity( mContext, getImagesPath(), position);

            }else if( v.getId() == R.id.item_workimage_delbtn ){

                AlertManager.showCustomDialog( mContext, mContext.getString(R.string.delete_image), "删除", "取消", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        UploadImage image = mList.get( position );
                        image.setDelfalg( true );                   //被删除的如何回收处理
                        mList.remove( image );

                        if( mList.size() == 0 ){        //图片已经删除完了，切换到正常状态
                            uploadStatusBean.setUploadStatus( UploadStatusBean.ST_UNUPLOAD );
                            mListener.setEditMode( false );
                            mListener.addAddTypeImage();
                        }
                        mListener.onRefresh();
                        //notifyDataSetChanged();

                        LocalImageManager localImageManager = LocalImageManager.getLocalImageManager();
                        if( localImageManager!= null ) localImageManager.saveData();

                    }
                }, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                } );

            }
        }

        @Override
        public boolean onLongClick(View v) {
            int status = uploadStatusBean.getViewStatus();
            if( v.getId() == R.id.item_workimage_image && ( status == EntranceListActivity.ST_NEW || status == EntranceListActivity.ST_EDIT ) ){
                uploadStatusBean.setUploadStatus( UploadStatusBean.ST_UPLOADEDIT );
                mListener.removeAddTypeImage();
                mListener.setEditMode( true );
                mListener.onRefresh();
            }
            return true;
        }
    }

    @NonNull
    private ArrayList<String> getImagesPath(){

        ArrayList<String> arrayList = new ArrayList<String>();
        for( UploadImage image : mList ){
            if( UploadImage.TYPE_ADDIMAGE == image.getImagetype() )
                continue;
            if( !TextUtils.isEmpty(image.getUrl()) ){
                arrayList.add( picDomain + image.getUrl());
            }else{
                arrayList.add( image.getLocalpath() );
            }
        }
        return arrayList;
    }

}
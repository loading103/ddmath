package com.tsinghuabigdata.edu.ddmath.module.mylearn.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.PicturePreviewActivity;
import com.tsinghuabigdata.edu.ddmath.adapter.DragGridBaseAdapter;
import com.tsinghuabigdata.edu.ddmath.module.mylearn.ScWorkCameraActivity;
import com.tsinghuabigdata.edu.ddmath.module.mylearn.ScWorkUtils;
import com.tsinghuabigdata.edu.ddmath.module.mylearn.bean.UploadImage;
import com.tsinghuabigdata.edu.ddmath.module.mylearn.bean.WaitWorkBean;
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
public class WaitImageAdapter extends BaseAdapter implements DragGridBaseAdapter {


    private ArrayList<UploadImage> mList = new ArrayList<>();

    private Context mContext;
    private WaitWorkBean waitWorkBean;
    private int mHidePosition = -1;
    private WaitWorkAdapter mWaitWorkAdapter;
    private WaitWorkAdapter.ViewHolder mViewHolder;
    private int imageWidth;

    public WaitImageAdapter(Context context, WaitWorkAdapter adapter){
        mContext = context;
        mWaitWorkAdapter = adapter;
        imageWidth = WindowUtils.dpToPixels( mContext, GlobalData.isPad()?180:120 );
    }

    public void setData( ArrayList<UploadImage> list,WaitWorkAdapter.ViewHolder viewHolder, WaitWorkBean bean ){
        //mList.clear();
        //mList.addAll( list );
        waitWorkBean = bean;
        mViewHolder = viewHolder;
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

            int uploadstatus = waitWorkBean.getUploadStatus();
            int status = image.getUploadStatus();
            if( image.isLastUploadPage() ){
                if( WaitWorkBean.ST_UNUPLOAD == uploadstatus || WaitWorkBean.ST_UPLOADED == uploadstatus )
                    status = image.getUploadStatus();
                else if( WaitWorkBean.ST_UPLOADING == uploadstatus ){   //上传中
                    status = image.getUploadStatusNew();    //上传中
                }else if( WaitWorkBean.ST_UPLOADFAIL == uploadstatus || WaitWorkBean.ST_UPLOADEDIT == uploadstatus ){      //
                    status = UploadImage.ST_ERROR;
                }
            }

            if( status == UploadImage.ST_SUCC && (uploadstatus==WaitWorkBean.ST_UPLOADFAIL || uploadstatus == WaitWorkBean.ST_UPLOADED || uploadstatus == WaitWorkBean.ST_UPLOADING) ){
                viewHolder.finishImageView.setImageResource( R.drawable.ic_upload_success_green);
                viewHolder.finishImageView.setVisibility( View.VISIBLE );
            }else if( status == UploadImage.ST_ERROR && (uploadstatus==WaitWorkBean.ST_UPLOADFAIL || uploadstatus == WaitWorkBean.ST_UPLOADED || uploadstatus == WaitWorkBean.ST_UPLOADING) ) {
                viewHolder.finishImageView.setImageResource( R.drawable.ic_failure);
                viewHolder.finishImageView.setVisibility( View.VISIBLE );
            } else{
                viewHolder.finishImageView.setVisibility( View.GONE );
            }

            if( status == UploadImage.ST_SUCC || uploadstatus==WaitWorkBean.ST_UPLOADED ){
                viewHolder.coverView.setVisibility( View.GONE );
            }else{
                viewHolder.coverView.setVisibility( View.VISIBLE );
            }

            if( (status == UploadImage.ST_NONE || status == UploadImage.ST_UING) && uploadstatus==WaitWorkBean.ST_UPLOADING ){
                viewHolder.uploadImageView.setVisibility( View.VISIBLE );
            }else{
                viewHolder.uploadImageView.setVisibility( View.GONE );
            }

            //viewHolder.imageView.setImageBitmap( image.getBitmap(imageWidth, imageWidth) );
            PicassoUtil.getPicasso( mContext ).load( new File(image.getLocalpath()) ).error(R.drawable.ic_broken_image).placeholder(R.drawable.ic_temporary_image).resize(imageWidth, imageWidth).into(viewHolder.imageView);
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
                intent.putExtra( ScWorkUtils.TASK_ID, waitWorkBean.getTaskId() );
                mContext.startActivity( intent );
            }else if( v.getId() == R.id.item_workimage_image ){

                //Date date = new Date();
                //date.setTime( waitWorkBean.getUploadTime() );
                PicturePreviewActivity.startPicturePreviewActivity( mContext, waitWorkBean.getImagesPath(), position);

            }else if( v.getId() == R.id.item_workimage_delbtn ){

                AlertManager.showCustomDialog( mContext, mContext.getString(R.string.delete_image), "删除", "取消", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        UploadImage image = mList.get( position );
                        image.setDelfalg( true );                   //被删除的如何回收处理
                        mList.remove( image );

                        if( mList.size() == 0 ){        //图片已经删除完了，切换到正常状态
                            waitWorkBean.setUploadStatus( WaitWorkBean.ST_UNUPLOAD );
                            waitWorkBean.addAddTypeImage();
                            waitWorkBean.setEditMode( false );
                            mViewHolder.gridView.setCanDrag( false );
                            mWaitWorkAdapter.notifyDataSetChanged();
                        }
                        notifyDataSetChanged();

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
            if( v.getId() == R.id.item_workimage_image && waitWorkBean.getUploadStatus() == WaitWorkBean.ST_UNUPLOAD ){
                waitWorkBean.setUploadStatus( WaitWorkBean.ST_UPLOADEDIT );
                waitWorkBean.removeAddTypeImage();
                waitWorkBean.setEditMode( true );
                mViewHolder.gridView.setCanDrag( true );
                mWaitWorkAdapter.notifyDataSetChanged();
            }
            return true;
        }
    }

}
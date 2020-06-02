package com.tsinghuabigdata.edu.ddmath.module.mylearn.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.ZxApplication;
import com.tsinghuabigdata.edu.ddmath.dialog.CustomDialog;
import com.tsinghuabigdata.edu.ddmath.module.mylearn.LocalWorkManager;
import com.tsinghuabigdata.edu.ddmath.module.mylearn.UploadManager;
import com.tsinghuabigdata.edu.ddmath.module.mylearn.bean.WaitWorkBean;
import com.tsinghuabigdata.edu.ddmath.module.neteaseim.base.util.NetworkUtil;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.edu.ddmath.util.WindowUtils;
import com.tsinghuabigdata.edu.ddmath.view.DragGridView;
import com.tsinghuabigdata.edu.ddmath.view.NetworkStatusView;

/**
 * 待上传作业列表 适配器
 */
public class WaitWorkAdapter extends ArrayAdapter<WaitWorkBean> {

    private Context mContext;
    private Activity mActivity;

    private ListView parentListView;
    UploadManager uploadManager;
    private Drawable delDrawable;

    private boolean hasUploading = false;

    public WaitWorkAdapter(Activity activity, int resource) {
        super(activity, resource);
        mActivity = activity;
        mContext = activity;
        uploadManager = UploadManager.getUploadManager( mContext );
        delDrawable = activity.getResources().getDrawable( R.drawable.ic_delete );
    }

    public void setHasUploading(boolean has){
        hasUploading = has;
    }

    public void setParentListView(ListView listView ){
        parentListView = listView;
    }

    class ViewHolder implements View.OnClickListener {

        private LinearLayout mainLayout;
        private int position;

        private TextView uploadStatusView;    //上传状态
        private TextView estimateCountView;       //预估数量

        private LinearLayout unUploadLayout;   //未上传
        private TextView editBtnView;          //编辑按钮
        private TextView uploadBtnView;        //

        private LinearLayout uploadFailLayout;   //上传失败
        private TextView deleteBtnView;          //全部删除按钮
        private TextView reUploadBtnView;        //重新上传

        private LinearLayout editLayout;        //编辑模式
        private TextView finishBtnView;          //

        private RelativeLayout editTipsLayout;         //编辑提示
        private RelativeLayout uploadSuccLayout;       //上传成功提示

        private LinearLayout uploadingLayout;           //上传中
        private NetworkStatusView networkStatusView;    //网络状态
        
        public DragGridView gridView;

        private ViewHolder(View convertView) {

            mainLayout       = (LinearLayout) convertView.findViewById( R.id.mylearn_waitwork_mainlayout );

            uploadStatusView = (TextView)convertView.findViewById( R.id.mylearn_waitwork_uploadstatus );
            estimateCountView = (TextView) convertView.findViewById(R.id.mylearn_waitwork_estimateCount);

            unUploadLayout   = (LinearLayout) convertView.findViewById(R.id.mylearn_waitwork_unuploadlayout);
            editBtnView   = (TextView) convertView.findViewById(R.id.mylearn_waitwork_editbtn);
            editBtnView.setOnClickListener( this );
            uploadBtnView   = (TextView) convertView.findViewById(R.id.mylearn_waitwork_uploadbtn);
            uploadBtnView.setOnClickListener( this );

            uploadFailLayout   = (LinearLayout) convertView.findViewById(R.id.mylearn_waitwork_uploadfaillayout);
            deleteBtnView   = (TextView) convertView.findViewById(R.id.mylearn_waitwork_deletebtn);
            deleteBtnView.setOnClickListener( this );
            reUploadBtnView   = (TextView) convertView.findViewById(R.id.mylearn_waitwork_reuploadbtn);
            reUploadBtnView.setOnClickListener( this );

            editLayout   = (LinearLayout) convertView.findViewById(R.id.mylearn_waitwork_editlayout);
            finishBtnView   = (TextView) convertView.findViewById(R.id.mylearn_waitwork_finishbtn);
            finishBtnView.setOnClickListener( this );

            editTipsLayout     = (RelativeLayout) convertView.findViewById(R.id.mylearn_waitwork_editTipsLayout);
            uploadSuccLayout   = (RelativeLayout) convertView.findViewById( R.id.mylearn_waitwork_uploadsucclayout );

            gridView       = (DragGridView)convertView.findViewById(R.id.mylearn_waitwork_gridview);
            gridView.setParentView( parentListView );
            gridView.setParentRectView( mainLayout );
            gridView.setDiverHeight(WindowUtils.dpToPixels( mContext, 36 ));
            gridView.setExcludeWidth( delDrawable.getIntrinsicWidth() );

            uploadingLayout     = (LinearLayout)convertView.findViewById( R.id.mylearn_waitwork_uploadinglayout );
            networkStatusView   = (NetworkStatusView)convertView.findViewById( R.id.mylearn_waitwork_networkstatusview );
            networkStatusView.getTextView().setTextColor( mContext.getResources().getColor(R.color.color_333333));

        }

        @Override
        public void onClick(View v) {

            final WaitWorkBean workBean = getItem(position);

            if( v.getId() == R.id.mylearn_waitwork_editbtn ){   //编辑

                //改变状态
                workBean.setUploadStatus( WaitWorkBean.ST_UPLOADEDIT );
                workBean.removeAddTypeImage();      //删除 add action bean
                workBean.setEditMode( true );
                gridView.setCanDrag( true );
                notifyDataSetChanged();

            }else if( v.getId() == R.id.mylearn_waitwork_uploadbtn ){   //上传

                //先输入预估题数
                CustomDialog dialog = new CustomDialog( mContext, R.style.FullTransparentDialog );
                dialog.setInputNumberData( mContext.getString(R.string.enter_count), "确定", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //预估设置
                        workBean.setEstimateCount( which );

                        hasUploading = true;
                        workBean.removeAddTypeImage();      //删除 add action bean
                        notifyDataSetChanged();

                        //网络检测
                        if( !NetworkUtil.isNetAvailable( ZxApplication.getApplication() ) ){
                            ToastUtils.showToastCenter( mContext, mContext.getResources().getString( R.string.upload_failure_tips) );
                            workBean.setUploadStatus( WaitWorkBean.ST_UPLOADFAIL );
                            notifyDataSetChanged();
                        }else{
                            //启动上传
                            uploadManager.addUploadTask( workBean );

                            //网络状态
                            uploadingLayout.setVisibility( View.GONE );
                            networkStatusView.start( mActivity );
                        }
                    }
                });
                dialog.show();


            }else if( v.getId() == R.id.mylearn_waitwork_deletebtn ){   //全部删除

                AlertManager.showCustomDialog( mContext, mContext.getString(R.string.delete_allimage), "全部删除", "取消", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //如何通知服务器相关图片被删除
                        remove( workBean );
                        notifyDataSetChanged();

                        //保存更改后的列表
                        LocalWorkManager localWorkManager = LocalWorkManager.getLocalWorkManager();
                        if( localWorkManager!= null ){
                            localWorkManager.getWaitWorkList().remove(workBean);
                            localWorkManager.saveData();
                        }

                    }
                },new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

            }else if( v.getId() == R.id.mylearn_waitwork_reuploadbtn ){   //重新上传

                //网络检测
                if( !NetworkUtil.isNetAvailable( ZxApplication.getApplication() ) ){
                    ToastUtils.showToastCenter( mContext, mContext.getResources().getString( R.string.upload_failure_tips) );
                    return;
                }

                //启动上传
                uploadManager.addUploadTask(workBean);

                //网络状态
                uploadingLayout.setVisibility( View.GONE );
                networkStatusView.start( mActivity );

                hasUploading = true;
                notifyDataSetChanged();

            }else if( v.getId() == R.id.mylearn_waitwork_finishbtn ){       //完成编辑
                workBean.setUploadStatus( WaitWorkBean.ST_UNUPLOAD );
                workBean.addAddTypeImage();
                workBean.setEditMode( false );
                gridView.setCanDrag( false );
                notifyDataSetChanged();
            }
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //AppLog.d("dsdsdssdsdd wwww position = " + position );
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate( GlobalData.isPad()?R.layout.item_mylearn_waitwork:R.layout.item_mylearn_waitwork_phone, parent, false);
            convertView.setTag(new ViewHolder(convertView));
        }
        ViewHolder viewHolder = ((ViewHolder) convertView.getTag());
        bindView(getItem(position), viewHolder, position);
        return convertView;
    }

    private void bindView(WaitWorkBean workBean, ViewHolder viewHolder, int position ) {

        viewHolder.position = position;

        String estimCount = "约"+workBean.getEstimateCount() + "题";

        if(  workBean.getUploadStatus() == WaitWorkBean.ST_UNUPLOAD ){             //未上传

            viewHolder.mainLayout.setSelected( true );
            //
            viewHolder.uploadStatusView.setText("未上传");
            viewHolder.uploadStatusView.setTextColor( mContext.getResources().getColor(R.color.color_48B8FF) );
            viewHolder.uploadStatusView.setBackgroundResource( R.drawable.bg_rect_unupload );

            if( workBean.getImageList()!=null && workBean.getImageList().size()>1 ){
                viewHolder.unUploadLayout.setVisibility( View.VISIBLE );
            }else{
                viewHolder.unUploadLayout.setVisibility( View.GONE );
            }
            viewHolder.uploadFailLayout.setVisibility( View.GONE );
            viewHolder.editLayout.setVisibility( View.GONE );
            viewHolder.estimateCountView.setVisibility( View.GONE );
            viewHolder.editTipsLayout.setVisibility( View.GONE );
            viewHolder.uploadSuccLayout.setVisibility( View.GONE );
            viewHolder.uploadingLayout.setVisibility( View.GONE );

        }else if( workBean.getUploadStatus() == WaitWorkBean.ST_UPLOADFAIL ){       //上传失败

            viewHolder.mainLayout.setSelected( false );
            //
            viewHolder.uploadStatusView.setText("上传失败");
            viewHolder.uploadStatusView.setTextColor( mContext.getResources().getColor(R.color.color_FF7555) );
            viewHolder.uploadStatusView.setBackgroundResource( R.drawable.bg_rect_uploadfail );

            viewHolder.unUploadLayout.setVisibility( View.GONE );
            viewHolder.uploadFailLayout.setVisibility( View.VISIBLE );
            viewHolder.editLayout.setVisibility( View.GONE );
            viewHolder.estimateCountView.setVisibility( View.VISIBLE );
            viewHolder.editTipsLayout.setVisibility( View.GONE );
            viewHolder.uploadSuccLayout.setVisibility( View.GONE );
            viewHolder.uploadingLayout.setVisibility( View.GONE );
            viewHolder.networkStatusView.stop();

            viewHolder.reUploadBtnView.setEnabled( !hasUploading );

            viewHolder.estimateCountView.setText( estimCount );

        }else if( workBean.getUploadStatus() == WaitWorkBean.ST_UPLOADEDIT ){       //编辑状态

            viewHolder.mainLayout.setSelected( true );
            //
            viewHolder.uploadStatusView.setText("未上传");
            viewHolder.uploadStatusView.setTextColor( mContext.getResources().getColor(R.color.color_48B8FF) );
            viewHolder.uploadStatusView.setBackgroundResource( R.drawable.bg_rect_unupload );

            viewHolder.unUploadLayout.setVisibility( View.GONE );
            viewHolder.uploadFailLayout.setVisibility( View.GONE );
            viewHolder.editLayout.setVisibility( View.VISIBLE );
            viewHolder.estimateCountView.setVisibility( View.GONE );
            viewHolder.uploadingLayout.setVisibility( View.GONE );
            if( isTipShowed() ){
                viewHolder.editTipsLayout.setVisibility( View.GONE );
            }else{
                viewHolder.editTipsLayout.setVisibility( View.VISIBLE );
            }
            viewHolder.uploadSuccLayout.setVisibility( View.GONE );

        }else if( workBean.getUploadStatus() == WaitWorkBean.ST_UPLOADING ) {       //上传中

            viewHolder.mainLayout.setSelected(false);
            //
            viewHolder.uploadStatusView.setText("上传中");
            viewHolder.uploadStatusView.setTextColor( mContext.getResources().getColor(R.color.color_57C724) );

            viewHolder.uploadStatusView.setBackgroundResource(R.drawable.bg_rect_uploading);

            viewHolder.unUploadLayout.setVisibility(View.GONE);
            viewHolder.uploadFailLayout.setVisibility(View.GONE);
            viewHolder.editLayout.setVisibility(View.GONE);
            viewHolder.estimateCountView.setVisibility(View.VISIBLE);
            viewHolder.editTipsLayout.setVisibility( View.GONE );
            viewHolder.uploadSuccLayout.setVisibility( View.GONE );
            viewHolder.uploadingLayout.setVisibility( View.GONE );

            viewHolder.estimateCountView.setText(estimCount);

        }else if( workBean.getUploadStatus() == WaitWorkBean.ST_UPLOADED ){       //上传成功

            viewHolder.mainLayout.setSelected( false );
            //
            viewHolder.uploadStatusView.setText("上传成功");
            viewHolder.uploadStatusView.setTextColor( mContext.getResources().getColor(R.color.color_57C724) );
            viewHolder.uploadStatusView.setBackgroundResource( R.drawable.bg_rect_uploading );

            viewHolder.unUploadLayout.setVisibility( View.GONE );
            viewHolder.uploadFailLayout.setVisibility( View.GONE );
            viewHolder.editLayout.setVisibility( View.GONE );
            viewHolder.estimateCountView.setVisibility( View.VISIBLE );
            viewHolder.editTipsLayout.setVisibility( View.GONE );
            viewHolder.uploadSuccLayout.setVisibility( View.VISIBLE );
            viewHolder.uploadingLayout.setVisibility( View.GONE );
            viewHolder.networkStatusView.stop();
            if( viewHolder.uploadSuccLayout.getHeight() == 0 ){
                ViewGroup.LayoutParams layoutParams = viewHolder.uploadSuccLayout.getLayoutParams();
                layoutParams.height = viewHolder.mainLayout.getHeight();
                viewHolder.uploadSuccLayout.setLayoutParams( layoutParams );
            }

            viewHolder.estimateCountView.setText( estimCount );

        }

        //图片列表
        setImageAdapter( workBean, viewHolder );

    }
    private void setImageAdapter( WaitWorkBean bean, ViewHolder viewHolder) {
        if (viewHolder.gridView.getAdapter() == null) {
            WaitImageAdapter adapter = new WaitImageAdapter( mContext, this );
            adapter.setData(bean.getImageList(),viewHolder, bean);
            viewHolder.gridView.setAdapter(adapter);
        } else {
            WaitImageAdapter adapter = (WaitImageAdapter) viewHolder.gridView.getAdapter();
            adapter.setData(bean.getImageList(),viewHolder, bean);
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 判断新手引导也是否已经显示了
     */
    public boolean isTipShowed() {
        String tag = "waitupload_edittips";
        SharedPreferences sp = mContext.getSharedPreferences(tag, Activity.MODE_PRIVATE);
        boolean showed = sp.getBoolean(tag, false);
        if( !showed ){
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean( tag, true);
            editor.apply();
        }
        return showed;
    }
}
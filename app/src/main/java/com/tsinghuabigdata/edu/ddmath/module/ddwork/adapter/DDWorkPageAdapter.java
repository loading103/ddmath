package com.tsinghuabigdata.edu.ddmath.module.ddwork.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.DDWorkDetail;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.LocalPageInfo;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.LocalWorkInfo;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.PageItemView;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

/**
 * 待上传作业列表 适配器
 */
public class DDWorkPageAdapter extends ArrayAdapter<LocalPageInfo> {

    private Context mContext;
//    private ListView parentListView;
//    UploadManager uploadManager;
//    private Drawable delDrawable;
//
//    private boolean hasUploading = false;

    private int submitStatus = 0;       //作业提交状态
    private int uploadType = 0;         //作业提交方式
    private LocalWorkInfo localWorkInfo;

    public DDWorkPageAdapter(Context context, int status ) {
        super(context, 0);
        mContext = context;
        submitStatus = status;

        //uploadManager = UploadManager.getUploadManager( mContext );
        //delDrawable = context.getResources().getDrawable( R.drawable.ic_delete );
    }

    public void setLocalWorkInfo( LocalWorkInfo localWorkInfo ){
        this.localWorkInfo = localWorkInfo;
    }

    public void selectItem( int position ){

        //AppLog.d("fdfdsfdsfds select postion = " + position );
        int count = getCount();
        for( int i=0; i<count; i++ ){
            LocalPageInfo item = getItem( i );
            if( item==null ) continue;
            if( position == i ) item.setSelected( true );
            else item.setSelected( false );
        }
        notifyDataSetChanged();
    }

    public void setUploadType(int uploadType) {
        this.uploadType = uploadType;
    }

    public void setSubmitStatus(int status){
        submitStatus = status;
    }
    public int getSelectIndex(){
        int count = getCount();
        for( int i=0; i<count; i++ ){
            LocalPageInfo item = getItem( i );
            if( item==null ) continue;
            if( item.isSelected() ) return i;
        }
        return -1;
    }


//    public void setParentListView(ListView listView ){
//        parentListView = listView;
//    }



    class ViewHolder{

        //private LinearLayout mainLayout;
        //private int position;

        private PageItemView pageItemView;
        private TextView materialNameView;

        private ViewHolder(View convertView) {
            //mainLayout       = (LinearLayout) convertView.findViewById( R.id.mylearn_waitwork_mainlayout );
            pageItemView     = (PageItemView)convertView.findViewById( R.id.item_ddwork_pageview );
            materialNameView = (TextView)convertView.findViewById( R.id.item_ddwork_materialname );
        }

        void bindView( LocalPageInfo pageInfo/*, ViewHolder viewHolder, int position*/ ){
            //this.position = position;

            //教材名称+章节名称
            if( !pageInfo.isShowBookName() && !pageInfo.isShowChapterName() /*TextUtils.isEmpty( pageInfo.getLearnMaterialName() ) || !pageInfo.isShowTitle()*/ ){
                materialNameView.setVisibility( View.GONE );
            }else{
                materialNameView.setVisibility( View.VISIBLE );
                String name = "";//pageInfo.getLearnMaterialName();
                //教材名称
                if( pageInfo.isShowBookName() ) name = pageInfo.getLearnMaterialName();
                //章节名称
                if( pageInfo.isShowChapterName() ){
                    if( TextUtils.isEmpty(name) ) name = pageInfo.getChapterName();
                    else{
                        name +=  "\n" + pageInfo.getChapterName();
                    }
                }
                materialNameView.setText( name );
            }

            if( submitStatus >= DDWorkDetail.WORK_WAITCORRECT){        //作业已提交
                pageItemView.setData( pageInfo.getPageNum(), pageInfo.getSubmitStatus()==1?PageItemView.ST_COMMITED:PageItemView.ST_UNCOMMIT, pageInfo.isSelected(), uploadType );
            }else{
                if( LocalWorkInfo.WORK_NONE==localWorkInfo.getWorkStatus() || pageInfo.getUploadStatus() == LocalPageInfo.ST_NONE ){  //  非上传状态
                    boolean hasimage = !TextUtils.isEmpty( pageInfo.getLocalpath() );
                    pageItemView.setData( pageInfo.getPageNum(), hasimage? PageItemView.ST_CAMERA : PageItemView.ST_UNCAMERA, pageInfo.isSelected(), uploadType );
                }else{  //上传状态 或者 上传失败
                    int status;
                    int workStatuss = localWorkInfo.getWorkStatus();
                    if( localWorkInfo == null || LocalWorkInfo.WORK_NONE == workStatuss || LocalWorkInfo.WORK_COMMITED == workStatuss || LocalWorkInfo.WORK_COMMITFAIL == workStatuss )
                        status = pageInfo.getUploadStatus();
                    else if( LocalWorkInfo.WORK_COMMITING == workStatuss ){   //上传中
                        status = pageInfo.getUploadStatusNew();
                    }else{      //
                        status = LocalPageInfo.ST_ERROR;
                    }
                    pageItemView.setData( pageInfo.getPageNum(), status+PageItemView.ST_CAMERA, pageInfo.isSelected(), uploadType );
                }
            }
        }
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(GlobalData.isPad()?R.layout.item_ddwork_page:R.layout.item_ddwork_page_phone, parent, false);
            convertView.setTag(new ViewHolder(convertView));
        }
        ViewHolder viewHolder = ((ViewHolder) convertView.getTag());
        viewHolder.bindView( getItem(position)/*, viewHolder, position*/);
        return convertView;
    }
}
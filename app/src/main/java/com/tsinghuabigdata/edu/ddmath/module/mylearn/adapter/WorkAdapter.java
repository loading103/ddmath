package com.tsinghuabigdata.edu.ddmath.module.mylearn.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.module.mylearn.bean.WorkBean;
import com.tsinghuabigdata.edu.ddmath.util.DateUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.view.MultiGridView;

import java.text.SimpleDateFormat;

/**
 * 作业列表 适配器
 */
public class WorkAdapter extends ArrayAdapter<WorkBean> {

    private Context mContext;

    private ListView parentListView;
    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");

    public WorkAdapter(Context context, int resource) {
        super(context, resource);
        mContext = context;
    }

    public void setParentListView(ListView listView ){
        parentListView = listView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(GlobalData.isPad()?R.layout.item_mylearn_work:R.layout.item_mylearn_work_phone, parent, false);
            convertView.setTag(new ViewHolder(convertView));
        }
        ViewHolder viewHolder = ((ViewHolder) convertView.getTag());
        bindView(getItem(position), viewHolder, position);
        return convertView;
    }

    private void bindView(WorkBean workBean, ViewHolder viewHolder, int position ) {

        //
        viewHolder.uploadTimeView.setText( formatter.format( DateUtils.parse( workBean.getCreateTime(), "yyyy-MM-dd HH:mm:ss" ) ));

        //批阅状态
        if( workBean.getCheckStatus() == WorkBean.ST_CORRECTED ){             //已批阅

            //
            viewHolder.correctStatusView.setText("已批阅");
            viewHolder.correctStatusView.setTextColor( mContext.getResources().getColor(R.color.color_57C724) );
            viewHolder.correctStatusView.setBackgroundResource( R.drawable.bg_rect_correted );

            if( workBean.getRelationStatus()!=0 ){
                viewHolder.correctedLayout.setVisibility( View.VISIBLE );
                viewHolder.rightCountView.setText( String.valueOf(workBean.getRightCount()) );
                viewHolder.wrongCountView.setText( String.valueOf(workBean.getWrongCount()) );
                viewHolder.halfCountView.setText( String.valueOf(workBean.getHalfRightCount()) );

                String data = "共"+ (workBean.getRelationCount()) + "题";
                viewHolder.allCountView.setText( data );

            }else{  //已批阅，还没有关联
                viewHolder.correctedLayout.setVisibility( View.GONE );

                String data = "约"+ workBean.getQuestionCount() + "题";
                viewHolder.allCountView.setText( data );
            }

        }else{       //未批阅  批阅中   是否区分

            //
//            if( workBean.getCheckStatus() == WorkBean.ST_CORRECTING ){
//                viewHolder.correctStatusView.setText( "批阅中");
//            }else{
                viewHolder.correctStatusView.setText( "批阅中");
                viewHolder.correctStatusView.setTextColor( mContext.getResources().getColor(R.color.color_F09823) );
//            }
            viewHolder.correctStatusView.setBackgroundResource( R.drawable.bg_rect_correting );

            //
            viewHolder.correctedLayout.setVisibility( View.GONE );

            String data = "约"+ workBean.getQuestionCount() + "题";
            viewHolder.allCountView.setText( data );
        }

        //图片列表
        setImageAdapter( workBean, viewHolder );

    }
    private void setImageAdapter(WorkBean workBean, ViewHolder viewHolder) {
        if (viewHolder.gridView.getAdapter() == null) {
            ImageAdapter adapter = new ImageAdapter( mContext );
            adapter.setData(workBean.getImages(),workBean);
            viewHolder.gridView.setAdapter(adapter);
        }else {
            ImageAdapter adapter = (ImageAdapter) viewHolder.gridView.getAdapter();
            adapter.setData(workBean.getImages(),workBean);
            adapter.notifyDataSetChanged();
        }
    }

    //---------------------------------------------------------------------------------------
    class ViewHolder{

        private View mainView;

        private TextView uploadTimeView;       //上传时间
        private TextView correctStatusView;    //批阅状态

        private LinearLayout correctedLayout;   //
        private TextView rightCountView;        //正确题数
        private TextView halfCountView;         //半对题数
        private TextView wrongCountView;         //错误题数

        private TextView allCountView;    //总数量

        private MultiGridView gridView;

        private ViewHolder(View convertView) {
            mainView        = convertView;

            uploadTimeView = (TextView) convertView.findViewById(R.id.mylearn_work_uploadtime);

            correctStatusView = (TextView)convertView.findViewById( R.id.mylearn_work_correctstatus );

            correctedLayout   = (LinearLayout) convertView.findViewById(R.id.mylearn_work_correctedlayout);
            rightCountView   = (TextView) convertView.findViewById(R.id.mylearn_work_rightcount);
            wrongCountView   = (TextView) convertView.findViewById(R.id.mylearn_work_wrongcount);
            halfCountView   = (TextView) convertView.findViewById(R.id.mylearn_work_halfcount);

            allCountView       = (TextView) convertView.findViewById(R.id.mylearn_work_allcount);

            gridView        = (MultiGridView)convertView.findViewById(R.id.mylearn_work_gridview);
            gridView.setParentView(parentListView);
        }
    }

}
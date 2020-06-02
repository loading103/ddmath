package com.tsinghuabigdata.edu.ddmath.module.errorbook.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.EBookDayCleanActivity;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.bean.DayCleanBean;
import com.tsinghuabigdata.edu.ddmath.module.neteaseim.base.util.TimeUtil;
import com.tsinghuabigdata.edu.ddmath.util.DateUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

import java.util.Locale;

/**
 * 日日清作业列表适配器
 */

public class DayCleanAdapter extends ArrayAdapter<DayCleanBean> {

    private Context mContext;
    private int correctTotal;

    public DayCleanAdapter(Context context ) {
        super(context, 0);
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(GlobalData.isPad()?R.layout.item_ebook_dayclean : R.layout.item_ebook_dayclean_phone, parent, false);
            convertView.setTag(new ViewHolder(convertView));
        }
        ViewHolder viewHolder = ((ViewHolder) convertView.getTag());
        viewHolder.bindView( position );
        return convertView;
    }

    public void setCorrectTotal( int total ){
        correctTotal = total;
    }
    //------------------------------------------------------------------------------------------
    class ViewHolder implements View.OnClickListener{

        //private LinearLayout mainLayout;
        private int position;

        private TextView nameView;

        private TextView correctView;
        private TextView rightView;
        private TextView wrongView;
        private TextView waitView;

        private TextView timeView;

        //private LinearLayout markLayout;
        private ImageView markView;
        private ImageView statusView;       //订正状态

        private ViewHolder(View convertView) {

            convertView.setOnClickListener( this );

            nameView = (TextView)convertView.findViewById( R.id.item_ebookday_examname );
            correctView = (TextView)convertView.findViewById( R.id.item_ebookday_correctcount );
            rightView = (TextView)convertView.findViewById( R.id.item_ebookday_rightcount );
            wrongView = (TextView)convertView.findViewById( R.id.item_ebookday_wrongcount );
            waitView = (TextView)convertView.findViewById( R.id.item_ebookday_waitcount );

            timeView = (TextView)convertView.findViewById( R.id.item_ebookday_time );

            //markLayout= (LinearLayout)convertView.findViewById( R.id.item_ebookday_markinglayout );
            markView  = (ImageView)convertView.findViewById( R.id.item_ebookday_newtips );
            statusView=(ImageView)convertView.findViewById( R.id.item_ebookday_correctstatus );
        }
        @Override
        public void onClick(View v) {
            DayCleanBean taskBean = getItem(position);
            if( taskBean == null ) return;
            Intent intent = new Intent( getContext(), EBookDayCleanActivity.class );
            intent.putExtra( EBookDayCleanActivity.PARAM_DATE, DateUtils.format(taskBean.getWrongQuestionDate() ));
            intent.putExtra( EBookDayCleanActivity.PARAM_TITLE, taskBean.getTitle() );
            intent.putExtra( EBookDayCleanActivity.PARAM_INDEX, taskBean.getListNumber() );
            intent.putExtra( EBookDayCleanActivity.PARAM_TOTAL, correctTotal );
            getContext().startActivity( intent );
        }
        void bindView( int position ){
            this.position = position;

            Resources resources = mContext.getResources();

            DayCleanBean taskBean = getItem(position);
            if( taskBean == null ) return;

            nameView.setText( taskBean.getTitle() );

            correctView.setText( String.format( Locale.getDefault(), "已订正%d道（", taskBean.getHasReviseCount() ) );
            rightView.setText( String.format(  Locale.getDefault(),"%d道,", taskBean.getReviseRightCount() ) );

            if( taskBean.getMarkingInCount()>0 ){
                wrongView.setText( String.format(Locale.getDefault(), "%d道,批阅中%d道)", taskBean.getReviseWrongCount(), taskBean.getMarkingInCount() ) );
            }else{
                wrongView.setText( String.format( Locale.getDefault(),"%d道)", taskBean.getReviseWrongCount() ) );
            }

            waitView.setText( String.format( Locale.getDefault(), "待订正%d道", taskBean.getTobeReviseCount() ) );
            if( taskBean.getNewestReviseTime() > 0 ){
                String data = resources.getString( R.string.ebook_lastcorrecttime ) + TimeUtil.getDateTimeString(taskBean.getNewestReviseTime(),"yyyy-MM-dd HH:mm");
                timeView.setText( data );
            }else{
                timeView.setText("");
            }

            markView.setVisibility( View.GONE );
            statusView.setVisibility( View.GONE );
            if( taskBean.getHasReviseCount() == 0 ){    //new 没有订正一题
                markView.setVisibility( View.VISIBLE );
                //statusView.setVisibility( View.VISIBLE );
                //statusView.setImageResource( R.drawable.uncorrect_dayclean );
            }else if( taskBean.getTobeReviseCount()==0 ){
                statusView.setVisibility( View.VISIBLE );
                statusView.setImageResource( R.drawable.yidingzheng );
            }
        }
    }
}

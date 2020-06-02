
package com.tsinghuabigdata.edu.ddmath.module.studyfeedback.view;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.bean.ReportInfo;
import com.tsinghuabigdata.edu.ddmath.module.studyfeedback.adapter.WorkExamAdapter;
import com.tsinghuabigdata.edu.ddmath.util.DensityUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.WindowUtils;

import java.util.Locale;

/**
 * 曲线图显示 封装一层
 */
public class ChartLineView extends LinearLayout implements OnChartValueSelectedListener, ExamLineChart.MarkerVisibilityListener, View.OnTouchListener{

    //
    public final static int TYPE_TREND_ALL = 0;
    public final static int TYPE_TREND_WORK = 1;
    public final static int TYPE_TREND_EXAM = 2;

    private FrameLayout mainLayout;
    private ExamLineChart mLineChartView;        //图表
    private ChartSelectView  chartSelectView;       //选择显示View
    private RelativeLayout nodataLayout;              //没有数据

    private Drawable bgDrawable;

    //关联对象
    private PullToRefreshListView mListView;
    private WorkExamAdapter  mAdapter;

    private int mDataType;

    public ChartLineView(Context context) {
        super(context);
        init( context );
    }

    public ChartLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init( context );
    }

    public ChartLineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init( context );
    }

    public void setData( int type, int buffer[], PullToRefreshListView listView, WorkExamAdapter adapter  ) {

        mLineChartView.clearHighlighted();
        chartSelectView.setVisibility( View.GONE );

        mDataType = type;
        mListView = listView;
        mAdapter  = adapter;

        if (buffer == null || buffer.length < 2) {
            //展示没有数据
            nodataLayout.setVisibility(View.VISIBLE);
        } else {
            nodataLayout.setVisibility(View.GONE);
            mLineChartView.setShowData(buffer);        //默认显示全部数据
        }
    }

    @Override
    public void onValueSelected(Entry e, Highlight highlight) {

        //if( highlight.getDrawX() == 0 ) return;

        int index = (int)highlight.getX();
        index = getIndexInAll( index ); //倒序排列

        ReportInfo reportInfo = mAdapter.getItem( index );
        if( reportInfo == null ) return;

        mListView.getRefreshableView().setSelection( index );

        chartSelectView.setVisibility( View.VISIBLE );
        String scoreData;
        String reportType = reportInfo.getReportType();
        if ("exerhReport".equals(reportType)) {
            scoreData = String.format( Locale.getDefault(), "正确%d题 错误%d题", reportInfo.getRightQuestionCount(), reportInfo.getWrongQuestionCount() );
        } else {
            scoreData = String.format( Locale.getDefault(), "得分%d分 总分%d分", (int)reportInfo.getStudentScore(), (int)reportInfo.getTotalScore() );
        }
        chartSelectView.setData( (int)highlight.getY(), reportInfo.getReportName(), scoreData );

        //比较文本长度
        String name = reportInfo.getReportName();
        Paint paint = new Paint();
        Rect rect = new Rect();
        paint.setTextSize(WindowUtils.spToPixels(getContext(),GlobalData.isPad()?16:12));
        paint.getTextBounds( name, 0, name.length(), rect);
        int width = rect.width();

        //
        paint.setTextSize(WindowUtils.spToPixels(getContext(),GlobalData.isPad()?22:14));
        paint.getTextBounds( scoreData, 0, scoreData.length(), rect);
        if( rect.width() > width )
            width = rect.width();

        //计算背景的长度
        if( width > bgDrawable.getIntrinsicWidth() ){
            width += DensityUtils.dp2px( getContext(), 4*2 );
        }else{
            width = bgDrawable.getIntrinsicWidth();
        }
        int size = mainLayout.getWidth();
        float offset = highlight.getDrawX() - width/2;
        if( offset < 0 ) offset = 0;
        if( offset+width > size ) offset = size - width;
        chartSelectView.setX( highlight.getXPx()-width/2-offset );
    }

    @Override
    public void onNothingSelected() {
        chartSelectView.setVisibility( View.GONE );
        //AppLog.d("dfssadfsd onNothingSelected ");
    }

    @Override
    public void onVisibilityChange(boolean show) {
        chartSelectView.setVisibility( show?View.VISIBLE:View.INVISIBLE );
        //AppLog.d("dfssadfsd onVisibilityChange show " + show );
    }

    private float xbak = 0f;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if( xbak != 0f ){
            if( Math.abs( xbak-event.getX()) > 4 ){
                mLineChartView.clearHighlighted();
                chartSelectView.setVisibility( View.GONE );
            }
        }
        xbak = event.getX();

        return super.onTouchEvent(event);
    }

    //-----------------------------------------------------------------------------------
    private void init(Context context){

        inflate( context, GlobalData.isPad()?R.layout.view_chart_line:R.layout.view_chart_line_phone, this );

        mainLayout        =  findViewById( R.id.view_workexam_mainlayout );
        mLineChartView    =  findViewById( R.id.view_workexam_linechart );
        chartSelectView   =  findViewById( R.id.view_workexam_select );
        nodataLayout      =  findViewById( R.id.view_workexam_nodata );

        mLineChartView.setOnChartValueSelectedListener( this );
        mLineChartView.setMarkerVisibilityListener( this );
        mLineChartView.setOnTouchListener( this );

        bgDrawable = getContext().getResources().getDrawable( R.drawable.xianshikuang );
    }

    //倒序排列，得到趋势图中对应的数据的序号
    private int getIndexInAll( int index ){
        //在作业中查找
        if( mDataType==TYPE_TREND_WORK ){
            int workIndex = 0;
            for( int i=mAdapter.getCount()-1; i>=0; i-- ){
                ReportInfo item = mAdapter.getItem(i);
                if( item==null ) continue;
                if ("exerhReport".equals(item.getReportType())) {
                    if( workIndex==index ) return i;
                    workIndex++;
                }
            }
        }
        //在考试中查找
        else if( mDataType==TYPE_TREND_EXAM ){
            int examIndex = 0;
            for( int i=mAdapter.getCount()-1; i>=0; i-- ){
                ReportInfo item = mAdapter.getItem(i);
                if( item==null ) continue;
                if ( !"exerhReport".equals(item.getReportType())) {
                    if( examIndex==index ) return i;
                    examIndex++;
                }
            }
        }
        //在全部中查找
        return mAdapter.getCount()-1-index;
    }


}

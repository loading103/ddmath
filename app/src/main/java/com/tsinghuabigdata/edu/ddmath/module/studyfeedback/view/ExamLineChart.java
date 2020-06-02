package com.tsinghuabigdata.edu.ddmath.module.studyfeedback.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

import java.util.ArrayList;

/**
 * 考试折线图
 */
public class ExamLineChart extends LineChart {


    private LineDataSet mySet;
    //private LineDataSet avSet;
//    private ExamMarkerView markerView;

    //我的数据
    private ArrayList<Entry> myData = new ArrayList<>();
    //平均数据
    private ArrayList<Entry> avData = new ArrayList<>();


    public ExamLineChart(Context context) {
        super(context);
        initUIData();
    }

    public ExamLineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        initUIData();
    }

    public ExamLineChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initUIData();
    }

    public void setShowData(int buffer[]){

        if( buffer==null || buffer.length==0 ) return;

        avData.clear();
        myData.clear();

        int len = buffer.length/2;
        for (int i = 0; i < len; i++) {
            avData.add( new Entry(i, buffer[2*i]) );
            myData.add( new Entry(i, buffer[2*i+1]));
        }

        //getData().notifyDataChanged();
        //notifyDataSetChanged();
        initChart();

        mySet = (LineDataSet)getData().getDataSetByIndex(0);
        moveViewTo( mySet.getEntryCount() - 5, 0f, YAxis.AxisDependency.RIGHT);

//        if (getData() != null &&  getData().getDataSetCount() >= 2) {
//            avSet = (LineDataSet)getData().getDataSetByIndex(0);
//            avSet.setValues( avData );
//            mySet = (LineDataSet)getData().getDataSetByIndex(1);
//            mySet.setValues( myData );
//            getData().notifyDataChanged();
//            notifyDataSetChanged();
//        }


        //setVisibleXRangeMaximum(6);
        //mChart.setVisibleYRangeMaximum(15, AxisDependency.LEFT);
    }

    public void onChartValueSelectedListener(Entry e, Highlight highlight){
        if( mSelectionListener!= null ){
            mSelectionListener.onValueSelected( e,highlight );
        }
    }

//    public boolean onTouchEvent(MotionEvent event) {
//        super.onTouchEvent(event);
//        AppLog.d("dhjdhjsdhj ---------onTouch");
//        return true;
//    }
//    public ExamMarkerView getExamMarkerView(){
//        return markerView;
//    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return  super.dispatchTouchEvent(ev);
    }

    public void clearHighlighted(){
        highlightValue(null);
    }

    boolean showMarker = false;
    @Override
    protected void drawMarkers(Canvas canvas) {
        super.drawMarkers(canvas);

        if(/*this.mMarker != null && this.isDrawMarkersEnabled() &&*/ this.valuesToHighlight()) {

            AppLog.d("dfssadfsd onVisibilityChange draw len = " + mIndicesToHighlight.length );

            for(int i = 0; i < this.mIndicesToHighlight.length; ++i) {
                Highlight highlight = this.mIndicesToHighlight[i];
                IDataSet set = this.mData.getDataSetByIndex(highlight.getDataSetIndex());
                Entry e = this.mData.getEntryForHighlight(this.mIndicesToHighlight[i]);
                int entryIndex = set.getEntryIndex(e);
                if(e != null && (float)entryIndex <= (float)set.getEntryCount() * this.mAnimator.getPhaseX()) {
                    float[] pos = this.getMarkerPosition(highlight);
                    //onMoveChange(pos[0], pos[1],highlight );
                    if(this.mViewPortHandler.isInBounds(pos[0], pos[1])) {
                        if( !showMarker ){
                            showMarker = true;
                            if(markerVisibilityListener!=null)markerVisibilityListener.onVisibilityChange( true );
                        }
                    }else{
                        if( showMarker ){
                            showMarker = false;
                            if(markerVisibilityListener!=null)markerVisibilityListener.onVisibilityChange( false );
                        }
                    }
                }
            }

        }
    }

    private float drawXBak, drawYBak;
    private void onMoveChange( float drawX, float drawY, Highlight highlight ){
        if( Math.abs(drawX-drawXBak) > 4 || Math.abs( drawY-drawYBak)>4 ){
            if( mSelectionListener!=null) mSelectionListener.onValueSelected( null, highlight);
        }
        drawXBak = drawX;
        drawYBak = drawY;
    }

    //----------------------------------------------------------------------------------------------
    private void initUIData(){
        setNoDataText("");
    }
    private void initChart(){
//      setOnChartGestureListener(this);
//      setOnChartValueSelectedListener(this);
        setDrawGridBackground(false);

        // no description text
        getDescription().setEnabled(false);

        // enable touch gestures
        setTouchEnabled(true);

        // enable scaling and dragging
        setDragEnabled(true);
        setScaleEnabled( false );

        // if disabled, scaling can be done on x- and y-axis separately
        setPinchZoom( false );
        //setViewPortOffsets(0,0,0,0);

        // set an alternative background color
        // setBackgroundColor(Color.GRAY);

        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
//        markerView = new ExamMarkerView( getContext(), this, R.layout.exam_markerview);
//        markerView.setChartView( this ); // For bounds control
//        setMarker(markerView); // Set the marker to the chart

        //X轴设置
        XAxis xAxis = getXAxis();
        xAxis.setXOffset( 0 );
        xAxis.setDrawAxisLine( false );             //X轴坐标线
        //xAxis.setGranularity( 1 );
        xAxis.setDrawGridLines( false );            //X网格线
//        xAxis.setValueFormatter(new IAxisValueFormatter() {
//            @Override
//            public String getFormattedValue(float value, AxisBase axis) {
//                Log.e("TTTT", ""+value );
//                return "12月" + (int)value +"日";
//            }
//
//            @Override
//            public int getDecimalDigits() {
//                return 0;
//            }
//        });
        xAxis.setDrawLabels( false );
        xAxis.setPosition( XAxis.XAxisPosition.BOTTOM );
        //xAxis.setAxisMaximum(110f);
        //xAxis.setAxisMinimum(-10f);

        //xAxis.setValueFormatter(new MyCustomXAxisValueFormatter());
        //xAxis.addLimitLine(llXAxis); // add x-axis limit line

        //Y轴设置
        YAxis leftAxis = getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.setAxisMaximum(110f);
        leftAxis.setAxisMinimum(-10f);
        //leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);

        //坐标左右不显示
        getAxisLeft().setEnabled( false );
        getAxisRight().setEnabled(false);

        //getViewPortHandler().setMaximumScaleY(2f);
        //getViewPortHandler().setMaximumScaleX(2f);

        // add data
        initData();

        setVisibleXRangeMaximum( 4.3f );        //X轴最多显示5.5个数据
//        setVisibleXRange(20);
//        setVisibleYRange(20f, AxisDependency.LEFT);
//        centerViewTo(20, 50, AxisDependency.LEFT);

        //animateX(2500);

        // get the legend (only possible after setting data)
        Legend l = getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.CIRCLE);
        l.setEnabled( false );

        //setViewPortOffsets(10, 0, 10, 30);
        // // dont forget to refresh the drawing
        // invalidate();

    }

    private void initData() {

//        int count = 10, range = 100;
//        for (int i = 0; i < count; i++) {
//            float val = (float) (Math.random() * range) + 3;
//            avData.add( new Entry(i, 0) );
//        }
//        for (int i = 0; i < count; i++) {
//            float val = (float) (Math.random() * range) + 3;
//            myData.add(new Entry(i, 0));
//        }

        getXAxis().mAxisRange = 1000;
        //------------------------------------------------------------------------
        //平均数据

        LineDataSet avSet = new LineDataSet(avData, "平均正确率");

        int avColor = getContext().getResources().getColor( R.color.color_EBFBFF );

        //线宽
        avSet.setLineWidth(2f);

        //数据点
        avSet.setDrawCircles( false );
        avSet.setDrawCircleHole( false );

        avSet.setColor( avColor );

        //选中高亮
        //avSet.enableDashedHighlightLine(10f, 5f, 0f);
        //avSet.setHighLightColor( Color.argb(46,255,255,255) );
        avSet.setHighlightEnabled( false );

        avSet.setHighlightLineWidth( 2f );
        avSet.setDrawHorizontalHighlightIndicator( false );
        avSet.setDrawVerticalHighlightIndicator( true );

        //
        avSet.setDrawValues( false );

        //-----------------------------------------------------------------------------------
        //我的数据线
        mySet = new LineDataSet(myData, "我的正确率");

        int lineColor = getContext().getResources().getColor( R.color.color_23C3FF );
        int circleColor= getContext().getResources().getColor( R.color.color_F97F3A );
        //线宽
        mySet.setLineWidth(2f);

        //数据点
        mySet.setCircleColor( circleColor );
        mySet.setCircleRadius(GlobalData.isPad()?6f:4f);

        mySet.setDrawCircleHole(true);
        mySet.setCircleHoleRadius(GlobalData.isPad()?3f:2f);
        mySet.setCircleColorHole( Color.WHITE );

        mySet.setColor( lineColor );

        //
        mySet.setDrawValues( false );

        //mySet.setValueTextSize(12f);

        //选中高亮
        mySet.enableDashedHighlightLine(10f, 5f, 0f);
        mySet.setHighLightColor( Color.argb(255,0x48,0xB8,0xFF) );

        mySet.setHighlightLineWidth( 2f );
        mySet.setDrawHorizontalHighlightIndicator( false );
        mySet.setDrawVerticalHighlightIndicator( true );

//            mySet.setValueFormatter(new IValueFormatter() {
//                @Override
//                public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
//                    return "第一名";        //
//                }
//            });
            //覆盖颜色
//        mySet.setDrawFilled( true );
//        if(Utils.getSDKInt() >= 18) {
//            Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.linechart_shadecolor_bg);
//            mySet.setFillDrawable(drawable);
//        }

        //----------------------------------------------------------------------------
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add( avSet );
        dataSets.add( mySet );

        // create a data object with the datasets
        LineData data = new LineData(dataSets);

        // set data
        setData(data);

//        if (getData() != null &&  getData().getDataSetCount() > 0) {
//            mySet = (LineDataSet)getData().getDataSetByIndex(0);
//            mySet.setValues( myData );
//            getData().notifyDataChanged();
//            notifyDataSetChanged();
//        } else {
//            // create a dataset and give it a type
//
//        }
    }

    //-------------------------------------------------------
    private MarkerVisibilityListener markerVisibilityListener;
    public void setMarkerVisibilityListener( MarkerVisibilityListener listener ){
        markerVisibilityListener = listener;
    }
    public interface MarkerVisibilityListener{
        void onVisibilityChange(boolean show);
    }
}
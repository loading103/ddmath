package com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.dialog;

import android.app.Dialog;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.tsinghuabigdata.edu.ddmath.MVPModel.MyWorldModel;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.bean.MyTutorClassInfo;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.DistributeAxisValueFormatter;
import com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.UserCountAxisValueFormatter;
import com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.bean.CityRankBean;
import com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.bean.CityRankResult;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.view.LoadingPager;

import java.util.ArrayList;
import java.util.Locale;

/**
 * 城市排名
 */

public class CityGloryDialog extends Dialog {

    private LoadingPager loadingPager;
    //private LinearLayout mainLayout;
    private TextView totalView;
    private TextView rateView;
    private BarChart mChart;

//    public CityGloryDialog(@NonNull Context context) {
//        this(context, R.style.dialog);
//    }

    public CityGloryDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        initView();
        loadData();
    }

    private void initView() {
        if (GlobalData.isPad()) {
            setContentView(R.layout.dialog_city_rank);
        } else {
            setContentView(R.layout.dialog_city_rank_phone);
        }
        setCancelable(true);
        setCanceledOnTouchOutside(false);

        Window dialogWindow = getWindow();
        if( dialogWindow == null ) return;
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.dimAmount = 0.3f;
        dialogWindow.setAttributes(lp);
        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);


        RelativeLayout mRlClose = findViewById(R.id.rl_close);
        mRlClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        loadingPager = findViewById(R.id.loadingPager);
        RelativeLayout mainLayout = findViewById(R.id.main_layout);
        totalView = findViewById(R.id.tv_rank_city_count);
        rateView = findViewById(R.id.tv_user_avrate);
        mChart = findViewById(R.id.chart_city_distribute);

        loadingPager.setTargetView(mainLayout);
        loadingPager.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
            }
        });

        //图表属性设置
        mChart.setDrawBarShadow(false);
        mChart.setDrawValueAboveBar( false );   //不显示每项数值
        mChart.setClickable( false );
        mChart.setEnabled(false);
        mChart.setTouchEnabled(false);

        mChart.getDescription().setEnabled(false);
        mChart.setDrawGridBackground( false );

        // if more than 60 entries are displayed in the chart, no values will be drawn
        //mChart.setMaxVisibleValueCount(60);   数值显示,

        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);
        mChart.setDoubleTapToZoomEnabled( false );

        mChart.setDrawGridBackground(false);

        //x坐标轴
        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //xAxis.setTypeface(mTfLight);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(6);
        xAxis.setAxisMinimum(-0.5f);
        xAxis.setAxisMaximum( 5.5f );
        xAxis.setValueFormatter(new DistributeAxisValueFormatter(mChart));

        //Y坐标轴 左边
        YAxis leftAxis = mChart.getAxisLeft();
        //leftAxis.setTypeface(mTfLight);
        leftAxis.setLabelCount(5, false);
        leftAxis.setValueFormatter(new UserCountAxisValueFormatter());
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        //leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        leftAxis.setDrawLimitLinesBehindData( false );
        leftAxis.setDrawGridLines( false );
        //leftAxis.setDrawAxisLine( false );

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawAxisLine( false );
        rightAxis.setDrawTopYLabelEntry( false );
        rightAxis.setDrawZeroLine( false );
        rightAxis.setInverted( false );
        rightAxis.setDrawGridLines( false );
        rightAxis.setDrawLimitLinesBehindData( false );
        rightAxis.setEnabled( false );

        Legend legend = mChart.getLegend();
        legend.setEnabled( false );
    }

    private void loadData() {

        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        MyTutorClassInfo classInfo = AccountUtils.getCurrentClassInfo();
        if( detailinfo==null || classInfo==null ){
            loadingPager.showEmpty();
            return;
        }

        loadingPager.showLoading();
        new MyWorldModel().querCityGloryRank(detailinfo.getStudentId(), classInfo.getClassId(), new RequestListener<CityRankResult>() {
            @Override
            public void onSuccess(CityRankResult result) {
                if (result == null || result.getList() == null) {
                    loadingPager.showEmpty();
                    return;
                }

                loadingPager.showTarget();
                totalView.setText( String.format(Locale.getDefault(),"参与统计学生总数：%d人", result.getTotalStudent()));
                rateView.setText( String.format(Locale.getDefault(),"%d%%", Math.round(result.getAccuracy()*100) ));
                showChartData( result );
            }

            @Override
            public void onFail(HttpResponse response, Exception ex) {
                loadingPager.showFault(ex);
            }
        });
    }

    private void showChartData(CityRankResult result) {

        ArrayList<BarEntry> yVals1 = new ArrayList<>();

        //Y轴最大值
        int maxCount = 0;
        for( CityRankBean rankBean : result.getList() ){
            if( maxCount < rankBean.getTotalCount() )
                maxCount = rankBean.getTotalCount();
            yVals1.add(new BarEntry( rankBean.getLevel()-1, rankBean.getTotalCount()));
        }
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setAxisMaximum( maxCount );

        //用户所在的位置
        int index;
        int accuracy = (int)Math.floor(result.getAccuracy()*100);
        if( accuracy >= 90 ) index = 0;
        else if( accuracy >= 80 ) index = 1;
        else if( accuracy >= 70 ) index = 2;
        else if( accuracy >= 60 ) index = 3;
        else if( accuracy >= 50 ) index = 4;
        else index = 5;
//        //X轴方向6个
//        int COUNT_X = 6;
//
//        //float start = 0f;
//        //X轴方向6个
//        XAxis xAxis = mChart.getXAxis();
//        xAxis.setLabelCount(COUNT_X);
//        xAxis.setAxisMinimum(0);
//        xAxis.setAxisMaximum(COUNT_X + 1);

        BarDataSet set1;

        if (mChart.getData() != null && mChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals1);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(yVals1, null);
            set1.setDrawValues( false );
            set1.setHighLightColor( getContext().getResources().getColor( R.color.color_1DA9FF) );
            set1.setHighLightAlpha( 255 );
            set1.setColor( getContext().getResources().getColor( R.color.color_F9C53E ) );
            //set1.setColors(ColorTemplate.MATERIAL_COLORS);

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setDrawValues( false );
            //data.setValueTypeface(mTfLight);
            data.setBarWidth(0.5f);

            mChart.setData(data);

            //高亮用户所在的组
            mChart.highlightValue( index, 0 );
        }
    }
}

package com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;

/**
 * 比率分布数据
 */
public class DistributeAxisValueFormatter implements IAxisValueFormatter
{

    private String[] distribute = new String[]{
            "90%-100%", "80%-89%", "70%-79%", "60%-69%", "50%-59%", "0%-49%"
    };

    private BarLineChartBase<?> chart;

    public DistributeAxisValueFormatter(BarLineChartBase<?> chart) {
        this.chart = chart;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        AppLog.d("dfdfdfd value = " + value );
        int index = (int) value;
        return distribute[ index % distribute.length ];

    }

    @Override
    public int getDecimalDigits() {
        return 0;
    }
}

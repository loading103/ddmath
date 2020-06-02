package com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

public class UserCountAxisValueFormatter implements IAxisValueFormatter
{

    //private DecimalFormat mFormat;

    public UserCountAxisValueFormatter() {
        //mFormat = new DecimalFormat("###,###,###,##0.0");
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return String.valueOf((int)value);
    }

    @Override
    public int getDecimalDigits() {
        return 1;
    }
}

package com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose;

import android.text.TextUtils;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.tsinghuabigdata.edu.ddmath.util.LogUtils;

import java.text.DecimalFormat;

import static android.R.attr.digits;

/**
 * Created by Administrator on 2018/8/13.
 */

public class MyValueFormatter implements IValueFormatter {

    protected DecimalFormat mFormat;
    protected int           mDecimalDigits;

    protected String[] mParties = new String[]{"Party A", "Party B", "Party C", "Party D"};
    private   String[] mTitles  = new String[]{"优", "良", "中", "差"};

    public MyValueFormatter() {
        this.mFormat = new DecimalFormat("###,###,##0.0");
    }


    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        if (entry instanceof PieEntry) {
            String label = ((PieEntry) entry).getLabel();
            int index = getIndex(label);
            //LogUtils.i("pieEntry label: " + label + " index: " + index);
            return mTitles[index] + " " + Math.round(value) + "%";
        }
        return mTitles[dataSetIndex] + this.mFormat.format((double) value) + " %";
    }

    public int getDecimalDigits() {
        return this.mDecimalDigits;
    }

    private int getIndex(String label) {
        for (int i = 0; i < mParties.length; i++) {
            if (TextUtils.equals(label, mParties[i])) {
                return i;
            }
        }
        return 0;
    }
}

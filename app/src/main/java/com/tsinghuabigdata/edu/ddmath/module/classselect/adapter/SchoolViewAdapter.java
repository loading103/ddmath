package com.tsinghuabigdata.edu.ddmath.module.classselect.adapter;

import android.content.Context;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.module.wheelview.adapter.AbstractWheelTextAdapter;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/12/20.
 */

public class SchoolViewAdapter extends AbstractWheelTextAdapter {

    private ArrayList<String> schoolNameList;

    public SchoolViewAdapter(Context context, ArrayList<String> schoolNameList, int currentIndex, int maxsize, int minsize) {
        super(context, GlobalData.isPad() ? R.layout.class_select_school : R.layout.class_select_school_phone, NO_RESOURCE, currentIndex, maxsize, minsize);
        this.schoolNameList = schoolNameList;
        setItemTextResource(R.id.tv_school);
    }

    @Override
    public int getItemsCount() {
        return schoolNameList.size();
    }

    @Override
    public CharSequence getItemText(int index) {
        return schoolNameList.get(index);
    }

}

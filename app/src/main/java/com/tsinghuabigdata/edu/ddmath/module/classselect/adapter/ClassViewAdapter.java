package com.tsinghuabigdata.edu.ddmath.module.classselect.adapter;

import android.content.Context;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.bean.MyTutorClassInfo;
import com.tsinghuabigdata.edu.ddmath.module.wheelview.adapter.AbstractWheelTextAdapter;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/12/20.
 */

public class ClassViewAdapter extends AbstractWheelTextAdapter {

    private ArrayList<MyTutorClassInfo> classinfoList;

    public ClassViewAdapter(Context context, ArrayList<MyTutorClassInfo> classinfoList, int currentIndex, int maxsize, int minsize) {
        super(context, GlobalData.isPad() ? R.layout.class_select_class : R.layout.class_select_class_phone, NO_RESOURCE, currentIndex, maxsize, minsize);
        this.classinfoList = classinfoList;
        setItemTextResource(R.id.tv_class);
    }

    @Override
    public int getItemsCount() {
        return classinfoList.size();
    }

    @Override
    public CharSequence getItemText(int index) {
        return classinfoList.get(index).getClassName();
    }
}

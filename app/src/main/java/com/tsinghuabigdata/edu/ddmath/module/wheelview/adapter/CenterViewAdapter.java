package com.tsinghuabigdata.edu.ddmath.module.wheelview.adapter;

import android.content.Context;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/17.
 */

public class CenterViewAdapter extends AbstractWheelTextAdapter {

    private List<String> stringList;

    public CenterViewAdapter(Context context, ArrayList<String> stringList, int currentIndex, int maxsize, int minsize) {
        super(context, GlobalData.isPad() ? R.layout.item_center : R.layout.item_center_phone, NO_RESOURCE, currentIndex, maxsize, minsize);
        this.stringList = stringList;
        setItemTextResource(R.id.tv_left);
    }

    @Override
    public int getItemsCount() {
        return stringList.size();
    }

    @Override
    public CharSequence getItemText(int index) {
        return stringList.get(index);
    }

}

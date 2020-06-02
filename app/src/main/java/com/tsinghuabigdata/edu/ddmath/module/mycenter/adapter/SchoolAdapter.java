package com.tsinghuabigdata.edu.ddmath.module.mycenter.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.adapter.CommonAdapter;
import com.tsinghuabigdata.edu.ddmath.adapter.ViewHolder;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.bean.SchoolBean;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

import java.util.List;

/**
 * Created by Administrator on 2018/1/25.
 */

public class SchoolAdapter extends CommonAdapter<SchoolBean> {

    private String keyword;

    public SchoolAdapter(Context context, List<SchoolBean> mDatas) {
        super(context, mDatas);
    }

    public void setKeyword(String key){
        keyword = key;
    }

    @Override
    protected int getLayoutId() {
        return GlobalData.isPad() ? R.layout.item_sel_school : R.layout.item_sel_school_phone;
    }

    @Override
    protected void convert(ViewHolder helper, int position, SchoolBean item) {
        String name = item.getSchoolName();
        TextView textView = helper.getView( R.id.tv_school );
        if(!TextUtils.isEmpty(keyword)){
            Spannable span = new SpannableString( name );
            int start = name.indexOf( keyword );
            span.setSpan( new ForegroundColorSpan(Color.rgb(0x48,0xB8, 0xFF)), start, start+keyword.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            textView.setText(span);
        }else{
            textView.setText(name);
        }
    }
}

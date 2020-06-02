package com.tsinghuabigdata.edu.ddmath.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.bean.MyCourse;
import com.tsinghuabigdata.edu.ddmath.bean.Records;
import com.tsinghuabigdata.edu.ddmath.module.mylearn.MyLearnListActivity;
import com.tsinghuabigdata.edu.ddmath.view.MultiGridView;

import java.util.List;

/**
 * Created by Administrator on 2017/2/17.
 */

public class UploadAdatper extends CommonAdapter<Records> {

    private int num;
    private int space;

    public UploadAdatper(Context context, List<Records> mDatas,int num,int space) {
        super(context, mDatas);
        this.num = num;
        this.space = space;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_upload;
    }

    @Override
    protected void convert(ViewHolder helper, int position, final Records item) {
        helper.setText(R.id.tv_date, item.getMonth());
        MultiGridView gridView = helper.getView(R.id.gv_month_upload);
        List<MyCourse> courses = item.getCourses();
        MonthUploadAdatper adatper = new MonthUploadAdatper(mContext, courses);
        gridView.setNumColumns(num);
        gridView.setHorizontalSpacing(space);
        gridView.setAdapter(adatper);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                List<MyCourse> list = item.getCourses();
                MyCourse myCourse = list.get(position);
                if (myCourse != null && myCourse.getDate() != null) {
                    Intent intent = new Intent(mContext, MyLearnListActivity.class);
                    intent.putExtra("date", myCourse.getDate());
                    Log.i("sky", "date="+myCourse.getDate());
                    mContext.startActivity(intent);
                }
            }
        });
    }
}

package com.tsinghuabigdata.edu.ddmath.module.classselect;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.bean.MyTutorClassInfo;
import com.tsinghuabigdata.edu.ddmath.module.wheelview.adapter.AbstractWheelTextAdapter;
import com.tsinghuabigdata.edu.ddmath.module.classselect.adapter.SchoolViewAdapter;
import com.tsinghuabigdata.edu.ddmath.module.wheelview.listener.OnWheelChangedListener;
import com.tsinghuabigdata.edu.ddmath.module.wheelview.listener.OnWheelScrollListener;
import com.tsinghuabigdata.edu.ddmath.module.wheelview.view.WheelView;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.LogUtils;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/12/20.
 */

public class ClassSelectDialog extends Dialog implements View.OnClickListener{

    private ArrayList<MyTutorClassInfo> mClassInfoList;
    private Context mContext;
    private TextView tvCancel;
    private TextView tvSure;
    private OnSureButtonClickListener clickListener;
    private WheelView wheelView;
    private SchoolViewAdapter adapter;
    private ArrayList<String> stringArrayList = new ArrayList<>();
    private int VISIABLEITEM = 3;

    private String currentSchoolName = "";
    private String currentClassName = "";
    private String currentClassId = "";

    private int TEXT_MAXSIZE = 20;
    private int TEXT_MINSIZE = 16;
    private int TEXT_MAXSIZE_PHONE = 14;
    private int TEXT_MINSIZE_PHONE = 12;

    private String TEXT_CLOROMAX = "#333333";
    private String TEXT_COLORMIN = "#999999";

    public ClassSelectDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        this.mContext = context;
        initView();
    }

    private void initView() {
        setContentView(GlobalData.isPad() ? R.layout.dialog_class_select : R.layout.dialog_class_select_phone);
        tvCancel = (TextView) findViewById(R.id.tv_cancel);
        tvSure = (TextView) findViewById(R.id.tv_sure);
        wheelView = (WheelView) findViewById(R.id.wheelView);
        wheelView.setVisibleItems(VISIABLEITEM);
        tvCancel.setOnClickListener(this);
        tvSure.setOnClickListener(this);
    }

    public void setData(ArrayList<MyTutorClassInfo> list) {
        this.mClassInfoList = list;
        for (int i = 0; i < mClassInfoList.size(); i++) {
            stringArrayList.add(new String(mClassInfoList.get(i).getSchoolName() + "     " + mClassInfoList.get(i).getClassName()));
        }
        getCurrentInfo();
        initListener();
    }

    private void getCurrentInfo() {
        MyTutorClassInfo currentClassInfo = AccountUtils.getCurrentClassInfo();
        if (currentClassInfo != null) {
            currentSchoolName = currentClassInfo.getSchoolName();
            currentClassName = currentClassInfo.getClassName();
            currentClassId = currentClassInfo.getClassId();
        }
    }

    public void setCurrentItem(String classId) {
        for (int i = 0; i < mClassInfoList.size(); i++) {
            if (mClassInfoList.get(i).getClassId().equals(classId)) {
                LogUtils.i("index = " + i);
                adapter = new SchoolViewAdapter(mContext, stringArrayList, i,
                        GlobalData.isPad() ? TEXT_MAXSIZE : TEXT_MAXSIZE_PHONE,
                        GlobalData.isPad() ? TEXT_MINSIZE : TEXT_MINSIZE_PHONE);
                wheelView.setViewAdapter(adapter);
                wheelView.setCurrentItem(i);
            }
        }
    }

    private void initListener() {
        wheelView.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                int currentItem = wheel.getCurrentItem();
                currentSchoolName = mClassInfoList.get(wheelView.getCurrentItem()).getSchoolName();
                currentClassName = mClassInfoList.get(wheelView.getCurrentItem()).getClassName();
                currentClassId  = mClassInfoList.get(wheelView.getCurrentItem()).getClassId();
                String text = (String) adapter.getItemText(currentItem);
                setItemTextSize(text, adapter);
            }
        });
        wheelView.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                int currentItem = wheel.getCurrentItem();
                currentSchoolName = mClassInfoList.get(wheelView.getCurrentItem()).getSchoolName();
                currentClassName = mClassInfoList.get(wheelView.getCurrentItem()).getClassName();
                currentClassId  = mClassInfoList.get(wheelView.getCurrentItem()).getClassId();
                String text = (String) adapter.getItemText(currentItem);
                setItemTextSize(text, adapter);
            }
        });
    }

    private void setItemTextSize(String currentItemText,AbstractWheelTextAdapter adapter){
//获取所有的View
        ArrayList arrayLists = adapter.getTextViews();

        int size = arrayLists.size();
        //当前条目的内容
        String currentText;
        for(int i = 0; i < size; i++) {
            TextView textview = (TextView) arrayLists.get(i);
            currentText = textview.getText().toString().trim();

            if (currentItemText.equals(currentText)) {
                textview.setTextSize(GlobalData.isPad() ? TEXT_MAXSIZE : TEXT_MAXSIZE_PHONE);
                textview.setTextColor(Color.parseColor(TEXT_CLOROMAX));
            } else {
                textview.setTextSize(GlobalData.isPad() ? TEXT_MINSIZE : TEXT_MINSIZE_PHONE);
                textview.setTextColor(Color.parseColor(TEXT_COLORMIN));
            }
        }

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_cancel) {
            dismiss();
        } else if (v.getId() == R.id.tv_sure) {
            clickListener.onSelect(getPosition());
        }
    }

    public int getPosition() {
        int position = 0;
        for (int i = 0; i < mClassInfoList.size(); i++) {
            if (mClassInfoList.get(i).getClassId().equals(currentClassId)) {
                position = i;
                break;
            }
        }
        return position;
    }

    public void setClickListener(OnSureButtonClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface OnSureButtonClickListener{
        public void onSelect(int index);
    }

}
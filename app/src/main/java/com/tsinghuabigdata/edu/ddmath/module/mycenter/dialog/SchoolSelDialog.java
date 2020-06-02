package com.tsinghuabigdata.edu.ddmath.module.mycenter.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.module.wheelview.adapter.AbstractWheelTextAdapter;
import com.tsinghuabigdata.edu.ddmath.module.wheelview.adapter.CenterViewAdapter;
import com.tsinghuabigdata.edu.ddmath.module.wheelview.listener.OnWheelChangedListener;
import com.tsinghuabigdata.edu.ddmath.module.wheelview.listener.OnWheelScrollListener;
import com.tsinghuabigdata.edu.ddmath.module.wheelview.view.WheelView;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Administrator on 2018/1/17.
 * 学校选择dialog
 */

public class SchoolSelDialog extends Dialog implements View.OnClickListener{

    private TextView tvCancel;

    private TextView tvSure;

    private TextView tvTitle;

    private Context context;

    private WheelView wheelView;

    private CenterViewAdapter adapter;

    private ArrayList<String> arrayList;

    private OnSureButtonClickListener clickListener;

    // 字体大小
    private int TEXT_MAXSIZE = 20;
    private int TEXT_MINSIZE = 16;
    private int TEXT_MAXSIZE_PHONE = 14;
    private int TEXT_MINSIZE_PHONE = 12;

    // 显示Item的数量
    private int VISIABLEITEM = 6;

    // 选中字体颜色
    private String TEXT_CLOROMAX = "#333333";
    // 未选中字体颜色
    private String TEXT_COLORMIN = "#999999";

    /**
     * 选择入学年份
     */
    public static void selectEnrolYear(Context context, int mSchoolType, final SchoolSelDialog.OnSureButtonClickListener listener ) {
        final ArrayList<String> arrayList = new ArrayList<>();
        // 选择年份的dialog
        final SchoolSelDialog yearDialog = new SchoolSelDialog(context, R.style.dialog);
        yearDialog.setTitle("选择入学年份");
        yearDialog.setClickListener(new SchoolSelDialog.OnSureButtonClickListener() {
            @Override
            public void onSelect(int index) {
                int enrolYear = Integer.parseInt(arrayList.get(index));
                listener.onSelect( enrolYear );
                yearDialog.dismiss();
            }
        });
        Window window = yearDialog.getWindow();
        if( window == null ) return;
        window.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = context.getResources().getDisplayMetrics().widthPixels; // 宽度
        window.setAttributes(lp);

        int currYear = Calendar.getInstance().get(Calendar.YEAR);
        int currMonth = Calendar.getInstance().get(Calendar.MONTH);
        if (mSchoolType == AppConst.PRIMARY) {
            if (currMonth <= Calendar.AUGUST) {
                // 九月之前不包括今年的6年
                for (int i = 6; i >= 0; i--) {
                    arrayList.add(String .valueOf(currYear - i));
                }
            } else {
                // 九月之后包括今年的6年
                for (int i = 5; i >= 0; i--) {
                    arrayList.add(String .valueOf(currYear - i));
                }
            }
        } else if (mSchoolType == AppConst.MIDDLE){
            if (currMonth <= Calendar.AUGUST) {
                // 九月之前不包括今年的3年
                for (int i = 3; i >= 0; i--) {
                    arrayList.add(String .valueOf(currYear - i));
                }
            } else {
                // 九月之后包括今年的3年
                for (int i = 2; i >= 0; i--) {
                    arrayList.add(String .valueOf(currYear - i));
                }
            }
        }
        yearDialog.setData(arrayList);
        yearDialog.setCurrentItem(0);
        yearDialog.show();
    }

    private SchoolSelDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
        initView();
    }

    private void initView() {
        setContentView(GlobalData.isPad() ? R.layout.dialog_school_sel : R.layout.dialog_school_sel_phone);

        setCanceledOnTouchOutside(false);
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.dimAmount = 0.5f;
        dialogWindow.setAttributes(lp);
        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        tvCancel = (TextView) findViewById(R.id.tv_cancel);
        tvSure = (TextView) findViewById(R.id.tv_sure);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        wheelView = (WheelView) findViewById(R.id.wheelView);
        wheelView.setVisibleItems(VISIABLEITEM);
        tvCancel.setOnClickListener(this);
        tvSure.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                dismiss();
                break;
            case R.id.tv_sure:
                clickListener.onSelect(wheelView.getCurrentItem());
                break;
            default:
                break;
        }
    }

    public void setData(ArrayList<String>  data) {
        this.arrayList = data;
        setAdapter();
        initListener();
    }

    public void setTitle(String s) {
        tvTitle.setText(s);
    }

    private void initListener() {
        wheelView.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String text = (String) adapter.getItemText(wheel.getCurrentItem());
                setItemTextSize(text, adapter);
            }
        });
        wheelView.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                String text = (String) adapter.getItemText(wheel.getCurrentItem());
                setItemTextSize(text, adapter);
            }
        });
    }

    public void setCurrentItem(int index) {
        wheelView.setCurrentItem(index);
    }

    private void setAdapter() {
        adapter = new CenterViewAdapter(context, arrayList, 0,
                GlobalData.isPad() ? TEXT_MAXSIZE : TEXT_MAXSIZE_PHONE,
                GlobalData.isPad() ? TEXT_MINSIZE : TEXT_MINSIZE_PHONE);
        wheelView.setViewAdapter(adapter);
    }

    // 改变选中字体大小
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

    public void setClickListener(OnSureButtonClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface OnSureButtonClickListener{
        void onSelect(int index);
    }

}

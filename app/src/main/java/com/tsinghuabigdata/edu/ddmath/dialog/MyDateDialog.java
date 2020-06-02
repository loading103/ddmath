

/*
 * Create on 2016-11-21 下午3:36
 * FileName: DateDialog.java
 * Author: huang qiqiang
 * Contact: http://www.huangqiqiang.cn
 *
 */


package com.tsinghuabigdata.edu.ddmath.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.util.DateUtils;
import com.tsinghuabigdata.edu.ddmath.util.LogUtils;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;


///**
// * @version V1.0 <描述当前版本功能>
// * @FileName:DateDialog.java
// * @author: 黄其强
// * @date: 2016/11/21  15:29
// */
public class MyDateDialog extends Dialog {
    //private String mTitle = "初始时间";

    private InterfaceDateDialog interfaceDateDialog;
    private long    mCustomStartTime;
    private long    mCustomEndTime;
    private boolean fromDiagnose; //是否展示在知识诊断页面

//    public MyDateDialog(Context context, InterfaceDateDialog interfaceDateDialog) {
//        super(context);
//        this.interfaceDateDialog = interfaceDateDialog;
//    }

    public MyDateDialog(Context context, boolean source, InterfaceDateDialog interfaceDateDialog) {
        super(context);
        fromDiagnose = source;
        this.interfaceDateDialog = interfaceDateDialog;
    }

//    public MyDateDialog(Context context, String title, int mode, InterfaceDateDialog interfaceDateDialog) {
//        super(context);
//        this.interfaceDateDialog = interfaceDateDialog;
//        mTitle = title;
//
//    }

//    public MyDateDialog(Context context, int theme) {
//        super(context, theme);
//    }

    //    private DatePicker mDp_datePicker; //
    private DatePicker mDatePickerStart;
    private DatePicker mDatePickerEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_my_date);
        mDatePickerStart = findViewById(R.id.dp_datePicker_start);
        mDatePickerEnd = findViewById(R.id.dp_datePicker_end);
        setCanceledOnTouchOutside(true);

        mDatePickerStart.setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
        //        mDatePickerStart.setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
        //        mDatePickerEnd.setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
        if (fromDiagnose) {
            //如果来知识诊断页面 设置开始时间范围为六个月前到昨天，结束时间固定为昨天
            if (mCustomStartTime == 0) {
                mCustomStartTime = DateUtils.getYesterdayLast();
                mCustomEndTime = mCustomStartTime;
            }
            mDatePickerStart.setMinDate(DateUtils.getTimesSixMonth());
            mDatePickerStart.setMaxDate(DateUtils.getYesterdayLast());
        } else {
            mDatePickerStart.setMaxDate(System.currentTimeMillis());
            mDatePickerEnd.setMaxDate(System.currentTimeMillis());
        }
        mDatePickerEnd.setEnabled(false);

        initDatePicker(mDatePickerStart, mCustomStartTime);
        initDatePicker(mDatePickerEnd, mCustomEndTime);

        /*SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String sixM = sdf.format(DateUtils.getTimesSixMonth());
        String yest = sdf.format(DateUtils.getYesterday());
        String yesLast = sdf.format(DateUtils.getYesterdayLast());
        LogUtils.i("sixM=" + sixM + " yest=" + yest + " yesLast=" + yesLast);*/


        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        findViewById(R.id.btn_succes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatePickerStart.clearFocus();
                mDatePickerEnd.clearFocus();
                //String startdate = String.format("%d-%02d-%02d", mDatePickerStart.getYear(), mDatePickerStart.getMonth() + 1, mDatePickerStart.getDayOfMonth());
                //String enddate = String.format("%d-%02d-%02d", mDatePickerEnd.getYear(), mDatePickerEnd.getMonth() + 1, mDatePickerEnd.getDayOfMonth());

                if (DateUtils.compare(mDatePickerStart, mDatePickerEnd)) {
                    long startTime = DateUtils.getmCustomTime(mDatePickerStart);
                    long endTime = DateUtils.getmCustomEndTime(mDatePickerEnd);
                    if (DateUtils.IsToday(mDatePickerEnd)) {
                        endTime = System.currentTimeMillis();
                    }
                    interfaceDateDialog.getTime(startTime, endTime);
                    dismiss();
                } else {
                    ToastUtils.showShort(getContext(), "请选择有效的时间段");
                    //                    ToastUtils.showShort(getContext(), "开始日期必须小于结束日期");
                }

            }
        });


    }

    private void initDatePicker(DatePicker datePicker, long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm上传");
        String sTime = sdf.format(time);
        LogUtils.i("sTime=" + sTime);
        if (time <= 0) {
            return;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        datePicker.init(year, month, day, null);
    }

//    /**
//     * 调整FrameLayout大小
//     *
//     * @param tp
//     */
//    private void resizePikcer(FrameLayout tp) {
//        List<NumberPicker> npList = findNumberPicker(tp);
//        for (NumberPicker np : npList) {
//            resizeNumberPicker(np);
//        }
//    }

//    /**
//     * 得到viewGroup里面的numberpicker组件
//     *
//     * @param viewGroup
//     * @return
//     */
//    private List<NumberPicker> findNumberPicker(ViewGroup viewGroup) {
//        List<NumberPicker> npList = new ArrayList<NumberPicker>();
//        View child = null;
//        if (null != viewGroup) {
//            for (int i = 0; i < viewGroup.getChildCount(); i++) {
//                child = viewGroup.getChildAt(i);
//                if (child instanceof NumberPicker) {
//                    npList.add((NumberPicker) child);
//                } else if (child instanceof LinearLayout) {
//                    child.setPadding(0, 0, 0, 0);
//
//                    List<NumberPicker> result = findNumberPicker((ViewGroup) child);
//                    if (result.size() > 0) {
//                        return result;
//                    }
//                } else if (child instanceof TextView) {
//                    child.setVisibility(View.GONE);
//                }
//
//            }
//        }
//        return npList;
//    }
//
//    /**
//     * 隐藏了 分
//     *
//     * @param mDatePicker
//     */
//    private void hideDay(DatePicker mDatePicker) {
//        try {
//            /* 处理android5.0以上的特殊情况 */
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                int daySpinnerId = Resources.getSystem().getIdentifier("day", "id", "android");
//                if (daySpinnerId != 0) {
//                    View daySpinner = mDatePicker.findViewById(daySpinnerId);
//                    if (daySpinner != null) {
//                        daySpinner.setVisibility(View.GONE);
//                    }
//                }
//            } else {
//                Field[] datePickerfFields = mDatePicker.getClass().getDeclaredFields();
//                for (Field datePickerField : datePickerfFields) {
//                    if ("mDaySpinner".equals(datePickerField.getName()) || ("mDayPicker").equals(datePickerField.getName())) {
//                        datePickerField.setAccessible(true);
//                        Object dayPicker = new Object();
//                        try {
//                            dayPicker = datePickerField.get(mDatePicker);
//                        } catch (IllegalAccessException e) {
//                            AppLog.i("", e);
//                        } catch (IllegalArgumentException e) {
//                            AppLog.i("", e);
//                        }
//                        ((View) dayPicker).setVisibility(View.GONE);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            AppLog.i("", e);
//        }
//    }
//
//    private void resizeNumberPicker(NumberPicker np) {
//        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//        params.setMargins(0, 0, 0, 0);
//        np.setLayoutParams(params);
//    }

    public interface InterfaceDateDialog {
        void getTime(long startTime, long endTime);
    }


    public long getCustomStartTime() {
        return mCustomStartTime;
    }

    public void setCustomStartTime(long customStartTime) {
        mCustomStartTime = customStartTime;
    }

    public long getCustomEndTime() {
        return mCustomEndTime;
    }

    public void setCustomEndTime(long customEndTime) {
        mCustomEndTime = customEndTime;
    }
}

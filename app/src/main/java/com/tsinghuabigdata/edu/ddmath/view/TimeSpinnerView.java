package com.tsinghuabigdata.edu.ddmath.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.adapter.TimeSpinnerAdapter;
import com.tsinghuabigdata.edu.ddmath.dialog.MyDateDialog;
import com.tsinghuabigdata.edu.ddmath.util.DataUtils;
import com.tsinghuabigdata.edu.ddmath.util.DensityUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

import java.util.List;


public class TimeSpinnerView extends LinearLayout implements OnClickListener {

    private Context             mContext;
    private TimeSpinnerAdapter  mAdapter;
    private OnItemClickListener mOnItemClickListener;
    private Dialog              mDialog;
    private LinearLayout        mLlFiltrate;
    private TextView            mTvFiltrate;
    private ImageView           mIvArrow;
    private CustomSelect        mCustomSelect;

    private LinearLayout mLlCustomDate;
    private TextView     mTvSelectDate;
    private LinearLayout mLlDate;
    private TextView     mTvStartDate;
    private TextView     mTvEndDate;
    //private ImageView    mIvEditDate;


    private List<String> mlist;
    private int          selectedPosition;
    //private int          tempPosition;
    private int          kind;
    private long         mCustomStartTime;
    private long         mCustomEndTime;

    private boolean showDate = true;
    private boolean fromDiagnose; //是否展示在知识诊断页面

    private int numColumns = 0;  //每行显示几个Item
    public TimeSpinnerView(Context context) {
        this(context, null);
    }

    public TimeSpinnerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //View.inflate(context, R.layout.v_time_spinner, this);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TimeSpinner);
        kind = a.getInt(R.styleable.TimeSpinner_questiontype, 0);
        fromDiagnose = a.getBoolean(R.styleable.TimeSpinner_source, false);
        if (kind == 0) {
            if (GlobalData.isPad()) {
                View.inflate(context, R.layout.v_time_spinner, this);
            } else {
                View.inflate(context, R.layout.v_time_spinner_phone, this);
            }
        } else {
            if (GlobalData.isPad()) {
                View.inflate(context, R.layout.v_kind_spinner, this);
            } else {
                View.inflate(context, R.layout.v_kind_spinner_phone, this);
            }
        }
        a.recycle();
        mContext = context;
        initView();
    }

    public void setShowDate(boolean show) {
        showDate = show;
        showCustom(showDate);
    }

    public void setNumColumns(int num){
        numColumns = num;
    }

    private void initView() {
        mLlFiltrate =  findViewById(R.id.ll_filtrate);
        mTvFiltrate =  findViewById(R.id.tv_filtrate);
        mIvArrow =  findViewById(R.id.iv_arrow);
        mLlFiltrate.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == mLlFiltrate) {
            //LogUtils.i("mLlFiltrate   onClick()");
            mIvArrow.setActivated(true);
            showPopupWindow();
        }
    }

    private int offsetTop = 0, offsetLeft = 0;

    public void setOffset(int top, int left) {
        offsetTop = top;
        offsetLeft = left;
    }

    private void showPopupWindow() {
        if (mDialog != null) {
            mAdapter.setSelectPostion(selectedPosition);
            if (selectedPosition == mlist.size() - 1) {
                showCustom(showDate);
            } else {
                showCustom(false);
            }
            mAdapter.notifyDataSetChanged();
            mDialog.show();
            return;
        }
        mDialog = new Dialog(getContext(), R.style.dialog);
        View root = View.inflate(mContext, GlobalData.isPad() ? R.layout.dialog_time_spinner : R.layout.dialog_time_spinner_phone, null);
        mDialog.setContentView(root);
        GridView gridView =  root.findViewById(R.id.gv_time_spinner);
        if(numColumns!=0) gridView.setNumColumns(numColumns);
        mLlCustomDate =  root.findViewById(R.id.ll_custom_date);
        mTvSelectDate =  root.findViewById(R.id.tv_select_date);
        mLlDate =  root.findViewById(R.id.ll_date);
        mTvStartDate =  root.findViewById(R.id.tv_start_date);
        mTvEndDate =  root.findViewById(R.id.tv_end_date);
        ImageView imageView =  root.findViewById(R.id.iv_kind_triangle);
        gridView.setAdapter(mAdapter);
        gridView.setOnItemClickListener(mOnItemClickListener);
        mLlCustomDate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog();
            }
        });

        Window dialogWindow = mDialog.getWindow();
        if (dialogWindow == null)
            return;
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.START | Gravity.TOP);
        int w;
        if (GlobalData.isPad()) {
            w = DensityUtils.dp2px(mContext, 378);
        } else {
            w = DensityUtils.dp2px(mContext, 337);
        }

        Rect rect = new Rect();
        getGlobalVisibleRect(rect);

        //先确定左边的位置
        int mid = (rect.left + rect.right) / 2;
        int left;
        if (mid > w / 2) {
            lp.x = mid - w / 2 + offsetLeft;
            left = w / 2 - imageView.getWidth() / 2;
        } else {
            lp.x = offsetLeft;
            left = mid - imageView.getWidth() / 2;
        }
        lp.y = rect.bottom + offsetTop;

        //确定三角形位置，让三角形居中
        LayoutParams layoutParams = (LinearLayout.LayoutParams) imageView.getLayoutParams();
        layoutParams.leftMargin = left;
        imageView.setLayoutParams(layoutParams);

        //LogUtils.i("left=" + left + " top=" + top);
        lp.width = w;
        lp.dimAmount = 0.2f;
        dialogWindow.setAttributes(lp);
        //        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mIvArrow.setActivated(false);
            }
        });
        mDialog.show();

    }

    private void showDateDialog() {
        MyDateDialog dateDialog = new MyDateDialog(getContext(), fromDiagnose, new MyDateDialog.InterfaceDateDialog() {
            @Override
            public void getTime(long startTime, long endTime) {
                mLlDate.setVisibility(VISIBLE);
                mTvSelectDate.setVisibility(GONE);
                mTvStartDate.setText(DataUtils.getFormat(startTime));
                mTvEndDate.setText(DataUtils.getFormat(endTime));
                mCustomStartTime = startTime;
                mCustomEndTime = endTime;
                if (mCustomSelect != null) {
                    mCustomSelect.selectTime(startTime, endTime);
                }
                /*SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String start = sdf.format(startTime);
                String end = sdf.format(endTime);
                LogUtils.i("showDateDialog start=" + start + " end=" + end);*/
            }

        });
        dateDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dismiss();
        dateDialog.setCustomStartTime(mCustomStartTime);
        dateDialog.setCustomEndTime(mCustomEndTime);
        dateDialog.show();
    }


    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
        if (mAdapter != null) {
            mAdapter.setSelectPostion(selectedPosition);
        }
    }

    /*public void setChanged(int selectedPosition) {
        this.selectedPosition = selectedPosition;
        if (mAdapter != null) {
            mAdapter.setSelectPostion(selectedPosition);
            mAdapter.notifyDataSetChanged();
        }

    }*/

    public void setTempPosition(int tempPosition) {
        //this.tempPosition = tempPosition;
        if (mAdapter != null) {
            mAdapter.setSelectPostion(tempPosition);
            mAdapter.notifyDataSetChanged();
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void setCustomSelect(CustomSelect customSelect) {
        mCustomSelect = customSelect;
    }

    public void setText(CharSequence text) {
        mTvFiltrate.setText(text);
    }

    public void setTextColor(int color) {
        mTvFiltrate.setTextColor(color);
    }

    public String getText() {
        return mTvFiltrate.getText().toString();
    }

    public void dismiss() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    public void setData(List<String> list, int selectedPosition) {
        mlist = list;
        this.selectedPosition = selectedPosition;
        mAdapter = new TimeSpinnerAdapter(mContext, list, selectedPosition);
    }


    public void showCustom(boolean vis) {
        if (mLlCustomDate != null) {
            if (vis) {
                mLlCustomDate.setVisibility(VISIBLE);
            } else {
                mLlCustomDate.setVisibility(GONE);
            }
        }
    }

    public void autoShowCustom() {
        if (mCustomEndTime == 0) {
            showDateDialog();
        }

    }

    public interface CustomSelect {
        void selectTime(long startTime, long endTime);
    }
}

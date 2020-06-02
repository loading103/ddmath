package com.tsinghuabigdata.edu.ddmath.module.mycenter.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.bean.AreaBean;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.bean.CityBean;
import com.tsinghuabigdata.edu.ddmath.module.wheelview.adapter.AbstractWheelTextAdapter;
import com.tsinghuabigdata.edu.ddmath.module.wheelview.adapter.LeftViewAdapter;
import com.tsinghuabigdata.edu.ddmath.module.wheelview.adapter.RightViewAdapter;
import com.tsinghuabigdata.edu.ddmath.module.wheelview.listener.OnWheelChangedListener;
import com.tsinghuabigdata.edu.ddmath.module.wheelview.listener.OnWheelScrollListener;
import com.tsinghuabigdata.edu.ddmath.module.wheelview.view.WheelView;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/16.
 * 选择省市的dialog
 */

public class ProCityDialog extends Dialog implements View.OnClickListener{

    private TextView tvCancel;

    private TextView tvSure;

    private WheelView leftView;

    private WheelView rightView;

    private LeftViewAdapter leftViewAdapter;

    private RightViewAdapter rightViewAdapter;

    private Context context;

    private OnSureButtonClickListener clickListener;

    private List<AreaBean> data;

    private ArrayList<String> leftStrings = new ArrayList<>();

    // 字体大小
    private int TEXT_MAXSIZE = 20;
    private int TEXT_MINSIZE = 16;
    private int TEXT_MAXSIZE_PHONE = 14;
    private int TEXT_MINSIZE_PHONE = 12;

    // 显示Item的数量
    //private int VISIABLEITEM = 10;

    // 选中字体颜色
    private String TEXT_CLOROMAX = "#333333";
    // 未选中字体颜色
    private String TEXT_COLORMIN = "#999999";

    public ProCityDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
        initView();
    }

    private void initView() {
        setContentView(GlobalData.isPad() ? R.layout.dialog_pro_city : R.layout.dialog_pro_city_phone);

        setCanceledOnTouchOutside(false);
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.dimAmount = 0.5f;
        dialogWindow.setAttributes(lp);
        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        tvCancel = (TextView) findViewById(R.id.tv_cancel);
        tvSure = (TextView) findViewById(R.id.tv_sure);
        leftView = (WheelView) findViewById(R.id.pro_view);
        rightView = (WheelView) findViewById(R.id.city_view);
        int VISIABLEITEM = GlobalData.isPad()?10:7;
        leftView.setVisibleItems(VISIABLEITEM);
        rightView.setVisibleItems(VISIABLEITEM);
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
                if( data==null  ) break;
                int leftIndex = leftView.getCurrentItem();
                int rightIndex = rightView.getCurrentItem();
                if( data.get(leftIndex).getCityVoList().size() == 0 ){
                    ToastUtils.show( getContext(),"暂不支持此地区!");
                    break;
                }
                clickListener.onSelect(data.get(leftIndex), data.get(leftIndex).getCityVoList().get(rightIndex));
                break;
            default:
                break;
        }
    }



    public void setData(List<AreaBean> data) {
        this.data = data;
        parseData();
        setAdapter();
        initListener();
    }

    private void parseData() {
        for (AreaBean area : data) {
            leftStrings.add(area.getProvinceName());
        }
    }

    private void setAdapter() {
        leftViewAdapter = new LeftViewAdapter(context, leftStrings, 0,
                GlobalData.isPad() ? TEXT_MAXSIZE : TEXT_MAXSIZE_PHONE,
                GlobalData.isPad() ? TEXT_MINSIZE : TEXT_MINSIZE_PHONE);
        leftView.setViewAdapter(leftViewAdapter);
        updateCity();
    }

    public void setCurrentItem(int index) {
        leftView.setCurrentItem(index);
        updateCity();
    }

    private void updateCity() {
        if( data==null ) return;
        int currentLeft = leftView.getCurrentItem();
        ArrayList<String> rightStrings = new ArrayList<>();
        List<CityBean> cityBeans = data.get(currentLeft).getCityVoList();
        for (int i = 0; i < cityBeans.size(); i++) {
            rightStrings.add(cityBeans.get(i).getCityName());
        }
        rightViewAdapter = new RightViewAdapter(context, rightStrings, 0,
                GlobalData.isPad() ? TEXT_MAXSIZE : TEXT_MAXSIZE_PHONE,
                GlobalData.isPad() ? TEXT_MINSIZE : TEXT_MINSIZE_PHONE);
        rightView.setViewAdapter(rightViewAdapter);
        // 每次市定位到第一条
        rightView.setCurrentItem(0);
    }

    private void initListener() {
        leftView.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                if( leftViewAdapter.getItemsCount() > 0){
                    String text = (String) leftViewAdapter.getItemText(wheel.getCurrentItem());
                    setItemTextSize(text, leftViewAdapter);
                    updateCity();
                }
            }
        });
        leftView.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                if( leftViewAdapter.getItemsCount() > 0){
                    String text = (String) leftViewAdapter.getItemText(wheel.getCurrentItem());
                    setItemTextSize(text, leftViewAdapter);
                    updateCity();
                }
            }
        });
        rightView.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                if( rightViewAdapter.getItemsCount() > 0){
                    String text = (String) rightViewAdapter.getItemText(wheel.getCurrentItem());
                    setItemTextSize(text, rightViewAdapter);
                }
            }
        });
        rightView.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                if( rightViewAdapter.getItemsCount() > 0){
                    String text = (String) rightViewAdapter.getItemText(wheel.getCurrentItem());
                    setItemTextSize(text, rightViewAdapter);
                }
            }
        });
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
        void onSelect(AreaBean areaBean, CityBean cityBean);
    }

}

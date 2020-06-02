package com.tsinghuabigdata.edu.ddmath.view;

/**
 * Created by 28205 on 2016/11/22.
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.util.WindowUtils;

public class FragmentActionbarView extends LinearLayout implements View.OnClickListener {
    private static final String TAG = "FragmentActionbarView";
    private View actionbarView;

    private TextView leftBtn;
    private TextView title;
    private ImageView left_icon;
    private ImageView rightIcon;
    private TextView barRightBtn;
    private RelativeLayout relActionbarContainer;
    private View topPaddingView;

    private OnClickListener listener;

    public FragmentActionbarView(Context context) {
        super(context);
        initialization();
    }

    public FragmentActionbarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialization();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public FragmentActionbarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialization();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    protected void initialization() {
        actionbarView = inflate(getContext(), R.layout.fragment_action_bar_layout, this);
        // 初始化控件
        leftBtn = (TextView) actionbarView.findViewById(R.id.bar_left_btn);
        title = (TextView) actionbarView.findViewById(R.id.bar_title);
        left_icon = (ImageView) actionbarView.findViewById(R.id.left_icon);
        rightIcon = (ImageView) actionbarView.findViewById(R.id.right_icon);
        barRightBtn = (TextView) actionbarView.findViewById(R.id.bar_right_btn);
        relActionbarContainer = (RelativeLayout) actionbarView.findViewById(R.id.rl_actionbar_container);
        topPaddingView = actionbarView.findViewById(R.id.toppadding);

        leftBtn.setOnClickListener(this);
        left_icon.setOnClickListener(this);
        rightIcon.setOnClickListener(this);
        barRightBtn.setOnClickListener(this);

    }

    public void setLeftIcon(int resid){
        left_icon.setImageResource(resid);
        left_icon.setVisibility(VISIBLE);
    }
    public void setLeftTitle(String s) {
        leftBtn.setText(s);
    }

    public void setTitle(String s) {
        title.setText(s);
    }

    public void setRightTitle(String s) {
        barRightBtn.setVisibility(VISIBLE);
        barRightBtn.setText(s);
    }

    public void setStatusbarHeightTopPadding() {
        int statusbarHeight = WindowUtils.getStatusBarHeight(getContext());
        Log.d("", "initView: statusbar height " + statusbarHeight);

        ViewGroup.LayoutParams linearParams = (ViewGroup.LayoutParams) topPaddingView.getLayoutParams();
        linearParams.height = statusbarHeight;
        topPaddingView.setLayoutParams(linearParams);
        topPaddingView.setVisibility(VISIBLE);
    }

    public void hideStatusbarHeightTopPadding() {
        ViewGroup.LayoutParams linearParams = (ViewGroup.LayoutParams) topPaddingView.getLayoutParams();
        linearParams.height = 0;
        topPaddingView.setLayoutParams(linearParams);
        topPaddingView.setVisibility(GONE);
    }

    public void setStatusbarColor(int resid) {
        topPaddingView.setBackgroundResource(resid);
        setStatusbarHeightTopPadding();
    }

    public void setActionbarColor(int resid) {
        relActionbarContainer.setBackgroundResource(resid);
    }

    public OnClickListener getListener() {
        return listener;
    }

    public void setListener(OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        if (listener != null) {
            listener.onClick(v);
        }
    }

}

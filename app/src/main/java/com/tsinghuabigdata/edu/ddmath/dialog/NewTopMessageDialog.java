package com.tsinghuabigdata.edu.ddmath.dialog;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.adapter.TopMessagePageAdapter;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.module.message.MessageDetailActivity;
import com.tsinghuabigdata.edu.ddmath.module.message.bean.MessageInfo;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.PicassoUtil;

import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * 首页消息活动页（9.0改版）
 * Created by Administrator on 2017/10/31.
 */
public class NewTopMessageDialog extends BaseDialog implements ViewPager.OnPageChangeListener {


    //首页消息中心弹框提示
//    private RelativeLayout mRlContainer;
    private ImageView      mIvHomeMessage;
    private ViewPager      mViewPager;
    private LinearLayout   mIndicatorContainer;
//    private ImageView      mIvClose;

    private TopMessagePageAdapter mAdapter;

    private Context           mContext;
    private List<MessageInfo> mList;

    public NewTopMessageDialog(Context context) {
        super(context);
        initData();
    }

    public NewTopMessageDialog(Context context, int theme) {
        super(context, theme);
        initData();
    }

    protected NewTopMessageDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initData();
    }

    protected void initData() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            if (window != null) {
                window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        }

        setContentView(GlobalData.isPad() ? R.layout.activity_top_message : R.layout.activity_top_message_phone);

        mContext = getContext();

        //mRlContainer = findViewById(R.id.rl_container);
        mIvHomeMessage = findViewById(R.id.iv_home_message);
        mViewPager = findViewById(R.id.viewPager);
        mIndicatorContainer = findViewById(R.id.ll_dot_container);
        ImageView mIvClose = findViewById(R.id.iv_close);

        mIvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishDismiss();
            }
        });

        setCancelable(false);
        setCanceledOnTouchOutside(false);
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.dimAmount = 0.5f;
        dialogWindow.setAttributes(lp);
        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }
    public void setData(List<MessageInfo> list){
        if (list == null || list.size() == 0) {
            finishDismiss();
            return;
        }
        if (list.size() > 4) {
            mList = list.subList(0, 4);
        } else {
            mList = list;
        }
        if (mList.size() == 1) {
            mViewPager.setVisibility(View.INVISIBLE);
            initImageView();
        } else {
            mIvHomeMessage.setVisibility(View.INVISIBLE);
            initViewPager();
        }
    }

    private void initImageView() {
        final MessageInfo messageInfo = mList.get(0);
        if (messageInfo == null) {
            finishDismiss();
            return;
        }
        PicassoUtil.displayImageIndetUrl(messageInfo.getPicUrl(), mIvHomeMessage);
        mIvHomeMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MessageDetailActivity.class);
                intent.putExtra(AppConst.MSG_ROWKEY, messageInfo.getRowKey());
                mContext.startActivity(intent);
                finishDismiss();
            }
        });
    }

    private void initViewPager() {
        mAdapter = new TopMessagePageAdapter(mContext, mList);
        mAdapter.setTopMessageListener(new TopMessagePageAdapter.TopMessageListener() {
            @Override
            public void clickMessage(int position) {
                if( position >= mList.size() ) return;

                MessageInfo messageInfo = mList.get(position);
                if (messageInfo != null) {
                    Intent intent = new Intent(mContext, MessageDetailActivity.class);
                    intent.putExtra(AppConst.MSG_ROWKEY, messageInfo.getRowKey());
                    intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                }
                removeMessage(position);
            }
        });
        mViewPager.setAdapter(mAdapter);

        // 添加indicatorContainer应有的视图
        for (int i = 0; i < mList.size(); i++) {
            ImageView indicator = new ImageView(mContext);
            int diameter;
            if (GlobalData.isPad()) {
                diameter = dp2px(20);
            } else {
                diameter = dp2px(13);
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(diameter, diameter);
            params.leftMargin = dp2px(5);
            if (GlobalData.isPad()) {
                indicator.setImageResource(R.drawable.sel_dot_focused);
            } else {
                indicator.setImageResource(R.drawable.sel_dot_focused_phone);
            }

            mIndicatorContainer.addView(indicator, params);
            // 默认第一个是选中的效果
            if (i == 0) {
                indicator.setSelected(true);
            } else {
                indicator.setSelected(false);
            }
        }
        mViewPager.setOnPageChangeListener(this);
    }

    private void removeMessage(int position) {
        mList.remove(position);
        mAdapter.notifyDataSetChanged();
    }

    private int dp2px(float dp) {
        return  (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, mContext.getResources()
                .getDisplayMetrics()) + .5f);
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        // 设置应该选中的点
        for (int i = 0; i < mList.size(); i++) {
            ImageView indicator = (ImageView) mIndicatorContainer.getChildAt(i);
            if (i == position % mList.size()) {
                indicator.setSelected(true);
            } else {
                indicator.setSelected(false);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}

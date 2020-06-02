package com.tsinghuabigdata.edu.ddmath.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.viewpager.widget.ViewPager;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.adapter.TopMessagePageAdapter;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.module.message.MessageDetailActivity;
import com.tsinghuabigdata.edu.ddmath.module.message.bean.MessageInfo;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.PicassoUtil;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * 首页消息活动页（9.0改版）
 * Created by Administrator on 2017/10/31.
 */
public class NewTopMessageActivity extends Activity implements ViewPager.OnPageChangeListener {


    //首页消息中心弹框提示
    @ViewInject(R.id.rl_container)
    private RelativeLayout mRlContainer;
    @ViewInject(R.id.iv_home_message)
    private ImageView      mIvHomeMessage;
    @ViewInject(R.id.viewPager)
    private ViewPager      mViewPager;
    @ViewInject(R.id.ll_dot_container)
    private LinearLayout   mIndicatorContainer;
    @ViewInject(R.id.iv_close)
    private ImageView      mIvClose;

    private TopMessagePageAdapter mAdapter;

    private Context           mContext;
    private List<MessageInfo> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            if (window != null) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
        }
        if (GlobalData.isPad()) {
            setContentView(R.layout.activity_top_message);
        } else {
            setContentView(R.layout.activity_top_message_phone);
        }
        x.view().inject(this);
        mContext = this;
        initView();
        initData();
    }

    private void initView() {

        mIvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initData() {
        List<MessageInfo> list = (List<MessageInfo>) getIntent().getSerializableExtra("list");
        if (list == null || list.size() == 0) {
            finish();
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
        String url = null;
        final MessageInfo messageInfo = mList.get(0);
        if (messageInfo != null) {
            url = messageInfo.getPicUrl();
        }
        PicassoUtil.displayImageIndetUrl(url, mIvHomeMessage);
        mIvHomeMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MessageDetailActivity.class);
                intent.putExtra(AppConst.MSG_ROWKEY, messageInfo.getRowKey());
                startActivity(intent);
                finish();
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
                    startActivity(intent);
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
        int i = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, mContext.getResources()
                .getDisplayMetrics()) + .5f);
        return i;
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

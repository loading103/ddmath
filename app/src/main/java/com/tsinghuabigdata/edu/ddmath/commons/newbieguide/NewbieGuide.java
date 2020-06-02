package com.tsinghuabigdata.edu.ddmath.commons.newbieguide;

import android.app.Activity;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 新手引导
 */
public class NewbieGuide {

    public static final int CENTER = 0;

    private boolean mEveryWhereTouchable = true;
    private OnGuideChangedListener mOnGuideChangedListener;
    private List<HoleBean> mHoleList;
    private Activity mActivity;
    private GuideView mGuideView;
    private FrameLayout mParentView;

    public NewbieGuide(Activity activity) {
        init(activity);
    }

    private NewbieGuide init(Activity activity) {
        mActivity = activity;
        mParentView = (FrameLayout) mActivity.getWindow().getDecorView();
        mGuideView = new GuideView(mActivity);
        mHoleList = new ArrayList<>();
        return this;
    }

    //高亮View
    public NewbieGuide addHighLightView(View view, int type) {
        HoleBean hole = new HoleBean(view, type);
        mHoleList.add(hole);
        return this;
    }
    //高亮View
    public NewbieGuide addHighLightView(View view, int type, int roundRadius) {
        HoleBean hole = new HoleBean(view, type);
        hole.setRoundRadius( roundRadius );
        mHoleList.add(hole);
        return this;
    }
    //箭头图标
    public NewbieGuide addIndicateImg(int id, int offsetX, int offsetY,int width, int height) {
        ImageView arrowImg = new ImageView(mActivity);
        arrowImg.setImageResource(id);
        mGuideView.addView(arrowImg, getLp(offsetX, offsetY, width, height));
        return this;
    }

    //提示信息
    public NewbieGuide addMessage(String msg, int offsetX, int offsetY) {
        mGuideView.addView(generateMsgTv(msg), getLp(offsetX, offsetY));
        return this;
    }

    //按钮
    public NewbieGuide addBtnTv(String text, int offsetX, int offsetY) {
        mGuideView.addView(generateKnowTv(text), getLp(offsetX, offsetY));
        return this;
    }
    public NewbieGuide addBtnImage(int id, int offsetX, int offsetY,int width, int height) {
        ImageView arrowImg = new ImageView(mActivity);
        arrowImg.setImageResource(id);
        arrowImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                synchronized( NewbieGuide.this ){
                    remove();
                }
            }
        });

        mGuideView.addView(arrowImg, getLp(offsetX, offsetY, width, height));
        return this;
    }

    //图片
    public NewbieGuide addImageView(int id, int offsetX, int offsetY,int width, int height) {
        ImageView arrowImg = new ImageView(mActivity);
        arrowImg.setImageResource(id);
        mGuideView.addView(arrowImg, getLp(offsetX, offsetY, width, height));
        return this;
    }

    public NewbieGuide addImageView(View view, int offsetX, int offsetY,int width, int height) {
        mGuideView.addView(view, getLp(offsetX, offsetY, width, height));
        return this;
    }

    public void show() {

        if( mGuideView.getParent() != null ){
            return;
        }

        int paddingTop = ScreenUtils.getStatusBarHeight(mActivity);
        mGuideView.setPadding(0, paddingTop, 0, 0);
        mGuideView.setDate(mHoleList);
        mParentView.addView(mGuideView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams
                .MATCH_PARENT));

        if(mOnGuideChangedListener != null) mOnGuideChangedListener.onShowed();

        if(mEveryWhereTouchable) {
            mGuideView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    synchronized( NewbieGuide.this ){
                        remove();
                    }
                    return true;
                }
            });
        }
    }

    public void remove() {
        synchronized (mActivity) {
            if (mGuideView != null && mGuideView.getParent() != null) {
                mGuideView.recycler();
                ((ViewGroup) mGuideView.getParent()).removeView(mGuideView);
                mGuideView.removeAllViews();
                mHoleList.clear();
                if (mOnGuideChangedListener != null) {
                    mOnGuideChangedListener.onRemoved();
                }
            }
        }
    }

    public NewbieGuide setEveryWhereTouchable(boolean everyWhereTouchable) {
        mEveryWhereTouchable = everyWhereTouchable;
        return this;
    }

    public void setOnGuideChangedListener(OnGuideChangedListener onGuideChangedListener) {
        this.mOnGuideChangedListener = onGuideChangedListener;
    }

    //--------------------------------------------------------------------
    //生成提示文本
    private TextView generateMsgTv(String msg) {
        TextView msgTv = new TextView(mActivity);
        msgTv.setText(msg);
        msgTv.setTextColor(0xffffffff);
        msgTv.setTextSize(15);
        msgTv.setLineSpacing(ScreenUtils.dpToPx(mActivity, 5), 1f);
        msgTv.setGravity(Gravity.CENTER);
        return msgTv;
    }

    //生成我知道了文本
    private TextView generateKnowTv(String text) {
        TextView knowTv = new TextView(mActivity);
        knowTv.setTextColor(0xffffffff);
        knowTv.setTextSize(15);
        knowTv.setPadding(ScreenUtils.dpToPx(mActivity, 15), ScreenUtils.dpToPx(mActivity, 5), ScreenUtils.dpToPx(mActivity, 15),
                ScreenUtils.dpToPx(mActivity, 5));
        knowTv.setBackgroundResource(R.drawable.solid_white_bg);
        knowTv.setText(text);
        knowTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                synchronized( NewbieGuide.this ){
                    remove();
                }
            }
        });
        return knowTv;
    }

    //生成提示文本和我知道了文本
//    private LinearLayout generateMsgAndKnowTv(String msg) {
//        LinearLayout container = new LinearLayout(mActivity);
//        container.setOrientation(LinearLayout.VERTICAL);
//        container.setGravity(Gravity.CENTER_HORIZONTAL);
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams
//                .WRAP_CONTENT);
//        container.addView(generateMsgTv(msg), lp);
//        lp.topMargin = ScreenUtils.dpToPx(mActivity, 10);
//        container.addView(generateKnowTv("我知道啦"), lp);
//        return container;
//    }

    //生成布局参数
    private RelativeLayout.LayoutParams getLp(int offsetX, int offsetY) {
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams
                .WRAP_CONTENT);
        //水平方向
        if(offsetX == CENTER) {
            lp.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        } else if(offsetX < 0) {
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            lp.rightMargin = -offsetX;
            lp.leftMargin = -offsetX;
        } else {
            lp.rightMargin = offsetX;
            lp.leftMargin = offsetX;
        }
        //垂直方向
        if(offsetY == CENTER) {
            lp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        } else if(offsetY < 0) {
            lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            lp.bottomMargin = -offsetY;
        } else {
            lp.topMargin = offsetY;
        }
        return lp;
    }
    private RelativeLayout.LayoutParams getLp(int offsetX, int offsetY, int width, int height) {

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams( width, height );
        //水平方向
        if(offsetX == CENTER) {
            lp.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        } else if(offsetX < 0) {
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            lp.rightMargin = -offsetX;
            lp.leftMargin = -offsetX;
        } else {
            //lp.rightMargin = offsetX;
            lp.leftMargin = offsetX;
        }
        //垂直方向
        if(offsetY == CENTER) {
            lp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        } else if(offsetY < 0) {
            lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            lp.bottomMargin = -offsetY;
        } else {
            lp.topMargin = offsetY;
        }
        return lp;
    }
    //-----------------------------------------------------------------------------------------------
    //浮层显示后的回调
    public interface OnGuideChangedListener {
        void onShowed();

        void onRemoved();
    }


}

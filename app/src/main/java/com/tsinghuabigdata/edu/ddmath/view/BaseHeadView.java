package com.tsinghuabigdata.edu.ddmath.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.HeadImageUtils;
import com.tsinghuabigdata.edu.ddmath.module.myscore.bean.ScoreProductBean;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.BitmapUtils;
import com.tsinghuabigdata.edu.ddmath.util.DensityUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.PicassoUtil;


/**
 * 头像——自定义控件
 * 支持四层效果展示，1：圆形头像，2：白色圆环(首页按钮使用)，3：头像挂件，4：身份VIP
 */

public class BaseHeadView extends RelativeLayout{

    private CircleImageView mIvHead;
    private ImageView       mIvSheadBg;
    private ImageView       mIvPendant;
    private ImageView       mIvMemberGrade;

    private boolean showFisrtBg = false, showPendant = false, showGrade = false;

    public BaseHeadView(Context context) {
        this(context, null);
        initView( null );
    }

    public BaseHeadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView( attrs );
    }

    public void showHeadImage( String headpath, String pendantpath, int level ){

        mIvHead.recycle();
        HeadImageUtils.setHeadImage(mIvHead, headpath, R.drawable.doudou_portrait_default);
        if( TextUtils.isEmpty(pendantpath) ){
            mIvPendant.setVisibility( GONE );
        }else{
            mIvPendant.setVisibility( VISIBLE );
            String comUrl = AccountUtils.getFileServer() + pendantpath;
            PicassoUtil.getPicasso().load(comUrl).into(mIvPendant);
        }

        if( level > AppConst.MEMBER_NORMAL ){
            mIvMemberGrade.setVisibility(  showGrade?VISIBLE:GONE  );
            mIvMemberGrade.setImageResource( level== AppConst.MEMBER_SVIP?R.drawable.svip_buy:R.drawable.vip_buy);
        }else{
            mIvMemberGrade.setVisibility( GONE );
        }
    }

    public void setPendentSize(int width ){
        int w = width * 4/8;
        int h = w * 34 / 74;
        ViewGroup.LayoutParams layoutParams = mIvMemberGrade.getLayoutParams();
        layoutParams.width = w;
        layoutParams.height = h;
        mIvMemberGrade.setLayoutParams( layoutParams );
    }

    //显示当前用户的头像
    public void showHeadImage() {
        //显示用户头像
        mIvHead.recycle();
        HeadImageUtils.setHeadImage(mIvHead);

        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if( detailinfo == null ) return;

        //显示挂件
        if( showPendant ){
            ScoreProductBean pendant = AccountUtils.getPendantInfo();
            if( pendant == null ){
                mIvPendant.setVisibility( GONE );
            }else{
                String url = BitmapUtils.getUrlWithToken(pendant.getImagePath());
                mIvPendant.setVisibility( VISIBLE );
                PicassoUtil.getPicasso().load(url).into(mIvPendant);
            }
        }
        //显示会员等级
        if( showGrade ){
            int level = detailinfo.getVipLevel();
            if( level > AppConst.MEMBER_NORMAL ){
                mIvMemberGrade.setVisibility(  showGrade?VISIBLE:GONE  );
                mIvMemberGrade.setImageResource( level== AppConst.MEMBER_SVIP?R.drawable.svip_buy:R.drawable.vip_buy);
            }else{
                mIvMemberGrade.setVisibility( GONE );
            }
        }
    }

    @Override
    public void setActivated(boolean activated) {
        super.setActivated(activated);
        if(showFisrtBg){
            mIvSheadBg.setBackgroundResource( activated?R.drawable.service:R.drawable.ic_header);

            //头像大小
            int margin;
            boolean isPad = GlobalData.isPad();
            ViewGroup.LayoutParams layoutParams = mIvHead.getLayoutParams();
            if( isPad ){
//                layoutParams.width = DensityUtils.dp2px( getContext(),activated?72:48);
//                layoutParams.height = DensityUtils.dp2px( getContext(),activated?72:48);
                layoutParams.width = DensityUtils.dp2px( getContext(),activated?120:80);
                layoutParams.height = DensityUtils.dp2px( getContext(),activated?120:80);
            }else {
//                layoutParams.width = DensityUtils.dp2px( getContext(),activated?36:24);
//                layoutParams.height = DensityUtils.dp2px( getContext(),activated?36:24);
                layoutParams.width = DensityUtils.dp2px( getContext(),activated?60:40);
                layoutParams.height = DensityUtils.dp2px( getContext(),activated?60:40);
            }
            mIvHead.setLayoutParams( layoutParams );
            mIvHead.recycle();

            //挂件
            layoutParams = mIvPendant.getLayoutParams();
            if( isPad ){
                layoutParams.width = DensityUtils.dp2px( getContext(),activated?120:80);
                layoutParams.height = DensityUtils.dp2px( getContext(),activated?120:80);
            }else {
                layoutParams.width = DensityUtils.dp2px( getContext(),activated?60:40);
                layoutParams.height = DensityUtils.dp2px( getContext(),activated?60:40);
            }
            mIvPendant.setLayoutParams( layoutParams );

            //调整位置
            if( isPad ){
                margin = (120 - (activated?120:80))/2;
                margin = margin - 4;          //图像下边有空白
            }else{
                margin = (60 - (activated?60:40))/2;
                margin = margin - (activated?1:3);          //图像下边有空白
            }
            RelativeLayout.LayoutParams rlayoutParams = (RelativeLayout.LayoutParams)mIvMemberGrade.getLayoutParams();
            rlayoutParams.bottomMargin = DensityUtils.dp2px( getContext(),margin);
            mIvMemberGrade.setLayoutParams( rlayoutParams );
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if( !changed ) return;
        //等比设置用户等级图片的大小  整个宽度的3/8
        int w = (r - l) * 4/8;
        int h = w * 34 / 74;
        ViewGroup.LayoutParams layoutParams = mIvMemberGrade.getLayoutParams();
        layoutParams.width = w;
        layoutParams.height = h;
        mIvMemberGrade.setLayoutParams( layoutParams );

        //用户头像要比挂件小  60%
//        if( !showFisrtBg ){
//            w = (r - l) * 7/10;
//            h = (b - t) * 7/10;
//            layoutParams = mIvHead.getLayoutParams();
//            layoutParams.width = w;
//            layoutParams.height = h;
//            mIvHead.setLayoutParams( layoutParams );
//        }
    }

    //---------------------------------------------------------------------------------------------------
    /**
     * 初始化界面元素
     */
    private void initView(AttributeSet attrs) {

        View.inflate(getContext(), GlobalData.isPad()? R.layout.view_basehead_phone:R.layout.view_basehead_phone, this);

        mIvHead =  findViewById(R.id.view_iv_head_img);
        mIvSheadBg =  findViewById(R.id.view_iv_shead_bg);
        mIvPendant =  findViewById(R.id.view_iv_head_pendant);
        mIvMemberGrade =  findViewById(R.id.view_iv_membergrade);

        if( attrs != null ){
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.BaseHeadView);
            showFisrtBg = a.getBoolean(R.styleable.BaseHeadView_showFirstBg,false);
            showPendant = a.getBoolean(R.styleable.BaseHeadView_showPendant,false);
            showGrade = a.getBoolean(R.styleable.BaseHeadView_showGrade,false);
            a.recycle();
        }

        mIvSheadBg.setVisibility( showFisrtBg?VISIBLE:GONE );
        mIvPendant.setVisibility( showPendant?VISIBLE:GONE );
        mIvMemberGrade.setVisibility( showGrade?VISIBLE:GONE );
    }

}

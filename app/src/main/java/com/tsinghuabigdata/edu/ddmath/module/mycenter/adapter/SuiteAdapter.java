package com.tsinghuabigdata.edu.ddmath.module.mycenter.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.adapter.CommonAdapter;
import com.tsinghuabigdata.edu.ddmath.adapter.ViewHolder;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.view.UserPrivilegeView;
import com.tsinghuabigdata.edu.ddmath.module.product.ProductUtil;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.ProductBean;
import com.tsinghuabigdata.edu.ddmath.util.AssetsUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cn.carbs.android.expandabletextview.library.ExpandableTextView;


/**
 * 我要购买——套餐列表适配器
 * Created by Administrator on 2018/4/8.
 */

public class SuiteAdapter extends CommonAdapter<ProductBean> {

    private int                 curPosition;
    private SuiteSelectListener mSuiteSelectListener;


    public SuiteAdapter(Context context, List<ProductBean> mDatas) {
        super(context, mDatas);
    }

    @Override
    protected int getLayoutId() {
        return GlobalData.isPad() ? R.layout.item_suite : R.layout.item_suite_phone;
    }

    private int textHeight = 0;

    @Override
    protected void convert(ViewHolder helper, final int position, final ProductBean item) {
        TextView tvSuitePrice = helper.getView(R.id.tv_suite_price);
        TextView tvSuiteOriginalPrice = helper.getView(R.id.tv_suite_original_price);
        final TextView tvSuiteName = helper.getView(R.id.tv_suite_name);
        LinearLayout llBuyItem = helper.getView(R.id.ll_buy_item);
        Button btnBuy = helper.getView(R.id.btn_buy);
        TextView tvUsedate = helper.getView(R.id.tv_usedate);
        final ExpandableTextView tvSuiteIntro = helper.getView(R.id.tv_suite_intro);
        LinearLayout llLookPrivilege = helper.getView(R.id.ll_look_privilege);
        //TextView tvLookPrivilege = helper.getView(R.id.tv_look_privilege);
        final CheckBox cbLookPrivilege = helper.getView(R.id.cb_look_privilege);
        //LinearLayout llUseCount = helper.getView(R.id.ll_use_count);
        //View viewLine = helper.getView(R.id.view_line);
        //MultiGridView gvUseCount = helper.getView(R.id.gv_use_count);
        UserPrivilegeView privilegeView = helper.getView(R.id.upv_user_priviledge_view);
        ImageView levelImageView = helper.getView(R.id.im_user_memberlevel);
        levelImageView.setVisibility(View.GONE);

        if( textHeight==0 ){
            String text = "套餐";
            Paint tvPaint = tvSuiteName.getPaint();
            Rect rect = new Rect();
            tvPaint.getTextBounds( text, 0, text.length(), rect);
            textHeight = rect.height();
        }

        //helper.setText(R.id.tv_suite_price, item.getName());
        //tvSuiteName.setText(item.getName());
        final String suiteName = item.getName();
        String imgpath = "";
        if( item.getVipLevel()>0 ){
            imgpath = "<img src=\"" + (item.getVipLevel()== AppConst.MEMBER_SVIP?"ic_svip.png":"ic_vip.png") + "\">";
        }
//        LocalQuestionInfo questionInfo = new LocalQuestionInfo();
//        questionInfo.setStem( suiteName );
//        questionInfo.setStemGraph( imgpath );
//        tvSuiteName.setQuestion( questionInfo, false );
        tvSuiteName.setText(Html.fromHtml(suiteName+imgpath, new Html.ImageGetter() {

            @Override
            public Drawable getDrawable(String source) {
                Drawable drawable = AssetsUtils.assets2Drawable(mContext, source);
                int dh = textHeight /* 3/4*/;
                int dw = dh * 108/45;
                if(drawable!=null)drawable.setBounds(0, 0, dw, dh);
                return drawable;
            }
        },null) );

        tvSuitePrice.setText(ProductUtil.getPrice(item));
        tvSuiteOriginalPrice.setText( String.format(Locale.getDefault(),"原价%s",ProductUtil.getPrice(item.getOriginalChargeDdAmt())));
        tvSuiteOriginalPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        tvUsedate.setText(ProductUtil.getSuiteUseTime(item));
        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSuiteSelectListener != null) {
                    mSuiteSelectListener.select(item);
                }
            }
        });

        //tvSuiteIntro.setText(item.getDescription());
        tvSuiteIntro.updateForRecyclerView(item.getDescription(), tvSuiteIntro.getWidth(), curPosition == position?ExpandableTextView.STATE_EXPAND:ExpandableTextView.STATE_SHRINK);

        if (curPosition == position) {
            llBuyItem.setActivated(true);
            privilegeView.setVisibility(View.VISIBLE);
            ArrayList<ProductBean> productVoList = item.getProductVoList();
//            if (GlobalData.isPad()) {
//                gvUseCount.setNumColumns(3);
//            } else if (productVoList.size() == 1) {
//                gvUseCount.setNumColumns(2);
//            } else if (isCountTitleLong(productVoList)) {
//                gvUseCount.setNumColumns(2);
//            } else {
//                gvUseCount.setNumColumns(3);
//            }
//            ProductCountAdapter adapter = new ProductCountAdapter(mContext, productVoList);
//            gvUseCount.setAdapter(adapter);
            //专属标志
            privilegeView.setSuiteData( item.getVipLevel(), item.getDefaultRecommend()>0, productVoList );
        } else {
            llBuyItem.setActivated(false);
            privilegeView.setVisibility(View.GONE);
        }
        cbLookPrivilege.setEnabled(false);
        cbLookPrivilege.setChecked(curPosition == position);
        llLookPrivilege.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedItem( cbLookPrivilege, position, tvSuiteIntro );
            }
        });
        llBuyItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedItem( cbLookPrivilege, position, tvSuiteIntro );
            }
        });

        //
//        levelImageView.setVisibility(View.INVISIBLE);
//        if( item.getVipLevel() == AppConst.MEMBER_SVIP ){
//            levelImageView.setVisibility(View.VISIBLE);
//            levelImageView.setImageResource( R.drawable.svip_buy );
//        }else if( item.getVipLevel() == AppConst.MEMBER_VIP ){
//            levelImageView.setVisibility(View.VISIBLE);
//            levelImageView.setImageResource( R.drawable.vip_buy );
//        }
    }

    private void selectedItem(CheckBox cbLookPrivilege, int position, ExpandableTextView tvSuiteIntro ){
        if (cbLookPrivilege.isChecked()) {
            curPosition = -1;
        } else {
            curPosition = position;
        }
        tvSuiteIntro.getOnClickListener( cbLookPrivilege );
        notifyDataSetChanged();
    }

//    private boolean isCountTitleLong(ArrayList<ProductBean> productVoList) {
//        List<ProductBean> tempList = new ArrayList<>();
//        tempList.addAll(productVoList);
//        Collections.sort(tempList, new Comparator<ProductBean>() {
//            @Override
//            public int compare(ProductBean lhs, ProductBean rhs) {
//                return rhs.getNameLength() - lhs.getNameLength();
//            }
//        });
//        int nameLength = tempList.get(0).getNameLength();
//        LogUtils.i("nameLength = " + nameLength);
//        if (nameLength < 6) {
//            return false;
//        }
//        int contentWidth = WindowUtils.getScreenWidth(mContext) - DensityUtils.dp2px(mContext, AppConst.NAVI_WIDTH_PHONE + 170 + 10 * 2 + 10 * 2 + 8 * 2);
//        float factWdith = DensityUtils.sp2pxFloat(mContext, (nameLength + 3.5f) * 12);
//        float w = factWdith - contentWidth / 3;
//        LogUtils.i("contentWidth = " + contentWidth + "  contentWidth / 3 = " + contentWidth / 3);
//        LogUtils.i("factWdith = " + factWdith);
//        return w > 0;
//    }

    public void setCurPosition(int curPosition) {
        this.curPosition = curPosition;
    }

    public interface SuiteSelectListener {
        void select(ProductBean suiteBean);
    }

    public void setSuiteSelectListener(SuiteSelectListener suiteSelectListener) {
        mSuiteSelectListener = suiteSelectListener;
    }


}

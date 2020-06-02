package com.tsinghuabigdata.edu.ddmath.parent.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.adapter.CommonAdapter;
import com.tsinghuabigdata.edu.ddmath.adapter.ViewHolder;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.adapter.ProductCountAdapter;
import com.tsinghuabigdata.edu.ddmath.module.product.ProductUtil;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.ProductBean;
import com.tsinghuabigdata.edu.ddmath.view.MultiGridView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * 我要购买——套餐列表适配器
 * Created by Administrator on 2018/4/8.
 */

public class ParentSuiteAdapter extends CommonAdapter<ProductBean> {

    private int                 curPosition;
    private SuiteSelectListener mSuiteSelectListener;


    public ParentSuiteAdapter(Context context, List<ProductBean> mDatas) {
        super(context, mDatas);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_suite_parent;
    }

    @Override
    protected void convert(ViewHolder helper, final int position, final ProductBean item) {
        LinearLayout mainLayout = helper.getView(R.id.ll_buy_item);
        //套餐名称
        TextView tvSuiteName = helper.getView(R.id.tv_suite_name);
        //套餐价格
        TextView tvSuitePrice = helper.getView(R.id.tv_suite_price);
        TextView tvSuiteOriginalPrice = helper.getView(R.id.tv_suite_original_price);
        //使用期限
        TextView tvUsedate = helper.getView(R.id.tv_usedate);

        //购买按钮
        Button btnBuy = helper.getView(R.id.btn_buy);

        //查看详情
        LinearLayout llLookPrivilege = helper.getView(R.id.ll_look_privilege);
        final CheckBox cbLookPrivilege = helper.getView(R.id.cb_look_privilege);

        //扩展信息区
        LinearLayout llExpandInfo = helper.getView(R.id.layout_expand_info);

        TextView tvSuiteIntro = helper.getView(R.id.tv_suite_intro);
        MultiGridView gvUseCount = helper.getView(R.id.gv_use_count);


        tvSuiteName.setText(item.getName());
        tvSuitePrice.setText(ProductUtil.getPrice(item));
        tvSuiteOriginalPrice.setText( String.format(Locale.getDefault(), "原价%s", ProductUtil.getPrice(item.getOriginalChargeDdAmt())));
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

        tvSuiteIntro.setText(item.getDescription());
        if (curPosition == position) {
            mainLayout.setActivated(true);
            llExpandInfo.setVisibility(View.VISIBLE);
            ArrayList<ProductBean> productVoList = item.getProductVoList();

            ProductCountAdapter adapter = new ProductCountAdapter(mContext, productVoList);
            adapter.setFromParent();
            gvUseCount.setAdapter(adapter);
        } else {
            mainLayout.setActivated(false);
            llExpandInfo.setVisibility(View.GONE);
        }
        cbLookPrivilege.setEnabled(false);
        cbLookPrivilege.setChecked(curPosition == position);
        llLookPrivilege.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedItem( cbLookPrivilege, position );
            }
        });
        mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedItem( cbLookPrivilege, position );
            }
        });
    }

    private void selectedItem(CheckBox cbLookPrivilege, int position){
        if (cbLookPrivilege.isChecked()) {
            curPosition = -1;
        } else {
            curPosition = position;
        }
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
//        if (nameLength < 6) {
//            return false;
//        }
//        int contentWidth = WindowUtils.getScreenWidth(mContext) - DensityUtils.dp2px(mContext, AppConst.NAVI_WIDTH_PHONE + 170 + 10 * 2 + 10 * 2 + 8 * 2);
//        float factWdith = DensityUtils.sp2pxFloat(mContext, (nameLength + 3.5f) * 12);
//        float w = factWdith - contentWidth / 3;
//        //LogUtils.i("contentWidth = " + contentWidth + "  contentWidth / 3 = " + contentWidth / 3);
//        //LogUtils.i("factWdith = " + factWdith);
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

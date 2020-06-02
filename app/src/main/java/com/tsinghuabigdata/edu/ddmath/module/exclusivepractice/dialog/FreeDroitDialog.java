package com.tsinghuabigdata.edu.ddmath.module.exclusivepractice.dialog;

import android.app.Dialog;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.module.exclusivepractice.adapter.FreeDroidAdapter;
import com.tsinghuabigdata.edu.ddmath.module.exclusivepractice.bean.FreeDroitDetailBean;
import com.tsinghuabigdata.edu.ddmath.util.DensityUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

import java.util.ArrayList;


/**
 * 免费使用权详情列表
 */

public class FreeDroitDialog extends Dialog {

    private Context mContext;
    //private ListView listView;
    private FreeDroidAdapter mAdapter;

    public FreeDroitDialog(@NonNull Context context) {
        super(context, R.style.dialog);
        initView();
    }

    public FreeDroitDialog(@NonNull Context context, @StyleRes int themeResId ) {
        super(context, themeResId);
        initView();
    }

    public void setData(ArrayList<FreeDroitDetailBean> list, int gleft, int gtop, int pbottom ) {

        if ( list == null || list.size() == 0) {
            return;
        }

        int size = list.size();

        //设置显示位置
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();

        int fLeft;
        int fTop;
        int w;
        int h;
        if (GlobalData.isPad()) {
            //fLeft = /*DensityUtils.dp2px(getContext(), AppConst.NAVI_WIDTH_PAD + 20 + 20) +*/ gleft;
            fTop = /*DensityUtils.dp2px(getContext(), 148 + 20) +*/ gtop;
            w = DensityUtils.dp2px(mContext, 320);
            //int remainHeight = pbottom - gtop;
            h = DensityUtils.dp2px(getContext(), 10 + (6+14+12) * size - 6);
            //int downHeight = ScreenUtils.getPhoneScreenContentHeight(mContext) - fTop - DensityUtils.dp2px(getContext(), 8);
            //h = calHeight;//Math.min(downHeight, Math.min(remainHeight, calHeight));
        } else {
            fLeft = DensityUtils.dp2px(getContext(), AppConst.NAVI_WIDTH_PHONE + 20);
            fTop = /*DensityUtils.dp2px(getContext(), 94 + 20) +*/ gtop;
            w = DensityUtils.dp2px(mContext, 280);
            //int remainHeight = pbottom - gtop/* - DensityUtils.dp2px(getContext(), 15 + 20 + 6)*/;
            h = DensityUtils.dp2px(getContext(), 10 + (6+6+10) * size - 6);
            //int downHeight = ScreenUtils.getPhoneScreenContentHeight(mContext) - fTop - DensityUtils.dp2px(getContext(), 4);
            //h = calHeight;//Math.min(downHeight, Math.min(remainHeight, calHeight));
        }

        lp.x = gleft;
        lp.y = gtop;
        lp.width = w;
        lp.height = h;
        lp.dimAmount = 0.1f;

        dialogWindow.setAttributes(lp);

        mAdapter.clear();
        mAdapter.addAll( list );
        mAdapter.notifyDataSetChanged();
    }

    //---------------------------------------------------------------------------------------

    private void initView() {
        mContext = getContext();
        setContentView( GlobalData.isPad()?R.layout.dialog_upload:R.layout.dialog_upload_phone);

        ImageView imageView = (ImageView)findViewById( R.id.iv_triangle );
        imageView.setVisibility( View.GONE );
        ListView listView = (ListView) findViewById(R.id.lv_upload);

        mAdapter = new FreeDroidAdapter(getContext());
        listView.setAdapter( mAdapter );

        Window dialogWindow = getWindow();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);
        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        setCancelable(false);
        setCanceledOnTouchOutside(true);
    }

}

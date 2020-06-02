package com.tsinghuabigdata.edu.ddmath.module.errorbook.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.adapter.UploadInfoAdatper;
import com.tsinghuabigdata.edu.ddmath.bean.ExamNameBean;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.bean.QuestionVo;
import com.tsinghuabigdata.edu.ddmath.commons.newbieguide.ScreenUtils;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.util.DensityUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.LogUtils;

import java.util.List;


/**
 * 错题本列表——上传记录Dialog
 * Created by Administrator on 2017/11/1.
 */

public class UploadRecordDialog extends Dialog {

    private Context mContext;


    public UploadRecordDialog(@NonNull Context context, QuestionVo questionVo, int left, int top, int bottom) {
        super(context, R.style.dialog);
        initView(questionVo, left, top, bottom);
        initData(questionVo);
    }

    public UploadRecordDialog(@NonNull Context context, @StyleRes int themeResId, QuestionVo questionVo, int left, int top, int bottom) {
        super(context, themeResId);
        initView(questionVo, left, top, bottom);
        initData(questionVo);
    }

    private void initView(QuestionVo questionVo, int gleft, int gtop, int pbottom) {
        mContext = getContext();
        if (GlobalData.isPad()) {
            setContentView(R.layout.dialog_upload);
        } else {
            setContentView(R.layout.dialog_upload_phone);
        }

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);
        int fLeft;
        int fTop;
        int w;
        int remainHeight;
        int calHeight;
        int h;
        if (GlobalData.isPad()) {
            fLeft = /*DensityUtils.dp2px(getContext(), AppConst.NAVI_WIDTH_PAD + 20 + 20) +*/ gleft;
            fTop = /*DensityUtils.dp2px(getContext(), 148 + 20) +*/ gtop;
            w = DensityUtils.dp2px(mContext, 440);
            remainHeight = pbottom - gtop /*- DensityUtils.dp2px(getContext(), 15 + 20 + 6)*/;
            calHeight = DensityUtils.dp2px(getContext(), 17 + 10 + 31 * questionVo.getExamNames().size() - 5);
            int downHeight = ScreenUtils.getPhoneScreenContentHeight(mContext) - fTop - DensityUtils.dp2px(getContext(), 8);
            h = Math.min(downHeight, Math.min(remainHeight, calHeight));
            LogUtils.i("isPad remainHeight=" + remainHeight + " calHeight=" + calHeight + " downHeight=" + downHeight);
        } else {
            fLeft = DensityUtils.dp2px(getContext(), AppConst.NAVI_WIDTH_PHONE + 20);
            fTop = /*DensityUtils.dp2px(getContext(), 94 + 20) +*/ gtop;
            w = DensityUtils.dp2px(mContext, 440);
            remainHeight = pbottom - gtop/* - DensityUtils.dp2px(getContext(), 15 + 20 + 6)*/;
            calHeight = DensityUtils.dp2px(getContext(), 17 + 10 + 31 * questionVo.getExamNames().size() - 5);
            int downHeight = ScreenUtils.getPhoneScreenContentHeight(mContext) - fTop - DensityUtils.dp2px(getContext(), 4);
            h = Math.min(downHeight, Math.min(remainHeight, calHeight));
            LogUtils.i("isPhone remainHeight=" + remainHeight + " calHeight=" + calHeight + " downHeight=" + downHeight);
        }
        //LogUtils.i("w=" + w + " h=" + h);
        //LogUtils.i("fLeft=" + fLeft + " fTop=" + fTop);
        lp.x = fLeft;
        lp.y = fTop;
        lp.width = w;
        lp.height = h;
        lp.dimAmount = 0.1f;
        dialogWindow.setAttributes(lp);
        //        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        setCancelable(false);
        setCanceledOnTouchOutside(true);
    }


    private void initData(QuestionVo questionVo) {
        ListView listView = (ListView) findViewById(R.id.lv_upload);
        if (questionVo == null || questionVo.getExamNames() == null || questionVo.getExamNames().size() == 0) {
            return;
        }
        List<ExamNameBean> examNames = questionVo.getExamNames();
        UploadInfoAdatper uploadInfoAdatper = new UploadInfoAdatper(mContext, examNames);
        listView.setAdapter(uploadInfoAdatper);
    }


}

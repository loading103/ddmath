package com.tsinghuabigdata.edu.ddmath.module.robotqa;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.bean.WebchatAdapter;
import com.tsinghuabigdata.edu.ddmath.util.KnowCognitionUtil;
import com.tsinghuabigdata.edu.ddmath.view.KnowCognitionView;

import java.util.List;


/**
 * Created by HP on 2016/10/21.
 */
public class KnowledgeView extends LinearLayout implements View.OnClickListener {
    public static final String KNOW_CONGNITION_VALUE = "know_congnition_value";
    public static final String KNOW_CONGNITION_TOTAL = "know_congnition_total";
    private boolean tvMoreEnabled = true;

    private TextView tv_more;
    private TextView ltvTitle;
    private LinearLayout llKnowviewContainer;
    private TextView tvKnowlegdeTitle;
    private Context context;

    private List<String> datas;
    private int visibleCounts;

    private boolean isHaveLowLevelKnow;
    private OnClickListener onClickListener;

    public KnowledgeView(Context context) {
        super(context);
        init(context);
    }

    public KnowledgeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public KnowledgeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public KnowledgeView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        this.context = context;

        LayoutInflater.from(getContext()).inflate(R.layout.listitem_message_comein_knowledge, this);
        tv_more = (TextView) findViewById(R.id.tv_more);
        tvKnowlegdeTitle = (TextView) findViewById(R.id.tv_knowledge_title);
        ltvTitle = (TextView) findViewById(R.id.ltv_title);
        llKnowviewContainer = (LinearLayout) findViewById(R.id.ll_knowledge_container);

        tv_more.setOnClickListener(this);

        //只展示一个
//        showFirstKonwview();
    }

    private void showFirstKonwview() {
        //低于平均水平的提示，红色字体
        showRedHints(isHaveLowLevelKnow);
        if (llKnowviewContainer.getChildCount() == 0) {
            addKnowcognitionView(context, datas.get(0), 0, true);
            visibleCounts = 1;
        }
    }

    public void setLtvTitle(int num) {
        ltvTitle.setText("你有" + num + "个错误知识点，赶紧从下面相关的知识网络图中查漏补缺吧~");
    }

    public List<String> getDatas() {
        return datas;
    }

    public void setDatas(List<String> datas,boolean isKnowviewExpand, OnClickListener listener) {

        llKnowviewContainer.removeAllViews();

        onClickListener = listener;

        if (isKnowviewExpand){
            tvMoreEnabled = false;
        }else {
            tvMoreEnabled = true;
        }
        this.datas = datas;
        if (datas.size() > 1 && tvMoreEnabled) {
            tv_more.setVisibility(VISIBLE);
        } else {
            tv_more.setVisibility(GONE);
        }
        //检查是否有错误知识点
        isHaveLowLevelKnow = false;
        for (String data : datas) {
            if (KnowCognitionUtil.isLowerAverage(data)) {
                isHaveLowLevelKnow = true;
                break;
            }
        }
        //开始绘图
        if (datas.size() > 0 ) {
            //黑色提示，你有X个错误知识点。。。
            setLtvTitle(datas.size());
            if (tvMoreEnabled){
                showFirstKonwview();
            }else {
                showFullKonwview();
            }
        }
    }

    private void showRedHints(boolean show){
        if (show){
            tvKnowlegdeTitle.setVisibility(VISIBLE);
        }else {
            tvKnowlegdeTitle.setVisibility(GONE);
        }
    }
    private void showFullKonwview(){
        int viewnum = llKnowviewContainer.getChildCount();
        if (viewnum == datas.size()) {
            return;
        }

        if (viewnum == 0){
            showFirstKonwview();
            if (datas.size() >= 2) {
                drawOtherKnowcognImg(true);
            }
        }
        if (viewnum ==1){
            if (datas.size() >= 2) {
                drawOtherKnowcognImg(true);
            }
        }
    }

    private void addKnowcognitionView(final Context context, final String data, final int position, boolean isAddShow) {
        KnowCognitionView knowCognitionView = new KnowCognitionView(context);
        //knowCognitionView.setKnowData(data);
        knowCognitionView.setBitmap(getKnowBitmap(position, data));
        knowCognitionView.setTag(position);
        knowCognitionView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //AlertManager.toast(context, "clicked " + v.getTag());
                Intent intent = new Intent(getContext(), KnowledgeImageActivity.class);
//                intent.putExtra(KNOW_CONGNITION_VALUE, data);
                intent.putExtra(KNOW_CONGNITION_VALUE, position);
                intent.putExtra(KNOW_CONGNITION_TOTAL, visibleCounts);
                getContext().startActivity(intent);
                ((Activity) getContext()).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        if (isAddShow){
            llKnowviewContainer.addView(knowCognitionView);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_more:
                if (datas.size() >= 2) {
                    drawOtherKnowcognImg(true);
                }
                tv_more.setVisibility(GONE);
                tvMoreEnabled = false;
                onClickListener.onClick(tv_more);
                break;
            default:
                break;
        }
    }

    private void drawOtherKnowcognImg(boolean isshow) {
        for (int i = 1; i < datas.size(); i++) {
            addKnowcognitionView(context, datas.get(i), i, isshow);
        }
        if (isshow){
            visibleCounts = datas.size();
        }
    }

    private Bitmap getKnowBitmap(int position, String data) {
        Bitmap bitmap = null;
        if (WebchatAdapter.getBitmaps().containsKey(position)) {
            bitmap = WebchatAdapter.getBitmaps().get(position);
        } else {
            KnowCognitionUtil knowCognition = new KnowCognitionUtil( getContext() );
            knowCognition.setKnowData(data);
            bitmap = knowCognition.getKnowBitmap();
            WebchatAdapter.getBitmaps().put(position, bitmap);
        }
        return bitmap;
    }
}

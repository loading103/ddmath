package com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

/**
 * 知识诊断 按钮
 */
public class KnowDiagnoseButtonView extends LinearLayout{

    private KnowDiagnoseDialView dialView;      //表盘
    private TextView textView;                   //

    public KnowDiagnoseButtonView(Context context) {
        super(context);
        init();
    }

    public KnowDiagnoseButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public KnowDiagnoseButtonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setKnowRate( float rate ){
        dialView.setKnowledgeRate( rate );
        textView.setText( String.valueOf( Math.round(rate*100) ) );
    }
    //-----------------------------------------------------------------------------
    private void init() {
        inflate( getContext(), GlobalData.isPad()?R.layout.view_diagnose_button:R.layout.view_diagnose_button_phone, this );

        dialView = (KnowDiagnoseDialView)findViewById( R.id.dv_dialview );
        textView = (TextView)findViewById( R.id.tv_ratevalue );
    }

}

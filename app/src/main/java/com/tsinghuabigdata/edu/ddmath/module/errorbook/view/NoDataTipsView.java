package com.tsinghuabigdata.edu.ddmath.module.errorbook.view;


import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.activity.KnowledgeActivity;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

/**
 * 没有数据时提示
 */
public class NoDataTipsView extends RelativeLayout implements View.OnClickListener{

    //
    private OvalTextView textView;
    private TextView tv_content;
    private String type;

    public NoDataTipsView(Context context) {
        super(context);
        init(context);
    }

    public NoDataTipsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public NoDataTipsView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init( context);
    }

    public void setData( String text, String key, String type ){
        textView.setText( text, key, textView.getTextSize() );
        this.type = type;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent( getContext(), KnowledgeActivity.class);
        intent.putExtra( KnowledgeActivity.PARAM_FROM_TYPE, type);
        getContext().startActivity(intent);
    }

    //-------------------------------------------------------------------------
    private void init(Context context){
        inflate( context, GlobalData.isPad()?R.layout.view_errbook_nodata :R.layout.view_errbook_nodata_phone, this );
        textView = findViewById( R.id.tv_nodata_tips );
        tv_content = findViewById( R.id.tv_empty );
        textView.setOnClickListener( this );
    }

    public void setTvEmty( String text ){
        tv_content.setText(text);
    }

}

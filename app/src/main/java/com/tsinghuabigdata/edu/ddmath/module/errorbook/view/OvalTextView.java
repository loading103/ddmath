package com.tsinghuabigdata.edu.ddmath.module.errorbook.view;


import android.annotation.SuppressLint;
import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;

/**
 * 用户等级View 周题练使用
 */
@SuppressLint("AppCompatCustomView")
public class OvalTextView extends TextView{

    public OvalTextView(Context context) {
        super(context);
    }

    public OvalTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OvalTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setText( String text, String ovalKey, float textSize ){
        if( text == null || TextUtils.isEmpty(ovalKey) )
            return;
        SpannableString spannableString = new SpannableString( text + ovalKey );
        OvalTextSpan hotSpan = new OvalTextSpan( getContext(), ovalKey );
        int color = getContext().getResources().getColor(R.color.color_3BCCD9);
        hotSpan.setColor( color, color, textSize );
        spannableString.setSpan( hotSpan, text.length(), spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        setText(spannableString);
    }
}

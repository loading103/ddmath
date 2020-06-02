package com.tsinghuabigdata.edu.ddmath.module.ddwork.view;


import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;


public class FocusTipsView extends LinearLayout {

    private ImageView imageView;
	private TextView   textView;

	public FocusTipsView(Context context) {
		super(context);
		init(context,null);
	}

	public FocusTipsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public FocusTipsView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init( context, attrs );
	}

	public void start(){
		AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getDrawable();
		animationDrawable.start();
	}
	public void stop(){
		AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getDrawable();
		animationDrawable.stop();
	}

	public void setTextView( String text ){
		textView.setText( text );
	}

	//-------------------------------------------------------------------------
	private void init(Context context, AttributeSet attrs){
        //
        inflate( context, GlobalData.isPad()?R.layout.view_focus_tips:R.layout.view_focus_tips_phone, this );

        imageView = (ImageView)findViewById( R.id.vfocus_image );
		 textView  = (TextView)findViewById( R.id.vfocus_text );
	}

}

package com.tsinghuabigdata.edu.ddmath.module.ddwork.view;


import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;


public class FixedPiontView extends LinearLayout {

    //
    public static final int ST_FIXING = 0;
    public static final int ST_FIXED = 1;

    //对齐类型
    public static final int TYPE_LEFT = 2;
    public static final int TYPE_RIGHT = 3;
    private int align_type = TYPE_LEFT;     //left or right

    //对象
    private RelativeLayout markLayout;
    private ImageView markImageView;
    private View  fixedRectView;

	public FixedPiontView(Context context) {
		super(context);
		initData(context,null);
	}

	public FixedPiontView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initData(context, attrs);
	}

	public FixedPiontView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initData( context, attrs );
	}

    public void setFixedStatus( int status ){
        if( ST_FIXED == status ){
            fixedRectView.setBackgroundResource( GlobalData.isPad()?R.drawable.camera_rect_success:R.drawable.camera_rect_success_phone );
            markImageView.setImageDrawable( getResources().getDrawable(R.drawable.ic_check_round) );
        }else{
            fixedRectView.setBackgroundResource( GlobalData.isPad()?R.drawable.camera_rect_normal:R.drawable.camera_rect_normal_phone );
            markImageView.setImageDrawable( getResources().getDrawable(R.drawable.ic_check_round_gray) );
        }
    }

    public void setFixedWidth( int width ){
        if( width == 0 ) return;
        ViewGroup.LayoutParams layoutParams = markLayout.getLayoutParams();
        layoutParams.width = width;
        markLayout.setLayoutParams( layoutParams );
    }

	//-------------------------------------------------------------------------
	private void initData(Context context, AttributeSet attrs){
        //
        if( attrs != null ){
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FixedPiontView);
            align_type = a.getInt(R.styleable.FixedPiontView_type, TYPE_LEFT);
            a.recycle();
        }

        //
        if( TYPE_LEFT == align_type ){
            inflate( context, GlobalData.isPad()?R.layout.view_camera_rect:R.layout.view_camera_rect_phone, this );
        }else{
            inflate( context, GlobalData.isPad()?R.layout.view_camera_rect:R.layout.view_camera_rect_phone, this );
        }

        markLayout = (RelativeLayout)findViewById( R.id.view_marklayout );
        markImageView = (ImageView)findViewById( R.id.view_markimageview );
        fixedRectView = findViewById( R.id.view_markrect );

        markImageView.setImageDrawable( getResources().getDrawable(R.drawable.ic_check_round_gray) );
        //markImageView.setVisibility( INVISIBLE );

	}

}

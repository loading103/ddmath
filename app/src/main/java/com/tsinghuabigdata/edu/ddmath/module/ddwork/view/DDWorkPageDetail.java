package com.tsinghuabigdata.edu.ddmath.module.ddwork.view;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;


public class DDWorkPageDetail extends LinearLayout {

    //
//    public static final int ST_FIXING = 0;
//    public static final int ST_FIXED = 1;
//
//    //对齐类型
//    public static final int TYPE_LEFT = 2;
//    public static final int TYPE_RIGHT = 3;
//    private int align_type = TYPE_LEFT;     //left or right
//
//    //对象
//    private RelativeLayout markImageView;
//    private View  fixedRectView;

	public DDWorkPageDetail(Context context) {
		super(context);
		initData(context,null);
	}

	public DDWorkPageDetail(Context context, AttributeSet attrs) {
		super(context, attrs);
		initData(context, attrs);
	}

	public DDWorkPageDetail(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initData( context, attrs );
	}

//    public void setFixedStatus( int status ){
//        if( ST_FIXED == status ){
//            markImageView.setVisibility( VISIBLE );
//            fixedRectView.setBackgroundResource( R.drawable.camera_rect_success );
//        }else{
//            markImageView.setVisibility( INVISIBLE );
//            fixedRectView.setBackgroundResource( R.drawable.camera_rect_normal );
//        }
//    }

	//-------------------------------------------------------------------------
	private void initData(Context context, AttributeSet attrs){
        //
//        if( attrs != null ){
//            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FixedPiontView);
//            align_type = a.getInt(R.styleable.FixedPiontView_type, TYPE_LEFT);
//            a.recycle();
//        }
//
//        //
//        if( TYPE_LEFT == align_type ){
//            inflate( context, R.layout.view_camera_rectleft, this );
//        }else{
//            inflate( context, R.layout.view_camera_rectright, this );
//        }
//
//        markImageView = (RelativeLayout)findViewById( R.id.view_markimageview );
//        fixedRectView = findViewById( R.id.view_markrect );
	}

}

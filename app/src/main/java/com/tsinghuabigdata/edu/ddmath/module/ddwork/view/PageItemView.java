package com.tsinghuabigdata.edu.ddmath.module.ddwork.view;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;


public class PageItemView extends RelativeLayout {

    //
    public static final int ST_UNCAMERA = 0;       //未拍照
    public static final int ST_CAMERA   = 1;       //已拍照
    public static final int ST_COMMITING= 2;       //提交中
    public static final int ST_FAILER   = 3;       //提交失败
    public static final int ST_COMMITED = 4;       //已提交
    public static final int ST_UNCOMMIT = 5;       //未提交


    //对象
    private RelativeLayout mainLayout;      //选中的背景
    private TextView pagenumView;               //第几页
    private RelativeLayout pageStatusLayout;    //
    private TextView pagestatusView;            //状态
    //private View splitLineView;                 //分割线

	public PageItemView(Context context) {
		super(context);
		initData(context,null);
	}

	public PageItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initData(context, attrs);
	}

	public PageItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initData( context, attrs );
	}

    public void setData( int pagenum, int status, boolean selected, int uploadType ){
        //AppLog.d("dffdf pagenum = " + pagenum + ",, selected = " + selected );
        Resources res = getResources();
        mainLayout.setSelected( selected );
        pagenumView.setTextColor( selected? res.getColor(R.color.color_FFAE55) : res.getColor( R.color.color_48B8FF )  );
        //splitLineView.setVisibility( selected ? GONE : VISIBLE );

        String data = "第 "+pagenum+" 页";
        pagenumView.setText( data );

        pagestatusView.setCompoundDrawables( null, null, null, null);
        if( ST_UNCAMERA == status || ST_UNCOMMIT == status ){
            pagestatusView.setText( uploadType == AppConst.UPLOAD_TYPE_SCAN?"未扫描":"未拍照" );
            pagestatusView.setTextColor( res.getColor( R.color.color_948ECD) );
            pageStatusLayout.setBackground( res.getDrawable(R.drawable.bg_pagestatus_uncamera) );
        }else if( ST_CAMERA == status ){
            pagestatusView.setText( uploadType == AppConst.UPLOAD_TYPE_SCAN?"已扫描":"已拍照" );
            pagestatusView.setTextColor( res.getColor( R.color.color_FFB000) );
            pageStatusLayout.setBackground( res.getDrawable(R.drawable.bg_pagestatus_cameraed) );
        }else if( ST_COMMITED == status ){
            pagestatusView.setText( "已提交" );
            pagestatusView.setTextColor( res.getColor( R.color.color_57C724) );
            pageStatusLayout.setBackground( res.getDrawable(R.drawable.bg_pagestatus_commited) );

            Drawable drawable = res.getDrawable(R.drawable.ic_check_green);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            pagestatusView.setCompoundDrawablesRelative( null, null, drawable, null );
        }else if( ST_COMMITING == status ){
            pagestatusView.setText( "正在提交" );
            pagestatusView.setTextColor( res.getColor( R.color.color_59CDBE) );
            pageStatusLayout.setBackground( res.getDrawable(R.drawable.bg_pagestatus_commiting) );
        }else if( ST_FAILER == status ){
            pagestatusView.setText( "提交失败" );
            pagestatusView.setTextColor( res.getColor( R.color.color_FF7555) );
            pageStatusLayout.setBackground( res.getDrawable(R.drawable.bg_pagestatus_failure) );
        }

    }

	//-------------------------------------------------------------------------
	private void initData(Context context, AttributeSet attrs){
        //
        inflate( context, GlobalData.isPad()?R.layout.view_ddwork_page:R.layout.view_ddwork_page_phone, this );

        mainLayout = (RelativeLayout)findViewById( R.id.item_ddwork_selectlayout );
        pagenumView    = (TextView) findViewById( R.id.item_ddwork_pagenum );
        pageStatusLayout=(RelativeLayout)findViewById( R.id.item_ddwork_pagestatuslayout);
        pagestatusView = (TextView) findViewById( R.id.item_ddwork_pagestatus );
        //splitLineView  = findViewById( R.id.item_ddwork_splitline );
	}

}


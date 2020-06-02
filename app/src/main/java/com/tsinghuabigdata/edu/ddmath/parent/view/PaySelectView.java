package com.tsinghuabigdata.edu.ddmath.parent.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;

import java.util.ArrayList;

/**
 * 支付选择View
 */
public class PaySelectView extends RelativeLayout implements View.OnClickListener{

    private final static ArrayList<PaySelectView> mList = new ArrayList<>();

    private TextView payNameView;
    private ImageView selectView;

    private View.OnClickListener onClickListener;       //回调使用

    public PaySelectView(Context context) {
        super(context);
        init( null );
    }

    public PaySelectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init( attrs );
    }

    public PaySelectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @Override
    public void setOnClickListener( View.OnClickListener listener){
        onClickListener = listener;
    }
//    public void setPayName( String name, int resId ){
//        payNameView.setText( name );
//        Drawable drawable = getContext().getResources().getDrawable( resId );
//        payNameView.setCompoundDrawablesRelativeWithIntrinsicBounds( drawable, null,null,null);
//    }
    public void setCheck( boolean check ){
        selectView.setSelected( check );
    }

//    public boolean isCheck(){
//        return selectView.isSelected();
//    }
    public void setSelectEnable( boolean enable ){
        selectView.setEnabled( enable );
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mList.add( this );
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mList.remove( this );
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.layout_main:{
                //当前被选中，不处理
                if( selectView.isSelected() || !selectView.isEnabled() )
                    return;

                //没有被选中，则先初始化其他
                for( PaySelectView paySelectView : mList ){
                    paySelectView.setCheck( false );
                }
                selectView.setSelected( true );
                if( onClickListener!=null ) onClickListener.onClick(this);
                break;
            }
            default:
                break;
        }
    }

    //-----------------------------------------------------------------------------
    private void init(AttributeSet attrs) {

        String payName = "";
        Drawable leftDrawable = null;
        boolean selected = false;
        if( attrs != null ){
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.PaySelectView);
            payName = a.getString(R.styleable.PaySelectView_payName);
            leftDrawable = a.getDrawable(R.styleable.PaySelectView_leftDrawable);
            selected = a.getBoolean(R.styleable.PaySelectView_selected,false);
            a.recycle();
        }

        inflate( getContext(),R.layout.view_pay_select, this );

        RelativeLayout layout = (RelativeLayout)findViewById( R.id.layout_main );
        layout.setOnClickListener( this );
        payNameView = (TextView)findViewById( R.id.tv_pay_name );
        selectView  = (ImageView)findViewById( R.id.iv_pay_select );

        if(!TextUtils.isEmpty(payName)){
            payNameView.setText( payName );
        }
        if( leftDrawable!=null ){
            payNameView.setCompoundDrawablesRelativeWithIntrinsicBounds( leftDrawable, null,null,null);
        }
        selectView.setSelected( selected );
    }

}

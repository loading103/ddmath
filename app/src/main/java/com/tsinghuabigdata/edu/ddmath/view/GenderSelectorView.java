package com.tsinghuabigdata.edu.ddmath.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.WindowUtils;

public class GenderSelectorView extends LinearLayout implements View.OnClickListener {

    //
    private RelativeLayout llGirl;
    private RelativeLayout llBoy;
    private ImageView rbBoy;
    private ImageView rbGirl;
    private RadioButton cbLeftBox;
    private RadioButton cbRightBox;
    //private View           llGenderContainer;

    private OnClickListener leftClickListener;
    private OnClickListener rightClickListener;

    /**
     * 选中状态
     * 男 10    二进制表示
     * 女 01
     */
    private int selGender = BOY;
    //private int checkedGender = BOY;
    private int currentChecked = BOY;
    public static final int BOY = 2;
    public static final int GIRL = 1;

    //private View root;

    public GenderSelectorView(Context context) {
        super(context);
        initialize();
    }

    public GenderSelectorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public GenderSelectorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    public void setCheckBoxText( String left, String right ){
        rbBoy.setVisibility( GONE );
        rbGirl.setVisibility(GONE);
        cbLeftBox.setVisibility(VISIBLE);
        cbRightBox.setVisibility(VISIBLE);
        cbLeftBox.setText( left );
        cbRightBox.setText( right );
    }
    public void setOnClickListener( OnClickListener leftClickListener, OnClickListener rightClickListener){
        this.leftClickListener = leftClickListener;
        this.rightClickListener = rightClickListener;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_boy:
            case R.id.rb_left:
                currentChecked = BOY;
                toggleGender();
                dealOnClickLisetener(v);
                break;
            case R.id.ll_girl:
            case R.id.rb_right:
                currentChecked = GIRL;
                toggleGender();
                dealOnClickLisetener(v);
                break;
            default:
                break;
        }
    }

    public int getSelGender() {
        return selGender;
    }

    public void setSelGender(int selGender) {
        if (this.selGender != selGender) {
            currentChecked = selGender;
            toggleGender();
        }
    }

    public void setLayoutWeight( int w1, int w2 ){
        llBoy.setLayoutParams(new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT, w1));
        llGirl.setLayoutParams(new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT, w2));
    }

    /**
     * 初始化界面元素
     */
    private void initialize() {

        inflate(getContext(), GlobalData.isPad()?R.layout.layout_gender:R.layout.layout_gender_phone, this);

        View llGenderContainer = findViewById(R.id.ll_gender_container);
        llBoy = findViewById(R.id.ll_boy);
        llGirl =  findViewById(R.id.ll_girl);
        rbBoy =  findViewById(R.id.rb_boy);
        rbGirl =  findViewById(R.id.rb_girl);
        cbLeftBox = findViewById(R.id.rb_left);
        cbRightBox = findViewById(R.id.rb_right);

        LayoutParams params = (LayoutParams) llGenderContainer.getLayoutParams();
        if (GlobalData.isPad()) {
            params.height = WindowUtils.dpToPixels(getContext(), 60);
        } else {
            params.height = WindowUtils.dpToPixels(getContext(), 40);
        }
        llGenderContainer.setLayoutParams(params);

        rbBoy.setSelected(true);
        cbLeftBox.setChecked( true );
        cbLeftBox.setOnClickListener(this);
        cbRightBox.setOnClickListener(this);
        llBoy.setOnClickListener(this);
        llGirl.setOnClickListener(this);
    }

    private void dealOnClickLisetener(View v){
        if( selGender == BOY  ){
            if(leftClickListener!=null)leftClickListener.onClick(v);
        }else{
            if(rightClickListener!=null)rightClickListener.onClick(v);
        }
    }
    private void toggleGender() {
//        if (selGender == currentChecked) {
//            return;
//        }
        boolean selected = selGender == GIRL;
        rbBoy.setSelected(selected);
        rbGirl.setSelected(!selected);
        cbLeftBox.setChecked( selected );
        cbRightBox.setChecked( !selected );
        selGender = selected?BOY:GIRL;
    }

}
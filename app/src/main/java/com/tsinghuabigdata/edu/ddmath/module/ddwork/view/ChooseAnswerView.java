package com.tsinghuabigdata.edu.ddmath.module.ddwork.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

import java.util.ArrayList;

//选择题答案view
public class ChooseAnswerView extends LinearLayout implements CompoundButton.OnCheckedChangeListener{

    //对象
    private LinearLayout mainLayout;

    private ArrayList<RadioButton> mList = new ArrayList<>();

    public ChooseAnswerView(Context context) {
        super(context);
        initData(context);
    }

    public ChooseAnswerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData(context);
    }

    public ChooseAnswerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initData( context );
    }

    /**
     * 获得用户选择的答案
     * @return zfc
     */
    public String getChooseAnswer(){

        int index = -1;
        for( int i=0; i<mList.size(); i++ ){
            RadioButton btn = mList.get(i);
            if( btn.isChecked() ){
                index = i;
                break;
            }
        }
        return index<0?null: Character.valueOf( (char)('A'+index) ).toString();
    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

        if( !b ) return;

        switch ( compoundButton.getId() ){
            case R.id.dialog_revise_radiobtnA:{
                setCheckBtn( 0 );
                break;
            }
            case R.id.dialog_revise_radiobtnB:{
                setCheckBtn( 1 );
                break;
            }
            case R.id.dialog_revise_radiobtnC:{
                setCheckBtn( 2 );
                break;
            }
            case R.id.dialog_revise_radiobtnD:{
                setCheckBtn( 3 );
                break;
            }
            default:
                break;
        }
    }

    public View getMainLayout(){ return mainLayout; }

    //-------------------------------------------------------------------------
    private void initData( Context context ){

        inflate( context, GlobalData.isPad()?R.layout.view_ddwork_chooseanswer:R.layout.view_ddwork_chooseanswer_phone, this );

        mainLayout = (LinearLayout) findViewById( R.id.dialog_revise_layout );

        ChooseRadioButtonView btn = (ChooseRadioButtonView)findViewById( R.id.dialog_revise_radiobtnA );
        btn.setOnCheckedChangeListener( this );
        mList.add( btn );
        btn = (ChooseRadioButtonView)findViewById( R.id.dialog_revise_radiobtnB );
        btn.setOnCheckedChangeListener( this );
        mList.add( btn );
        btn = (ChooseRadioButtonView)findViewById( R.id.dialog_revise_radiobtnC );
        btn.setOnCheckedChangeListener( this );
        mList.add( btn );
        btn = (ChooseRadioButtonView)findViewById( R.id.dialog_revise_radiobtnD );
        btn.setOnCheckedChangeListener( this );
        mList.add( btn );
    }

    private void setCheckBtn( int index ){
        for( int i=0; i<mList.size(); i++ ){
            RadioButton btn = mList.get(i);
            btn.setChecked( i == index );
        }
    }
}

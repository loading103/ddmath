package com.tsinghuabigdata.edu.ddmath.module.exclusivepractice.view;


import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.KeyboardUtil;

/**
 * 搜索框
 */
public class SearchEditView extends LinearLayout{

    private EditText editText;
    //private ImageView imageView;
    private SearchListener mSearchListener;

    public SearchEditView(Context context) {
        super(context);
        init(context);
    }

    public SearchEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SearchEditView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init( context);
    }

    /**
     *
     */
    public EditText getEditText(){
        return editText;
    }
    public String getSearchKeyword(){
        String key = "";
        if( editText!=null ) key = editText.getText().toString().trim();
        return key;
    }

    public void setSearchListener(SearchListener listener){
        mSearchListener = listener;
    }

    public interface SearchListener{
        void search();
    }
    //-------------------------------------------------------------------------
    private void init(Context context){
        inflate( context, GlobalData.isPad()?R.layout.view_search_editview :R.layout.view_search_editview_phone, this );
        editText = (EditText) findViewById( R.id.edt_search );
        ImageView imageView= (ImageView)findViewById( R.id.iv_search );

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if( getContext() instanceof Activity){
                        KeyboardUtil.hidInput( (Activity) getContext() );
                    }
                    if(mSearchListener!=null)mSearchListener.search();
                }
                return false;
            }
        });
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSearchListener!=null)mSearchListener.search();
            }
        });
    }

}

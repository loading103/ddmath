package com.tsinghuabigdata.edu.ddmath.module.ddwork.view;


import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.QuestionInfo;
import com.tsinghuabigdata.edu.ddmath.module.xbook.XBookUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.view.MultiGridView;

import java.util.ArrayList;

import static com.tsinghuabigdata.edu.ddmath.R.id.view_text;


public class QuestionItemView extends LinearLayout {

    //对象
    private ItemTitleView titleView;
    private QuestionTextView questionTextView;
    private MultiGridView gridView;

    private ErrorReasonAdapter mAdapter;

    public QuestionItemView(Context context) {
        super(context);
        initData(context,null);
    }

    public QuestionItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData(context, attrs);
    }

    public QuestionItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initData( context, attrs );
    }

    public void setData( String title, String content, String graph, String latexgraph ){
        if( TextUtils.isEmpty(content) ){
            titleView.setVisibility( GONE );
            questionTextView.setVisibility( GONE );
            gridView.setVisibility(GONE);
            return;
        }
        titleView.setVisibility( VISIBLE );
        titleView.setTitle( title );

        questionTextView.setVisibility( VISIBLE );
        gridView.setVisibility(GONE);

        //模拟是题目
        QuestionInfo question = new QuestionInfo();
        question.setStem( content );
        question.setStemGraph( graph );
        question.setStemLatexGraph( latexgraph );
        questionTextView.setQuestionBook( question, false );
    }

    /**
     * 设置错误原因
     * @param title     标题
     * @param reasons   错误原因，多个原因用，分割
     */
    public void setReasonData(String title, String reasons ){

        titleView.setTitle( title );

        questionTextView.setVisibility( GONE );
        gridView.setVisibility(VISIBLE);

        if( TextUtils.isEmpty( reasons ) ){
            return;
        }
        String reason_array[] = reasons.split(",");
        ArrayList<String> list = new ArrayList<>();
        for( String reason : reason_array ){
            list.add(XBookUtils.getReasonName(reason));
        }

        mAdapter.clear();
        mAdapter.addAll( list );
        mAdapter.notifyDataSetChanged();
    }

    //-------------------------------------------------------------------------
    private void initData(Context context, AttributeSet attrs){

        inflate( context, GlobalData.isPad()?R.layout.view_ddwork_questionitem:R.layout.view_ddwork_questionitem_phone, this );

        //TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.QuestionItem);
        //int color = a.getInt(R.styleable.QuestionItem_blockColor, 0xFF66C1FF);
        //a.recycle();
        //View block = findViewById( R.id.view_block );
        //block.setBackgroundColor(color);
        titleView = (ItemTitleView) findViewById( R.id.view_title );
        questionTextView = (QuestionTextView)findViewById( view_text );
        gridView = (MultiGridView)findViewById( R.id.view_grid );

        mAdapter = new ErrorReasonAdapter( getContext() );
        gridView.setAdapter( mAdapter );
    }

    //-------------------------------------------------------------------------
    private class ErrorReasonAdapter extends ArrayAdapter<String> {

        ErrorReasonAdapter(Context context) {
            super( context, 0);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {

            if (convertView == null) {
                convertView = LayoutInflater.from( getContext() ).inflate(GlobalData.isPad()?R.layout.item_error_reason :R.layout.item_error_reason_phone, parent, false);
                convertView.setTag(new ViewHolder(convertView));
            }
            ViewHolder viewHolder = ((ViewHolder) convertView.getTag());
            viewHolder.bindView( getItem(position) );
            return convertView;
        }

        class ViewHolder{
            private TextView textView;

            private ViewHolder(View convertView) {
                textView     = (TextView)convertView.findViewById( view_text );
            }

            void bindView( String reason ){
                textView.setText( reason );
            }
        }
    }
}

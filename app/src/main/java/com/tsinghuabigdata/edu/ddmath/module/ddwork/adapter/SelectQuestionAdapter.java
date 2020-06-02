package com.tsinghuabigdata.edu.ddmath.module.ddwork.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.module.learnmaterial.bean.SelectQuestionBean;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.view.MultiGridView;


/**
 * 题目序号适配器（按類型）
 * 1，有题型时，按题型分类
 * 2，按章节功能
 */

public class SelectQuestionAdapter extends ArrayAdapter<SelectQuestionBean> {

    private int type;
    private boolean isExam;

    public SelectQuestionAdapter(Context context ) {
        super(context, 0);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from( getContext() ).inflate(GlobalData.isPad()?R.layout.item_dupload_selectquestion : R.layout.item_dupload_selectquestion_phone, parent, false);
            convertView.setTag(new ViewHolder(convertView));
        }
        ViewHolder viewHolder = ((ViewHolder) convertView.getTag());
        viewHolder.bindView( position );
        return convertView;
    }

    public void setType( int type){
        this.type = type;
    }

    public void setExam(boolean exam){
        isExam = exam;
    }
    //------------------------------------------------------------------------------------------
    class ViewHolder{

        private TextView typeView;
        private MultiGridView gridView;

        private QuestionIndexAdapter mAdapter;

        private ViewHolder(View convertView) {

            mAdapter = new QuestionIndexAdapter( getContext() );
            mAdapter.setType(type);

            typeView       = convertView.findViewById( R.id.item_lm_qtype );
            if( QuestionIndexAdapter.TYPE_ERRQUESTION == type ){
                typeView.getPaint().setFakeBoldText(true);
            }
            gridView       = convertView.findViewById( R.id.item_lm_gridview );
            gridView.setAdapter( mAdapter );
        }

        void bindView( int position ){

            SelectQuestionBean bean = getItem( position );
            if( bean == null ) return;

            if( !TextUtils.isEmpty(bean.getType()) ){
                typeView.setVisibility( View.VISIBLE );
                typeView.setText( bean.getType() );
            }else{
                typeView.setVisibility( View.GONE );
            }

            if( bean.getList()!=null ){
                //更新题目序号
                mAdapter.clear();
                mAdapter.addAll( bean.getList() );
                mAdapter.notifyDataSetChanged();
                mAdapter.setExam( isExam );
            }
        }

    }
}

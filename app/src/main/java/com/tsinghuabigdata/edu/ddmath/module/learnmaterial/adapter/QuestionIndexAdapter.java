package com.tsinghuabigdata.edu.ddmath.module.learnmaterial.adapter;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.LocalQuestionInfo;
import com.tsinghuabigdata.edu.ddmath.module.learnmaterial.LMPreviewActivity;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

/**
 * 题目序号适配器
 */

public class QuestionIndexAdapter extends ArrayAdapter<LocalQuestionInfo> {

    private Context mContext;
    private LMPreviewActivity mActivity;

    public QuestionIndexAdapter(Context context ) {
        super(context, 0);
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(GlobalData.isPad()?R.layout.item_lm_question : R.layout.item_lm_question_phone, parent, false);
            convertView.setTag(new ViewHolder(convertView));
        }
        ViewHolder viewHolder = ((ViewHolder) convertView.getTag());
        viewHolder.bindView( position );
        return convertView;
    }

    public void setRelateActivity( LMPreviewActivity activity){
        mActivity = activity;
    }
    //------------------------------------------------------------------------------------------
    class ViewHolder implements View.OnClickListener{

        //private LinearLayout mainLayout;
        private int position;

        private View mainView;
        private ImageView imageView;
        private TextView indexView;

        private ViewHolder(View convertView) {

            mainView = convertView.findViewById( R.id.item_lm_mainlayout );
            mainView.setOnClickListener( this );

            imageView       = (ImageView)convertView.findViewById( R.id.item_lm_bg );
            indexView       = (TextView)convertView.findViewById( R.id.item_lm_index );
        }

        @Override
        public void onClick(View v) {
            if( R.id.item_lm_mainlayout == v.getId() ){
                LocalQuestionInfo bean = getItem(position);
                if( bean == null ) return;

                bean.setSelectCache( !bean.isSelectCache() );

                //刷新列表
                notifyDataSetChanged();

                //刷新答题区域显示
                if( mActivity!=null ) mActivity.refreshSplitView( bean.getNumInPaper(), bean.isSelectCache() );

//                //判断是否选择了题目
//                int count = getCount();
//                boolean select = false;
//                for( int i=0; i<count; i++ ){
//                    bean = getItem(i);
//                    if( bean!=null) select = select || bean.isSelect();
//                }
//                if( !select ){
//                    ToastUtils.showToastCenter( mContext, "还没有选择题目哦～" );
//                }
            }
        }


        void bindView( int position ){
            this.position = position;

            LocalQuestionInfo bean = getItem(position);
            if( bean == null ) return;

            indexView.setText( bean.getNumInPaper() );
            imageView.setSelected( bean.isSelectCache() );
            if( bean.isSelectCache() ){
                indexView.setTextColor( Color.WHITE );
            }else{
                indexView.setTextColor( Color.BLACK );
            }
        }

    }
}

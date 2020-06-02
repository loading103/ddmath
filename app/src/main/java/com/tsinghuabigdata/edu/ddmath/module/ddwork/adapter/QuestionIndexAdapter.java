package com.tsinghuabigdata.edu.ddmath.module.ddwork.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.event.RefreshAllBtnEvent;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.DDUploadActivity;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.LocalQuestionInfo;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.SubQuestionInfo;
import com.tsinghuabigdata.edu.ddmath.util.DensityUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

import org.greenrobot.eventbus.EventBus;

/**
 * 题目序号适配器
 */

public class QuestionIndexAdapter extends ArrayAdapter<LocalQuestionInfo> {

    private final static int TYPE_QUESTION = 0;         //
    public final static int TYPE_ERRQUESTION = 1;      //拍照上传已批阅

    private Context mContext;
    private DDUploadActivity mActivity;
    private int type = TYPE_QUESTION;
    private boolean isExam = false;

    /*public*/ QuestionIndexAdapter(Context context ) {
        super(context, 0);
        mContext = context;
        if( mContext instanceof DDUploadActivity) mActivity = (DDUploadActivity)mContext;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(GlobalData.isPad()?R.layout.item_lm_question : R.layout.item_lm_question_phone, parent, false);
            convertView.setTag(new ViewHolder(convertView));
//        } else if (((ViewHolder)convertView.getTag()).needReInflate) {
//            convertView = LayoutInflater.from(mContext).inflate(GlobalData.isPad()?R.layout.item_lm_question : R.layout.item_lm_question_phone, parent, false );
//            convertView.setTag( new ViewHolder(convertView) );
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
    class ViewHolder implements View.OnClickListener{

//        boolean needReInflate;

        //private LinearLayout mainLayout;
        private int position;

        private View convertView;

        private View mainView;
        private ImageView imageView;
        private TextView indexView;

        private RelativeLayout scoreLayout;
        private View scorebgView;
        private TextView scoreTextView;

        private ViewHolder(View convertView) {
            this.convertView = convertView;
            convertView.setOnClickListener( this );

            mainView = convertView.findViewById( R.id.item_lm_mainlayout );
            imageView       = (ImageView)convertView.findViewById( R.id.item_lm_bg );
            indexView       = (TextView)convertView.findViewById( R.id.item_lm_index );

            scoreLayout    = (RelativeLayout)convertView.findViewById( R.id.layout_question_score );
            scorebgView     = convertView.findViewById( R.id.item_score_bg );
            scoreTextView  = (TextView)convertView.findViewById( R.id.item_tv_score );

            //设置大小
            if( TYPE_ERRQUESTION == type ){
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)imageView.getLayoutParams();
                int wh = DensityUtils.dp2px( getContext(), GlobalData.isPad()?75:50 );
                layoutParams.width = wh;
                layoutParams.height = wh;
                mainView.setLayoutParams( layoutParams );
            }
        }

        @Override
        public void onClick(View v) {
            //if( R.id.item_lm_mainlayout == v.getId() ){
                LocalQuestionInfo bean = getItem(position);
                if( bean == null ) return;

                if( type == TYPE_ERRQUESTION ){
                    if( bean.getSubQuestions()==null || bean.getSubQuestions().size()==0 ){
                        bean.setCorrectCache( !bean.isCorrectCache() );
                    }else{      //带子问
                        int index = getSubQuestionIndex(bean,position);
                        changeSubQuestionCorrectStatus( bean, index );

                        //根据子问，确定整个题目的对错状态
                        boolean correct = true;
                        for( SubQuestionInfo subQuestionInfo:bean.getSubQuestions() ){
                            if( subQuestionInfo.getChildren()==null || subQuestionInfo.getChildren().size()==0 ){
                                correct = correct && subQuestionInfo.isSubCorrectCache();
                            }else{
                                boolean subCorrect = true;
                                for( SubQuestionInfo subSubInfo : subQuestionInfo.getChildren() ){
                                    subCorrect = subCorrect && subSubInfo.isSubCorrectCache();
                                }
                                subQuestionInfo.setSubCorrectCache( subCorrect );
                                correct = correct && subCorrect;
                            }
                        }
                        bean.setCorrectCache( correct );
                    }
                    EventBus.getDefault().post( new RefreshAllBtnEvent() );
                    notifyDataSetChanged();
                }else{
                    //刷新答题区域显示  同时隐藏
                    if( mActivity!=null ) mActivity.refreshQuestionListView( bean.getQuestionId() );
                }
//            }
        }

        void bindView( int position ){
            this.position = position;

            LocalQuestionInfo bean = getItem(position);
            if( bean == null ) return;

            if( type == TYPE_ERRQUESTION ){
                convertView.setVisibility( bean.isSelectCache()?View.VISIBLE:View.GONE );
                //题目序号
                int index = getSubQuestionIndex(bean,position);
                showSubQuestionInfo( bean, index );

                //显示背景  或者 分数
                boolean correct = getSubQuestionCorrect(bean,index);     //包含子问的对错
                if( isExam ){
                    imageView.setImageResource( R.drawable.sel_errorquestion_item_score );
                    imageView.setSelected( correct );

                    scoreLayout.setVisibility( View.VISIBLE );
                    int score = getSubQuestionScore(bean,index);      //题目或者子问的分数
                    scoreTextView.setText( String.valueOf(score) );
                    scorebgView.setSelected( correct );
                }else{
                    imageView.setImageResource( R.drawable.sel_errorquestion_item );
                    imageView.setSelected( correct );
                    scoreLayout.setVisibility( View.GONE );
                }
            }else{
                convertView.setVisibility( bean.isSelect()?View.VISIBLE:View.GONE );
                //文本
                indexView.setText( bean.getNumInPaper() );
                imageView.setImageResource( bean.isCorrect()?R.drawable.selector_select_rightqustion:R.drawable.selector_select_wrongqustion );
            }
        }

        //处理带子问的题目序号
//        private String getNumInPaper( LocalQuestionInfo bean, int index ){
//            if( type == TYPE_QUESTION  || bean.getSubQuestions()==null || bean.getSubQuestions().size()==0 || index >= bean.getSubQuestions().size() )
//                return bean.getNumInPaper();
//            else{
//                return bean.getNumInPaper() + bean.getSubQuestions().get(index).getSubTitle();
//            }
//        }

        //查找到当前子问的序号
        private int getSubQuestionIndex(  LocalQuestionInfo bean, int position ){
            //查找到当前是第几个小问
            int start = position;
            while ( start>=0 ){
                if( start==0 ) break;
                if( getItem(start-1) != bean ) break;
                start--;
            }
            return position-start;
        }

        //查找到小问，改变批阅结果
        private void changeSubQuestionCorrectStatus(LocalQuestionInfo questionInfo, int index ){

            int current = 0;
            for( SubQuestionInfo subQuestionInfo:questionInfo.getSubQuestions() ){

                //先确定当前对象的位置
                if( subQuestionInfo.getChildren()==null || subQuestionInfo.getChildren().size()==0 ){   //没有下一级子问
                    if( current == index ){
                        subQuestionInfo.setSubCorrectCache( !subQuestionInfo.isSubCorrectCache() );
                        break;
                    }else{
                        current++;
                    }
                }else{ //没有下一级子问
                    if( current + subQuestionInfo.getChildren().size() > index ){
                        SubQuestionInfo subSubInfo = subQuestionInfo.getChildren().get(index-current);
                        subSubInfo.setSubCorrectCache( !subSubInfo.isSubCorrectCache() );
                        break;
                    }else{
                        current +=  subQuestionInfo.getChildren().size();
                    }
                }
            }
        }

        private void showSubQuestionInfo(LocalQuestionInfo questionInfo, int index ){

            if( questionInfo.getSubQuestions() == null || questionInfo.getSubQuestions().size()==0){        //没有小问
                indexView.setText( questionInfo.getNumInPaper() );
                imageView.setImageResource( questionInfo.isCorrectCache()?R.drawable.selector_select_rightqustion:R.drawable.selector_select_wrongqustion );
                return;
            }

            //带小问的题目处理
            int current = 0;
            for( SubQuestionInfo subQuestionInfo:questionInfo.getSubQuestions() ){

                //先确定当前对象的位置
                if( subQuestionInfo.getChildren()==null || subQuestionInfo.getChildren().size()==0 ){   //没有下一级子问
                    if( current == index ){
                        String text = questionInfo.getNumInPaper() + subQuestionInfo.getSubTitle();
                        indexView.setText( text );
                        imageView.setImageResource( subQuestionInfo.isSubCorrectCache()?R.drawable.selector_select_rightqustion:R.drawable.selector_select_wrongqustion );
                        break;
                    }else{
                        current++;
                    }
                }else{ //没有下一级子问
                    if( current + subQuestionInfo.getChildren().size() > index ){
                        SubQuestionInfo subSubInfo = subQuestionInfo.getChildren().get(index-current);
                        String text = questionInfo.getNumInPaper() + subQuestionInfo.getSubTitle();
                        if( subQuestionInfo.getChildren().size() > 1 ){
                            text += subSubInfo.getSubTitle();
                        }
                        indexView.setText( text );
                        imageView.setImageResource( subSubInfo.isSubCorrectCache()?R.drawable.selector_select_rightqustion:R.drawable.selector_select_wrongqustion );
                        break;
                    }else{
                        current +=  subQuestionInfo.getChildren().size();
                    }
                }
            }
        }

        private boolean getSubQuestionCorrect(LocalQuestionInfo questionInfo, int index ){

            if( questionInfo.getSubQuestions() == null || questionInfo.getSubQuestions().size()==0){        //没有小问
                return questionInfo.isCorrectCache();
            }

            //带小问的题目处理
            int current = 0;
            for( SubQuestionInfo subQuestionInfo:questionInfo.getSubQuestions() ){

                //先确定当前对象的位置
                if( subQuestionInfo.getChildren()==null || subQuestionInfo.getChildren().size()==0 ){   //没有下一级子问
                    if( current == index ){
                        return subQuestionInfo.isSubCorrectCache();
                    }else{
                        current++;
                    }
                }else{ //没有下一级子问
                    if( current + subQuestionInfo.getChildren().size() > index ){
                        SubQuestionInfo subSubInfo = subQuestionInfo.getChildren().get(index-current);
                        return subSubInfo.isSubCorrectCache();
                    }else{
                        current +=  subQuestionInfo.getChildren().size();
                    }
                }
            }
            return false;
        }

        private int getSubQuestionScore(LocalQuestionInfo questionInfo, int index ){

            if( questionInfo.getSubQuestions() == null || questionInfo.getSubQuestions().size()==0){        //没有小问
                return questionInfo.isCorrectCache()?(int)questionInfo.getQuestionScore():0;
            }

            //带小问的题目处理
            int current = 0;
            for( SubQuestionInfo subQuestionInfo:questionInfo.getSubQuestions() ){

                //先确定当前对象的位置
                if( subQuestionInfo.getChildren()==null || subQuestionInfo.getChildren().size()==0 ){   //没有下一级子问
                    if( current == index ){
                        return subQuestionInfo.isSubCorrectCache()?(int)subQuestionInfo.getQuestionScore():0;
                    }else{
                        current++;
                    }
                }else{ //没有下一级子问
                    if( current + subQuestionInfo.getChildren().size() > index ){
                        SubQuestionInfo subSubInfo = subQuestionInfo.getChildren().get(index-current);
                        return subSubInfo.isSubCorrectCache()?(int)subSubInfo.getQuestionScore():0;
                    }else{
                        current +=  subQuestionInfo.getChildren().size();
                    }
                }
            }
            return 0;
        }
    }
}

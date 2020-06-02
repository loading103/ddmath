package com.tsinghuabigdata.edu.ddmath.module.ddwork.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.DDWorkUtil;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.adapter.SelectQuestionAdapter;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.LocalQuestionInfo;
import com.tsinghuabigdata.edu.ddmath.module.learnmaterial.bean.SelectQuestionBean;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

import java.util.ArrayList;

/**
 * 错题选择
 */
public class SelectQuestionView extends LinearLayout implements CompoundButton.OnCheckedChangeListener {

    private FilterListener mFilterListener;

    //private CheckBox checkBox;
    //private ListView listView;

    private SelectQuestionAdapter mAdapter;

    ArrayList<LocalQuestionInfo> questionList;

    public SelectQuestionView(Context context) {
        super(context);
        init();
    }

    public SelectQuestionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SelectQuestionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    public SelectQuestionView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//        init();
//    }

    public void setFilterListener( FilterListener listener ){
        mFilterListener = listener;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if(  questionList == null )return;

        for( LocalQuestionInfo questionInfo : questionList ){
            //仅仅处理错题
            if( questionInfo.isCorrect() ){
                questionInfo.setSelect( !isChecked );
            }
        }
        //触发更新
        mAdapter.notifyDataSetChanged();

        if( mFilterListener!=null ){
            mFilterListener.filterChange();
        }
    }

    public void setData( ArrayList<LocalQuestionInfo> list ){

        questionList = list;

        //更新题目序号
        mAdapter.clear();
        mAdapter.addAll( getShowData(list) );
        mAdapter.notifyDataSetChanged();
    }

    //---------------------------------------------------------------------------
    private void init(){

        LayoutInflater.from( getContext() ).inflate(GlobalData.isPad()?R.layout.view_selectquestion_filtrate:R.layout.view_selectquestion_filtrate_phone, this);

        CheckBox checkBox = (CheckBox)findViewById( R.id.view_sq_showwrong );
        checkBox.setOnCheckedChangeListener( this );

        ListView listView = (ListView)findViewById( R.id.view_sq_gridview );
        mAdapter = new SelectQuestionAdapter( getContext() );
        listView.setAdapter( mAdapter );
    }

    private ArrayList<SelectQuestionBean> getShowData(  ArrayList<LocalQuestionInfo> list ){
        ArrayList<SelectQuestionBean> sqlist = new ArrayList<>();

        ArrayList<LocalQuestionInfo> qlist = new ArrayList<>();
        String type = null;
        for( LocalQuestionInfo questionInfo : list ){
            //兼容处理,先按题型处理
            String ty = questionInfo.getShowQuestionType();
            if( TextUtils.isEmpty(ty) && !TextUtils.isEmpty( questionInfo.getIndexQuestionType()) ){
                ty = questionInfo.getIndexQuestionType() + "、" + DDWorkUtil.getQuestionTypeName(questionInfo.getQuestionType());
            }
            //章节处理
            if(TextUtils.isEmpty( ty )) ty = questionInfo.getShowChapterName();
            if( TextUtils.isEmpty( type ) ){
                type = ty;
                qlist.add( questionInfo );
            }else{
                if( type.equals( ty ) ){        //相等，还是同一类
                    qlist.add( questionInfo );
                }
                //不等，新的一类了,
                else{
                    //增加一类
                    SelectQuestionBean bean = new SelectQuestionBean();
                    bean.setType( type );
                    bean.setList( qlist );
                    sqlist.add( bean );

                    //下一个
                    qlist = new ArrayList<>();
                    qlist.add( questionInfo );
                    type = ty;
                }
            }
        }
        //最后的数据一类
        SelectQuestionBean bean = new SelectQuestionBean();
        bean.setType( type );
        bean.setList( qlist );
        sqlist.add( bean );

        return sqlist;
    }

    //--------------------------------------------------------------------------------------------------
    public interface FilterListener{
        void filterChange();
    }

}
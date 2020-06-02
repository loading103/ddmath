package com.tsinghuabigdata.edu.ddmath.module.ddwork.view;

import android.content.Context;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.event.RefreshAllBtnEvent;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.DDWorkUtil;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.adapter.QuestionIndexAdapter;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.adapter.SelectQuestionAdapter;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.LocalQuestionInfo;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.SubQuestionInfo;
import com.tsinghuabigdata.edu.ddmath.module.learnmaterial.bean.SelectQuestionBean;
import com.tsinghuabigdata.edu.ddmath.module.product.ProductHelpActivity;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.LogUtils;
import com.tsinghuabigdata.engine.util.TFProvider;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 错题选择,共批阅结果上传使用
 */
public class SelectErrorQuestionView extends LinearLayout implements View.OnClickListener{

    private Button enterBtn;
    private SelectListener mSelectListener;

    private SelectQuestionAdapter mAdapter;
    private boolean singleUse = false;      //单独使用，不是拍照流程里面
    //private boolean isExam = false;         //是否考试，有分数
    private boolean landScreen = true;

    ArrayList<LocalQuestionInfo> questionList = new ArrayList<>();

    public SelectErrorQuestionView(Context context) {
        super(context);
        init();
    }

    public SelectErrorQuestionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SelectErrorQuestionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setLandScreen(boolean landScreen) {
        this.landScreen = landScreen;
    }

    public void setSelectListener(SelectListener listener ){
        mSelectListener = listener;
    }
    public void setSingleUse(){
        singleUse = true;
    }
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }

    public void setData(ArrayList<LocalQuestionInfo> list, List<TFProvider.Recognition> recognitionList, boolean isExam){

        initCacheData( list );
        questionList.clear();
        //处理子问结果
        for( LocalQuestionInfo questionInfo:list ){
            if( !questionInfo.isSelectCache() )
                continue;
            if( questionInfo.getSubQuestions() == null || questionInfo.getSubQuestions().size() == 0 ){
                questionList.add( questionInfo );
            }else{
                for(SubQuestionInfo subQuestionInfo:questionInfo.getSubQuestions()){
                    if( subQuestionInfo.getChildren() == null || subQuestionInfo.getChildren().size() == 0  ){
                        questionList.add( questionInfo );
                    }else{
                        for( SubQuestionInfo subInfo:subQuestionInfo.getChildren() ){
                            AppLog.d("subInfo = " + subInfo );
                            questionList.add( questionInfo );
                        }
                    }
                }
            }
        }

        //处理自动识别的勾叉信息到对应的题目
        if( recognitionList != null && AccountUtils.isEnableAutoRecGc() ){
            for( LocalQuestionInfo questionInfo:list ){
                parseMarked2QuestionInfo( questionInfo, recognitionList );
            }
        }

        //更新题目序号
        mAdapter.clear();
        mAdapter.addAll( getShowData(questionList) );
        mAdapter.notifyDataSetChanged();
        mAdapter.setExam( isExam );
        updateAllBtnEnable();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return true;//super.onInterceptTouchEvent(ev);
    }

    @Override
    public void onClick(View v) {
        if( R.id.iv_cancel == v.getId() ){
            setVisibility(GONE);
        }else if( R.id.iv_help == v.getId() ){
            //引导图片
            ProductHelpActivity.startProductHelpActivityAsset( getContext(), "题目小问的举例说明", "img_example.png", singleUse||landScreen);
        }else if( R.id.btn_enter == v.getId() ){
            setVisibility(GONE);
            if(mSelectListener!=null)mSelectListener.onSelectEnter();
            useCacheData( questionList );
        }else if( R.id.btn_all == v.getId() ){
            for(LocalQuestionInfo questionInfo:questionList ){
                questionInfo.setCorrectCache(true);
                if( questionInfo.getSubQuestions()!=null ){
                    for( SubQuestionInfo subQuestionInfo:questionInfo.getSubQuestions() ){
                        subQuestionInfo.setSubCorrectCache( true );
                        if( subQuestionInfo.getChildren()!=null ){
                            for( SubQuestionInfo subSubInfo:subQuestionInfo.getChildren()){
                                subSubInfo.setSubCorrectCache(true);
                            }
                        }
                    }
                }
            }
            setVisibility(GONE);
            if(mSelectListener!=null)mSelectListener.onSelectEnter();
            useCacheData( questionList );
        }
    }

    @Subscribe
    public void receive(RefreshAllBtnEvent event) {
        LogUtils.i("event RefreshAllBtnEvent " + event );
        updateAllBtnEnable();
    }
    //---------------------------------------------------------------------------
    private void init(){

        LayoutInflater.from( getContext() ).inflate(GlobalData.isPad()?R.layout.view_select_errorquestion :R.layout.view_select_errorquestion_phone, this);

        //RelativeLayout mainLayout = (RelativeLayout)findViewById(R.id.layout_main);
        //mainLayout.setOnClickListener(this);

        ImageView imageView = findViewById( R.id.iv_cancel );
        imageView.setOnClickListener( this );
        imageView = findViewById( R.id.iv_help );
        imageView.setOnClickListener( this );

        enterBtn = findViewById( R.id.btn_enter );
        enterBtn.setOnClickListener( this );
        Button allBtn = findViewById( R.id.btn_all );
        allBtn.setOnClickListener( this );

        ListView listView = findViewById( R.id.lv_listview );
        mAdapter = new SelectQuestionAdapter( getContext() );
        mAdapter.setType(QuestionIndexAdapter.TYPE_ERRQUESTION);
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
            if(TextUtils.isEmpty( ty )){
                ty = questionInfo.getChapterName();
                if( !TextUtils.isEmpty(ty) && !TextUtils.isEmpty(questionInfo.getSectionName()) ){
                    ty += "  "+questionInfo.getSectionName();
                }
            }
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

    //btn状态，全对，不能点击
    private void updateAllBtnEnable(){
        boolean enable = true;
        for(LocalQuestionInfo questionInfo:questionList ){
            if( questionInfo.getSubQuestions()!=null && questionInfo.getSubQuestions().size()>0 ){
                for( SubQuestionInfo subQuestionInfo:questionInfo.getSubQuestions() ){
                    if( subQuestionInfo.getChildren()!=null && subQuestionInfo.getChildren().size()>0 ){
                        for( SubQuestionInfo subSubInfo:subQuestionInfo.getChildren()){
                            enable = enable && subSubInfo.isSubCorrectCache();
                        }
                    }else{
                        enable = enable && subQuestionInfo.isSubCorrectCache();
                    }
                }
            }else{
                enable = enable && questionInfo.isCorrectCache();
            }
        }
        enterBtn.setEnabled( !enable );
    }

    //按题目范围来分析勾叉对应的题目
    private void parseMarked2QuestionInfo( LocalQuestionInfo questionInfo, List<TFProvider.Recognition> recList ){

        //先获得属于本题的勾叉信息

        //支持多个答题框
        ArrayList<RectF> rectList = questionInfo.getAnswerAreaTmpList();
        if( rectList==null || rectList.size()==0 ) return;

        //适当扩展 0.01
        float OFF_SET = 0.00f;
        List<TFProvider.Recognition> tmpList = new ArrayList<>();           //属于此题的勾叉信息列表

        for( RectF qrect : rectList ){
            qrect.left = qrect.left - OFF_SET;
            qrect.right= qrect.right + OFF_SET;
            qrect.top  = qrect.top - OFF_SET;
            qrect.bottom=qrect.bottom + OFF_SET;

            for( TFProvider.Recognition recognition : recList ){
                if( "right".equals( recognition.getTitle() ) ){ //勾勾（ 对的时候 ）以 X轴中心，y轴3/4位置为依据点
                    float px = recognition.getLocation().centerX();
                    float py = recognition.getLocation().top + recognition.getLocation().height()*3/4;
                    if( qrect.contains( px, py ) ){
                        tmpList.add( recognition );
                    }
                }else{      //叉叉 以中心点作为判断的依据
                    if( qrect.contains( recognition.getLocation().centerX(), recognition.getLocation().centerY() ) ){
                        tmpList.add( recognition );
                    }
                }
            }
        }

        //下面进行勾叉对应题目处理
        int childCount = getChildCount( questionInfo );
        boolean allRight = isAllRightMark( tmpList );

        //得到框对应n个小问，以及m个对错位置:
        //m==0，认为对
        //m>=1，且全对，认为全对
        //m>=1，且有错。n==1，认为错
        //n==m，按照框中心点从左到右、从上到下一一匹配。
        //n!=m，认为全对

        //本题没有识别出勾叉, 全对
        if( tmpList.size() == 0 || allRight ){
            //全对
            setQuestionCorrectStatus( questionInfo, true );
        } else if( childCount == 1  ){  //只有一个题目，有错误标记
            //全错
            setQuestionCorrectStatus( questionInfo, false );
        } else if( childCount!=tmpList.size() ){
            //全对
            setQuestionCorrectStatus( questionInfo, true );
        }else{
            //多问，多标记，先对标记进行排序，然后按顺序对应
            // 排序，按中心点比较，从左到右，从上到下的原则
            Collections.sort(tmpList, new Comparator<TFProvider.Recognition>() {
                        @Override
                        public int compare(TFProvider.Recognition o1, TFProvider.Recognition o2) {
                            float OFF_SET = 0.03f;
                            float dx = o1.getLocation().centerX() - o2.getLocation().centerX();
                            float dy = o1.getLocation().centerY() - o2.getLocation().centerY();

                            int result;
                            if( Math.abs(dx) < OFF_SET && Math.abs(dy) < OFF_SET ){
                                //非常近
                                result = dy<0f ? -1:1;
                            }else if(Math.abs(dx) < OFF_SET){
                                //x位置 一样  判断 dy值得正负，<0, o2在前面，> 0, o1在前面
                                result = dy < 0f ? -1:1;
                            }else if(Math.abs(dy) < OFF_SET){
                                //y位置 一样  判断 dx值得正负，<0, o2在前面，> 0, o1在前面
                                result = dx < 0f ? -1:1;
                            }else {
                                //两者 x,y 都不一样
                                result = dy<0f ? -1:1;
                            }
                            return result;
                        }
                    }
            );
            setQuestionCorrectStatus( questionInfo, tmpList );
        }
    }

    //获得题目小问个数,没有小问 ，返回1
    private int getChildCount( LocalQuestionInfo questionInfo){
        int count = 0;
        if( questionInfo.getSubQuestions()==null || questionInfo.getSubQuestions().size() == 0 ){
            count = 1;
        }else{
            ArrayList<SubQuestionInfo> childList = questionInfo.getSubQuestions();
            for( SubQuestionInfo subQuestionInfo : childList ){
                if( subQuestionInfo.getChildren()==null || subQuestionInfo.getChildren().size() == 0 ){
                    count++;
                }else{
                    count += subQuestionInfo.getChildren().size();
                }
            }
        }
        return count;
    }

    private boolean isAllRightMark(List<TFProvider.Recognition> list){
        for(TFProvider.Recognition recognition:list){
            if( !recognition.getTitle().equals("right") )
                return false;
        }
        return true;
    }

    private void setQuestionCorrectStatus(  LocalQuestionInfo questionInfo, boolean correct ){
        questionInfo.setCorrectCache( correct );
        if( questionInfo.getSubQuestions()!=null && questionInfo.getSubQuestions().size() > 0 ){
            ArrayList<SubQuestionInfo> childList = questionInfo.getSubQuestions();
            for( SubQuestionInfo subQuestionInfo : childList ){
                subQuestionInfo.setSubCorrectCache( correct );
                if( subQuestionInfo.getChildren()!=null && subQuestionInfo.getChildren().size() > 0 ){
                    for( SubQuestionInfo subSubInfo : subQuestionInfo.getChildren() ){
                        subSubInfo.setSubCorrectCache( correct );
                    }
                }
            }
        }
    }

    private void setQuestionCorrectStatus(  LocalQuestionInfo questionInfo, List<TFProvider.Recognition> list ){

        int index = 0;
        boolean correct = list.get(index).getTitle().equals("right");     //整个题目
        if( questionInfo.getSubQuestions()!=null && questionInfo.getSubQuestions().size() > 0 ){
            ArrayList<SubQuestionInfo> childList = questionInfo.getSubQuestions();
            for( SubQuestionInfo subQuestionInfo : childList ){
                boolean childCorrect = true;    //一级小问
                if( subQuestionInfo.getChildren()!=null && subQuestionInfo.getChildren().size() > 0 ){
                    for( SubQuestionInfo subSubInfo : subQuestionInfo.getChildren() ){
                        boolean subSubCorrect = list.get(index).getTitle().equals("right");
                        subSubInfo.setSubCorrectCache( subSubCorrect );
                        childCorrect = childCorrect && subSubCorrect;
                        index++;
                    }
                    correct = correct && childCorrect;
                }else{
                    childCorrect = list.get(index).getTitle().equals("right");
                    correct = correct && childCorrect;
                    index++;
                }
                subQuestionInfo.setSubCorrectCache( childCorrect );
            }
        }

        questionInfo.setCorrectCache( correct );
    }

    private void initCacheData( ArrayList<LocalQuestionInfo> list){
        if( singleUse && list != null){
            for( LocalQuestionInfo questionInfo : list ){
                questionInfo.initCacheData();
            }
        }
    }
    private void useCacheData( ArrayList<LocalQuestionInfo> list){
        if( singleUse && list != null){
            for( LocalQuestionInfo questionInfo : list ){
                questionInfo.useCacheData();
            }
        }
    }
    //--------------------------------------------------------------------------------------------------
    public interface SelectListener{
        void onSelectEnter();
    }

}
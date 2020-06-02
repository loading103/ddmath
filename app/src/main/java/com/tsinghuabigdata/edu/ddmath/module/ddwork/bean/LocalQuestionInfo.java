package com.tsinghuabigdata.edu.ddmath.module.ddwork.bean;

import android.graphics.RectF;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.NumberUtil;
import com.tsinghuabigdata.edu.ddmath.module.learnmaterial.bean.AnswerAreaBean;
import com.tsinghuabigdata.edu.ddmath.module.learnmaterial.bean.AreaBean;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 问题本地图片信息
 */
public class LocalQuestionInfo extends QuestionInfo implements Serializable {

    private static final long serialVersionUID = 8086359967898611335L;

    //答案图片
    private String localpath;       //图片保存在本地的路径

    //订正图片
    //private String reviseLocalpath;

    //答案区域
    private QuestionRect questionRect;

    //批改区域
//    private transient ArrayList<ImageCorrectBean> correctRects;

    //显示页
    private transient String pageInfo;
    private transient int pageNum;

    //显示题型
    private transient String showQuestionType;
    //显示题型
    private transient String indexQuestionType;

    //周练分数，和 颜色
    private transient String keywordString;
    private transient int keywordColor;

    //原版教辅上传使用
    private int index;
    private transient boolean select;
    private ArrayList<AnswerAreaBean> answerAreaList;
    private ArrayList<AnswerAreaBean> figureAreaList;
    private ArrayList<String> localpathList;

    private transient ArrayList<RectF> answerAreaTmpList;       //临时答案区域，自动识别勾叉使用

    private transient String showChapterName;           //章节名称

    private transient boolean onlyErrQuestion;      //仅仅拍照上传错题

    //拍照时否移动过切题框
    private boolean moveFlag = false;

    //缓存
    private boolean moveFlagCache = false;
    private boolean correctCache = false;
    private transient boolean selectCache;

    public void init(){
        localpath = "";
        answerAreaList = null;
        figureAreaList  = null;
        localpathList = null;
        moveFlag = false;
    }

    //for 上传使用
    public JSONObject getJson(){
        JSONObject json = new JSONObject();
        try {
            json.put( "questionId", questionId );
            json.put( "moveFlag", moveFlag );
            if( onlyErrQuestion ){
                json.put( "correct",  correct );            //拍照错题使用，
                json.put( "studentScore", calStudentScore() );
                if( subQuestions!=null && subQuestions.size()>0 ){
                    JSONArray jsonArray = new JSONArray();
                    for( SubQuestionInfo subQuestionInfo:subQuestions ){
                        JSONObject subjion = subQuestionInfo.getJson();
                        jsonArray.put( subjion );
                    }
                    json.put( "subQuestionVos",  jsonArray );
                }
            }

            getQuestionRect();      //保证 questionRect 对象存在
            if( questionRect != null ){
                json.put( "x", Float.valueOf( NumberUtil.double2floatFormat( questionRect.getX(), 4 )) );
                json.put( "y", Float.valueOf( NumberUtil.double2floatFormat( questionRect.getY(), 4 )) );
                json.put( "width", Float.valueOf( NumberUtil.double2floatFormat( questionRect.getWidth(), 4 ) ) );
                json.put( "height", Float.valueOf( NumberUtil.double2floatFormat( questionRect.getHeight(), 4 ) ) );

                QuestionRect figureRect = questionRect.getFigure();
                if( figureRect!=null ){

                    //检验 figure 的值，处理成合法
                    if( figureRect.getY() < 0 ) figureRect.setY( 0 );
                    if( figureRect.getY()+figureRect.getHeight() > 1 ) figureRect.setHeight( 1-figureRect.getY() );

                    json.put( "figure", figureRect.getJsonObject() );
                }
            }
        }catch (Exception e){
            AppLog.i("",e);
        }
        return json;
    }

    //原版教辅 for 上传使用
    /*public */ JSONObject getLmJson(){
        JSONObject json = new JSONObject();

        try{
            json.put( "questionId", questionId );
            if( onlyErrQuestion ){
                json.put( "correct",  correct );            //拍照错题使用，
                json.put( "studentScore", calStudentScore() );
                if( subQuestions!=null && subQuestions.size()>0 ){
                    JSONArray jsonArray = new JSONArray();
                    for( SubQuestionInfo subQuestionInfo:subQuestions ){
                        JSONObject subjion = subQuestionInfo.getJson();
                        jsonArray.put( subjion );
                    }
                    json.put( "subQuestionVos",  jsonArray );
                }
            }

            //答题区域
            if( answerAreaList!=null ){
                JSONArray jsonArray = new JSONArray();
                for( AnswerAreaBean bean : answerAreaList ){
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put( "serialNum", bean.getSerialNum() );
                    jsonObject.put( "moveFlag", bean.isMoveFlag() );
                    AreaBean manualArea = bean.getManualArea();
                    if( manualArea!=null ){
                        covertAreaBean2Json( manualArea, jsonObject );
                    }
                    jsonArray.put( jsonObject );
                }
                json.put("answerAreas", jsonArray );
            }

            //辅助图区域
            if( figureAreaList!=null ){
                JSONArray jsonArray = new JSONArray();
                for( AnswerAreaBean bean : figureAreaList ){
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put( "serialNum", bean.getSerialNum() );
                    AreaBean manualArea = bean.getManualArea();

                    if( manualArea!=null ){
                        covertAreaBean2Json( manualArea, jsonObject );
                    }
                    jsonArray.put( jsonObject );
                }
                json.put("figureAreas", jsonArray );
            }
        }catch (Exception e){
            AppLog.d("",e);
        }

        return json;
    }

    private void covertAreaBean2Json( AreaBean manualArea, JSONObject jsonObject ){
        try {
            float x = manualArea.getX();
            if( x < 0 ) x = 0;
            jsonObject.put( "x", Float.valueOf( NumberUtil.double2floatFormat( x, 4 )) );
            float y = manualArea.getY();
            if( y < 0 ) y = 0;
            jsonObject.put( "y", Float.valueOf( NumberUtil.double2floatFormat( y, 4 )) );

            float width = manualArea.getWidth();
            if( width <= 0 ){
                width = 0.05f;
                if( manualArea.getX()+width>1.0f ){
                    width = 1.0f - manualArea.getX();
                }
            }
            jsonObject.put( "width", Float.valueOf( NumberUtil.double2floatFormat( width, 4 )) );
            float height = manualArea.getHeight();
            if( height <= 0 ){
                height = 0.05f;
                if( manualArea.getY()+height>1.0f ){
                    height = 1.0f - manualArea.getY();
                }
            }
            jsonObject.put( "height", Float.valueOf( NumberUtil.double2floatFormat( height, 4 )) );
        }catch (Exception e){
            AppLog.d("",e);
        }
    }

    //原版教辅 题目信息 for 上传使用
    /*public */ JSONObject getLmQuestionJson(){
        JSONObject json = new JSONObject();

        try{
            json.put( "questionId", questionId );
            json.put( "indexInPaper", getIndexInPaper() );
            json.put( "numInPaper", getNumInPaper() );
            json.put( "bookId", getBookId() );
            json.put( "sectionId", getSectionId() );
            json.put( "sectionIndex", getSectionIndex() );
            json.put( "questionType", getQuestionType() );
        }catch (Exception e){
            AppLog.d("",e);
        }

        return json;
    }
    //for 本地保存使用
    public JSONObject getJsonObject(){
        JSONObject json = new JSONObject();
        try {
            json.put( "questionId", questionId );
            json.put( "localpath", localpath );
            json.put( "correct", correct );
            json.put( "moveFlag", moveFlag );
            if( getQuestionRect()!= null )
                json.put( "questionRect", getQuestionRect().getJsonObject() );

            //老师布置 原版教辅
            if( answerAreaList != null ){
                JSONArray jsonArray = new JSONArray();
                for( AnswerAreaBean areaBean : answerAreaList ){
                    jsonArray.put( areaBean.getJsonObject() );
                }
                json.put( "answerAreaList", jsonArray );
            }
            if( figureAreaList != null ){
                JSONArray jsonArray = new JSONArray();
                for( AnswerAreaBean areaBean : figureAreaList ){
                    jsonArray.put( areaBean.getJsonObject() );
                }
                json.put( "figureAreaList", jsonArray );
            }
            if( localpathList != null ){
                JSONArray jsonArray = new JSONArray();
                for( String path : localpathList ){
                    jsonArray.put( path );
                }
                json.put( "localpathList", jsonArray );
            }
            if( subQuestions != null ){
                JSONArray jsonArray = new JSONArray();
                for( SubQuestionInfo subQuestionInfo: subQuestions ){
                    jsonArray.put( subQuestionInfo.getJsonObject() );
                }
                json.put( "subQuestions", jsonArray );
            }

        }catch (Exception e){
            AppLog.i("",e);
        }
        return json;
    }

    //定制教辅使用
    public void setMoveFlagCache(){
        moveFlagCache = true;
    }
    //原版教辅使用
    public void setMoveFlagCache(int serialNum) {
        if( answerAreaList!=null ){
            for( AnswerAreaBean bean : answerAreaList ){
                if( bean.getSerialNum() == serialNum ){
                    bean.setMoveFlagCache();
                }
            }
        }
    }
    public void setCorrectCache( boolean correct ){
        correctCache = correct;
    }
    public boolean isCorrectCache(){ return correctCache; }

    public void initCacheData(){
        //moveFlagCache = moveFlag;     //使用默认值
        correctCache = correct;
        selectCache  = select;

        //原版教辅 题目移动标志
        if( answerAreaList!=null ){
            for( AnswerAreaBean bean : answerAreaList ){
                bean.initCacheData();
            }
        }

        //子问对错
        if( subQuestions != null){
            for( SubQuestionInfo subQuestionInfo:subQuestions ){
                subQuestionInfo.initCacheData();
            }
        }
    }
    /*public*/ void setDeflautCorrectCache(){
        correctCache = true;

        //子问对错
        if( subQuestions != null){
            for( SubQuestionInfo subQuestionInfo:subQuestions ){
                subQuestionInfo.setDeflautCorrectCache();
            }
        }
    }

    public void useCacheData(){
        moveFlag = moveFlagCache;
        correct = correctCache;
        select  = selectCache;

        //原版教辅 题目移动标志
        if( answerAreaList!=null ){
            for( AnswerAreaBean bean : answerAreaList ){
                bean.useCacheData();
            }
        }

        //子问对错
        if( subQuestions != null){
            for( SubQuestionInfo subQuestionInfo:subQuestions ){
                subQuestionInfo.useCacheData();
            }
        }
    }

    //-------------------------------------------------------------------

    public String getLocalpath() {
        return localpath;
    }

    public void setLocalpath(String localpath) {
        this.localpath = localpath;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public QuestionRect getQuestionRect() {
        if( questionRect == null ){

            ArrayList<QuestionRect> list = new Gson().fromJson(getQuestionArea(), new TypeToken<ArrayList<QuestionRect>>() {
            }.getType());
            if( list!=null && list.size() > 0 ) questionRect = list.get(0);           //默认获取第一个
        }
        return questionRect;
    }

    //得到题目框区域，画图区域
//    public QuestionRect getQuestionManualRect(){
//        if( !TextUtils.isEmpty(questionArea) && !questionArea.contains("manualArea") ){
//            return getQuestionRect();
//        }else if( !TextUtils.isEmpty(getAreaInfo()) ){
//            AreaInfoBean areaInfo = new Gson().fromJson( getAreaInfo(), new TypeToken<AreaInfoBean>() {
//            }.getType());
//
//            if( areaInfo!=null && areaInfo.getAnswerAreas()!=null && areaInfo.getAnswerAreas().size()>0 ){
//                AnswerAreaBean answerAreaBean = areaInfo.getAnswerAreas().get(0);
//                AreaBean areaBean = answerAreaBean.getManualArea();
//                if( areaBean!=null ){
//                    QuestionRect questionRect = new QuestionRect();
//                    questionRect.setX( areaBean.getX() );
//                    questionRect.setY( areaBean.getY() );
//                    questionRect.setWidth( areaBean.getWidth() );
//                    questionRect.setHeight( areaBean.getHeight() );
//                    return questionRect;
//                }
//            }
//
//        }
//
//        return null;
//    }

//    public void setQuestionRect(QuestionRect questionRect) {
//        this.questionRect = questionRect;
//    }
//
//    public ArrayList<ImageCorrectBean> getCorrectRects() {
//        if( correctRects == null ){
//            correctRects = new Gson().fromJson( getAnswerArea(), new TypeToken<ArrayList<QuestionRect>>() {
//            }.getType());
//        }
//        return correctRects;
//    }

//    public void setCorrectRects(ArrayList<ImageCorrectBean> correctRects) {
//        this.correctRects = correctRects;
//    }

    public String getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(String pageInfo) {
        this.pageInfo = pageInfo;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public String getShowQuestionType() {
        return showQuestionType;
    }

    public void setShowQuestionType(String questionType) {
        this.showQuestionType = questionType;
    }

    public String getIndexQuestionType() {
        return indexQuestionType;
    }

    public void setIndexQuestionType(String indexQuestionType) {
        this.indexQuestionType = indexQuestionType;
    }

    public String getKeywordString() {
        return keywordString;
    }

    public void setKeywordString(String keywordString) {
        this.keywordString = keywordString;
    }

    public int getKeywordColor() {
        return keywordColor;
    }

    public void setKeywordColor(int keywordColor) {
        this.keywordColor = keywordColor;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public boolean isSelectCache() {
        return selectCache;
    }

    public void setSelectCache(boolean select) {
        this.selectCache = select;
    }

    public ArrayList<AnswerAreaBean> getAnswerAreaList() {
        return answerAreaList;
    }

    public void setAnswerAreaList(ArrayList<AnswerAreaBean> aswerAreaList) {
        this.answerAreaList = aswerAreaList;
    }

    public ArrayList<AnswerAreaBean> getFigureAreaList() {
        return figureAreaList;
    }

    public void setFigureAreaList(ArrayList<AnswerAreaBean> figureAreaList) {
        this.figureAreaList = figureAreaList;
    }

    public ArrayList<String> getLocalpathList() {
        return localpathList;
    }

    public void setLocalpathList(ArrayList<String> localpathList) {
        this.localpathList = localpathList;
    }

    public String getShowChapterName() {
        return showChapterName;
    }

    public void setShowChapterName(String chapterName) {
        this.showChapterName = chapterName;
    }

//    public boolean isOnlyErrQuestion() {
//        return onlyErrQuestion;
//    }

    public void setOnlyErrQuestion(boolean onlyErrQuestion) {
        this.onlyErrQuestion = onlyErrQuestion;
    }

//    public boolean isMoveFlag() {
//        return moveFlag;
//    }


    public float calStudentScore(){

        //没有小问
        if( getSubQuestions() == null || getSubQuestions().size()==0){
            return isCorrect()?getQuestionScore():0;    //要么全分，要么零分
        }

        //带小问的题目处理
        float score = 0;
        for( SubQuestionInfo subQuestionInfo : getSubQuestions() ){
            score += subQuestionInfo.calStudentScore();
        }
        return score;
    }

    public ArrayList<RectF> getAnswerAreaTmpList() {
        return answerAreaTmpList;
    }

    public void setAnswerAreaTmpList(ArrayList<RectF> answerAreaTmpList) {
        this.answerAreaTmpList = answerAreaTmpList;
    }

//    public boolean calStudentCorrect(){
//
//        //没有小问
//        if( getSubQuestions() == null || getSubQuestions().size()==0){
//            return correct;    //
//        }
//
//        //带小问的题目处理
//        boolean tmp_correct = true;
//        for( SubQuestionInfo subQuestionInfo : getSubQuestions() ){
//            tmp_correct = tmp_correct && subQuestionInfo.calStudentCorrect();
//        }
//        this.correct = tmp_correct;
//        return correct;
//    }
}

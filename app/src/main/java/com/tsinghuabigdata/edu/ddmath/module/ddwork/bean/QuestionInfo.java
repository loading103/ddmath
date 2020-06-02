package com.tsinghuabigdata.edu.ddmath.module.ddwork.bean;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tsinghuabigdata.edu.ddmath.bean.StdAnswerBean;
import com.tsinghuabigdata.edu.ddmath.module.learnmaterial.bean.AreaInfoBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 题目信息
 */
public class QuestionInfo implements Serializable {

    private static final long serialVersionUID = 2879881565252216167L;

    public static final String TYPE_CHOICE      = "choice";                  //单项选择题
    public static final String TYPE_MUTICHOICE  = "mutichoice";          //多选题
    public static final String TYPE_FILL_IN     = "fill_in";                //填空题
    public static final String TYPE_JUDGE       = "judge";                    //判断题
    public static final String TYPE_SOLUTION    = "solution";              //解答题
    public static final String TYPE_CALCULATION = "calculation";        //计算题

    public static final int CORRECTSTATUS_NONE     = 0;                     //0：
    public static final int CORRECTSTATUS_UNAPPLY  = 1;                  //1：未申请
    public static final int CORRECTSTATUS_APPLYED  = 2;                  //2：已申请
    public static final int CORRECTSTATUS_FINISHED = 3;                  //3：已重新批阅

    protected String questionId;
    private String questionType;

    private int indexInPaper;       //题顺序索引

    private String numInPaper;      //题编号
    private String stemg;
    private String stemGraph;
    private String subStemg;
    private String subStemGraph;

    private String stem;
    private String subStem;

    private String stemLatexGraph;
    private String subLatexGraph;
    private String answerUrl;           //对应答案图片的URL
    /*protected*/ String questionArea;        //题目答案区域信息 [{“x”:0.322323, “y”:0.2222222, “width”:1.0, “height”:0.1412,"figer"}]
    private String answerArea;         //答案批改标示区域信息 [{“x”:0.322323, “y”:0.2222222, “width”:1.0, “height”:0.1412}]

    protected String subStemLatexGraph;

    private ArrayList<KnowledgePiontBean> knowledges;     //知识点列表
    private ArrayList<SolveThinkBean>     solutions;      //解题思路
    private List<StdAnswerBean>           stdAnswers;                 //参考答案

    private ArrayList<KnowledgePiontBean> subKnowledges;       //错误子问知识点

    //private boolean overdue;            //作业截止时间后七天内 true，否则:false
    private int correctionStatus = CORRECTSTATUS_UNAPPLY; //申请纠错状态 1：未申请  2：已申请，3：已重新批阅
    private int asked;       //此题问小豆的次数

    //周练/考试 得分与总分
    /*private*/ float questionScore;        //总分
    /*private*/ float studentScore;         //得分

    //订正答案信息
    private ReviseResultInfo reviseResultResponse;

    //批阅结果是否正确
    /*protected*/ boolean correct = false;         //
    private String  correctTime;        //批阅时间

    //原版教辅添加
    private String areaInfo;        //坐标信息  原版教辅 答案区域 & 辅助图区域

    private String bookId;
    private String sectionId;
    private int    sectionIndex;

    private float difficult;
    private int   difficultLevel;

    /*private*/ ArrayList<SubQuestionInfo> subQuestions;
    private int uploadType; // 上传方式，0：拍照上传，1：扫描上传，2：批阅上传

    private String chapterName;           //章节名称
    private String sectionName;

    private String getAreaInfo() {
        if (areaInfo == null)
            return questionArea;     //老师布置的原版教辅 放在这个字段
        return areaInfo;
    }
    private AreaInfoBean areaInfoBean;
    public AreaInfoBean getAreaInfoBean(){
        if( areaInfoBean != null ) return areaInfoBean;

        areaInfoBean = new Gson().fromJson( getAreaInfo(), new TypeToken<AreaInfoBean>() {
        }.getType());
        return areaInfoBean;
    }
    //    public void setAreaInfo(String areaInfo) {
    //        this.areaInfo = areaInfo;
    //    }

    public void copy(QuestionInfo questionInfo) {
        questionId = questionInfo.questionId;
        questionType = questionInfo.questionType;
        indexInPaper = questionInfo.indexInPaper;
        numInPaper = questionInfo.numInPaper;
        stemg = questionInfo.stemg;
        stemGraph = questionInfo.stemGraph;
        subStemg = questionInfo.subStemg;
        subStemGraph = questionInfo.subStemGraph;
        stemLatexGraph = questionInfo.stemLatexGraph;
        subLatexGraph = questionInfo.subLatexGraph;
        answerUrl = questionInfo.answerUrl;
        questionArea = questionInfo.questionArea;
        answerArea = questionInfo.answerArea;
        knowledges = questionInfo.getKnowledges();
        solutions = questionInfo.solutions;
        correctionStatus = questionInfo.correctionStatus;
        stdAnswers = questionInfo.stdAnswers;
        subKnowledges = questionInfo.subKnowledges;
        asked = questionInfo.asked;

        questionScore = questionInfo.questionScore;
        studentScore = questionInfo.studentScore;

        reviseResultResponse = questionInfo.reviseResultResponse;
        //        correctionAnswerArea= questionInfo.correctionAnswerArea;
        //        errorAnalysis    = questionInfo.errorAnalysis;
        //        isCorrectionCorrect=questionInfo.isCorrectionCorrect;
//        correct = questionInfo.correct;      只有未上传的作业才用到copy函数，这是corect=false，不用拷贝
        correctTime = questionInfo.correctTime;
        sectionName = questionInfo.sectionName;
        chapterName = questionInfo.chapterName;

        areaInfo = questionInfo.areaInfo;
        bookId = questionInfo.bookId;
        sectionId = questionInfo.sectionId;
        sectionIndex = questionInfo.sectionIndex;
        if( subQuestions == null ){
            subQuestions = questionInfo.subQuestions;
        }else{
            int min_count = subQuestions.size()<questionInfo.subQuestions.size() ? subQuestions.size() : questionInfo.subQuestions.size();
            for( int i=0; i<min_count; i++ ){
                subQuestions.get(i).copy( questionInfo.subQuestions.get(i) );
            }
        }
        uploadType = questionInfo.uploadType;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public int getIndexInPaper() {
        return indexInPaper;
    }

    public void setIndexInPaper(int indexInPaper) {
        this.indexInPaper = indexInPaper;
    }

    public String getNumInPaper() {
        return numInPaper;
    }

    public void setNumInPaper(String numInPaper) {
        this.numInPaper = numInPaper;
    }

    public String getStem() {
        return TextUtils.isEmpty(stemg) ? stem : stemg;
    }

    public void setStem(String stem) {
        this.stemg = stem;
    }

    public String getStemGraph() {
        return stemGraph;
    }

    public void setStemGraph(String stemGraph) {
        this.stemGraph = stemGraph;
    }

    public String getSubStem() {
        return subStemg != null ? subStemg : subStem;
    }

    public void setSubStem(String subStem) {
        this.subStemg = subStem;
    }

    public String getSubStemGraph() {
        return subStemGraph;
    }

    public void setSubStemGraph(String subStemGraph) {
        this.subStemGraph = subStemGraph;
    }

    public String getStemLatexGraph() {
        return stemLatexGraph;
    }

    public void setStemLatexGraph(String stemLatexGraph) {
        this.stemLatexGraph = stemLatexGraph;
    }

    public String getSubLatexGraph() {
        return subLatexGraph;
    }

    public void setSubLatexGraph(String subLatexGraph) {
        this.subLatexGraph = subLatexGraph;
    }

    public String getAnswerUrl() {
        return answerUrl;
    }

    public void setAnswerUrl(String answerUrl) {
        this.answerUrl = answerUrl;
    }

    public String getQuestionArea() {
        return questionArea;
    }

    public void setQuestionArea(String questionArea) {
        this.questionArea = questionArea;
    }

    public String getAnswerArea() {
        return answerArea;
    }

    public void setAnswerArea(String answerArea) {
        this.answerArea = answerArea;
    }

    public String getSubStemLatexGraph() {
        return subStemLatexGraph;
    }

    public void setSubStemLatexGraph(String subStemLatexGraph) {
        this.subStemLatexGraph = subStemLatexGraph;
    }

    public ArrayList<KnowledgePiontBean> getKnowledges() {
        return knowledges;
    }

    public void setKnowledges(ArrayList<KnowledgePiontBean> knowledges) {
        this.knowledges = knowledges;
    }

    public ArrayList<SolveThinkBean> getSolutions() {
        return solutions;
    }

    public void setSolutions(ArrayList<SolveThinkBean> solutions) {
        this.solutions = solutions;
    }

    //    public boolean isOverdue() {
    //        return overdue;
    //    }
    //
    //    public void setOverdue(boolean overdue) {
    //        this.overdue = overdue;
    //    }

    public int getCorrectErrorStatus() {
        return correctionStatus;
    }

    public void setCorrectErrorStatus(int status) {
        this.correctionStatus = status;
    }

    public List<StdAnswerBean> getStdAnswers() {
        return stdAnswers;
    }

    public void setStdAnswers(List<StdAnswerBean> stdAnswers) {
        this.stdAnswers = stdAnswers;
    }

    public ArrayList<KnowledgePiontBean> getSubKnowledges() {
        return subKnowledges;
    }

    public void setSubKnowledges(ArrayList<KnowledgePiontBean> subKnowledges) {
        this.subKnowledges = subKnowledges;
    }

    public int getAsked() {
        return asked;
    }

    public void setAsked(int asked) {
        this.asked = asked;
    }

    public float getQuestionScore() {
        return questionScore;
    }

    public void setQuestionScore(float questionScore) {
        this.questionScore = questionScore;
    }

    public float getStudentScore() {
        return studentScore;
    }

    public void setStudentScore(float studentScore) {
        this.studentScore = studentScore;
    }

    public boolean isCorrect() {
        return correct;
    }

//    public void setCorrect(boolean correct) {
//        this.correct = correct;
//    }

    public ReviseResultInfo getReviseResultResponse() {
        return reviseResultResponse;
    }

    public void setReviseResultResponse(ReviseResultInfo reviseResultResponse) {
        this.reviseResultResponse = reviseResultResponse;
    }

    public String getCorrectTime() {
        return correctTime;
    }

    public void setCorrectTime(String correctTime) {
        this.correctTime = correctTime;
    }


    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getSectionId() {
        return sectionId;
    }

    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
    }

    public int getSectionIndex() {
        return sectionIndex;
    }

    public void setSectionIndex(int sectionIndex) {
        this.sectionIndex = sectionIndex;
    }

    public float getDifficult() {
        return difficult;
    }

    public void setDifficult(float difficult) {
        this.difficult = difficult;
    }

    public int getDifficultLevel() {
        return difficultLevel;
    }

    public void setDifficultLevel(int difficultLevel) {
        this.difficultLevel = difficultLevel;
    }

    public ArrayList<SubQuestionInfo> getSubQuestions() {
        return subQuestions;
    }

//    public void setSubQuestions(ArrayList<SubQuestionInfo> subQuestionVos) {
//        this.subQuestionVos = subQuestionVos;
//    }

    public void setUploadType(int uploadType) {
        this.uploadType = uploadType;
    }

    public int getUploadType() {
        return uploadType;
    }

    public String getChapterName() {
        if(TextUtils.isEmpty(chapterName)) chapterName = "";
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public String getSectionName() {
        if(TextUtils.isEmpty(sectionName)) sectionName = "";
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }
}

package com.tsinghuabigdata.edu.ddmath.module.exclusivepractice.bean;

import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.KnowledgePiontBean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 专属套题结构
 */

public class PracticeBean implements Serializable {

    private static final long serialVersionUID = 8696600986443148868L;

    public static final int ST_DETAIL           = 0;       //0，查看详情
    public static final int ST_WAITCAMERA       = 1;       //1，拍照上传
    public static final int ST_WAITCORRECT      = 2;       //2，待批阅
    public static final int ST_CORRECTING       = 3;       //3, 批阅中
    public static final int ST_CORRECTED        = 4;       //4, 已批阅
    public static final int ST_STATED           = 5;       //5, 统计完成

    public static final int ST_NOBUY            = 0;       //0, 未购买
    public static final int ST_BUYED            = 1;       //1, 已购买

    public static final int Q_DIFFICULT_NO       = 0;       //不分难度
    public static final int Q_DIFFICULT_EASY     = 1;       //习题难度:易
    public static final int Q_DIFFICULT_MID      = 2;       //习题难度:中
    public static final int Q_DIFFICULT_HIGH     = 3;       //习题难度:难
    public static final int Q_DIFFICULT_A        = 4;       //套题A卷
    public static final int Q_DIFFICULT_B        = 5;       //套题B卷

    private String excluId;             //习题ID
    private String examId;              //作业ID     	作业ID（购买后才返回）
    private int difficult;              //习题难度：1，难，2，中，3，易  0:不分难度

    private int questionCount;          //题目数
    private ArrayList<KnowledgePiontBean> knowledgePoints;      //	学生掌握较差，但经常考的知识点

    private int status;                 //购买状态：0，未购买，1，已购买
    private int exerStatus;             //作业状态：1,待上传，2,待批阅，3,批阅中，4,已批阅，5，统计完成

    private int rightCount;             //正确数量
    private int wrongCount;             //错误数量

    private int score;                  //得分                有分数的套题
    private int totalScore;             //总分

    private String title;               //试卷标题
    private boolean created;            //true:已生成，false:生成中

    private long endDate;            //作业提交截止时间
    private transient int paperType;              //类型，来自商品的属性

    private long startTime;             //每周查漏补缺开始和结束时间
    private long endTime;
    private long createTime;

    private int xuedou;                 //学豆（未购买时返回）

    private transient PracticeProductBean productBean;      //商品信息

    private transient int showType;     //0:默认显示套题实体，!=0: 显示提示信息

    public PracticeBean(){}
    public PracticeBean(int type){
        this.showType = type;
    }

    public String getExcluId() {
        return excluId;
    }

    public void setExcluId(String excluId) {
        this.excluId = excluId;
    }

    public String getExamId() {
        return examId;
    }

    public void setExamId(String examId) {
        this.examId = examId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getExerStatus() {
        return exerStatus;
    }

    public void setExerStatus(int exerStatus) {
        this.exerStatus = exerStatus;
    }

    public int getDifficult() {
        return difficult;
    }

    public void setDifficult(int difficult) {
        this.difficult = difficult;
    }

    public int getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(int questionCount) {
        this.questionCount = questionCount;
    }

    public int getRightCount() {
        return rightCount;
    }

    public void setRightCount(int rightCount) {
        this.rightCount = rightCount;
    }

    public int getWrongCount() {
        return wrongCount;
    }

    public void setWrongCount(int wrongCount) {
        this.wrongCount = wrongCount;
    }

//    public String getPaperType() {
//        return paperType;
//    }
//
//    public void setPaperType(String paperType) {
//        this.paperType = paperType;
//    }

    public ArrayList<KnowledgePiontBean> getKnowledgePoints() {
        return knowledgePoints;
    }

    public void setKnowledgePoints(ArrayList<KnowledgePiontBean> knowledgePoints) {
        this.knowledgePoints = knowledgePoints;
    }

    public int getXuedou() {
        return xuedou;
    }

    public void setXuedou(int xuedou) {
        this.xuedou = xuedou;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public int getScore() {
        return score;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public String getTitle(){
        return title;
    }

    public int getPaperType() {
        return paperType;
    }

    public void setPaperType(int paperType) {
        this.paperType = paperType;
    }

    public boolean isCreated() {
        return created;
    }

    public long getSubmitTime() {
        return endDate;
    }
    public void setSubmitTime(long time) {
        endDate = time;
    }
    public PracticeProductBean getProductBean() {
        return productBean;
    }

    public void setProductBean(PracticeProductBean productBean) {
        this.productBean = productBean;
    }

    public long getCreateTime() {
        return createTime;
    }

    public int getShowType() {
        return showType;
    }
}

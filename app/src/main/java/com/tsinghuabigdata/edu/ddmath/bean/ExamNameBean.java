package com.tsinghuabigdata.edu.ddmath.bean;

import java.io.Serializable;

/**
 * 上传历史
 */
public class ExamNameBean implements Serializable {
    private static final long serialVersionUID = 1557158345411694553L;

//    "examName":"2017-03-12作业",
//            "uploadTime": 1492704000000，
//    correct:true

    private String examId;
    private String examName;
    private long uploadTime;
    private boolean correct;

    public String getExamId() {
        return examId;
    }

    public void setExamId(String examId) {
        this.examId = examId;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public long getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(long uploadTime) {
        this.uploadTime = uploadTime;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }
}

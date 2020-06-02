package com.tsinghuabigdata.edu.ddmath.bean;

import java.io.Serializable;

/**
 * 作业列表
 */

public class SubmitQuestion implements Serializable {


    private static final long serialVersionUID = 5459548185181206719L;

    //public static final int CAMERA_UPLOAD = 0;
    //public static final int SCAN_UPLOAD = 1;
    public static final int CORRECTED = 4;  //已经批阅
    /**
     * createTime : 1490869666000
     * creator : {"accountId":"48138855A7614806A5989F771C59EC7F","headImage":null,"reallyName":"8.51冒烟辅导老师","teacherId":"TE07A5CD8C5CD2459BA8D3477E2B04FBAD"}
     * endTime : 1490956663000
     * examId : EXERH1E1C691684CC473DA6D4CC219C82D059
     * examName : 2017-03-30作业
     * exerStatus : 1
     * overdue : false
     * pageCount : 0
     * questionCount : 3
     * reviewSchedule : 0
     * reviewTeacher : {}
     * reviewTime : null
     * rightQuestionCount : 0
     * startTime : 1490870263000
     * submitTime : null
     * tmpCreator : TE07A5CD8C5CD2459BA8D3477E2B04FBAD
     * tmpReviewer : TE07A5CD8C5CD2459BA8D3477E2B04FBAD
     * wrongQuestionCount : 0
     */

    private long              createTime;
    private CreatorBean       creator;
    private long              endTime;
    private String            examId;
    private String            examName;
    private int               exerStatus;
    private boolean           overdue;
    private int               pageCount;
    private int               questionCount;
    private int               reviewSchedule;
    private ReviewTeacherBean reviewTeacher;
    private long              reviewTime;
    private int               rightQuestionCount;
    private long              startTime;
    private long              submitTime;
    private String            tmpCreator;
    private String            tmpReviewer;
    private int               wrongQuestionCount;

    private String           resourceType;
    private int             uploadType;
    private int             usePrivilege;
    /*private String            examId;
    private String            examName;
    private int               exerStatus;
    private String            startTime;
    private int               week;
    private String            endTime;
    private int               pageCount;
    private String            questionCount;
    private String            submitTime;
    private int               reviewSchedule;
    private ReviewTeacherBean reviewTeacher;
    private CreatorBean       creator;
    private String            reviewTime;
    private int               wrongQuestionCount;
    private int               rightQuestionCount;
    private boolean           overdue;*/

    private int weekday;
    private int status;
    private int takedpageCount;
    private int submittedCount; //    已提交人数
    private int studentScore;
    private int totalScore;


    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public CreatorBean getCreator() {
        return creator;
    }

    public void setCreator(CreatorBean creator) {
        this.creator = creator;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

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

    public int getExerStatus() {
        return exerStatus;
    }

    public void setExerStatus(int exerStatus) {
        this.exerStatus = exerStatus;
    }

    public boolean isOverdue() {
        return overdue;
    }

    public void setOverdue(boolean overdue) {
        this.overdue = overdue;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public int getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(int questionCount) {
        this.questionCount = questionCount;
    }

    public int getReviewSchedule() {
        return reviewSchedule;
    }

    public void setReviewSchedule(int reviewSchedule) {
        this.reviewSchedule = reviewSchedule;
    }

    public ReviewTeacherBean getReviewTeacher() {
        return reviewTeacher;
    }

    public void setReviewTeacher(ReviewTeacherBean reviewTeacher) {
        this.reviewTeacher = reviewTeacher;
    }

    public long getReviewTime() {
        return reviewTime;
    }

    public void setReviewTime(long reviewTime) {
        this.reviewTime = reviewTime;
    }

    public int getRightQuestionCount() {
        return rightQuestionCount;
    }

    public void setRightQuestionCount(int rightQuestionCount) {
        this.rightQuestionCount = rightQuestionCount;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(long submitTime) {
        this.submitTime = submitTime;
    }

    public String getTmpCreator() {
        return tmpCreator;
    }

    public void setTmpCreator(String tmpCreator) {
        this.tmpCreator = tmpCreator;
    }

    public String getTmpReviewer() {
        return tmpReviewer;
    }

    public void setTmpReviewer(String tmpReviewer) {
        this.tmpReviewer = tmpReviewer;
    }

    public int getWrongQuestionCount() {
        return wrongQuestionCount;
    }

    public void setWrongQuestionCount(int wrongQuestionCount) {
        this.wrongQuestionCount = wrongQuestionCount;
    }

    public int getWeekday() {
        return weekday;
    }

    public void setWeekday(int weekday) {
        this.weekday = weekday;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getTakedpageCount() {
        return takedpageCount;
    }

    public void setTakedpageCount(int takedpageCount) {
        this.takedpageCount = takedpageCount;
    }

    public int getSubmittedCount() {
        return submittedCount;
    }

    public void setSubmittedCount(int submittedCount) {
        this.submittedCount = submittedCount;
    }

    public String getResourceType() {
        return resourceType;
    }

    public int getUploadType() {
        return uploadType;
    }

    public void setUploadType(int uploadType) {
        this.uploadType = uploadType;
    }

    public int getStudentScore() {
        return studentScore;
    }

    public void setStudentScore(int studentScore) {
        this.studentScore = studentScore;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public int getUsePrivilege() {
        return usePrivilege;
    }

    public class ReviewTeacherBean implements Serializable {
        private static final long serialVersionUID = -8248859321019745856L;
        /**
         * name : jiangziya
         * id : XXXXXXXXX
         * image : XXXXXXX
         */

        private String name;
        private String id;
        private String image;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }
    }

    public class CreatorBean implements Serializable {
        private static final long serialVersionUID = 7202087817115360962L;
        /**
         * name : jiangziya
         * id : XXXXXXXXX
         * image : XXXXXXX
         */

        private String name;
        private String id;
        private String image;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }
    }


}

package com.tsinghuabigdata.edu.ddmath.module.message;

/**
 */
public class MessageRemarkType {
    //豆豆作业 待上传界面 布置作业
    public static final String ACTIVITY_DDWORK_UPDETAIL = "ddmath.ddwork.uploaddetail";
    //豆豆作业 已上传界面 批阅完成
    public static final String ACTIVITY_DDWORK_DETAIL = "ddmath.ddwork.detail";
    //学校作业，已提交界面
    public static final String ACTIVITY_SCWORK_DETAIL = "ddmath.schoolwork.detail";
    //学生信息更新
    public static final String STUDENT_INFO_UPDATE = "ddmath.student.update";
    //评测报告生成提醒 url
    public static final String EXAM_REPORT_CREATE = "ddmath.report.create.exam";
    //作业报告生成提醒 url
    public static final String HOMEWORK_REPORT_CREATE = "ddmath.report.create.homework";
    //周练作业报告生成提醒 url
    public static final String WEEKEXAM_REPORT_CREATE = "ddmath.report.create.weekExam";
    //知识报告生成提醒  url
    public static final String KNOWLEDGE_REPORT_CREATE = "ddmath.report.create.knowledge";
    //综合能力报告生成提醒  url
    public static final String ALLROUND_REPORT_CREATE = "ddmath.report.create.allround";
    //直播结束提醒
    /*public*/ static final String LIVE_END = "ddmath.live.end";
    //入学评测报告被退回提醒
    public static final String ENROLEXAM_REPORT_RETRIEVE = "ddmath.report.retrieve.enrolexam";
    //入学评测报告同意提醒
    /*public*/ static final String ENROLEXAM_REPORT_AGREED = "ddmath.report.agreed.enrolexam";
    //入学评测报告生成提醒 url
    public static final String ENROLEXAM_REPORT_CREATE = "ddmath.report.create.enrolexam";
    //重新批阅完成
    public static final String APPLY_RECORRECT_QUESTION = "ddmath.errorddwork.detail";

    //错题订正报告
    /*public*/ static final String REVISE_ERRORQUESTION_REPORT = "ddmath.report.lookover.reviseerror";

    //错题订正批阅完成
    //public static final String REVISE_QUESTION_CORRECTFINISH = "ddmath.errorddwork.detail";
    //错题订正重新批阅完成
    //public static final String REVISE_QUESTION_RECORRECTFINISH = "ddmath.errorddwork.detail";
    //错题订正-申诉批阅完成
    //public static final String REVISE_QUESTION_APPLYCORRECTFINISH = "ddmath.errorddwork.detail";

    //周题练批阅完成
    /*public*/ static final String EBOOK_WEEKTRAIN_CORRECTED = "ddmath.corrected.weekTopicPractice";
    //周题练订正报告生成
    /*public*/ static final String EBOOK_WEEKTRAIN_REPORT = "ddmath.report.create.weekTopicPractice";

    //周题练批阅完成
    /*public*/ static final String EXCESIVE_PRACTICE_CORRECTED = "ddmath.corrected.exclusivePractice";
    //周题练订正报告生成
    /*public*/ static final String EXCESIVE_PRACTICE_REPORT = "ddmath.report.create.exclusivePractice";

    //学情反馈界面跳转
    /*public*/ static final String STUDYCONDITION_REPORT = "ddmath.ddwork.studycondition";
    //个人知识图谱
    static final String STUDYCONDITION_MODEL_KNOWGRAP = "knowgrap";

    //错题本界面
    /*public*/ static final String ERRBOOK_FRAGMENT = "ddmath.ddwork.errbook";
    //周题练
    static final String ERRBOOKFRAGMENT_MODEL_WEEKTRAIN = "weektrain";
    //变式训练
    static final String ERRBOOKFRAGMENT_MODEL_VARIANTTRAIN = "vartrain";
    //错题本下载
    static final String ERRBOOKFRAGMENT_MODEL_STAGEREVIEW = "stagereview";

    //注册正式学豆
    static final String MYCENTER_LEARNDOU = "ddmath.personcenter.mydd";

    //发放了宝贝使用权
    public static final String GIVE_PRODUCT_RIGHT = "ddmath.product.right";

    //1对1习题
    /*public*/ static final String ENCHANCE_FRAGMENT = "ddmath.ddwork.enhance";
    //每周培优
    //static final String ENCHANCE_MODEL_WEEKTRAIN = "weekenchance";
    //考前培优
    //static final String ENCHANCE_MODEL_STAGEREVIEW = "examenchance";
    //考前查漏补缺
    //static final String ENCHANCE_MODEL_FEAKFILLING = "examleekfilling";

    //家长端
    public static final String PARENT_EXPORT_WORK = "ddmath.parentreport.create.homework";      //作业报告
    public static final String PARENT_EXPORT_WEEK = "ddmath.parentreport.create.week";          //周报告
    public static final String PARENT_REPORT_BUYSUIT = "purchase.product.suite";                 //购买套餐

    //
    public static final String MYCENTER_BUYSUIT = "ddmath.mycenter.buysuit";          //个人中心-购买套餐

    public static final String TEACHER_COMMENT_WEEK = "ddmath.teacher.comment.week";

    public static final String STUDY_ANALYSISREPORT_CREATE = "ddmath.report.create.weekStudyAnalysis";        //周学习分析报告生成
}

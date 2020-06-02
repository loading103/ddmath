package com.tsinghuabigdata.edu.ddmath.constant;

/**
 * <p>
 * Created by blueto
 * </p>
 *
 * @author blueto
 * @version V1.0
 */

public class AppRequestConst {

    /**
     * 服务器地址
     */
    public static final String RESTFUL_ADDRESS = BaseConfig.RESTFUL_ADDRESS;                                // 多配置打包

    public static final String   UPGRADE_APK_HOST = BaseConfig.UPGRADE_APK_ADDRESS;
    /**
     * 内嵌页
     */
    public static final Object   WEB_ADDRESS      = BaseConfig.WEB_ADDRESS;
    //    public static final Object WEB_ADDRESS = "http://172.16.1.83:3100";
    /**
     * DNS服务器
     */
    public static final String[] DNS_SERVERS      = new String[]{"114.114.114.114"}; //NOSONAR
    /**
     * 是否使用代理
     */
    public static final boolean  IS_USE_DNSPROXY  = BaseConfig.IS_USE_DNSPROXY;
    /*
     * 日志上传接口
     */
    public static final String   LOG_UPLOAD       = "/rest/log/trace/file/:plat/";

    /**
     * 登录接口
     * POST /studying/login			（学生）
     * POST /teaching/login			（老师）
     * 参数名	必选	类型及范围	说明
     * loginName	true	String	登录名
     * password	true	String	登录密码
     * deviceId	true	String	设备Id
     * clientType	true	String	客户端类型
     * validCode	true	String	验证码（用户登录失败达到一定次数后需要提供，允许的错误次数由后台配置）
     */
    public static final String POST_LOGIN_STUDENT = "/rest/studying/login";
    //public static final String POST_LOGIN_TEACHER = "/rest/teaching/login";
    public static final String LOGIN_ACCESS       = "/rest/upload/config";

    /**
     * 退出登录
     * POST  /studying/v1 /users/logout
     * POST  /teaching/v1/users /logout
     */
    public static final String POST_LOGOUT_STUDENT          = "/rest/studying/v1/users/logout";
    /**
     * 注册
     */
    public static final String POST_REGISTER                = "/rest/studying/dd/register";
    /**
     * 注册发验证码
     */
    public static final String REGISTER_VERIFYCODE          = "/rest/studying/register/code";
    /**
     * 获取学生班级列表
     */
    public static final String GET_GET_CLASSLIST            = "/rest/studying/v1/users/trip/:studentId";
    public static final String GET_QUERY_TUTOR_CLASSINFO    = "/rest/studying/dd/v1/school/student/:studentId";
    /**
     * 省市查询
     */
    public static final String GET_QUERY_AREA               = "/rest/studying/dd/v1/school/area/search";
    /**
     * 学校查询
     */
    public static final String GET_QUERY_SCHOOL_LIST        = "/rest/studying/dd/v1/school/search";
    /**
     * 班级查询
     */
    public static final String GET_QUERY_CLASS_LIST         = "/rest/studying/dd/v1/school/class/search";
    /**
     * 加入班级
     */
    public static final String JOIN_CLASS                   = "/rest/studying/v1/student/class/relate";
    /**
     * 个人信息补充
     */
    public static final String POST_UPDATE_EXTRA_PERSONINFO = "/rest/studying/personal/supplement";
    /**
     * 学校模糊查询接口
     * GET /studying/schools?schoolName=
     */
    public static final String GET_QUERY_BLURSCHOOL         = "/rest/studying/schools";
    /**
     * 获取验证码
     * GET  /studying/captcha?phone=:phone
     */
    public static final String GET_GET_VERIFYCODE           = "/rest/studying/captcha";
    /**
     * 重置密码接口
     * POST  /studying /password/reset
     */
    public static final String POST_RESET_PASS              = "/rest/studying/password/reset";
    /**
     * 修改密码接口
     * POST  /studying /password/reset
     */
    public static final String POST_MODIFY_PASS             = "/rest/studying/v3/password/modify";
    /**
     * 个人基本信息查询接口
     * GET  /studying/v1 /users/{accountId}
     */
    public static final String GET_USER_DETAILINFO          = "/rest/studying/v1/users/:accountId";
    /**
     * 个人基本信息编辑接口
     * POST /studying/v1/users/{accountId}/update
     */
    public static final String POST_PERSONINFO_EDIT_UPDATE  = "/rest/studying/v1/users/:accountId/update";
    /**
     * 个人头像上传
     * POST  /studying/v1/users/headimage/upload      （学生）
     * POST  /teaching/v1/users/headimage/upload			（老师）
     */
    public static final String POST_HEADIMG_UPLOAD          = "/rest/studying/v1/users/headimage/upload";
    /**
     * 4.1.1.6校验手机号是否被使用
     * GET  /studying/verify?phone=:phone
     */
    public static final String GET_PHONENUM_ISUSED          = "/rest/studying/verify?";
    /**
     * 绑定手机号
     * post  http://xx.com/rest/studying/cellphone/binding
     */
    public static final String POST_BINDMOBILE              = "/rest/studying/cellphone/binding";
    /**
     * 我的学习相关接口
     */

    //4.1	上传作业图片
    //file	True	File	学生上传的作业
    public static final String POST_MYLEARN_UPLOADIMAGE     = "/rest/upload/v1/tutorial/images/v2";

    //4.2	提交上传作业信息
    //studentId	true	String	学生ID
    //classId	True	String	Class Id
    //files	True	FileParam[]	本次上传的文件ID
    //path	True	String	图片路径
    //delete	True	Boolean	是否删除
    public static final String POST_MYLEARN_WORKINFO = "/rest/upload/v1/tutorial/images/submit";

    //豆豆作业提交
    public static final String POST_MYLEARN_DDWORKINFO = "/rest/upload/v1/exercise/submit/v3";

    //图片删除
    public static final String POST_MYLEARN_REMOVEIMAGE = "/rest/upload/v1/tutorial/images/remove";

    //4.3	查询拍照题集
    //studentId	true	String	学生ID
    //classId	True	String	班级ID
    //pageNum	True	Int	当前分页
    //pageSize	True	Int	每页记录数
    public static final String GET_MYLEARN_WORKLIST = "/rest/studying/v1/tutorial/records";

    //豆豆作业详情
    public static final String GET_MYLEARN_DDWORK_DETAIL = "/rest/studying/homework/v2/:studentId/detail";

    public static final String GET_MY_COURSE = "/rest/studying/v1/tutorial/records-day";

//    public static final String QUERY_CLASS_INFO = "/rest/studying/v2/classes/:classId/students";

    public static final String QUERY_DOUDOU_WORK = "/rest/studying/homework/v2/:studentId/list";

//    public static final String QUERY_TEACHER_STATE = "/rest/studying/v1/live/teachers";
//
//    public static final String QUERY_TUTOR_QUEUE = "";
//
//    public static final String APPLY_TUTOR = "/rest/studying/v1/live/coaching";
//
//    public static final String IS_TUTOR_APPLIED = "/rest/studying/v1/live/coach";
//
//    public static final String IS_TUTOR_TURN = "/rest/studying/v1/live/turn";
//
//    public static final String MODIFY_TUTOR_STATE = "/rest/live/v1/coaching/{coachId}";
//
//    public static final String QUERY_UPLOAD_IMAGES = "/rest/studying/v1/tutorial/images";
//
//    //    public static final String START_TUTOR = "/rest/live/coach/:coachId/start";
//    public static final String START_TUTOR         = "/rest/studying/v1/live/coach/:coachId/start";
//    public static final String END_TUTOR_NORMAL    = "/rest/studying/v1/live/coach/:coachId/over";
//    public static final String END_TUTOR_EXCEPTION = "/rest/studying/v1/live/coach/:coachId/start";
//
//    public static final String GET_NETEASE_TOKEN = "/rest/studying/live/token";
//
//    public static final String DELETE_ROOMID = "/rest/studying/v1/live/coach/suspend";

    public static final String QUESTION_BOOK = "/rest/studying/dd/v2/student/questions/wrong/:studentId";

    /**
     * 豆豆Android app更新
     * GET http://upgrade.iclassedu.com/doudou/upgrade/student/android.json
     */
    public static final String GET_APK_UPDGRADE = "/doudou/upgrade/student/android.json";
    //青豆数学
    public static final String GET_APK_UPDGRADE_QD = "/doudou/upgrade/student/qingdou/android.json";
    //北教豆豆
    public static final String GET_APK_UPDGRADE_BJDD = "/doudou/upgrade/student/beijiaodd/android.json";

    /**
     * 取首页消息
     */
    public static final String GET_QUERY_HOME_MSG_LIST = "/rest/msgApp/queryHomePageMsg";

    /**
     * 获取首页重要轮播消息
     */
    public static final String GET_IMPORTANT_MSG_LIST = "/rest/msgApp/homePageCarousel/:userId";

    /**
     * 消息列表
     */
    public static final String GET_MESSAGE_LIST = "/rest/msgApp/message/latterly/:userId";

    /**
     * 消息详情
     */
    public static final String GET_MESSAGE_DETAIL = "/rest/msgApp/queryUserMsgInfo";

    /**
     * 是否有新消息
     */
    public static final String GET_MESSAGE_NEW = "/rest/msgApp/queryUserNewMsg/:accountId";

    /**
     * 分享并下载
     */
    public static final String GET_DDWORK_SHARE    = "/rest/studying/dd/v1/homework/share/:examId";
    public static final String GET_DDWORK_DOWNLOAD = "/rest/studying/dd/v1/homework/download/";

    /**
     * 检查作业状态,是否被撤回
     */
    public static final String GET_DDWORK_STATUS = "/rest/studying/homework/v3/revoked-status";


    //超级错题本
    /**
     * 入学评测 查询入学评测详情
     */
    public static final String ENTRANCE_QUERY_INFO = "/rest/studying/v1/enter-evaluate-info/:studentId";

    /**
     * 入学评测 入学评测申请领取报告
     */
    public static final String ENTRANCE_APPLY_INFO = "/rest/studying/v1/enter-evaluate-info/apply";


    /**
     * 入学评测 知识分析查询
     */
    public static final String ENTRANCE_QUERY_KNOWLEDGE = "/rest/studying/v1/knowledge/:studentId";

    /**
     * 入学评测 知识分析保存
     */
    public static final String ENTRANCE_SAVE_KNOWLEDGE = "/rest/studying/v1/knowledge";

    /**
     * 素质分析报告edit
     */
    public static final String QUALITY_ANALYSIS_REPORT      = "/phone.html#/survey/quality/:classId/:studentId/:status";
    /**
     * 报告列表查询
     */
    public static final String GET_QUERY_REPORTS            = "/rest/studying/dd/v1/reports/:studentId/:reportType";
    /**
     * 更新报告阅读状态
     */
    public static final String GET_UPDATE_REPORT_READSTATUS = "/rest/studying/dd/v1/reports/update/:reportType/:reportId";
    /**
     * 查询是否有新报告
     */
    public static final String GET_ISHAVE_NEWREPORT         = "/rest/studying/dd/v1/reports/check/new/:studentId";

    /**
     * //综合能力报告生成提醒  url
     * public static final String ALLROUND_REPORT_CREATE = "ddmath.report.create.allround";
     * 报告地址：/phone.html#/report/comprehensive/:reportedId?access_token=
     */
    public static final String WEB_ALLROUND_REPORT             = "/phone.html#/report/comprehensive/:reportedId";
    /**
     * //评测报告生成提醒 url
     * public static final String EXAM_REPORT_CREATE = "ddmath.report.create.exam";
     * 报告地址：/phone.html#/reports/exam/:studentId/:examId/:classId?access_token = token
     */
    public static final String WEB_EXAM_REPORT                 = "/phone.html#/report/exam/:studentId/:examId/:classId";
    /**
     * //作业报告生成提醒 url
     * public static final String HOMEWORK_REPORT_CREATE = "ddmath.report.create.homework";
     * 报告地址：/phone.html#/reports/homework/:studentId/:examId/:classId?access_token = token
     */
    public static final String WEB_HOMEWORK_REPORT             = "/phone.html#/report/homework/:studentId/:examId/:classId";
    /**
     * //周练作业报告生成提醒 url
     * public static final String HOMEWORK_REPORT_CREATE = "ddmath.report.create.homework";
     * 报告地址：/phone.html#/reports/homework/:studentId/:examId/:classId?access_token = token
     */
    public static final String WEB_WEEKWORK_REPORT             = "/phone.html#/report/homework/personalReport/weekly/:studentId/:examId/:classId";
    /**
     * //知识报告生成提醒  url
     * public static final String KNOWLEDGE_REPORT_CREATE = "ddmath.report.create.knowledge";
     * 报告地址：/phone.html#/report/knowledge/:studentId/:examId/:lastExamId?access_token=
     */
    public static final String WEB_KNOWLEDGE_REPORT            = "/phone.html#/report/knowledge/:studentId";
    /**
     * //知识图谱报告——图谱模式  url
     */
    public static final String WEB_KNOWLEDGE_REPORT_ATLAS_MODE = "/phone.html#/report/knowledgeMap/:studentId";
//    /**
//     * //入学评测报告生成提醒 url
//     * public static final String ENROLEXAM_REPORT_CREATE = "ddmath.report.create.enrolexam";
//     * 报告地址：  /phone.html#/report/quality/:studentId?access_token=
//     */
    //public static final String WEB_ENCROL_EXAM_REPORT          = "/phone.html#/report/quality/:studentId";
    /**
     * 错题周题练报告
     */
    public static final String QUESTION_WEEKPRACTICE_REPORT    = "/phone.html#/report/cycleRefinement/:studentId/:examId/:classId";
    /**
     * 错题订正报告
     */
    public static final String ERROR_CORRECT_REPORT            = "/phone.html#/report/errorCorrectReport/:studentId/:classId";

    /**
     * 用户错题纠错
     */
    public static final String POST_USER_CORRECTERROR = "/rest/studying/dd/v1/student/questions/correction-apply/:studentId";

    /**
     * 错题订正--用户错题纠错
     */

    public static final String POST_REVISE_CORRECTERROR = "/rest/studying/dd/v1/student/questions/revise-correction-apply";


    //机器人问答
    /**
     * 做题前答疑获取
     */
    public static final String GET_QAANS_DO_BEFORE     = "/rest/robot/robotHelpBefore/:studentId/:questionId";
    /**
     * 做题后查找错误原因
     */
    public static final String GET_QAANS_DO_AFTER      = "/rest/robot/robotHelpAfter/:studentId/:examId/:questionId";
    /**
     * 查找相似题
     */
    public static final String GET_ALIKE_QUESTION      = "/rest/robot/alikeQuestion/:questionId/:diff";
    /**
     * 查找相似题
     */
    public static final String GET_NEW_ALIKE_QUESTION  = "/rest/studying/dd/v2/student/questions/recommend";
    /**
     * 学生做题总数
     */
    public static final String GET_QUESTION_DO_TIMES   = "/rest/robot/questionCountInfo/:studentId/:questionId";
    /**
     * 总提问次数
     */
    public static final String GET_QUESTION_HELP_COUNT = "/rest/questionHelpCount/:questionId";
    /**
     * 机器人评价
     */
    public static final String POST_COMMENT_ROBOT      = "/rest/robot/scoreRobot";
    /**
     * 相似题，认知点总结点赞
     */
    public static final String POST_GIVE_LIKE          = "/rest/robot/giveLike";
    /**
     * 提问请求接口，用于统计提问次数
     */
    public static final String POST_ASK_ROBOT          = "/rest/robot/ask";

//    /**
//     * 获取题目详情
//     */
//    public static final String GET_ROBOT_QUESTION_INDFO     = "/rest/robot/questionInfo/:questionId";
    /**
     * 获取题目详情 8.0增加
     */
    public static final String GET_ROBOT_QUESTION_DETAIL    = "/rest/studying/dd/v1/questions/detail/:questionId";
    /**
     * 查询学生是否可以查看推荐题答案的权限 8.0增加
     */
    public static final String GET_ANSWER_PERMISSION        = "/rest/studying/v1/users/answer/authority/:studentId";
    /**
     * 获取学生访问微问信息
     */
    public static final String GET_CLICK_ROBOT_INFO         = "/rest/robot/getClickRobotCount/:classId/:studentId";
    /**
     * 获取学生历史提问记录
     */
    public static final String GET_STU_HISTORY_CLICK_QA     = "/rest/robot/getHistoryCount/:studentId";
    /**
     * 获取学生提问班级排行版
     */
    public static final String GET_STU_CLICKQA_RANK_INCLASS = "/rest/robot/getClassRank/:classId/:studentId";
    /**
     * 获取学生提问准星排行版
     */
    public static final String GET_STU_CLICKQA_RANK_INZX    = "/rest/robot/getALLRank/:studentId";
    /**
     * 同学的正确答案
     */
    public static final String GET_OTHER_STU_ANS            = "/rest/robot/getOtherStudentAnswers/:studentId/:examId/:questionId";

    /**
     * 给同学的正确答案点赞
     */
    public static final String GET_GIVELIKE_TO_OTHER_STUANS = "/rest/robot/updateAnswersLike";

    /**
     * 相似题详情
     */
//    public static final String ROBOT_ALIKE_DETAIL = "/phone.html#/homework/piece/:studentID/:questionId";

    public static final String QA_ADDRESS = "http://teach.iclassedu.com";

    /*
     * 行为上传接口
     */
    public static final String BRHAVIOR_UPLOAD = "/rest/log/clickFlow/:plat/";

    /**
     * 2.5.1.	查询学力个人信息
     * /rest/studying/v1/si/student/{studentId}
     */
    public static final String STUDY_ABILITY_URL = "/rest/studying/v1/studyingindex/strategies/:studentId";

//    /**
//     * 查询是否有新学习任务
//     * rest/learntask/hasnew
//     */
//    public static final String HAS_NEW_URL = "/rest/studying/v1/learntask/hasnew";


    /**
     * 查询我的学力
     * GET
     */
    public static final String MY_STUDY_ABILITY = "/rest/studying/v1/users/studyability/:studentId";

    /**
     * 查询我的学豆
     * GET
     */
    public static final String MY_STUDY_BEAN = "/rest/studying/v2/product/balance/:studentId";

    /**
     * 查询赠送学豆
     */
    public static final String REWARD_STUDY_BEAN = "/rest/studying/ddreturn/find";

    /**
     * 充值记录
     */
    public static final String RECHARGE_RECORD = "/phone.html#/report/mgt/recharge/:studentId";

    /**
     * 兑换记录
     */
    public static final String EXCHANGE_RECORD = "/phone.html#/report/mgt/exchange/:studentId";

    /**
     * 学豆使用详情
     */
    public static final String USE_BEAN_DETAIL = "/phone.html#/report/mgt/detail/:studentId";

    /**
     * VIP卡兑换记录
     */
    public static final String CARD_EXCHANGE_DETAIL = "/phone.html#/report/mgt/exchange/:studentId?type=vip";

    /**
     * 充值赠送协议
     */
    public static final String SEND_PROTOCOL = "http://www.doudoushuxue.com/protocol.html";

    /**
     * 提交兑换码
     * POST
     */
    public static final String SUBMIT_REDEEM_CODE = "/rest/studying/dd/v2/student/exchange/code/add";

    /**
     * 查询我的今日学力
     * GET
     */
    public static final String MY_TODAY_STUDY_ABILITY = "/rest/studying/v1/users/today/studyability/:studentId";

    /**
     * 意见反馈
     * POST
     */
    public static final String ADD_ADVICE = "/rest/studying/v1/users/advice/add";

    /**
     * 首页荣耀值排行榜
     * GET
     */
    public static final String FIRST_GLORY_RANK = "/rest/studying/dd/v1/student/gloryrank/:classId/:studentId";

    /**
     * 荣耀排行榜-班级排行榜-个人中心
     * GET
     */
    public static final String CLASS_GLORY_RANK = "/rest/studying/dd/v1/student/gloryrank/classrank/:classId/:studentId";

    /**
     * 查询商品列表 v11.0
     * GET
     */
    public static final String GET_PRODUCT_LIST = "/rest/studying/v2/product/list/:studentId";

    /**
     * 商品分类查询
     * GET
     */
    public static final String GET_SCHOOLID_PRODUCT_LIST = "/rest/studying/v2/product/list/:schoolId/:classId/:studentId";

    /**
     * App精品套餐列表查询 13.0新增
     * GET
     */
    public static final String GET_PRODUCT_SUITE_LIST = "/rest/studying/v2/product/suite/getProductSuiteList/:schoolId/:classId/:studentId";

    /**
     * 查询所有商品分类列表 13.0新增
     * GET
     */
    public static final String GET_PRODUCT_GROUP_LIST = "/rest/studying/v2/product/catalog/list";

    /**
     * 查询商品使用手册图片路径
     * POST
     */
    public static final String GET_PRODUCT_GUIDE = "/rest/studying/v2/product/getProductByPrivilegeId/:privilegeId";

    /**
     * 查询商品详情
     * GET
     */
    public static final String GET_PRODUCT_DETAIL = "/rest/studying/v2/product/detail/:privilegeId";

    /**
     * 荣耀排行榜-年级排名-个人中心
     * GET
     */
    public static final String GRADE_GLORY_RANK = "/rest/studying/dd/v1/student/gloryrank/graderank/:classId/:studentId";

    /**
     * 强化训练，查询题目
     * GET
     */
    public static final String TRAIN_GET_QUESTION = "/rest/studying/v1/studyingindex/strategies/strengthen/:studentId/:continueNum";


    /**
     * 强化训练，提交答案
     * POST
     */
    public static final String TRAIN_POST_COMMITANSWER = "/rest/studying/v1/studyingindex/strategies/strengthen/commit/";

    /**
     * 错题回顾 查询题目
     * GET /rest/si/v1/question/wrong/{studentId}
     */
    public static final String ERRORQUESTION_GET_QUESTION = "/rest/studying/v1/studyingindex/strategies/review/:studentId";

    /**
     * 错题回顾 提交结果
     * GET /rest/si/v1/question/wrong/{studentId}
     */
    public static final String ERRORQUESTION_POST_RESULT = "/rest/studying/v1/studyingindex/strategies/review/commit/";

    /**
     * 错题回顾 题目详情
     */
    public static final String WEB_ERROR_QUESTION_REVIEW = "/mobile/errorReview.html";

    //    /**
    //     * 错题本打印
    //     */
    //  public static final String WEB_ERROR_QUESTION_PRINT = "/phone.html#/errorquestion";

    /**
     * pdf生成请求
     */
    public static final String PRODUCT_PDF = "/rest/studying/dd/student/pdf/product/request";

    /**
     * pdf生成结果查询
     */
    public static final String SERACH_PDF = "/rest/studying/dd/student/pdf/serach";

    /**
     * 网速测试
     */
    public static final String SPEED_TEST = "/rest/upload/v1/upload/validate/network/speed/v2";

    /**
     * 错题订正
     */
    public static final String POST_USER_REVISEERROR = "/rest/upload/v1/revise/submit/v2";

    /**
     * 错题订正 列表
     */
    public static final String EBOOK_DAYCLEAN_LIST = "/rest/studying/dd/v1/student/questions/wrongquestiondayclear/:studentId/list";

    /**
     * 错题订正详细查询
     */
    public static final String EBOOK_DAYCLEAN_DETAIL = "/rest/studying/dd/v1/student/questions/wrongquestiondayclear/:studentId/detail";

    /**
     * 错题周题练 列表
     */
    //public static final String EBOOK_WEEKTRAIN_LIST = "/rest/studying/dd/v1/distill/week/:studentId/list/new";
    public static final String EBOOK_WEEKTRAIN_LIST  = "/rest/studying/dd/v1/distill/week/paper/:studentId/list/new";
    /**
     * 错题周题练 分享下载
     */
    public static final String EBOOK_WEEKTRAIN_SHARE = "/rest/studying/dd/v1/refine/download/v1";

    /**
     * 错题本下载  列表
     */
    //public static final String EBOOK_STAGEREVIEW_LIST = "/rest/studying/dd/v1/student/questions/wrongquestionperiodreview/:studentId/list/new";
    public static final String EBOOK_STAGEREVIEW_LIST  = "/rest/studying/dd/v1/student/questions/wrongquestionperiodreview/:studentId/v2/list/new";
    /**
     * 错题本下载  分享下载
     */
    //public static final String EBOOK_STAGEREVIEW_SHARE = "/rest/studying/dd/v1/student/questions/wrongquestionperiodreview/:studentId/sharedownload/new";
    public static final String EBOOK_STAGEREVIEW_SHARE = "/rest/studying/dd/v1/student/questions/wrongquestionperiodreview/:studentId/sharedownload/v3";

    /**
     * 错题本下载  查看详情
     */
    public static final String EBOOK_STAGEREVIEW_DETAIL = "/rest/studying/dd/v1/student/questions/wrongquestionperiodreview/:studentId/detail";

    /**
     * 专属习题列表
     */
    //public static final String GET_PRACTICE_LIST = "/rest/studying/exclusive/paper/list";
    public static final String GET_PRACTICE_LIST = "/rest/studying/variant/training/list";

    /**
     * 精品套题列表
     */
    public static final String GET_CLASSIC_PRACTICE_LIST  = "/rest/studying/classic/paper/list";
    /**
     * 精品套题列表
     */
    public static final String GET_CLASSIC_PRACTICE_SHARE = "/rest/studying/classic/share/download";

    /**
     * 商品使用次数 PrivilegeId 获取
     */
    public static final String GET_REVISE_TIMES = "/rest/studying/v2/product/total/use/times";

    /**
     * 商品使用次数 ProductId 获取
     */
    public static final String GET_PRODUCT_USETIMES = "/rest/studying/v2/product/use/times";

    /**
     * 兑换商品接口
     */
    public static final String POST_EXCHANGE_PRODUCT = "/rest/studying/v2/product/exchange";

    /**
     * 充值返现 推荐
     */
    public static final String GET_RECHARGE_RECOMMEND = "/rest/studying/ddreturn/recommend";

    /**
     * 专属套题 分享下载
     */
    public static final String GET_PRACTICE_SHARE = "/rest/studying/exclusive/share/download";

    /**
     * 支付宝APP购买学豆请求签名数据返回(13.3新增，可以抵扣学豆)
     */
    public static final String GET_ALIPAY_SIGN_Deduction = "/rest/studying/v2/product/suite/purchase";

    /**
     * 支付宝APP购买学豆请求签名数据返回
     */
    public static final String GET_ALIPAY_SIGN = "/rest/studying/alipay/sign/";

    /**
     * 使用支付宝APP充值-更新充值表状态
     */
    public static final String UPDATE_TRADE = "/rest/studying/recharge/update/";

    /**
     * 支付宝家长代付生成二维码
     */
    public static final String GET_ALIPAY_QRCODE = "/rest/studying/alipay/qrcode/";

    /**
     * 支付宝支付完成后查询充值表
     */
    public static final String QUERY_TRADE = "/rest/studying/recharge/query/:tradeNo";

    /**
     * 微信统一下单接口
     */
    public static final String GET_WX_SIGN = "/rest/studying/wechat/pay/unifiedorder";
    public static final String GET_WX_SIGN_PARENT = "/rest/studying/wechat/pay/unifiedorder/v2";

    /**
     * 兑换vip卡
     */
    public static final String EXCHANGE_CARD = "/rest/studying/dd/v3/student/exchange/code/validate";

    /**
     * 查询教辅对应的题目和框信息
     */
    public static final String QUERY_LMQUETSIONS_ANSWERAREA = "/rest/studying/book/page/question";

    /**
     * 学生创建作业-自我检查作业
     */
    public static final String POST_SUBMIT_LMSELFWORK = "/rest/upload/exercise/homework/create";

    /**
     * 查询学生可用的原版教辅列表
     */
    public static final String QUERY_BOOK_LIST = "/rest/studying/dd/v1/book/app/list";

    /**
     * 学生选择原版教辅
     */
    public static final String SELECT_BOOK = "/rest/studying/v1/book/use";

    /**
     * 查询教辅章和页码信息
     */
    public static final String QUERY_BOOK_CATALOG = "/rest/studying/dd/v1/book/catalog";

    /**
     * 查询单题批阅详情
     */
    public static final String SINGLE_DETAIL = "/rest/studying/homework/v2/:studentId/single/detail";

    /**
     * 查询错题订正报告详情（老师端接口）
     */
    public static final String REVISE_DETAIL = "/rest/teaching/dd/v1/report/revise/class/student";

    /**
     * 记录学生视频模块浏览记录
     */
    public static final String VIDEO_RECORD = "/rest/studying/v1/video/:studentId/record/";

    /**
     * 记录学生视频模块浏览记录
     */
    public static final String CONFIRM_PHONENUM = "/rest/studying/confirm/phonenum";

    /**
     * 验证验证码
     */
    public static final String VERIFY_CODE = "/rest/studying/verify/code";

    /**
     * 获取豆豆数学学生注册人数
     */
    public static final String GET_REGISTE_COUNT = "/rest/studying/register/count";

    /**
     * 使用人次统计
     */
    public static final String GET_USE_COUNT = "/rest/studying/v1/student/usestatistics";

    /**
     * 获取注册奖励配置
     */
    public static final String GET_REGISTE_REAWRD = "/rest/studying/register/reward/config";

    //    /**
    //     * 商品套餐  详情
    //     */
    //public static final String GET_PRODUCTSUITE_DETAIL = "/rest/operate/v1/productright/suitedetail";

    /**
     * 商品套餐  兑换
     */
    public static final String POST_PRODUCTSUITE_EXCHANGE = "/rest/studying/v2/product/suite/exchange";

    /**
     * 商品套餐详情
     */
    public static final String GET_PRODUCTSUITE_DETAIL = "/rest/studying/v2/product/suite/detail/:productSuiteId";

    /**
     * 获取名师精讲商品列表
     */
    public static final String get_Famous_Product_List = "/rest/studying/v1/video/product/list";

    /**
     * 学习任务精讲视频列表查询
     */
    public static final String get_Video_List = "/rest/studying/v1/video/:productId/list";

    /**
     * 查看已兑换视频列表
     */
    public static final String get_Exchanged_Video_List = "/rest/studying/v1/video/:productId/owned/list";

    /**
     * 根据知识点获取视频列表
     */
    public static final String get_Video_List_ByKnowledge = "/rest/studying/v1/video/recommendation";

    /**
     * 记录视频播放
     */
    public static final String record_Video_Play = "/rest/studying/v1/video/play/record";

    /**
     * 查询用户知识诊断情况
     */
    public static final String GET_USER_KNOWDIAGNOSE = "/rest/studying/dd/v1/reports/knowledge/analysis/detail/:studentId/:examId";

    /**
     * 分享Url
     */
    public static final String URL_SHARE_RIGISTER = "/#/reg?c=%s&u=%s";

    /**
     * 获取学豆数与推荐人数
     */
    public static final String GET_INVITE_COUNT = "/rest/studying/dd/used/num";

    /**
     * 获取背景图
     */
    public static final String GET_SHARE_IMAGES = "/rest/studying/share/images";

    /**
     * 每日错题订正状态查询
     */
    public static final String DAY_ERROR_REVISE_STATUS = "/rest/studying/dd/v1/student/questions/wrongquestiondayclear/:studentId/status";

    /**
     * 查询每日最近一次布置作业的状态
     */
    public static final String RECENT_WORK_STATUS = "/rest/studying/homework/:studentId/recent/status";

    /**
     * 诊断首页查询最近一周周题练和每周培优状态
     */
    public static final String RECENT_WEEK_ERROR_STATUS = "/rest/studying/dd/v1/distill/recent/list/:studentId";

    /**
     * 家长端每日报告列表
     */
    public static final String DAY_REPORT_LIST = "/rest/studying/parent/reports/list/daily/:parentId";

    /**
     * 家长端每周报告列表
     */
    public static final String WEEK_REPORT_LIST = "/rest/studying/week/report/list/:parentId";

    /**
     * 家长阅读报告更新报告状态
     */
    public static final String UPADAT_READ_STATUS = "/rest/studying/parent/reports/updateExerStatus/:studentId/:examId";

    /**
     * 每日报告详情
     */
    public static final String DAY_REPORT_DETAIL = "/phone.htmlt=t#/report/parent/dayReport/:studentId";
    //变式训练和错题精炼调用
    public static final String DAY_REPORT_DETAIL_NEW = "/phone.html#/report/parent/privateReport/:studentId";
    /**
     * 每周报告详情
     */
    public static final String WEEK_REPORT_DETAIL = "/phone.html#/report/parent/weekReport/:recordId";

    /**
     * 文章详情
     */
    public static final String ARTICLE_SHARE_DETAIL = "/phone.html#/report/parent/article/:parentId/:articleId";

    /**
     * 文章列表
     */
    public static final String ARTICLE_LIST = "/rest/studying/article/list";

    /**
     * 社区界面详情
     */
    public static final String ARTICLE_DETAIL = "/rest/studying/article/detail/:parentId/:articleId";

    /**
     * 更新点赞转发阅读记录
     */
    public static final String OPERATE_ARTICLE = "/rest/studying/article/save";

    /**
     * 通过班级码查询班级信息
     */
    public static final String GET_QUERY_CLASSINFO_CLASSCODE = "/rest/studying/dd/v1/school/class/:classCode";

    /**
     * 知识诊断
     */
    public static final String KNOWLEDGE_DIAGNOSE = "/rest/studying/knowledge/report/search/:studentId";

    /**
     * 创建提升练习返回题目数
     */
    public static final String PROMOTE_QUESTION_COUNT = "/rest/studying/knowledge/report/questioncount/:studentId";

    /**
     * 创建提升练习返回题目数
     */
    //public static final String PRODUCE_QUESTION_SET = "/rest/studying/knowledge/report/paperset/create";
    public static final String PRODUCE_QUESTION_SET = "/rest/studying/knowledge/report/paperset/create/v2";

    /**
     * 用戶使用特权
     */
    public static final String PRODUCE_USE_PRIVILEDGE = "/rest/studying/v2/product/learning/privilege/list";

    /**
     * 是否使用自动识别勾叉的功能
     */
    public static final String ENABLE_AUTO_REC_GUOCHA = "/rest/upload/v1/recogTickCross";

    /**
     * 用户首次登陆获得的用户权益
     */
    public static final String USER_FIRST_PRIVILEGE = "/rest/studying/v1/student/newuser/reward/record";

    /**
     * 查询用户积分
     */
    public static final String USER_SCORE = "/rest/studying/dd/v1/credit/product/myCredit/:studentId";

    /**
     * 查询兑换的商品列表
     */
    public static final String SCORE_PRODUCT_LIST = "/rest/studying/dd/v1/credit/product/list/:studentId";

    /**
     * 兑换记录
     */
    public static final String EXCHANGE_RECORD_LIST = "/rest/studying/dd/v1/credit/product/exchange/list/:studentId";

    /**
     * 商品详情
     */
    public static final String EXCHANGE_PRODUCT_DETAIL = "/rest/studying/dd/v1/credit/product/detail/:studentId/:productId";

    /**
     * 执行商品兑换
     */
    public static final String EXEC_EXCHANGE_PRODUCT = "/rest/studying/dd/v1/credit/product/exchange/:studentId/:productId";

    /**
     * 立即使用头像挂件
     */
    public static final String POST_USE_HEADER_PENDANT = "/rest/studying/v1/users/use/headpendent";

    /**
     * 知识诊断 城市排名
     */
    public static final String GET_KNOW_RANK_CITY = "/rest/studying/dd/v1/student/gloryrank/city/:classId/:studentId";

    /**
     * 推荐积分
     */
    public static final String GET_COMMAND_SROCE_LIST = "/rest/studying/dd/v1/credit/recommend/list/:studentId";

    /**
     * 积分记录
     */
    public static final String GET_SROCE_RECORD_LIST = "/rest/studying/dd/v1/credit/record/list/:studentId";

    /**
     * 增加积分
     */
    public static final String POST_ADD_USERSCORE = "/rest/studying/dd/v1/credit/add";
    /**
     * 功能使用说明
     */
    public static final String FUCKYION_USE_DESCRIP ="/instructions";
    /**
     * 功能使用说明
     */
    public static final String COMMON_QUESWTION  = "/faq";
    /**
     * 定制学日期列表
     */
    public static final String GET_SCORE_WEEKLIST = "/rest/studying/dd/v1/personal/design/timeline";
    /**
     * 定制学首页查询
     */
    public static final String GET_SCORE_FIRSTPAGE = "/rest/studying/customize/v1/task/remind";
    /**
     * 定制学错题订正列表
     */
    public static final String GET_CORRET_LIST = "/rest/studying/dd/v1/personal/design/revise/task";
    /**
     * 获取周错题再练周变式训练
     */
    public static final String GET_WEEK_TRAIN_LIST = "/rest/studying/dd/v1/personal/design/week/task";
    /**
     * 获取自定义错题本变式训练错题再练任务列表
     */
    public static final String GET_DEFINE_TRAIN_LIST = "/rest/studying/dd/v1/personal/design/selfCreate/task";
    /**
     * 今天自我检查作业布置的次数
     */
    public static final String GET_TODAY_CHECKWORK_COUNT = "/rest/studying/dd/v1/homework/today/count";

    /**
     * 检查教辅是否布置过作业还有未提交的作业
     */
    public static final String GET_CHECKWORK_TEACHER = "/rest/studying/dd/v1/homework/book/unsubmit/check";

    /**
     * 学生app首页作业状态查询
     */
    public static final String GET_APP_FIRST_WORKSTATUS = "/rest/studying/homework/:studentId/front/page";

    /**
     * 定制学-确认加入
     */
    public static final String GET_CONFIRM_JOIN_CUSTOPMPLAN = "/rest/studying/customize/v1/confirm/join";

    /**
     * 定制学介绍页面
     */
    public static final String WEB_CUSTOMPLAN_INTRODUCE = "/phone.html#/report/improveScheme?studentId=";
    /**
     * 周分析报告列表
     */
    public static final String GET_WEEKANALYSIS_REPORT = "/rest/studying/dd/v2/reports/week/analysis/:studentId";

    /**
     * 周分析报告页面详情
     */
    public static final String WEB_WEEKANALYSIS_REPORT  = "/phone.html#/report/studyingAnalysis/:reportId";
    /**
     *下载pdf
     */
    public static final String WEB_DOWNLOAD_PDF  = "/rest/studying/dd/v2/reports/download";

}

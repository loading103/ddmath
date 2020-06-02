package com.tsinghuabigdata.edu.ddmath.constant;

/**
 * <p>
 * Created by yanshen@tsinghuabigdata.com on 2015/10/27.
 * </p>
 *
 * @author yanshen@tsinghuabigdata.com
 * @version V1.0
 */
public class AppConst {

    /**
     * 应用ID 微信开放平台审核通过的应用APPID
     */
    public static final String APP_ID = "wx33b5179ba509c8c2";
    /**
     * 商户号 微信支付分配的商户号
     */
    public static final String PARTNER_ID = "1494218202";

    public static final String APP_NAME = "/ddmath";

    //每次上传的作业最多的数量
    public static final int MAX_WORK_IMAGE = 12;

    //作业列表单次加载排数
    public static final int WORK_ROWS = 4;

    //入学评测最大上传图片数
    public static final int MAX_ENTRANCE_IMAGE = 20;
    public static final String IMAGE_DIR = APP_NAME + "/image";
    /**
     * 图片后缀
     */
    public static final String IMAGE_SUFFIX_NAME = ".png";
    /**
     * 序列化缓存目录地址
     */
    public static final String OBJECT_CACHE_DIR = "/obj";
    /**
     * 登录用户
     */
    public static final int SESSION_LOGIN_USER = 1;
    /**
     * 标志位，登录成功
     */
    public static final String LOGIN_SUCCESS = "login_success";
    /**
     * 标志位，登录来源
     */
    public static final String LOGIN_FROM_MYSTUDY = "login_from_mystudy";
    /**
     * 学习绑定的笔序列号
     */
    //public static final int SESSION_SERIAL_NUMS = 5;

//    /**
//     * 登录用户
//     */
    //public static final int SESSION_NS_LOGIN = 2;

//    /**
//     * 更新信息
//     */
    //public static final int SESSION_UPDATE_INFO = 3;

//    /**
//     * MAC地址
//     */
    //public static final int SESSION_NS_DEVICE = 4;

//    /**
//     * 标志位，主页个人中心是否显示
//     */
    //public static final String IS_USERCENTER_SHOW = "isshowusercenter";
    /**
     * 学生信息
     */
    public static final int SESSION_STUDENT_INFO = 7;
    /**
     * 网络状态
     */
    public static final int SESSION_NETWORK_STSTE = 8;
    /**
     * 是否使用DNS代理
     */
    public static final int SESSION_USE_PROXY = 11;
//    /**
//     * 离线文件列表
//     */
    //public static final int SESSION_OFFLINE_DATA = 6;
    /**
     * 更新APK信息
     */
    public static final int APK_LATEST_VERSION_INFO = 12;
    /**
     * 班级序号
     */
    public static final int SESSION_CLASS_INDEX = 13;
    public static final int SESSION_CLASS_ID = 1300;

//    /**
//     * 用户类型
//     */
    //public static final int SESSION_ACCOUNT_TYPE = 9;
    /**
     * 学豆数量缓存
     */
    public static final int SESSION_STUDYBEAN_COUNT = 14;
    /**
     * 充值返现
     */
    public static final int SESSION_PRODUCT_CASHBACK = 16;

    /**
     * 注册赠送学豆
     */
    public static final int SESSION_REGISTER_REWARD = 17;

    /**
     * 家长登录用户
     */
    public static final int SESSION_LOGIN_PARENT = 18;

    //用户荣耀值
    public static final int USER_GLORY = 19;

    //------------------------------------------------------------------------------
    /**
     * 消息数量
     */
    public static final String MESSAGE_COUNT = "messsageCount";
    /**
     * 最大缓存过期时间
     */
    public static final long MAX_EXPIRE_TIME = 1000 * 60 * 60;
    /**
     * 用户名
     */
    public static final String LOGIN_NAME = "loginName";
    /**
     * 密码
     */
    public static final String LOGIN_PASS = "loginPass";

    /**
     * 用户名
     */
    public static final String LOGIN_PARENTNAME = "parentloginName";
    /**
     * 密码
     */
    public static final String LOGIN_PARENTPASS = "parentloginPass";

//    /**
//     * 最大扫描设备数量
//     */
    //public static final int MAX_DEVICES_SIZE = 10;

//    /**
//     * 最大扫描时间
//     */
    //public static final int MAX_SEARCH_TIME = 10 * 1000;
    /**
     * 发送验证码倒计时
     */
    public static final int CODE_MAX_TIME = 60;
//    /**
//     * 单次页面最大加载数
//     */
//    public static final int PAGE_SIZE = 15;
    /**
     * 最低5kb为慢网络,不允许上传
     */
    public static final int MIN_SPEED = 5;
    /**
     * 30000毫秒
     */
    public static final long NET_MONITOR_SLEEP = 30000;
    /**
     * 监控数据包大小10kb
     */
    public static final int MONITOR_SEND_SIZE = 10;
    /**
     * 连接异常状态
     */
    public static final int CONNECT_UNKNOW = 0;
    public static final int CONNECT_NONET = 1;
    public static final int CONNECT_TIMEOUT = 2;

    //Latex 最大字体设置
    //public static final int LATEX_MAX_FONT = 7;
    public static final int CONNECT_SERVER = 3;
    /**
     * 多页每页数量
     */
    public static final int MAX_PAGE_NUM = 10;
    public static final int REPORT_PAGE_NUM = 10;
    /**
     * 欢迎页是否打开字段
     */
    public static final String WELCOME_OPENED = "welcome_opened";
    /**
     * 请求code  启动界面时使用
     */
//    public static final int REQUEST_CODE_COMMSUBMIT = 1000;
//    public static final int REQUEST_CODE_COMMOTHER  = 1001;
    public static final int REQUEST_CODE_XBOOKDETAIL  = 1002;
    //
    public static final int SESSION_XBOOK_QUESTION = 1001;
    //public static final int MAX_PAGE_STAT = 40;
    public static final int SESSION_XBOOK_BITMAP = 1002;

    public static final int SESSION_LM_PAGEINFO = 1003;
    /**
     * 接受推送消息状态
     */
    public static final String RECV_PUSH_MSG_ENABLE = "is_recv_push_msg";
    /**
     * 消息中心更新
     */
    public static final String MSG_CENTER_UPDATE_ENABLE = "msg_center_update_enable";
    /**
     * 新消息更新广播字段
     */
    public static final String IS_HAVE_NEW_MSG = "is_have_new_msg";

    /**
     * 分享图片中生成二维码的宽度所占背景图宽度比例
     */
    public static final float WIDTH_RATIO = 0.3f;
    /**
     * 分享图片中生成二维码的底部距离所占背景图高度比例
     */
    public static final float DIS_RATIO = 0.36f;


    //    /**
//     * 从消息中跳转
//     * 页面题目跳转
//     */
    //public static final String SUBJECT_INDEX = "index";
    //public static final String DATE = "date";
    public static final String IS_HAVE_NEW_REPORT = "is_have_new_report";
    //public static final String IS_HAVE_NEW_MSG_TYPE = "is_have_new_msg_type";
    //public static final int MSG_RECORDER = 109;
    //public static final int HOME_MSG_CACHE = 1003;
//    public static final int NEW_HOME_MSG_CACHE = 1004; //9.0更改首页消息显示逻辑
//    public static final String MSG_TYPE = "msgtype";
    public static final String MSG_TYPE_MYMSG = "wdxx";
    public static final String MSG_TYPE_REC = "zxtj";
    public static final String MSG_ROWKEY = "rowkey";
    /**
     *
     */
    public static final int SESSION_SCWORK_BITMAP = 1002;
    public static final String ACTION_CLOSE_ACTIVITY = "android.action.zxbook.close";
    public static final String ACTION_SCWORK_ADD = "android.action.zxbook.add";
    public static final String ACTION_SCWORK_EDIT = "android.action.zxbook.edit";
    public static final String ACTION_START_WAITUPLOAD = "android.action.zxbook.waitupload";
    public static final String ACTION_APPLICATION_EXIT = "android.action.ddapp.exit";

    //public static final String MESSAGE_SERVER_UPGRADING = "message.server.upgrading";
    //public static final String MESSAGE_MAIN_ASYNC = "message.main.async";
    //public static final String MESSAGE_UPLOAD_SUCCESS = "message.upload.success";
    //public static final String MESSAGE_AGENCY_WORK = "message.agency.work";
    //public static final String MESSAGE_AGENCY_RECORRECT = "message.agency.work.recorrect";
    //public static final String MESSAGE_SCHOOL_WORK = "message.school.work";
    //public static final String MESSAGE_JOIN_CLASS = "message.join.class";
    //public static final String MESSAGE_UPDATE_CLASS = "message.update.classinfo";
    //public static final String MESSAGE_CHANGE_CLASS = "message.change.class";
//    public static final String MESSAGE_UPDATE_ABILITY = "message.update.ability";
    //public static final String MESSAGE_CHANGE_ABILITY = "message.change.ability";
    //public static final String MESSAGE_UPDATE_STUDYBEAN = "message.update.studybean";
    //public static final String MESSAGE_SYNC_SHOW_STUDYBEAN = "message.sync.show.studybean";
    //public static final String MESSAGE_EDIT_INFO = "message.edit.info";
    //ublic static final String MESSAGE_EDIT_HEADER = "message.edit.header";
    //public static final String MESSAGE_REFRESH_MYWORLD = "message.refresh.myworld";
    //public static final String MESSAGE_REFRESH_CHECKWORK = "message.refresh.studytask.checkwork";
    //public static final String MESSAGE_REFRESH_FAMOUS_TEACHER = "message.refresh.studytask.famous.teacher";
    //public static final String MESSAGE_REFRESH_PRACTICE = "message.refresh.studytask.practice";
    //public static final String MESSAGE_REFRESH_EBOOK_DAYCLEAN = "message.refresh.errbook.dayclean";
    //public static final String MESSAGE_REFRESH_EBOOK_BROWER = "message.refresh.errbook.brower";
    //public static final String MESSAGE_REFRESH_EBOOK_WEEKTRAIN = "message.refresh.errbook.weektrain";
    //public static final String MESSAGE_REFRESH_EBOOK_STAGEVIEW = "message.refresh.errbook.stageview";
    //public static final String MESSAGE_REGISTERANDLOGIN_SUCCESS = "message.registerandlogin.success";
    //public static final String MESSAGE_GIVE_VIDEO_RIGHT = "message.give.video.right";

    //public static final String MESSAGE_GOTO_STUDYTASK = "message.goto.studytask";
    //public static final String MESSAGE_GOTO_CREATEWORK = "message.goto.creatework";
    //public static final String MESSAGE_GOTO_ERRORBOOK = "message.goto.errorbook";
    //public static final String MESSAGE_GOTO_STUDYCONDITION = "message.goto.studycondition";
    //public static final String MESSAGE_GOTO_PERSONALCENTER = "message.goto.personalcenter";
    //public static final String MESSAGE_ASSIGN_NEWWORK = "message.assign.newwork";
    //public static final String MESSAGE_LOOK_NEWWORK = "message.look.newwork";
    //public static final String MESSAGE_BANNER_MSGLIST = "message.banner.msglist";
    //public static final String MESSAGE_LOOK_WORK = "message.look.work";
    //public static final String MESSAGE_LOOK_MSG_DETAIL = "message.look.msg.detail";

    /**
     * 在线辅导异常状态
     */
    //登录网易出错 加入白板失败 加入音视频失败 进入辅导接口调用失败 被其他端踢出 老师断开连接  学生退出
    public static final int LOGINIM_FAIL       = 0;
    public static final int RTS_FAIL           = 1;
    public static final int VIDEO_FAIL         = 2;
    public static final int START_TUTOR_FAIL   = 3;
    public static final int CONFLICT_NIMLOGIN = 4;
    public static final int INACTIVE_DISCONNECT = 11;  //被动离开房间
    public static final int ACTIVE_EXIT = 12;  //主动退出房间
    //入学年份 最近六年
    public static final int MAX_YEAR = 6;
    /**
     * 左边导航栏宽度
     */
    public static final int NAVI_WIDTH_PAD       = 120;
    public static final int NAVI_WIDTH_PHONE     = 70;
    /**
     * 超级错题本 图片最大值
    */
    public static final int MAX_IMAGE_COUNT = 2;
    //图片剪切时 图片来源 相机
    public static final String FROM_CAMERA = "camera";
    public static final String FROM_PICS = "pics";
    //
    public static final String TYPE_EDIT = "edit";              //编辑
    public static final String ACTION_XBOOK_ADD = "android.action.zxbook.add";
    public static final String ACTION_XBOOK_EDIT = "android.action.zxbook.edit";
    //入学评测
    public static final String TYPE_FROM_ENTRANCE = "entrance";
//    public static final String TYPE_REVISE = "revise";          //错题订正
    //作业类型周练:training
    public static final String WORK_TYPE_NORMAL     = "paperSet";       //套题 普通平台作业
    public static final String WORK_TYPE_LEARNMATER     = "teachingBook";   //教辅作业  作业列表使用
    public static final String WORK_TYPE_LEARNBOOK     = "book";   //教辅作业  作业详情里面使用
    public static final String WORK_TYPE_WEEKTRAIN      = "training";       //周练作业
    public static final String WORK_TYPE_VACATION_WORK   = "vacation";         //假期作业
    public static final String WORK_TYPE_ORIGINALMATERIAL   = "dd_check";    //原版教辅
    public static final String WORK_TYPE_CLASSIC   = "rongclass_evaluation";    //精品套题  蓉城课堂摸底测评

    //错题精练
    public static final String WORK_TYPE_EBOOKWEEKTRAIN  = "refine";         //错题本周题练作业    每周错题精炼
    public static final String WORK_TYPE_MONTH_REFINE = "month_refine";     //每月错题精练
    public static final String WORK_TYPE_CUSTOM_REFINE = "cust_refine";     //自定义错题精练

    //变式训练
    public static final String WORK_TYPE_WEEK_VARIANT  = "weekleakfilling";         //每周查漏补缺    每周变式训练
    public static final String WORK_TYPE_MONTH_VARIANT = "month_variant";     //每月变式训练
    public static final String WORK_TYPE_CUSTOM_VARIANT = "cust_variant";     //自定义变式训练

    public static final String WORK_TYPE_DDCHECK = "dd_check";      //豆豆检查作业
    //WRONG_RETRAINING("wrong_retraining"),// 错题再练
    //GAPS_FILLING("gaps_filling"),   // 查漏补缺

    //增值包id privilegeIds
    public static final String PRIVILEGE_QUESTION_DAYCLEAR      = "questiondayclear";            //错题订正
    public static final String PRIVILEGE_VACATION_WORK         = "vacationwork";               //假期作业
    public static final String PRIVILEGE_SETWORK              = "schoolsetwork";              //校内套题作业
    public static final String PRIVILEGE_ASSISTANTWORK         = "schoolassistantwork";        //校内教辅作业
    public static final String PRIVILEGE_ORIGINALMATERIAL      = "ddcheckhomework";            //原版教辅作业
    public static final String PRIVILEGE_SYNC_CLASSROOM      = "videocourse";                   //同步课堂
    public static final String PRIVILEGE_CAMPUS_SCANNING      = "schoolscanservice";            //校园扫描作业
    //public static final String PRIVILEGE_FAMOUS_VIDEO         = "videocourse";            //名师
    public static final String PRIVILEGE_CLASSICPRATICE        = "cdrcktinvestigateevaluate";        //精品套题

    public static final String PRIVILEGE_EXAM_LEAKFILLING       = "leakfilling";        //考前培优
    public static final String PRIVILEGE_EXAM_RETRAINING      = "wrongtopractice";        //考前错题再练

    //自定义
    public static final String PRIVILEGE_WEEKLEAKFILLING        = "weekleakfilling";        //每周查漏补缺  每周培优   变式训练本
    public static final String PRIVILEDGE_VARTRAIN_MONTH = "monthlyvariabletraining";
    public static final String PRIVILEDGE_VARTRAIN_CUSTOM = "userdefinedvariabletraining";

    //错题再练本
    public static final String PRIVILEGE_QUESTION_WEEKPRACTICE  = "questionweekpractice";    //错题周题练     错题再练本
    public static final String PRIVILEDGE_ERRORREFINE_MONTH = "monthlyquestionrefined";
    public static final String PRIVILEDGE_ERRORREFINE_CUSTOM = "userdefinedquestionrefined";

    //错题浏览本
    public static final String PRIVILEGE_PERIODREVIEW       = "periodreview";                  //错题本下载    错题浏览本
    public static final String PRIVILEGE_PERIODREVIEW_CUSTOM = "userdefinedwrongbookdownload";

    public static final String SERVER_PHONE = "4009928918";
    public static final String SERVER_EMAIL = "service-fw@tsinghuabigdata.com";
    //错题本 最近7天对应的毫秒值
    public static final int RECENT_7_DAY = 7 * 24 * 60 * 60 * 1000;
    public static final int RECENT_15_DAY = 15 * 24 * 60 * 60 * 1000;
    /**
     * 缓存目录地址
     */
    private static final String CACHE_DIR = APP_NAME + "/cache";
    /**
     * 图片缓存目录地址
     */
    public static final String IMAGE_CACHE_DIR = "/image";
    /**
     * PDF作业缓存
     */
    public static final String PDFCACHE_DIR = CACHE_DIR + "/pdfwork";

    // 学校类型 小学1 初中2
    public static final int PRIMARY = 1;
    public static final int MIDDLE = 2;

    //作业上传方式
    public static final int UPLOAD_TYPE_CAMERA = 0;                       //1：拍照上传
    public static final int UPLOAD_TYPE_SCAN = 1;                         //2：扫描上传
    public static final int UPLOAD_TYPE_MARKED = 2;                       //3：批阅上传
    public static final int UPLOAD_TYPE_MIXED = 3;                        //3：混合模式,老师网阅

    //分享通道ID
    public final static String WORK_SHARE_CHANNELID = "5K7M9";       //作业分享
    public final static String INVITE_SHARE_CHANNELID = "3B4C6";     //邀请好友

    //会员等级
    public final static int MEMBER_NORMAL = 0;
    public final static int MEMBER_VIP = 1;
    public final static int MEMBER_SVIP = 2;

    //套题类型 1: 每周，2：自定义,3:每月，4,考前
    public static final int PAPER_TYPE_ALL      = 0;
    public static final int PAPER_TYPE_WEEK     = 1;
    public static final int PAPER_TYPE_CUSTOM   = 2;
    public static final int PAPER_TYPE_MONTH    = 3;
    public static final int PAPER_TYPE_EXAM     = 4;

    //来源类型
    public static final String FROM_VARIANT = "Variant";        //变式训练
    public static final String FROM_REFINE = "refine";         //错题再练
    public static final String FROM_BROWER = "brower";         //错题浏览

}

package com.tsinghuabigdata.edu.ddmath.module.product.bean;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 我的世界——商品实体类
 * Created by Administrator on 2017/11/20.
 */

public class ProductBean implements Serializable {

    private static final long serialVersionUID = -6029203509767595752L;

    public static final int USED      = 1;
    public static final int NOT_USED  = 2;
    public static final int RECOMMEND = 1;

    public static final int IS_NOT_AUTOUSERNAME = 0;
    public static final int IS_AUTOUSERNAME     = 1;
    public static final int STARTING_TIME       = 1;
    public static final int EXCHANGE_TIME       = 2;
    public static final int USETIME_ENABLE      = 1;
    public static final int USETIME_NOT_ENABLE  = 2;
    public static final int NOT_ALLOW_REPEAT_PURCHASE  = 0;

    //是product就是商品 suite是套餐
    public static final String TYPE_PRODUCT = "product";
    public static final String TYPE_SUITE   = "suite";

    public static final String EXCLUSIVE_RECOMMEND_ID = "exclusive_recommend";
    public static final String PRODUCT_SET_ID         = "product_set";
    public static final String RAISE_RESOUCE          = "1";  //作业助教
    public static final String DAY_CLEAR              = "2";  //日清 月结 周练
    public static final String VACATION_WORK          = "3";  //假期作业
    public static final String SELECTION_SET          = "4";  //精选套题
    public static final String EXCLUSIVE_SET          = "5";  //专属套题
    public static final String FAMOUS_TEACHER         = "6";  //名师精讲
    public static final String OTHER                  = "99";  //其他

    //收费类型
    public static final int CHARGE_QUESTION_TIMES    = 1;       //1-按题次
    public static final int CHARGE_QUESTIONSET_TIMES = 2;       //2-按套题次
    public static final int CHARGE_SINGLE_PRODUCT    = 3;       //3-按增值包单次

    //商品使用时间类型
    //使用时间类型 1-按起始时间设置 2-按兑换起时长设置
    public static final int PRODUCT_USETYPE_ONLINE   = 1;       //1-按起始时间设置
    public static final int PRODUCT_USETYPE_EXCHANGE = 2;       //2-按兑换起时长设置

    //商品使用计时单位
    public static final int PRODUCT_USEUNIT_DAY   = 1;       //1-日
    public static final int PRODUCT_USEUNIT_WEEK  = 2;       //2-周
    public static final int PRODUCT_USEUNIT_MONTH = 3;       //3-月
    public static final int PRODUCT_USEUNIT_YEAR  = 4;       //4-年

    public static final int PRODUCT_BINDED_CONTENT = 1;       //1：绑定
    public static final int PRODUCT_UNBIND_CONTENT = 2;       //2：未绑定

    public static final int PRODUCT_SOURCE_MANUAL = 1;       //1：人工录入资源
    public static final int PRODUCT_SOURCE_AUTO   = 2;       //2：自组成

    //商品内容来源 1-准星人工录入 2-准星自组成
    private int type;

    //商品是否绑定内容 1：绑定 2：未绑定   仅仅针对 自主成的商品
    private int contentType;

    private int vipLevel;           //会员等级

    /**
     * 商品id
     */
    private String productId;
    /**
     * 商品名称
     */
    private String name;
    /**
     * 商品描述
     */
    private String description;
    /**
     * 商品图片路径
     */
    private String imagePath;
    /**
     * 商品介绍图片路径
     */
    private String introImagePath;
    /**
     * 商品在学生端APP首页的位置
     */
    private int    indexPosition;
    /**
     * 使用手册图片路径
     */
    private String userGuideImagePath;
    private int    userGuideImageWidth;
    private int    userGuideImageHeight;
    /**
     * 商品介绍图片宽度
     */
    private int    introImageWidth;
    /**
     * 商品介绍图片高度
     */
    private int    introImageHeight;
    /**
     * 分类id, 1是提分之源，其他类型全部我的宝藏
     */
    private String catalogId;
    /**
     * 商品名称是否自动加上用户姓名 0-否 1-是
     */
    private int    autoUserName;
    /**
     * 学生端APP显示开始时间
     */
    private long   displayStartTime;
    /**
     * 学生端APP显示结束时间
     */
    private long   displayEndTime;
    /**
     * 使用时间类型 1-按起始时间设置 2-按兑换起时长设置
     */
    private int    useTimeType;
    /**
     * 商品使用开始时间
     */
    private long   useStartTime;
    /**
     * 商品使用结束时间
     */
    private long   useEndTime;
    /**
     * 商品创建时间
     */
    private long   createTime;
    /**
     * 商品使用计时单位 1-日 2-周 3-月 4-年
     */
    private int    useTimeUnit;
    /**
     * 商品使用计时期限
     */
    private int    useTimeLimit;
    /**
     * 商品类型， 1 使用过的商品。 2 没有使用过的商品。  没有使用过的商品才会在专属推荐里面。
     */
    private int    productType;
    /**
     * 商品排名
     */
    private int    rank;


    /**
     * 类型 标识商品或套餐
     */
    private String                 productOrSuite;
    /**
     * 套餐ID
     */
    private String                 productSuiteId;
    /**
     * 套餐编号
     */
    private String                 productSuiteNum;
    /**
     * 套餐使用人数
     */
    private int                    usePeopleCount;
    /**
     * 套餐包含的商品列表
     */
    private ArrayList<ProductBean> productVoList;

    /**
     * 套餐里面的商品使用次数
     */
    private int useTimes;


    private int               chargeDdAmt;            //兑换学豆数量
    private int               originalChargeDdAmt;            //原价
    private long              chargeStartTime;
    private long              chargeEndTime;
    private int               useResidueTimes; //	剩余使用次数
    private int               chargeType; //收费类型 1-按题次 2-按套题次 3-按增值包单次

    //商品特权
    private int surplusUsageTimes;             //剩余使用次数
    private int hasUsageTimes;                  //已经使用的次数
    private int hasUseRight;                    //是否是可用商品，即已经购买了


    /**
     * 权限实体类
     */
    private List<PrivilegeVo> privilegeVos;
    /**
     * 权限（13.7新增）
     */
    private String privilegeId;
    /**
     * 剩余使用次数实体类
     */
    private List<UseTimesVo>  classUseTimesList;
    /**
     * 副标题
     */
    private String            subName;

    private int useTimeEnable;

    /*套餐购买次数*/
    private int purchaseCount;

    /**
     * 自定义字段——控制显示“该宝贝已失效”
     */
    private boolean showIneffectiveText;
    /**
     * 是否是默认推荐套餐，0不是，1表示是
     */
    private int     defaultRecommend;
    /*是否可以多次购买,1：默认可以，0：不可以*/
    private int allowRepeatPurchase;
    /*本用户购买次数*/
    private int curPurchaseCount;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getNameLength() {
        if (TextUtils.isEmpty(name)){
            return 0;
        }
        return name.length();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getIntroImagePath() {
        return introImagePath;
    }

    public void setIntroImagePath(String introImagePath) {
        this.introImagePath = introImagePath;
    }

    public int getIndexPosition() {
        return indexPosition;
    }

    public void setIndexPosition(int indexPosition) {
        this.indexPosition = indexPosition;
    }

    public String getUserGuideImagePath() {
        return userGuideImagePath;
    }

    public void setUserGuideImagePath(String userGuideImagePath) {
        this.userGuideImagePath = userGuideImagePath;
    }

    public int getUserGuideImageWidth() {
        return userGuideImageWidth;
    }

    public void setUserGuideImageWidth(int userGuideImageWidth) {
        this.userGuideImageWidth = userGuideImageWidth;
    }

    public int getUserGuideImageHeight() {
        return userGuideImageHeight;
    }

    public void setUserGuideImageHeight(int userGuideImageHeight) {
        this.userGuideImageHeight = userGuideImageHeight;
    }

    public int getIntroImageWidth() {
        return introImageWidth;
    }

    public void setIntroImageWidth(int introImageWidth) {
        this.introImageWidth = introImageWidth;
    }

    public int getIntroImageHeight() {
        return introImageHeight;
    }

    public void setIntroImageHeight(int introImageHeight) {
        this.introImageHeight = introImageHeight;
    }

    public String getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(String catalogId) {
        this.catalogId = catalogId;
    }

    public int getAutoUserName() {
        return autoUserName;
    }

    public void setAutoUserName(int autoUserName) {
        this.autoUserName = autoUserName;
    }

    public long getDisplayStartTime() {
        return displayStartTime;
    }

    public void setDisplayStartTime(long displayStartTime) {
        this.displayStartTime = displayStartTime;
    }

    public long getDisplayEndTime() {
        return displayEndTime;
    }

    public void setDisplayEndTime(long displayEndTime) {
        this.displayEndTime = displayEndTime;
    }

    public int getUseTimeType() {
        return useTimeType;
    }

    public void setUseTimeType(int useTimeType) {
        this.useTimeType = useTimeType;
    }

    public long getUseStartTime() {
        return useStartTime;
    }

    public void setUseStartTime(long useStartTime) {
        this.useStartTime = useStartTime;
    }

    public long getUseEndTime() {
        return useEndTime;
    }

    public void setUseEndTime(long useEndTime) {
        this.useEndTime = useEndTime;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getUseTimeUnit() {
        return useTimeUnit;
    }

    public void setUseTimeUnit(int useTimeUnit) {
        this.useTimeUnit = useTimeUnit;
    }

    public int getUseTimeLimit() {
        return useTimeLimit;
    }

    public void setUseTimeLimit(int useTimeLimit) {
        this.useTimeLimit = useTimeLimit;
    }

    public int getProductType() {
        return productType;
    }

    public void setProductType(int productType) {
        this.productType = productType;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public long getChargeStartTime() {
        return chargeStartTime;
    }

    public void setChargeStartTime(long chargeStartTime) {
        this.chargeStartTime = chargeStartTime;
    }

    public long getChargeEndTime() {
        return chargeEndTime;
    }

    public void setChargeEndTime(long chargeEndTime) {
        this.chargeEndTime = chargeEndTime;
    }

    public int getUseResidueTimes() {
        return useResidueTimes;
    }

    public void setUseResidueTimes(int useResidueTimes) {
        this.useResidueTimes = useResidueTimes;
    }

    public int getChargeType() {
        return chargeType;
    }

    public void setChargeType(int chargeType) {
        this.chargeType = chargeType;
    }

    public List<PrivilegeVo> getPrivilegeVos() {
        return privilegeVos;
    }

    public void setPrivilegeVos(List<PrivilegeVo> privilegeVos) {
        this.privilegeVos = privilegeVos;
    }

    public String getPrivilegeId() {
        if( !TextUtils.isEmpty(privilegeId) ){
            return privilegeId;
        }
        if( privilegeVos!=null && privilegeVos.size()>0 ){
            return privilegeVos.get(0).getId();
        }

        return "";
    }

    public void setPrivilegeId(String privilegeId) {
        this.privilegeId = privilegeId;
    }

    public List<UseTimesVo> getClassUseTimesList() {
        return classUseTimesList;
    }

    public void setClassUseTimesList(List<UseTimesVo> classUseTimesList) {
        this.classUseTimesList = classUseTimesList;
    }

    public String getSubName() {
        return subName;
    }

    public void setSubName(String subName) {
        this.subName = subName;
    }

    public int getChargeDdAmt() {
        return chargeDdAmt;
    }

    public void setChargeDdAmt(int chargeDdAmt) {
        this.chargeDdAmt = chargeDdAmt;
    }


    public int getOriginalChargeDdAmt() {
        return originalChargeDdAmt;
    }

    public void setOriginalChargeDdAmt(int originalChargeDdAmt) {
        this.originalChargeDdAmt = originalChargeDdAmt;
    }

    public int getUseTimeEnable() {
        return useTimeEnable;
    }

    public void setUseTimeEnable(int useTimeEnable) {
        this.useTimeEnable = useTimeEnable;
    }

    public boolean isShowIneffectiveText() {
        return showIneffectiveText;
    }

    public void setShowIneffectiveText(boolean showIneffectiveText) {
        this.showIneffectiveText = showIneffectiveText;
    }

    public int getContentType() {
        return contentType;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getProductOrSuite() {
        return productOrSuite;
    }

    public void setProductOrSuite(String productOrSuite) {
        this.productOrSuite = productOrSuite;
    }

    public String getProductSuiteId() {
        return productSuiteId;
    }

    public void setProductSuiteId(String productSuiteId) {
        this.productSuiteId = productSuiteId;
    }

    public String getProductSuiteNum() {
        return productSuiteNum;
    }

    public void setProductSuiteNum(String productSuiteNum) {
        this.productSuiteNum = productSuiteNum;
    }

    public int getUsePeopleCount() {
        return usePeopleCount;
    }

    public ArrayList<ProductBean> getProductVoList() {
        return productVoList;
    }

    public int getUseTimes() {
        return useTimes;
    }

    public int getPurchaseCount() {
        return purchaseCount;
    }

    public void setPurchaseCount(int purchaseCount) {
        this.purchaseCount = purchaseCount;
    }

    public int getDefaultRecommend() {
        return defaultRecommend;
    }

    public void setDefaultRecommend(int defaultRecommend) {
        this.defaultRecommend = defaultRecommend;
    }

    public int getAllowRepeatPurchase() {
        return allowRepeatPurchase;
    }

    public void setAllowRepeatPurchase(int allowRepeatPurchase) {
        this.allowRepeatPurchase = allowRepeatPurchase;
    }

    public int getCurPurchaseCount() {
        return curPurchaseCount;
    }

    public void setCurPurchaseCount(int curPurchaseCount) {
        this.curPurchaseCount = curPurchaseCount;
    }

    public int getSurplusUsageTimes() {
        return surplusUsageTimes;
    }

    public void setSurplusUsageTimes(int surplusUsageTimes) {
        this.surplusUsageTimes = surplusUsageTimes;
    }

    public int getHasUsageTimes() {
        return hasUsageTimes;
    }

    public void setHasUsageTimes(int hasUsageTimes) {
        this.hasUsageTimes = hasUsageTimes;
    }

    public int getHasUseRight() {
        return hasUseRight;
    }

    public void setHasUseRight(int hasUseRight) {
        this.hasUseRight = hasUseRight;
    }

    public int getVipLevel() {
        return vipLevel;
    }
}

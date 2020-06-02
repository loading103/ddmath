package com.tsinghuabigdata.edu.ddmath.module.product;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;

import com.tsinghuabigdata.edu.ddmath.MVPModel.FamousTeacherModel;
import com.tsinghuabigdata.edu.ddmath.MVPModel.ProductModel;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.MVPModel.UserCenterModel;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.bean.MyTutorClassInfo;
import com.tsinghuabigdata.edu.ddmath.bean.StudyBean;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.module.famousteacher.bean.FamousProductBean;
import com.tsinghuabigdata.edu.ddmath.module.login.model.LoginModel;
import com.tsinghuabigdata.edu.ddmath.module.mystudybean.BuySuiteActivity;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.ProductBean;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.ProductUseTimesBean;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.UseTimesVo;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.DateUtils;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.String.format;


/**
 * 商品相关功能
 */

public class ProductUtil {

    //没有权限，购买套餐时 不同来源提示语不一样
    public static final int FROM_NORMAL = 0;        //提升栏目
    public static final int FROM_VIDEO = 1;         //视频微课
    public static final int FROM_CUSTOM = 2;        //定制学


    //查找对应班级的剩余使用次数
    static int getLeaveUseTimes(List<UseTimesVo> list) {
        int times = 0;

        MyTutorClassInfo classInfo = AccountUtils.getCurrentClassInfo();
        if (list == null || classInfo == null)
            return times;

        for (UseTimesVo timesVo : list) {
            if (classInfo.getClassId().equals(timesVo.getClassId())) {
                if (timesVo.getUseTimes() == -1)
                    return -1;
                times += timesVo.getUseTimes();
            }
        }
        return times;
    }

    //更新充值返现数据
    //    public static void updateRechargeCashback() {
    //        if (AccountUtils.getLoginUser() != null) {
    //            ProductModel productModel = new ProductModel();
    //            productModel.getRechargeCashbackRecommend(null);
    //        }
    //    }

    //更新用户注册赠送数据
    public static void updateRegisterReward() {
        if (AccountUtils.getLoginUser() != null) {
            LoginModel loginModel = new LoginModel();
            loginModel.getRegisteReward(null);
        }
    }

    //检查是的商品是不是有使用次数
    public static boolean checkProductUseTimes(String productId, String privilegeId, RequestListener<List<ProductUseTimesBean>> requestListener) {

        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        MyTutorClassInfo classInfo = AccountUtils.getCurrentClassInfo();
        if (detailinfo == null || classInfo == null || TextUtils.isEmpty(privilegeId))
            return false;

        ProductModel productModel = new ProductModel();
        productModel.getProductUseTimes(detailinfo.getStudentId(), classInfo.getClassId(), privilegeId, productId, requestListener);
        return true;
    }

    //更新学豆
    public static void updateLearnDou(RequestListener<StudyBean> requestListener) {
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if (detailinfo == null){
            detailinfo = AccountUtils.getParentUserDetailinfo();
            if( detailinfo == null ) return;
        }
        new UserCenterModel().queryMyStudyBean(detailinfo.getStudentId(), requestListener);
    }

    //提示用户兑换套题
    private static void showExchangePracticeDialog(final Context context, final int fromFamous, final String provilegeId, int freeCount, String provilegeName, final DialogInterface.OnClickListener enterListener, /*final DialogInterface.OnClickListener cancelListener,*/ final AtomicBoolean atomicBoolean ) {

        //StudyBean studyBean = AccountUtils.getStudyBean();
        if (context == null) {
            if(atomicBoolean!=null) atomicBoolean.set( false );
            AppLog.d("  context = " + null  );
            //if( cancelListener!=null ) cancelListener.onClick(null,0);
            return;
        }

        //无限次, 或者 有使用次数 直接开始兑换
        if (freeCount < 0 ) {
            if(atomicBoolean!=null) atomicBoolean.set( false );
            enterListener.onClick(null, 0);
        }
        //
        else if (freeCount > 0) {
            //提示 用户免费次数兑换一次 机会
            String data = String.format(Locale.getDefault(),"已使用1次%s的学习特权,还剩%d次", provilegeName,freeCount-1 );

            //直接 消耗次数
            if(atomicBoolean!=null) atomicBoolean.set( false );
            enterListener.onClick(null, 0);
            ToastUtils.showToastCenter( context, data );
        }  else {  //提示用户直接购买

            UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
            if( detailinfo!=null && !detailinfo.enableBuySuite() ){     //不允许买,精品套题允许学生充值购买
                ToastUtils.show( context, context.getResources().getString(R.string.tj_nobuy_tips) );
                if(atomicBoolean!=null) atomicBoolean.set( false );
            }else{  //跳转到购买界面
                //定制学提示不一样
                String data = "你还没有该套题的学习特权，去购买套餐开始愉快的学习吧～";
                if( FROM_VIDEO == fromFamous ){
                    data = "你还没有该课程的学习特权，去购买套餐开始愉快地学习吧～";
                }else if( FROM_CUSTOM == fromFamous ){
                    data = "你的定制学学习特权已失效，去购买套餐开始愉快地学习吧～";
                }
                String btnNmae = "购买套餐";

                //充值优惠提示
                AlertManager.showCustomImageBtnDialog(context, data, btnNmae, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //去充值 还要返回，，刷新剩余学豆
                        if(atomicBoolean!=null) atomicBoolean.set( false );
                        gotoStudyBean(context, provilegeId, fromFamous);        // : 2018/12/11 跳转到哪里
                    }
                }, new DialogInterface.OnClickListener(){   //点击CloseBtn
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(atomicBoolean!=null) atomicBoolean.set( false );
                    }
                });
            }
        }
    }

    //跳转到我的学豆
    private static void gotoStudyBean(Context context, String provilegeId, int from) {
        if( FROM_CUSTOM == from ){
            BuySuiteActivity.openActivityForSuite( context, AccountUtils.mScorePlanBean!=null?AccountUtils.mScorePlanBean.getCustomizedPrivilegeIds():"");
        }else{
            BuySuiteActivity.startBuySuiteActivity( context, 0, provilegeId);
            //ProductUtil.goToSuite(provilegeId);
            //context.sendBroadcast(new Intent(RoboActivity.ACTION));
        }
    }

    //根据排名对商品进行排序
    /*public*/
    static void rankExclusiveRecommend(List<ProductBean> listExclusiveRecommend) {
        for (int i = 0; i < listExclusiveRecommend.size(); i++) {
            ProductBean vo = listExclusiveRecommend.get(i);
            if (vo.getRank() == 0) {
                vo.setRank(Integer.MAX_VALUE);
            }
        }
        Collections.sort(listExclusiveRecommend, new Comparator<ProductBean>() {
            @Override
            public int compare(ProductBean lhs, ProductBean rhs) {
                return lhs.getRank() - rhs.getRank();
            }
        });
    }

    //商品是否可用
    public static boolean vailable(ProductBean productBean, String classId) {
        List<UseTimesVo> list = productBean.getClassUseTimesList();
        if (list == null || list.size() == 0) {
            return false;
        }
        for (int i = 0; i < list.size(); i++) {
            UseTimesVo vo = list.get(i);
            if (classId.equals(vo.getClassId())) {
                return vo.getUseTimes() > 0 || vo.getUseTimes() == -1;
            }
        }
        return false;
    }

    //商品是否有效
    public static boolean effective(ProductBean item, String classId) {
        long mills = System.currentTimeMillis();
        boolean inDisplayTime = mills > item.getDisplayStartTime() && mills < item.getDisplayEndTime();
        //        LogUtils.i("item getName="+item.getName()+" inDisplayTime="+inDisplayTime+" inChargeTime="+inChargeTime);
        return inDisplayTime || vailable(item, classId);
    }

    //商品是否在显示时间内
    /*public*/ static boolean inDisplayTime(ProductBean item) {
        long mills = System.currentTimeMillis();
        return mills > item.getDisplayStartTime() && mills < item.getDisplayEndTime();
    }

    //检查商品是否过期
    /*public*/
    static boolean ckeckProductOverdue(Context context, ProductBean productBean) {
        //是否已过期
        long currTime = System.currentTimeMillis();

        //未上架
        if (currTime < productBean.getDisplayStartTime()) {
            ToastUtils.show(context, "商品未上架，不能再进行兑换。");
            return true;
        }
        //已下架
        else if (currTime > productBean.getDisplayEndTime()) {
            ToastUtils.show(context, "商品已下架，不能再进行兑换。");
            return true;
        }

        //未到兑换时间
        else if (currTime < productBean.getChargeStartTime()) {
            ToastUtils.show(context, format(Locale.getDefault(), "兑换还未开始，请在%s之后开始兑换", DateUtils.format(productBean.getChargeStartTime(), DateUtils.FORMAT_DATA_TIME)));
            return true;
        }
        //已过兑换期
        else if (currTime > productBean.getChargeEndTime()) {
            ToastUtils.show(context, "商品已过期，不能再进行兑换。");
            return true;
        }

        //检查商品是否绑定了内容  仅仅人工录入的资源需要绑定内容
        else if (ProductBean.PRODUCT_SOURCE_MANUAL == productBean.getType() && ProductBean.PRODUCT_UNBIND_CONTENT == productBean.getContentType()) {
            ToastUtils.show(context, "该宝贝还未绑定内容，请稍后再试或者联系客服询问。");
            return true;
        }

        return false;
    }

    //检查套餐是否过期
    public static boolean ckeckSuiteOverdue(Context context, ProductBean productBean) {
        //是否已过期
        long currTime = System.currentTimeMillis();

        //未上架
        if (currTime < productBean.getDisplayStartTime()) {
            ToastUtils.show(context, "套餐未上架，不能再进行购买。");
            return true;
        }
        //已下架
        else if (currTime > productBean.getDisplayEndTime()) {
            ToastUtils.show(context, "套餐已下架，不能再进行购买。");
            return true;
        }

        //未到兑换时间
        else if (currTime < productBean.getChargeStartTime()) {
            ToastUtils.show(context, format(Locale.getDefault(), "购买还未开始，请在%s之后开始购买", DateUtils.format(productBean.getChargeStartTime(), DateUtils.FORMAT_DATA_TIME)));
            return true;
        }
        //已过兑换期
        else if (currTime > productBean.getChargeEndTime()) {
            ToastUtils.show(context, "套餐已过期，不能再进行购买。");
            return true;
        }

        //检查商品是否绑定了内容  仅仅人工录入的资源需要绑定内容
        else if (ProductBean.PRODUCT_SOURCE_MANUAL == productBean.getType() && ProductBean.PRODUCT_UNBIND_CONTENT == productBean.getContentType()) {
            ToastUtils.show(context, "该宝贝还未绑定内容，请稍后再试或者联系客服询问。");
            return true;
        }

        return false;
    }

    //检查套餐是否重复购买
    public static boolean ckeckAllowRepeatBuy(Context context, ProductBean productBean) {
        if (productBean.getAllowRepeatPurchase() == ProductBean.NOT_ALLOW_REPEAT_PURCHASE && productBean.getCurPurchaseCount() > 0) {
            ToastUtils.show(context, "该套餐只能购买一次");
            return false;
        }
        return true;
    }

    //得到商品使用期限
    public static String getProductUseTime(ProductBean bean) {
        //使用期限
        String data = "";
        //1-按起始时间设置
        if (ProductBean.PRODUCT_USETYPE_ONLINE == bean.getUseTimeType()) {
            data = format(Locale.getDefault(), "使用期限：%s 至 %s", DateUtils.format(bean.getUseStartTime(), DateUtils.FORMAT_DATA_TIME), DateUtils.format(bean.getUseEndTime(), DateUtils.FORMAT_DATA_TIME));
        }
        //2-按兑换起时长设置
        else if (ProductBean.PRODUCT_USETYPE_EXCHANGE == bean.getUseTimeType()) {
            String[] ta = {"日", "周", "月", "年"};
            int index = bean.getUseTimeUnit();
            if (index <= 0 || index > ta.length)
                index = 1;
            data = format(Locale.getDefault(), "使用期限：从购买时间起%d%s内有效", bean.getUseTimeLimit(), ta[index - 1]);
        }
        return data;
    }

    //得到使用期限
    public static String getUseTime(ProductBean bean) {
        //使用期限
        String data = "";
        //1-按起始时间设置
        if (ProductBean.PRODUCT_USETYPE_ONLINE == bean.getUseTimeType()) {
            data = format(Locale.getDefault(), "使用期限：%s-%s", DateUtils.format(bean.getUseStartTime(), DateUtils.FORMAT_DATA_PRODUCT), DateUtils.format(bean.getUseEndTime(), DateUtils.FORMAT_DATA_PRODUCT));
        }
        //2-按兑换起时长设置
        else if (ProductBean.PRODUCT_USETYPE_EXCHANGE == bean.getUseTimeType()) {
            String[] ta = {"日", "周", "月", "年"};
            int index = bean.getUseTimeUnit();
            if (index <= 0 || index > ta.length)
                index = 1;
            data = format(Locale.getDefault(), "使用期限：从购买时间起%d%s内有效", bean.getUseTimeLimit(), ta[index - 1]);
        }
        return data;
    }

    //得到套餐使用期限（我要购买界面）
    public static String getSuiteUseTime(ProductBean bean) {
        //使用期限
        String data = "";
        //1-按起始时间设置
        if (ProductBean.PRODUCT_USETYPE_ONLINE == bean.getUseTimeType()) {
            data = format(Locale.getDefault(), "%s-%s", DateUtils.format(bean.getUseStartTime(), DateUtils.FORMAT_DATA_PRODUCT), DateUtils.format(bean.getUseEndTime(), DateUtils.FORMAT_DATA_PRODUCT));
        }
        //2-按兑换起时长设置
        else if (ProductBean.PRODUCT_USETYPE_EXCHANGE == bean.getUseTimeType()) {
            String[] ta = {"日", "周", "月", "年"};
            int index = bean.getUseTimeUnit();
            if (index <= 0 || index > ta.length)
                index = 1;
            data = format(Locale.getDefault(), "从购买之日起%d%s内有效", bean.getUseTimeLimit(), ta[index - 1]);
        }
        return data;
    }
    //----------------------------------------------------------------------------------------------------------
    //微课
    public static void videoCheckPermissionAndExchange(final Context context, final String videoId, final AtomicBoolean atomicBoolean, final int from, final ProductCallBack callBack ) {

        final UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        final MyTutorClassInfo classInfo= AccountUtils.getCurrentClassInfo();
        if( detailinfo == null || classInfo == null ){
            ToastUtils.show( context, "请重新登录");
            atomicBoolean.set( false );
            return;
        }

        //先得到商品信息
        new FamousTeacherModel().getFamousProductList( detailinfo.getStudentId(), classInfo.getSchoolId(), new RequestListener<List<FamousProductBean>>() {

            @Override
            public void onSuccess(List<FamousProductBean> res) {
                if (res == null || res.size() == 0 || res.get(0) == null) {
                    ToastUtils.show(context, R.string.no_product);
                    atomicBoolean.set( false );
                    return;
                }
                final FamousProductBean bean = res.get(0);
                int freeCount = bean.getFreeUseTimes();

                //执行商品兑换检查
                ProductUtil.showExchangePracticeDialog(context, from, AppConst.PRIVILEGE_SYNC_CLASSROOM, freeCount, "同步微课", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //点击了确认， 开始兑换
                        ProductUtil.exchangePracticeProduct( context, bean.getProductId(), AppConst.PRIVILEGE_SYNC_CLASSROOM, videoId, atomicBoolean, callBack );
                    }
                }, /*null,*/ atomicBoolean );    //取消处理， 和多次点击处理
            }

            @Override
            public void onFail(HttpResponse<List<FamousProductBean>> response, Exception ex) {
                ToastUtils.showShort( context,R.string.query_times_failure);
                atomicBoolean.set( false );
            }
        });
    }

    //----------------------------------------------------------------------------------------------------------
    public interface ProductCallBack{
        void onSuccess();
        //void onFail();
    }

    //完成套题 使用次数检查和有次数的自动兑换流程
    public static void productCheckPermissionAndExchange(final Context context, final String productId, final String provilegeId, final String recordId, final String provilegeName, final AtomicBoolean atomicBoolean, final int from, final ProductCallBack callBack ) {
        checkProductUsePermission( context, productId, provilegeId, provilegeName, from, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //点击了确认， 开始兑换
                exchangePracticeProduct( context, productId, provilegeId, recordId, atomicBoolean, callBack );
            }
        }, atomicBoolean );
    }

    //兑换套题商品,不区分用免费使用权 还是 学豆，服务器后台处理
    public static void exchangePracticeProduct(final Context context, final String productId, final String privilegeId, final String recordId, final AtomicBoolean atomicBoolean, final ProductCallBack callBack ){

        final LoginInfo loginInfo = AccountUtils.getLoginUser();
        final UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        final MyTutorClassInfo classInfo= AccountUtils.getCurrentClassInfo();
        if( detailinfo == null || loginInfo == null || classInfo == null ){
            ToastUtils.show( context, "请重新登录");
            atomicBoolean.set( false );
            //if( callBack!=null ) callBack.onFail();
            return;
        }

        //开始兑换
        new ProductModel().exchangePracticeProduct( detailinfo.getStudentId(), classInfo.getClassId(), productId, privilegeId, recordId, new RequestListener() {
            @Override
            public void onSuccess(Object res) {
                if( callBack!=null ) callBack.onSuccess();
                atomicBoolean.set( false );
            }

            @Override
            public void onFail(HttpResponse response, Exception ex) {
                String data = "兑换失败，再来一次吧！";
                AlertManager.showCustomImageBtnDialog( context, data, "再次发起兑换",  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //再次购买
                        exchangePracticeProduct( context, productId, privilegeId, recordId, atomicBoolean, callBack );
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //放弃兑换
                        //if( callBack!=null ) callBack.onFail();
                        atomicBoolean.set( false );
                    }
                } );
            }
        });
    }
    //------------------------------------------------------------------------------------------------------------------------------
    //检查商品是否有使用次数
    private static void checkProductUsePermission(final Context context, final String productId, final String provilegeId, final String usetimeText, final int from, final DialogInterface.OnClickListener enterListener, final AtomicBoolean atomicBoolean ) {

        //再检查有没有使用次数
        boolean succ = ProductUtil.checkProductUseTimes(productId, provilegeId, new RequestListener<List<ProductUseTimesBean>>() {

            @Override
            public void onSuccess(List<ProductUseTimesBean> list) {

                if (list == null || list.size() == 0 || list.get(0) == null) {
                    ToastUtils.show(context, R.string.relogin);
                    if(atomicBoolean!=null) atomicBoolean.set( false );
                    return;
                }
                int freeCount = list.get(0).getUseTimes();
                ProductUtil.showExchangePracticeDialog(context, from, provilegeId, freeCount, String.format(Locale.getDefault(), usetimeText, freeCount),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(atomicBoolean!=null) atomicBoolean.set( false );
                                //点击了确认， 开始兑换
                                enterListener.onClick(null, 1);
                            }
                        }, /*null,*/ atomicBoolean );
            }

            @Override
            public void onFail(HttpResponse<List<ProductUseTimesBean>> response, Exception ex) {
                if(atomicBoolean!=null) atomicBoolean.set( false );
                AlertManager.showErrorInfo(context, ex);     //提示没有使用权
            }
        });
        if (!succ){
            if(atomicBoolean!=null) atomicBoolean.set( false );
            ToastUtils.show(context, R.string.relogin);
        }
    }

    //获取商品价格
    public static String getPrice(ProductBean item) {
        int chargeDdAmt = item.getChargeDdAmt();
        if (chargeDdAmt % 10 == 0) {
            return chargeDdAmt / 10 + "元";
        }
        float pr = (float) chargeDdAmt / 10;
        return pr + "元";
    }

    //根据学豆折算价格
    public static String getPrice(int bean) {
        if (bean % 10 == 0) {
            return bean / 10 + "元";
        }
        float pr = (float) bean / 10;
        return pr + "元";
    }

    public static int getPrivilegePosition(List<ProductBean> tempSuiteList, String privilegeId) {
        if (TextUtils.isEmpty(privilegeId) || tempSuiteList == null || tempSuiteList.size() < 1) {
            return -1;
        }
        List<ProductBean> sortSuiteList = new ArrayList<>();
        sortSuiteList.addAll(tempSuiteList);
        Collections.sort(sortSuiteList, new Comparator<ProductBean>() {
            @Override
            public int compare(ProductBean lhs, ProductBean rhs) {
                return rhs.getChargeDdAmt() - lhs.getChargeDdAmt();
            }
        });
        String speSuiteId = getSpecificSuiteId(sortSuiteList, privilegeId);
        if (TextUtils.isEmpty(speSuiteId)) {
            return -1;
        }
        for (int i = 0; i < tempSuiteList.size(); i++) {
            String suiteId = tempSuiteList.get(i).getProductSuiteId();
            if (!TextUtils.isEmpty(suiteId) && suiteId.equals(speSuiteId)) {
                return i;
            }
        }
        return -1;
    }

    private static String getSpecificSuiteId(List<ProductBean> sortSuiteList, String privilegeId) {

        //先在推荐里面进行
        for (int i = 0; i < sortSuiteList.size(); i++) {
            ProductBean suiteBean = sortSuiteList.get(i);
            if( suiteBean.getDefaultRecommend() != 1 )
                continue;
            ArrayList<ProductBean> productVoList = suiteBean.getProductVoList();
            for (int j = 0; j < productVoList.size(); j++) {
                ProductBean vo = productVoList.get(j);
                if (!TextUtils.isEmpty(vo.getPrivilegeId()) && vo.getPrivilegeId().equals(privilegeId)) {
                    return suiteBean.getProductSuiteId();
                }
            }
        }

        //再在全部里面进行
        for (int i = 0; i < sortSuiteList.size(); i++) {
            ProductBean suiteBean = sortSuiteList.get(i);
            ArrayList<ProductBean> productVoList = suiteBean.getProductVoList();
            for (int j = 0; j < productVoList.size(); j++) {
                ProductBean vo = productVoList.get(j);
                if (!TextUtils.isEmpty(vo.getPrivilegeId()) && vo.getPrivilegeId().equals(privilegeId)) {
                    return suiteBean.getProductSuiteId();
                }
            }
        }
        return null;
    }


    //跳转到包含指定类型商品的套餐
//    private static void goToSuite(String privilegeId) {
//        //GlobalData.setExpandPrivilege(privilegeId);
//        EventBus.getDefault().post(new JumpSuiteEvent(privilegeId));
//    }

    public static List<ProductBean> rankSuite(List<ProductBean> allList) {
        List<ProductBean> zsRankList = new ArrayList<>();
        List<ProductBean> zsNorankList = new ArrayList<>();
        List<ProductBean> recList = new ArrayList<>();
        List<ProductBean> otherList = new ArrayList<>();
        for (int i = 0; i < allList.size(); i++) {
            ProductBean vo = allList.get(i);
            if (!ProductUtil.inDisplayTime(vo)) {
                continue;
            }
            if (vo.getDefaultRecommend() == ProductBean.RECOMMEND) {
                if (vo.getRank() > 0) {
                    zsRankList.add(vo);
                } else {
                    zsNorankList.add(vo);
                }
            } else if (vo.getRank() > 0) {
                recList.add(vo);
            } else {
                otherList.add(vo);
            }
        }
        sortSuite(zsRankList);
        sortSuite(recList);
        List<ProductBean> tempSuiteList = new ArrayList<>();
        tempSuiteList.addAll(zsRankList);
        tempSuiteList.addAll(zsNorankList);
        tempSuiteList.addAll(recList);
        tempSuiteList.addAll(otherList);
        return tempSuiteList;
    }

    private static void sortSuite(List<ProductBean> recList) {
        if (recList.size() > 1) {
            Collections.sort(recList, new Comparator<ProductBean>() {
                @Override
                public int compare(ProductBean lhs, ProductBean rhs) {
                    return lhs.getRank() - rhs.getRank();
                }
            });
        }
    }
}

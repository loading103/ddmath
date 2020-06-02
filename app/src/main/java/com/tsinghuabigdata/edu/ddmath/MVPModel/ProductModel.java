package com.tsinghuabigdata.edu.ddmath.MVPModel;

import com.tsinghuabigdata.edu.ddmath.commons.http.AppAsyncTask;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.event.UpdateStudybeanEvent;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.FreeUseTimesBean;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.ProductBean;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.ProductCataBean;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.ProductGuideBean;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.ProductListBean;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.ProductPrivilegeBean;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.ProductUseTimesBean;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.RechargeCashbackBean;
import com.tsinghuabigdata.edu.ddmath.requestHandler.ProductService;
import com.tsinghuabigdata.edu.ddmath.requestHandler.requestImpl.ProductServiceImpl;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * 商品
 */

public class ProductModel {

    private ProductService mProductService = new ProductServiceImpl();

    public void getProductList(String studentId, RequestListener requestListener) {
        ProductListTask task = new ProductListTask(requestListener);
        task.executeMulti(studentId);
    }

    public void getSchoolProductList(String studentId, String schoolId, String classId, RequestListener requestListener) {
        SchoolProductListTask task = new SchoolProductListTask(requestListener);
        task.execute(studentId,schoolId,classId);
    }

    public void getSuiteList(String studentId, String schoolId, String classId, String schoolIds, RequestListener requestListener) {
        SuiteListTask task = new SuiteListTask(requestListener);
        task.execute(studentId,schoolId,classId, schoolIds);
    }

    public void getProductGroupList(String studentId, String schoolId, String classId, RequestListener requestListener) {
        GroupListTask task = new GroupListTask(requestListener);
        task.executeMulti(studentId,schoolId,classId);
    }

    //商品引导图
    public void getProductGuide(String privilegeId, RequestListener requestListener) {
        ProductGuideTask task = new ProductGuideTask(requestListener);
        task.executeMulti(privilegeId);
    }

    //商品详情 通过PrivilegeId查询
    public void getProductDetail(String schoolId, String classId, String studentId, String privilegeId, RequestListener requestListener) {
        ProductDetailTask task = new ProductDetailTask(requestListener);
        task.executeMulti(schoolId, classId, studentId, privilegeId, "0" );
    }
    public void getProductDetailByProductId(String schoolId, String classId, String studentId, String productId, RequestListener requestListener) {
        ProductDetailTask task = new ProductDetailTask(requestListener);
        task.executeMulti(schoolId, classId, studentId, productId, "1" );
    }

    //商品兑换次数列表
    /*public void getProductExchangeList(String studentId, String productId, RequestListener requestListener) {
        ProductExchangeListTask task = new ProductExchangeListTask(requestListener);
        task.executeMulti(studentId, productId );
    }*/

    //兑换商品的使用次数
    public void exchangeProductUseTimes( String studentId, String classId, String productId, String privilegeId, int times, RequestListener requestListener ){
        ProductExchangeTask task = new ProductExchangeTask(requestListener);
        task.executeMulti( studentId, classId, productId, privilegeId, String.valueOf(times), "" );
    }

    //兑换单独的商品
    public void exchangeSingleProduct( String studentId, String classId, String productId, RequestListener requestListener ){
        ProductExchangeTask task = new ProductExchangeTask(requestListener);
        task.executeMulti(studentId, classId, productId, "", "0", "" );
    }

    //兑换套题
    public void exchangePracticeProduct( String studentId, String classId, String productId, String privilegeId, String recordId, RequestListener requestListener ){
        ProductExchangeTask task = new ProductExchangeTask(requestListener);
        task.executeMulti(studentId, classId, productId, privilegeId, "1", recordId );
    }

    //查询商品使用次数，多个商品用，分割
    public void getProductUseTimes( String studentId, String classId, String privilegeIds, String productId, RequestListener requestListener ){
        QueryProductUseTimesTask task = new QueryProductUseTimesTask(requestListener);
        task.executeMulti(studentId, classId, privilegeIds, productId );
    }
    public void getProductUseTimesByProduct( String studentId, String classId, String productId, RequestListener requestListener ){
        QueryProductUseTimesByProductIdTask task = new QueryProductUseTimesByProductIdTask(requestListener);
        task.executeMulti(studentId, classId, productId );
    }

    //查询商品使用次数，多个商品用，分割
    public void getRechargeCashbackRecommend( RequestListener requestListener ){
        QueryRechargeRecommendTask task = new QueryRechargeRecommendTask(requestListener);
        task.executeMulti();
    }

    //兑换商品套餐
    public void exchangeProductSuite( String classId, String studentId, String suiteId, String accountId, RequestListener requestListener ){
        ProductSuiteExchangeTask task = new ProductSuiteExchangeTask(requestListener);
        task.executeMulti( classId, studentId, suiteId, accountId );
    }

    //商品套餐详情
    public void queryProductSuitedetail( String suiteId, RequestListener requestListener ){
        ProductSuiteDetailTask task = new ProductSuiteDetailTask(requestListener);
        task.executeMulti( suiteId );
    }

    //---------------------------------------------------------------------------------------------------------
    /**
     * 查询商品列表
     */
    private class ProductListTask extends AppAsyncTask<String, Void, List<ProductListBean>> {

        private RequestListener reqListener;

        ProductListTask(RequestListener requestListener) {
            reqListener = requestListener;
        }


        @Override
        protected List<ProductListBean> doExecute(String... params) throws Exception {
            String studentId = params[0];
            return mProductService.getProductList(studentId);
        }

        @Override
        protected void onResult(List<ProductListBean> list) {
            if(reqListener!=null)reqListener.onSuccess(list);
        }

        @Override
        protected void onFailure(HttpResponse<List<ProductListBean>> response, Exception ex) {
            if(reqListener!=null)reqListener.onFail(response, ex);
        }

    }

    /**
     * 查询学校商品列表
     */
    private class SchoolProductListTask extends AppAsyncTask<String, Void, List<ProductBean>> {

        private RequestListener reqListener;

        SchoolProductListTask(RequestListener requestListener) {
            reqListener = requestListener;
        }


        @Override
        protected List<ProductBean> doExecute(String... params) throws Exception {
            String studentId = params[0];
            String schoolId = params[1];
            String classId = params[2];
            return mProductService.getSchoolIProductList(studentId,schoolId,classId);
        }

        @Override
        protected void onResult(List<ProductBean> list) {
            if(reqListener!=null)reqListener.onSuccess(list);
        }

        @Override
        protected void onFailure(HttpResponse<List<ProductBean>> response, Exception ex) {
            if(reqListener!=null)reqListener.onFail(response, ex);
        }

    }

    /**
     * App精品套餐列表查询 13.0新增
     */
    private class SuiteListTask extends AppAsyncTask<String, Void, List<ProductBean>> {

        private RequestListener reqListener;

        SuiteListTask(RequestListener requestListener) {
            reqListener = requestListener;
        }


        @Override
        protected List<ProductBean> doExecute(String... params) throws Exception {
            String studentId = params[0];
            String schoolId = params[1];
            String classId = params[2];
            String schoolIds = params[3];
            return mProductService.getProductSuiteList(studentId,schoolId,classId, schoolIds);
        }

        @Override
        protected void onResult(List<ProductBean> list) {
            if(reqListener!=null)reqListener.onSuccess(list);
        }

        @Override
        protected void onFailure(HttpResponse<List<ProductBean>> response, Exception ex) {
            if(reqListener!=null)reqListener.onFail(response, ex);
        }

    }

    /**
     * App精品套餐列表查询 13.0新增
     */
    private class GroupListTask extends AppAsyncTask<String, Void, List<ProductCataBean>> {

        private RequestListener reqListener;

        GroupListTask(RequestListener requestListener) {
            reqListener = requestListener;
        }


        @Override
        protected List<ProductCataBean> doExecute(String... params) throws Exception {
            String studentId = params[0];
            String schoolId = params[1];
            String classId = params[2];
            return mProductService.getProductGroupList(studentId,schoolId,classId);
        }

        @Override
        protected void onResult(List<ProductCataBean> list) {
            if(reqListener!=null)reqListener.onSuccess(list);
        }

        @Override
        protected void onFailure(HttpResponse<List<ProductCataBean>> response, Exception ex) {
            if(reqListener!=null)reqListener.onFail(response, ex);
        }

    }

    /**
     * 查询商品使用手册图片路径
     */
    private class ProductGuideTask extends AppAsyncTask<String, Void, List<ProductGuideBean>> {

        private RequestListener reqListener;

        ProductGuideTask(RequestListener requestListener) {
            reqListener = requestListener;
        }


        @Override
        protected List<ProductGuideBean> doExecute(String... params) throws Exception {
            String privilegeId = params[0];
            return mProductService.getProductGuide(privilegeId);
        }

        @Override
        protected void onResult(List<ProductGuideBean> list) {
            if(reqListener!=null)reqListener.onSuccess(list);
        }

        @Override
        protected void onFailure(HttpResponse<List<ProductGuideBean>> response, Exception ex) {
            if(reqListener!=null)reqListener.onFail(response, ex);
        }

    }


    /**
     * 查询商品详情
     */
    private class ProductDetailTask extends AppAsyncTask<String, Void, ProductPrivilegeBean> {

        private RequestListener reqListener;

        ProductDetailTask(RequestListener requestListener) {
            reqListener = requestListener;
        }


        @Override
        protected ProductPrivilegeBean doExecute(String... params) throws Exception {
            String schoolId = params[0];
            String classId = params[1];
            String studentId = params[2];
            String privilegeId = params[3];
            int queryType = Integer.valueOf(params[4]);
            return mProductService.getProductDetail( studentId, privilegeId, schoolId, classId, queryType );
        }

        @Override
        protected void onResult(ProductPrivilegeBean bean) {
            if(reqListener!=null)reqListener.onSuccess(bean);
        }

        @Override
        protected void onFailure(HttpResponse<ProductPrivilegeBean> response, Exception ex) {
            if(reqListener!=null)reqListener.onFail(response, ex);
        }

    }

    /**
     * 兑换商品
     */
    private class ProductExchangeTask extends AppAsyncTask<String, Void, Boolean > {

        private RequestListener reqListener;

        ProductExchangeTask(RequestListener requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected Boolean doExecute(String... params) throws Exception {
            String studentId = params[0];
            String classId   = params[1];
            String productId = params[2];
            String privilegeId = params[3];
            int times = Integer.parseInt( params[4] );
            String recordId = params[5];
            return mProductService.exchangeProduct( studentId, classId, productId, privilegeId, times, recordId );
        }

        @Override
        protected void onResult(Boolean success) {
            if(reqListener!=null)reqListener.onSuccess(success);

            //触发学豆更新
            EventBus.getDefault().post(new UpdateStudybeanEvent());
            //ProductUtil.updateLearnDou( null );
        }

        @Override
        protected void onFailure(HttpResponse<Boolean> response, Exception ex) {
            if(reqListener!=null)reqListener.onFail(response, ex);
        }
    }

    /**
     * 查询已兑换的商品次数
     */
    private class QueryProductUseTimesTask extends AppAsyncTask<String, Void, List<ProductUseTimesBean> > {

        private RequestListener reqListener;

        QueryProductUseTimesTask(RequestListener requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected List<ProductUseTimesBean> doExecute(String... params) throws Exception {
            String studentId = params[0];
            String classId   = params[1];
            String privilegeIds = params[2];
            String productId  = params[3];
            return mProductService.getProductUseTimes(studentId,classId,privilegeIds,productId);
        }

        @Override
        protected void onResult(List<ProductUseTimesBean> list) {
            if(reqListener!=null)reqListener.onSuccess(list);
            if( list == null || list.size() == 0 ){
                AppLog.i("QueryProductUseTimesTask = null");
                return;
            }
        }

        @Override
        protected void onFailure(HttpResponse<List<ProductUseTimesBean>> response, Exception ex) {
            if(reqListener!=null)reqListener.onFail(response, ex);
            AppLog.i("QueryProductUseTimesTask ex = " + ex.toString() );
        }
    }

    private class QueryProductUseTimesByProductIdTask extends AppAsyncTask<String, Void, FreeUseTimesBean> {

        private RequestListener reqListener;

        QueryProductUseTimesByProductIdTask(RequestListener requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected FreeUseTimesBean doExecute(String... params) throws Exception {
            String studentId = params[0];
            String classId   = params[1];
            String productId = params[2];
            return mProductService.getProductUseTimesByProductId(classId,studentId, productId);
        }

        @Override
        protected void onResult(FreeUseTimesBean bean) {
            if(reqListener!=null)reqListener.onSuccess(bean);
        }

        @Override
        protected void onFailure(HttpResponse<FreeUseTimesBean> response, Exception ex) {
            if(reqListener!=null)reqListener.onFail(response, ex);
            AppLog.i("QueryProductUseTimesTask ex = " + ex.toString() );
        }
    }

    /**
     * 充值返现推荐
     */
    private class QueryRechargeRecommendTask extends AppAsyncTask<String, Void, RechargeCashbackBean > {

        private RequestListener reqListener;

        QueryRechargeRecommendTask(RequestListener requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected RechargeCashbackBean doExecute(String... params) throws Exception {
            return mProductService.getRechargeCashbackRecommend();
        }

        @Override
        protected void onResult(RechargeCashbackBean bean) {
            if(reqListener!=null)reqListener.onSuccess(bean);
            if( bean == null ){
                AppLog.i("QueryRechargeRecommendTask = null");
                return;
            }

            //恢复
            RechargeCashbackBean detail = AccountUtils.getRechargeCashback();
            if( detail == null )
                detail = new RechargeCashbackBean();

            AppLog.i("QueryRechargeRecommendTask = " + bean.getRechargeMoney() );
            AppLog.i("QueryRechargeRecommendTask = " + bean.getReturnDdAmt() );

            detail.setRechargeMoney( (int) bean.getRechargeMoney() );
            detail.setReturnDdAmt( bean.getReturnDdAmt() );

            //保存
            AccountUtils.setRechargeCashback( detail );
        }

        @Override
        protected void onFailure(HttpResponse<RechargeCashbackBean> response, Exception ex) {
            if(reqListener!=null)reqListener.onFail(response, ex);
            AppLog.i("QueryRechargeRecommendTask ex = " + ex.toString() );
        }
    }

    /**
     * 商品套餐兑换
     */
    private class ProductSuiteExchangeTask extends AppAsyncTask<String, Void, Boolean> {

        private RequestListener reqListener;

        ProductSuiteExchangeTask(RequestListener requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected Boolean doExecute(String... params) throws Exception {
            String classId   = params[0];
            String studentId = params[1];
            String suiteId   = params[2];
            String accountId  = params[3];
            return mProductService.exchangeProductSuite( classId, studentId, suiteId, accountId );
        }

        @Override
        protected void onResult(Boolean bean) {
            if(reqListener!=null)reqListener.onSuccess(bean);
            //触发学豆更新
            //EventBus.getDefault().post(new UpdateStudybeanEvent());
        }

        @Override
        protected void onFailure(HttpResponse<Boolean> response, Exception ex) {
            if(reqListener!=null)reqListener.onFail(response, ex);
        }
    }

    /**
     * 商品套餐兑换
     */
    private class ProductSuiteDetailTask extends AppAsyncTask<String, Void, ProductBean> {

        private RequestListener reqListener;

        ProductSuiteDetailTask(RequestListener requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected ProductBean doExecute(String... params) throws Exception {
            String suiteId   = params[0];
            return mProductService.queryProductSuiteDetail( suiteId );
        }

        @Override
        protected void onResult(ProductBean bean) {
            if(reqListener!=null)reqListener.onSuccess(bean);
        }

        @Override
        protected void onFailure(HttpResponse<ProductBean> response, Exception ex) {
            if(reqListener!=null)reqListener.onFail(response, ex);
        }
    }


}

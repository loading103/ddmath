package com.tsinghuabigdata.edu.ddmath.requestHandler.requestImpl;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tsinghuabigdata.edu.commons.http.HttpRequest;
import com.tsinghuabigdata.edu.commons.http.HttpRequestException;
import com.tsinghuabigdata.edu.ddmath.commons.http.AppRequestUtils;
import com.tsinghuabigdata.edu.ddmath.constant.AppRequestConst;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.FreeUseTimesBean;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.ProductBean;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.ProductCataBean;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.ProductGuideBean;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.ProductListBean;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.ProductPrivilegeBean;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.ProductUseTimesBean;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.RechargeCashbackBean;
import com.tsinghuabigdata.edu.ddmath.requestHandler.ProductService;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * 商品
 */

public class ProductServiceImpl extends BaseService implements ProductService {

    @Override
    public List<ProductListBean> getProductList(String studentId) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.GET_PRODUCT_LIST);
        HttpRequest request = AppRequestUtils.get(url);
        request.putRestfulParam("studentId", studentId);
        String res = request.requestJson().getDataBody();
        return new Gson().fromJson(res, new TypeToken<List<ProductListBean>>() {
        }.getType());
    }

    @Override
    public List<ProductBean> getSchoolIProductList(String studentId, String schoolId, String classId) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.GET_SCHOOLID_PRODUCT_LIST);
        HttpRequest request = AppRequestUtils.get(url);
        request.putRestfulParam("studentId", studentId)
                .putRestfulParam("schoolId", schoolId)
                .putRestfulParam("classId", classId);
        String res = request.requestJson().getDataBody();
        return new Gson().fromJson(res, new TypeToken<List<ProductBean>>() {
        }.getType());
    }

    @Override
    public List<ProductBean> getProductSuiteList(String studentId, String schoolId, String classId, String schoolIds) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.GET_PRODUCT_SUITE_LIST);
        HttpRequest request = AppRequestUtils.get(url);
        request.putRestfulParam("studentId", studentId)
                .putRestfulParam("schoolId", schoolId)
                .putRestfulParam("classId", classId);
        if( !TextUtils.isEmpty(schoolIds) ){
            request.putRequestParam("schoolIds",schoolIds);
        }
        String res = request.request().getDataBody();
        return new Gson().fromJson(res, new TypeToken<List<ProductBean>>() {
        }.getType());
    }

    @Override
    public List<ProductCataBean> getProductGroupList(String studentId, String schoolId, String classId) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.GET_PRODUCT_GROUP_LIST);
        HttpRequest request = AppRequestUtils.get(url);
        request.putRequestParam("studentId", studentId)
                .putRequestParam("schoolId", schoolId)
                .putRequestParam("classId", classId);
        String res = request.requestJson().getDataBody();
        return new Gson().fromJson(res, new TypeToken<List<ProductCataBean>>() {
        }.getType());
    }

    @Override
    public List<ProductGuideBean> getProductGuide(String privilegeId) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.GET_PRODUCT_GUIDE);
        HttpRequest request = AppRequestUtils.get(url);
        request.putRestfulParam("privilegeId", privilegeId);
        String res = request.requestJson().getDataBody();
        return new Gson().fromJson(res, new TypeToken<List<ProductGuideBean>>() {
        }.getType());
    }

    @Override
    public ProductPrivilegeBean getProductDetail(String studentId, String privilegeId, String schoolId, String classId,int querytype ) throws HttpRequestException, JSONException{
        String url = getUrl(AppRequestConst.GET_PRODUCT_DETAIL);
        HttpRequest request = AppRequestUtils.get(url);
        request.putRestfulParam("privilegeId", privilegeId)
               .putRequestParam("studentId", studentId)
               .putRequestParam("schoolId", schoolId)
               .putRequestParam("classId", classId)
               .putRequestParam("type", String.valueOf(querytype) );
        String res = request.request().getDataBody();
        return new Gson().fromJson(res, new TypeToken<ProductPrivilegeBean>() {
        }.getType());
    }

//    @Override
//    public ExchangeDetail getProductExchangeList(String studentId, String productId) throws HttpRequestException, JSONException{
//        String url = getUrl(AppRequestConst.GET_PRODUCT_DETAIL);
//        HttpRequest request = AppRequestUtils.get(url);
//        request.putRestfulParam("studentId", studentId)
//                .putRestfulParam("productId", productId);
//        String res = request.requestJson().getDataBody();
//
//        return new Gson().fromJson(res, new TypeToken<List<ExchangeTimeBean>>() {
//        }.getType());
//    }

    @Override
    public boolean exchangeProduct(String studentId, String classId, String productId, String privilegeId, int times, String contentId) throws HttpRequestException, JSONException {

        String url = getUrl(AppRequestConst.POST_EXCHANGE_PRODUCT);
        HttpRequest request = AppRequestUtils.post(url);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("studentId", studentId);
            jsonObject.put("classId", classId);

            //优先
            if (!TextUtils.isEmpty(productId))
                jsonObject.put("productId", productId);

                //其次
            else if( !TextUtils.isEmpty(privilegeId) ) jsonObject.put("privilegeId", privilegeId);

            //兑换使用次数时使用
            if (times > 0)
                jsonObject.put("times", String.valueOf(times));

            //兑换套题时使用
            if (!TextUtils.isEmpty(contentId)) {
                jsonObject.put("contentId", contentId);
            }
        } catch (Exception e) {
            AppLog.i("", e);
        }
        request.setJsonStringParams(jsonObject.toString());
        request.requestJson().getDataBody();
        return true;
    }

    @Override
    public List<ProductUseTimesBean> getProductUseTimes(String studentId, String classId, String privilegeIds, String productId  ) throws HttpRequestException, JSONException{

        String url = getUrl(AppRequestConst.GET_REVISE_TIMES);
        HttpRequest request = AppRequestUtils.get(url);
        request.putRequestParam("studentId", studentId)
                .putRequestParam("classId", classId)
                .putRequestParam("privilegeIds", privilegeIds );

        if( !TextUtils.isEmpty(productId) )
            request.putRequestParam("productId", productId );

        String res = request.request().getDataBody();

        return new Gson().fromJson(res, new TypeToken<List<ProductUseTimesBean>>() {
        }.getType());

    }

    /**
     * 查询商品使用次数
     */
    @Override
    public FreeUseTimesBean getProductUseTimesByProductId(String classId, String studentId, String productId ) throws HttpRequestException, JSONException{

        String url = getUrl(AppRequestConst.GET_PRODUCT_USETIMES);
        HttpRequest request = AppRequestUtils.get(url);
        request.putRequestParam("studentId", studentId)
                .putRequestParam("classId", classId)
                .putRequestParam("productId", productId );
        String res = request.request().getDataBody();

        return new Gson().fromJson(res, new TypeToken<FreeUseTimesBean>() {
        }.getType());
    }


    /**
     * 充值返现推荐提示
     */
    public RechargeCashbackBean getRechargeCashbackRecommend() throws HttpRequestException, JSONException {

        String url = getUrl(AppRequestConst.GET_RECHARGE_RECOMMEND);
        HttpRequest request = AppRequestUtils.get(url);
        String res = request.request().getDataBody();

        return new Gson().fromJson(res, new TypeToken<RechargeCashbackBean>() {
        }.getType());

    }

    /**
     * 商品套餐兑换
     */
    public boolean exchangeProductSuite( String classId, String studentId, String suiteId, String accountId ) throws HttpRequestException, JSONException{

        String url = getUrl(AppRequestConst.POST_PRODUCTSUITE_EXCHANGE);
        HttpRequest request = AppRequestUtils.post(url);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("studentId", studentId);
            jsonObject.put("classId", classId);
            jsonObject.put("productSuiteId", suiteId);
            if( !TextUtils.isEmpty(accountId) ) jsonObject.put("accountId", accountId);
        } catch (Exception e) {
            AppLog.i("", e);
        }
        request.setJsonStringParams(jsonObject.toString());
        String res = request.requestJson().getDataBody();
        return "Success".equals( res );
    }

    /**
     * 商品套餐详情
     */
    public ProductBean queryProductSuiteDetail( String suiteId ) throws HttpRequestException, JSONException{

        String url = getUrl(AppRequestConst.GET_PRODUCTSUITE_DETAIL);
        HttpRequest request = AppRequestUtils.get(url);
        request.putRestfulParam( "productSuiteId", suiteId );
        String res = request.request().getDataBody();
        return new Gson().fromJson(res, new TypeToken<ProductBean>() {
        }.getType());

    }


}

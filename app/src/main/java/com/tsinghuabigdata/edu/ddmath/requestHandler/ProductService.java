package com.tsinghuabigdata.edu.ddmath.requestHandler;

import com.tsinghuabigdata.edu.commons.http.HttpRequestException;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.FreeUseTimesBean;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.ProductBean;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.ProductCataBean;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.ProductGuideBean;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.ProductListBean;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.ProductPrivilegeBean;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.ProductUseTimesBean;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.RechargeCashbackBean;

import org.json.JSONException;

import java.util.List;

/**
 * 我的世界--商品 模块
 * Created by Administrator on 2017/11/16.
 */

public interface ProductService {

    /**
     * 查询商品列表
     */
    List<ProductListBean> getProductList(String studentId) throws HttpRequestException, JSONException;

    /**
     * 查询学校商品列表
     */
    List<ProductBean> getSchoolIProductList(String studentId, String schoolId, String classId) throws HttpRequestException, JSONException;

    /**
     * App精品套餐列表查询 13.0新增
     */
    List<ProductBean> getProductSuiteList(String studentId, String schoolId, String classId, String schoolIds) throws HttpRequestException, JSONException;

    /**
     * 查询所有商品分类列表 13.0新增
     */
    List<ProductCataBean> getProductGroupList(String studentId, String schoolId, String classId) throws HttpRequestException, JSONException;

    /**
     * 查询商品使用手册图片路径
     */
    List<ProductGuideBean> getProductGuide(String privilegeId) throws HttpRequestException, JSONException;

    /**
     * 查询商品详情
     */
    ProductPrivilegeBean getProductDetail(String studentId, String privilegeId, String schoolId, String classId,int querytype ) throws HttpRequestException, JSONException;

    /**
     * 查询商品使用次数
     */
    FreeUseTimesBean getProductUseTimesByProductId(String classId, String studentId, String productId)  throws HttpRequestException, JSONException;

    /**
     * 兑换商品
     * @param studentId 学生ID
     * @param productId  商品ID
     * @param times     兑换次数时，必填
     * @param contentId 套题ID     套题时必传
     */
    boolean exchangeProduct(String studentId, String classId, String productId, String privilegeId, int times, String contentId  ) throws HttpRequestException, JSONException;

    /**
     * 错题订正次数
     */
    List<ProductUseTimesBean> getProductUseTimes(String studentId, String classId, String privilegeId, String productId ) throws HttpRequestException, JSONException;

    /**
     * 充值返现推荐提示
     */
    RechargeCashbackBean getRechargeCashbackRecommend() throws HttpRequestException, JSONException;

    /**
     * 商品套餐兑换
     */
    boolean exchangeProductSuite( String classId, String studentId, String suiteId, String accountId ) throws HttpRequestException, JSONException;

    /**
     * 商品套餐详情
     */
    ProductBean queryProductSuiteDetail( String suiteId ) throws HttpRequestException, JSONException;

}

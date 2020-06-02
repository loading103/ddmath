package com.tsinghuabigdata.edu.ddmath.requestHandler.requestImpl;

import com.tsinghuabigdata.edu.commons.cache.CacheManager;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.constant.AppRequestConst;

public class BaseService {

    protected String getUrl(String restful) {
        //        if( restful.contains("studying") ){
        //            return "http://192.168.30.24:8080" + restful + "?t=" + System.currentTimeMillis();
        //        }
        return AppRequestConst.RESTFUL_ADDRESS + restful + "?t=" + System.currentTimeMillis();
    }

    protected String getTeacherUrl(String restful) {
        //        if( restful.contains("studying") ){
        //            return "http://192.168.30.24:8080" + restful + "?t=" + System.currentTimeMillis();
        //        }
        return AppRequestConst.WEB_ADDRESS + restful /*+ "?t=" + System.currentTimeMillis()*/;
    }

//    protected String getLocalUrl(String restful) {
//        return "http://192.168.30.22:8080" + restful/* + "?t=" + System.currentTimeMillis()*/;
//    }

//    protected String getTempUrl(String restful) {
//        return "" + restful;
//    }

    protected String getUpgradeApkUrl(String restful) {
        return AppRequestConst.UPGRADE_APK_HOST + restful;
    }

    /**
     * 获取缓存
     *
     * @param key
     * @return
     */
    protected Object getCache(String key) {
        return CacheManager.getAppCache().getCache(key);
    }

    /**
     * 存放缓存
     *
     * @param key
     * @param value
     */
    protected void putCache(String key, Object value) {
        putCache(key, value, AppConst.MAX_EXPIRE_TIME);
    }

    /**
     * 存放缓存
     *
     * @param key
     * @param value
     */
    protected void putCache(String key, Object value, long expire) {
        CacheManager.getAppCache().putCache(key, value, expire);
    }

    /**
     * 删除缓存
     *
     * @param key
     */
    protected void removeCache(String key) {
        CacheManager.getAppCache().remove(key);
    }

}

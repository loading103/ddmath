package com.tsinghuabigdata.edu.ddmath.util;

import java.io.IOException;

//import roboguice.util.Strings;

/**
 * 系统帮组类
 * <p/>
 * <p>
 * Created by yanshen@tsinghuabigdata.com on 2016/1/6.
 * </p>
 *
 * @author yanshen@tsinghuabigdata.com
 * @version V1.0
 * @packageName: com.tsinghuabigdata.edu.ddmath.util
 * @createTime: 2016/1/6 18:51
 */
public class OSUtils {

    /**
     * 是否是魅族手机
     *
     * @return
     */
//    public static boolean isMeiZu() {
//        try {
//            BuildProperties buildProperties = BuildProperties.newInstance();
//            boolean brand = "Meizu".equals(buildProperties.getProperty("ro.product.brand"));
////            String version = buildProperties.getProperty("ro.custom.build.version");
//            String version = buildProperties.getProperty("ro.build.version.incremental");
//            boolean flyme = false;
//            if (brand && Strings.notEmpty(version)) {
//                flyme = version.contains("Flyme_OS");
//            }
//            return brand && flyme;
//        } catch (IOException e) {
//            AppLog.i("err", e);
//            return false;
//        }
//    }


    public static boolean isMeiZuBrand() {
        try {
            BuildProperties buildProperties = BuildProperties.newInstance();
            boolean brand = "Meizu".equals(buildProperties.getProperty("ro.product.brand"));
            return brand;
        } catch (IOException e) {
            AppLog.i("err", e);
            return false;
        }
    }
}

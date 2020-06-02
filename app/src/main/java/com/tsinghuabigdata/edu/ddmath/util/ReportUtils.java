package com.tsinghuabigdata.edu.ddmath.util;

import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import java.text.SimpleDateFormat;

/**
 * Created by Administrator on 2018/7/9.
 */

public class ReportUtils {


    public static String getCreateTimeText(long createtime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm 生成");
        return dateFormat.format(createtime);
    }

    public static String getUpdateTimeText(long createtime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm 更新");
        return dateFormat.format(createtime);
    }

    //根据报告名称组装分享报告名称
    public static String getShareName(String name) {
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if (detailinfo != null) {
            return detailinfo.getReallyName() + "的" + name + "报告";
        }
        return name + "报告";
    }

}

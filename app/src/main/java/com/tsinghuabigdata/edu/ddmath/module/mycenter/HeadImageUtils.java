package com.tsinghuabigdata.edu.ddmath.module.mycenter;

import android.text.TextUtils;
import android.widget.ImageView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.PicassoUtil;

import java.util.regex.Pattern;

/**
 * 设置头像显示到ImageView
 * Created by Administrator on 2017/11/8.
 */

public class HeadImageUtils {


    /**
     * 设置头像
     */
    public static void setHeadImage(ImageView imageView) {
        setHeadImage(imageView, R.drawable.doudou_portrait_default);
    }

    /**
     * 设置头像
     */
    public static void setHeadImage(ImageView imageView, int deafaultDrawable) {
        UserDetailinfo userDetailinfo = AccountUtils.getUserdetailInfo();
        if (userDetailinfo == null || !isValid(userDetailinfo.getHeadImage())) {
            PicassoUtil.getPicasso().load(deafaultDrawable).into(imageView);
        } else {
            String url = AccountUtils.getFileServer() + userDetailinfo.getHeadImage();
            PicassoUtil.getPicasso().load(url).error(deafaultDrawable).placeholder(deafaultDrawable).into(imageView);
        }
    }

    private static boolean isValid(String headImage) {
        if (TextUtils.isEmpty(headImage)) {
            return false;
        }
        return !Pattern.matches("\\{.*?\\}", headImage);
    }

    public static void setHeadImage(ImageView imageView, String url, int deafaultDrawable) {
        if (TextUtils.isEmpty(url)) {
            PicassoUtil.getPicasso().load(deafaultDrawable).into(imageView);
        } else {
            String comUrl = AccountUtils.getFileServer() + url;
            PicassoUtil.getPicasso().load(comUrl).error(deafaultDrawable).placeholder(deafaultDrawable).into(imageView);
        }
    }
}

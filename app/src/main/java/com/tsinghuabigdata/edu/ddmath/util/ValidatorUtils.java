/*
 * Copyright (c) 2016. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.tsinghuabigdata.edu.ddmath.util;

import android.text.TextUtils;
import java.util.regex.Pattern;

//import roboguice.util.Strings;

/**
 * 验证器
 *
 * @author yanshen
 *         Created by yanshen on 2016/3/24.
 */
public class ValidatorUtils {

    /**
     * 手机验证规则
     */
//    public static final String MOBILE_REGEX = "^1(3[0-9]|5[0-35-9]|8[025-9])\\d{8}$";
    public static final String MOBILE_REGEX = "^1[3|4|5|6|7|8|9]\\d{9}$";
    /**
     * 入学年份格式：2016
     */
    public static final String ENCROLL_YEAR_REGEX = "^20\\d{2}$";
    /**
     * 用户名规则：6-16字符，只能包含数字／字母／下划线／连字符
     */
    //    public static final String MOBILE_REGEX = "^1(3[0-9]|5[0-35-9]|8[025-9])\\d{8}$";
    public static final String ACCOUNT_REGEX = "^[0-9a-zA-Z_—]{6,16}$";


    /**
     * 验证手机号
     *
     * @param mobile
     * @return
     */
    public static boolean validateMobile(String mobile) {
        if (TextUtils.isEmpty(mobile)) return false;
        return Pattern.matches(MOBILE_REGEX, mobile);
    }

    public static boolean validateEncrollYears(String mobile) {
        if (TextUtils.isEmpty(mobile)) return false;
        return Pattern.matches(ENCROLL_YEAR_REGEX, mobile);
    }

}

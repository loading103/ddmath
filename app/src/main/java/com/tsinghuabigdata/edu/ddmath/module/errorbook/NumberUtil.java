package com.tsinghuabigdata.edu.ddmath.module.errorbook;

import java.math.BigDecimal;

/**
 *
 */

public class NumberUtil {

    /**
     * 具体数据转换未最近的约数
     *
     * @param number 900
     * @return 例如: 1000+
     */
    public static String approximateNumber(int number) {
        String str = "1000+";
        /*if( number > 100000000 ){       //亿
            int num = number/100000000;
            str = num + "亿+";
        }else if( number > 10000 ){       //万
            int num = number/1000;
            str = num + "000+";
        }else*/
        number += 1000;
        if (number >= 1000) {  //次
            int num = number / 1000;
            str = num + "000+";
        }
        return str;
    }

    /**
     * 具体数据转换未最近的约数(豆豆数学学生注册人数)
     *
     * @param number 900
     */
    public static int getRegisteNumber(int number) {
        if (number < 100000){
            return 10;
        }
        return number / 10000;
    }


    /**
     * double 转 float，并设置指定小数位
     */
    public static float double2floatFormat(double d, int num) {
        BigDecimal b = new BigDecimal(d);
        return b.setScale(num, BigDecimal.ROUND_HALF_UP).floatValue();
    }
}

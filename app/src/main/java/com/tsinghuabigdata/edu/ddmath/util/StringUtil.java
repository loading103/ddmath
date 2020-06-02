package com.tsinghuabigdata.edu.ddmath.util;

/**
 * Created by 28205 on 2016/8/30.
 */

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class StringUtil {
    public static boolean isEmpty(String s) {
        boolean empty = false;
        if (TextUtils.isEmpty(s) || (s.trim().toLowerCase()).equals("null")) {
            empty = true;
        } else {
            empty = false;
        }
        return empty;
    }

    /**
     * 地址拼接
     */
    public static String getUrl(HashMap<String, String> params) {
        // 添加url参数
        String param = null;
        if (params != null) {
            Iterator<String> it = params.keySet().iterator();
            StringBuffer sb = null;
            while (it.hasNext()) {
                String key = it.next();
                String value = params.get(key);
                if (sb == null) {
                    sb = new StringBuffer();
                    sb.append("?");
                } else {
                    sb.append("&");
                }
                sb.append(key);
                sb.append("=");
                sb.append(value);
            }
            if (!TextUtils.isEmpty(sb)) {
                param = sb.toString();
            }
        }
        return param;
    }


    /**
     * 检测是否有emoji表情
     */
    public static boolean containsEmoji(String source) {
        int len = source.length();
        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);
            if (!isEmojiCharacter(codePoint)) { // 如果不能匹配,则该字符是Emoji表情
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否是Emoji
     *
     * @param codePoint 比较的单个字符
     */
    private static boolean isEmojiCharacter(char codePoint) {
        return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) || (codePoint == 0xD)
                || ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD))
                || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF));
    }

    public static String transListToString(List<String> list) {
        if (list == null || list.size() == 0) {
            return "";
        }
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            //buffer.append("\"");
            buffer.append(list.get(i));
            if (i < list.size() - 1) {
                buffer.append(",");
            }
        }
        return buffer.toString();
    }
}
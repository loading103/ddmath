package com.tsinghuabigdata.edu.ddmath.util;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.tsinghuabigdata.edu.ddmath.R;

import java.io.File;


/**
 * <p>
 * Created by yanshen@tsinghuabigdata.com on 2015/11/19.
 * </p>
 *
 * @author yanshen@tsinghuabigdata.com
 * @version V1.0
 * @packageName: com.tsinghuabigdata.edu.ddmath.util
 * @createTime: 2015/11/19 10:43
 */
public class HeadImageUtils {

    /**
     * 显示用户头像
     */
    public static void showHeadImage(Context context, String url, ImageView headView){
        PicassoUtil.getPicasso( context ).load( BitmapUtils.getUrlWithToken(url) ).error(R.drawable.doudou_portrait_default).placeholder(R.drawable.doudou_portrait_default).into(headView);
    }


    /**
     * 获取本地头像路径
     *
     * @param headImage
     * @return
     */
    public static String getLocalHeadImage(String headImage) {
        String translateHead = translateHeadImage(headImage);
        StringBuilder headPath = new StringBuilder();
        if (translateHead != null) {
            headPath.append(getSource(translateHead.charAt(0))).append(File.separator);
            headPath.append(getSex(translateHead.charAt(1))).append(File.separator);
            headPath.append(getRole(translateHead.charAt(2))).append(File.separator);
            headPath.append(getSize(translateHead.charAt(3))).append(translateHead.substring(4))
                    .append(".png");
        }
        return headPath.toString();
    }

    private static String getSource(char source) {
        switch (source) {
            case 'f':
                return "face";
            default:
                AppLog.i("translate head image path [source not found!]");
                return null;
        }
    }

    private static String getSex(char sex) {
        switch (sex) {
            case 'b':
                return "boy";
            case 'g':
                return "girl";
            case 'c':
                return "common";
            default:
                AppLog.i("translate head image path [sex not found!]");
                return null;
        }
    }

    private static String getRole(char role) {
        switch (role) {
            case 't':
                return "teacher";
            case 's':
                return "student";
            case 'p':
                return "parent";
            case 'c':
                return "common";
            default:
                AppLog.i("translate head image path [role not found!]");
                return null;
        }
    }

    private static String getSize(char size) {
        switch (size) {
            case 'b':
                return "01";
            case 'm':
                return "02";
            case 's':
                return "03";
            default:
                AppLog.i("translate head image path [size not found!]");
                return null;
        }
    }

    private static String translateHeadImage(String headImage) {
        if (headImage == null || headImage.trim().equals("")) {
            AppLog.i("translate head image path [headImage is null]");
            return null;
        }

        headImage = headImage.trim();

        // 必须花括号括起来
        if (!headImage.startsWith("{") || !headImage.endsWith("}")) {
            AppLog.i("translate head image path [headImage does not match, see {}]");
            return null;
        }

        headImage = headImage.replaceAll("\\{|\\}", "");

        // 内容必须是7位
        if (headImage.length() < 7) {
            AppLog.i("translate head image path [headImage length < 7]");
            return null;
        }

        return headImage;
    }


//    /**
//     * 获取本地头像路径
//     *
//     * @param path
//     * @return
//     */
//    public static String parseHeadImage(String path) {
//        if (TextUtils.isEmpty(path)) {
//            return null;
//        }
//        String[] segments = path.split("/");
//        if (segments.length != 4) {
//            return null;
//        }
//
//        StringBuilder head = new StringBuilder();
//        head.append(parseSource(segments[0]));
//        head.append(parseSex(segments[1]));
//        head.append(parseRole(segments[2]));
//        head.append(parseSize(segments[3]));
//        head.append(segments[3].substring(2, 5));
//        return String.format("{%s}", head.toString());
//    }

    private static char parseSource(String segment) {
        if ("face".equals(segment)) {
            return 'f';
        }
        throw new RuntimeException("parseSource Failure");
    }

    private static char parseSex(String segment) {
        if ("boy".equals(segment)) {
            return 'b';
        } else if ("girl".equals(segment)) {
            return 'g';
        } else if ("common".equals(segment)) {
            return 'c';
        }
        throw new RuntimeException("parseSource Failure");
    }

    private static char parseRole(String segment) {
        if ("teacher".equals(segment)) {
            return 't';
        } else if ("student".equals(segment)) {
            return 's';
        } else if ("parent".equals(segment)) {
            return 'p';
        } else if ("common".equals(segment)) {
            return 'c';
        }
        throw new RuntimeException("parseSource Failure");
    }

    private static char parseSize(String segment) {
        segment = !TextUtils.isEmpty(segment) && segment.trim().length() > 2 ? segment.substring(0, 2) : "";
        if ("01".equals(segment)) {
            return 'b';
        } else if ("02".equals(segment)) {
            return 'm';
        } else if ("03".equals(segment)) {
            return 's';
        }
        throw new RuntimeException("parseSource Failure");
    }
}

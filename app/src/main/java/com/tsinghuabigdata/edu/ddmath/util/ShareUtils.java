package com.tsinghuabigdata.edu.ddmath.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;

/**
 * 分享功能
 */
public class ShareUtils {

    public static final String PLAT_QQ       = "QQ";
    public static final String PLAT_QZONE    = "QZone";
    public static final String PLAT_WEIXIN   = "Wechat";
    public static final String PLAT_WXCIRCLE = "WechatMoments";
    public static final String PLAT_WEIBO    = "SinaWeibo";


    /**
     * 分享到本地功能
     * 进程名
     */
    public static void shareUrl(Context context, String url) {
        if (context == null)
            return;

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, url);
        shareIntent.setType("text/plain");

        //设置分享列表的标题，并且每次都显示分享列表
        context.startActivity(Intent.createChooser(shareIntent, "分享到"));
    }

    /**
     * 分享
     */
    public void share(Context context, String shareUrl, String plattype, String imgpath, PlatformActionListener listener) {

        //String domain = context.getResources().getString(R.string.domain);
        String shareText = "";   // 豆豆在手，数学我有
        if (PLAT_WEIBO.equals(plattype)) {
            shareWeiBo(shareText, imgpath, listener);
            //shareOneKey( context, imgpath );
        } else if (PLAT_QZONE.equals(plattype)) {
            shareQZone(context, shareText, shareUrl, imgpath, listener);
        } else if (PLAT_QQ.equals(plattype)) {
            qq(context, /*domain, */imgpath, /*shareText, */listener);
        } else {
            if (!isWeixinAvilible(context)) {
                ToastUtils.show(context, "请先安装微信!");
                return;
            }

            Platform.ShareParams shareParams = new Platform.ShareParams();
            shareParams.setShareType(Platform.SHARE_IMAGE);
            shareParams.setImagePath(imgpath);

            if (plattype.equals(PLAT_WEIXIN)) {
                shareParams.setTitle(context.getResources().getString(R.string.share_title));
            } else {
                shareParams.setTitle(context.getResources().getString(R.string.share_title));
            }

            shareParams.setText(shareText);
            shareParams.setUrl(shareUrl);

            Platform plat = ShareSDK.getPlatform(plattype);
            plat.setPlatformActionListener(listener);
            plat.share(shareParams);
        }
    }

    //    public void sharePic(Context context, String plattype, Bitmap bitmap, PlatformActionListener listener) {
    //
    //        Platform.ShareParams sp = new Platform.ShareParams();
    //        sp.setShareType(Platform.SHARE_IMAGE);
    //        sp.setImageData(bitmap);
    //
    //
    //        Platform platform = ShareSDK.getPlatform(plattype);
    //        // 设置分享事件回调（注：回调放在不能保证在主线程调用，不可以在里面直接处理UI操作）
    //        platform.setPlatformActionListener(listener);
    //        // 执行图文分享
    //        platform.share(sp);
    //
    //    }

    public void sharePic(Context context, String plattype, String path, PlatformActionListener listener) {

        if (PLAT_QQ.equals(plattype) || PLAT_QZONE.equals(plattype)) {
            qq(context, path, listener);
            return;
        }

        Platform.ShareParams sp = new Platform.ShareParams();
        sp.setShareType(Platform.SHARE_IMAGE);
        sp.setImagePath(path);

        Platform platform = ShareSDK.getPlatform(plattype);
        // 设置分享事件回调（注：回调放在不能保证在主线程调用，不可以在里面直接处理UI操作）
        platform.setPlatformActionListener(listener);
        // 执行图文分享
        platform.share(sp);

    }

    public void shareLink(Context context, String plattype, String title, String url, String text, PlatformActionListener listener) {

        if ((PLAT_QQ.equals(plattype) || PLAT_QZONE.equals(plattype)) && !isQQClientAvailable(context)) {
            ToastUtils.show(context, "请先安装移动QQ客户端!");
            return;
        }

        Platform.ShareParams sp = new Platform.ShareParams();
        sp.setShareType(Platform.SHARE_WEBPAGE);
        sp.setTitle(title);
        sp.setTitleUrl(url);
        sp.setText(text);
        //sp.setImagePath(imgpath);
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
        sp.setImageData(bitmap);
        if (PLAT_QZONE.equals(plattype)) {
            sp.setImagePath(getLogoPath(context));
        }
        sp.setSite(title);
        sp.setSiteUrl(url);
        sp.setUrl(url);

        Platform platform = ShareSDK.getPlatform(plattype);
        // 设置分享事件回调（注：回调放在不能保证在主线程调用，不可以在里面直接处理UI操作）
        platform.setPlatformActionListener(listener);
        // 执行图文分享
        platform.share(sp);

    }


    //测试使用
    //    private void shareOneKey( Context context,String imgpath ){
    //        OnekeyShare oks = new OnekeyShare();
    //        oks.setTitle("趣分享");
    //        oks.setText("我为技术带盐，我骄傲，我自豪");
    //        oks.setImagePath(imgpath);
    //        oks.setTitleUrl("http://wwww.baidu.com");
    //        oks.show( context );
    //    }

    /**
     * 分享到QQ空间 或者 QQ空间
     */
    private void qq(Context context, /*String domain,*/ String imgpath, /*String shareText, */PlatformActionListener listener) {

        //先检查是否安装了QQ
        if (!isQQClientAvailable(context)) {
            ToastUtils.show(context, "请先安装移动QQ客户端!");
            return;
        }

        Platform.ShareParams sp = new Platform.ShareParams();
        sp.setShareType(Platform.SHARE_IMAGE);
        sp.setTitle(null /*context.getResources().getString(R.string.share_title)*/);
        sp.setTitleUrl(null /*domain*/); // 标题的超链接
        sp.setText(null /*shareText*/);
        sp.setImagePath(imgpath);
        sp.setComment(null /*shareText*/);

        Platform qq = ShareSDK.getPlatform(QQ.NAME);
        qq.setPlatformActionListener(listener);
        qq.share(sp);
    }

    private void shareQZone(Context context, String shareText, String domain, String imgpath, PlatformActionListener listener) {

        boolean clientValid = ShareSDK.getPlatform(QQ.NAME).isClientValid();
        if (!clientValid) {
            clientValid = ShareSDK.getPlatform(QZone.NAME).isClientValid();
            if (!clientValid) {
                ToastUtils.show(context, "请先安装QQ或者QQ空间应用。");
                return;
            }
        }

        Platform.ShareParams sp = new Platform.ShareParams();
        sp.setShareType(Platform.SHARE_IMAGE);
        //sp.setTitle("豆豆数学");
        //sp.setTitleUrl(domain);
        //sp.setText(shareText);
        sp.setImagePath(imgpath);
        //sp.setSite("豆豆数学");
        //sp.setSiteUrl(domain);

        Platform qzone = ShareSDK.getPlatform(QZone.NAME);
        // 设置分享事件回调（注：回调放在不能保证在主线程调用，不可以在里面直接处理UI操作）
        qzone.setPlatformActionListener(listener);
        // 执行图文分享
        qzone.share(sp);
    }

    private void shareWeiBo(String shareText, String imgpath, PlatformActionListener listener) {
        Platform.ShareParams sp = new Platform.ShareParams();
        sp.setShareType(Platform.SHARE_IMAGE);
        sp.setText(shareText);
        sp.setImagePath(imgpath);
        Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
        weibo.setPlatformActionListener(listener); // 设置分享事件回调
        // 执行图文分享
        weibo.share(sp);
    }

    /**
     * 生成邀请好友url
     */
    public static String getshareLinkUrl(Context context) {
        String appName = AppUtils.getAppName();
        if(TextUtils.isEmpty(appName) || context == null) return "";

        String channelId = context.getString(R.string.channelId);
        if( appName.contains("豆豆数学") ){     //豆豆数学的推广算个人的
            channelId = AppConst.INVITE_SHARE_CHANNELID;
        }
        return getsharekUrl( channelId );
    }

    /**
     * 生成分享图片url
     */
    public static String getsharePicUrl(Context context) {
        return getshareLinkUrl( context );
    }

    /**
     * 生成分享作业url
     */
//    public static String getshareWorkUrl() {
//        return getsharekUrl("5K7M9");
//    }

    private static String getsharekUrl(String channelId) {
        //http://teach.doudoushuxue.com/#/reg?c=11223&u=1234
        String baseUrl = /*AppRequestConst.WEB_ADDRESS*/"http://teach.doudoushuxue.com/#/reg";
        HashMap<String, String> map = new LinkedHashMap<>();
        map.put("c", channelId);
        if (AccountUtils.getLoginUser() != null) {
            String accountId = AccountUtils.getLoginUser().getAccountId();
            map.put("u", accountId);
        } else if (AccountUtils.getParentInfo() != null) {
            String accountId = AccountUtils.getParentInfo().getStudentInfos().get(0).getAccountId();
            map.put("u", accountId);
        }
        return baseUrl + StringUtil.getUrl(map);
    }

    /**
     * 判断qq是否可用
     */
    private static boolean isQQClientAvailable(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }
        return false;
    }

    //判断微信是否可用
    public static boolean isWeixinAvilible(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        // 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        // 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }
        return false;
    }

    //生成豆豆图片路径
    private String getLogoPath(Context context) {
        String filePath = Environment.getExternalStorageDirectory().toString() + AppConst.IMAGE_DIR;
        Bitmap logoBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
        String fpath = BitmapUtils.saveImage(filePath, logoBitmap);
        logoBitmap.recycle();
        return fpath;
    }
}

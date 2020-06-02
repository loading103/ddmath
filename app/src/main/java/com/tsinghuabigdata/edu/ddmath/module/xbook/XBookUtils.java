package com.tsinghuabigdata.edu.ddmath.module.xbook;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.PicturePreviewActivity;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.AppSessionCache;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 超级错题本
 */
public class XBookUtils {


    private static HashMap<String,String> questionHashMap = new HashMap<>();


    public static void init(Context mContext){

        //题目类型
        String qtype_ch[] = mContext.getResources().getStringArray( R.array.question_type_ch_xbook );
        String qtype_en[] = mContext.getResources().getStringArray( R.array.question_type_en_xbook );

        int minlen = qtype_ch.length <qtype_en.length ? qtype_ch.length: qtype_en.length;
        for( int i=0; i<minlen; i++ ){
            questionHashMap.put( qtype_ch[i], qtype_en[i] );
            questionHashMap.put( qtype_en[i], qtype_ch[i] );
        }

        //错误原因
        String qreason_ch[] = mContext.getResources().getStringArray( R.array.question_reason_ch );
        String qreason_en[] = mContext.getResources().getStringArray( R.array.question_reason_en );

        minlen = qreason_ch.length <qreason_en.length ? qreason_ch.length: qreason_en.length;
        for( int i=0; i<minlen; i++ ){
            questionHashMap.put( qreason_ch[i], qreason_en[i] );
            questionHashMap.put( qreason_en[i], qreason_ch[i] );
        }

        //时间
        String subject_ch[] = mContext.getResources().getStringArray( R.array.time_filter_array );
        String subject_en[] = mContext.getResources().getStringArray( R.array.time_filter_array_en );

        minlen =subject_ch.length <subject_en.length ? subject_ch.length: subject_en.length;
        for( int i=0; i<minlen; i++ ){
            questionHashMap.put( subject_ch[i], subject_en[i] );
            questionHashMap.put( subject_en[i], subject_ch[i] );
        }

        //增加未标注
        questionHashMap.put("未标注","unMark");
        questionHashMap.put("unMark","未标注");
    }

    public static int setBitmap(Bitmap bitmap){
        AppSessionCache.getInstance().put(AppConst.SESSION_XBOOK_BITMAP, bitmap );
        return AppConst.SESSION_XBOOK_BITMAP;
    }
    public static Bitmap getBitmap(int session){
        AppSessionCache cache = AppSessionCache.getInstance();
        if( cache == null ){
            return null;
        }
        return cache.get( session );
    }
    public static void removeBitmap(int session ){
        AppSessionCache.getInstance().remove( session );
    }

//    public static int setErrorQuestion(ErrorQuestion question){
//        AppSessionCache.getInstance().put(AppConst.SESSION_XBOOK_QUESTION, question );
//        return AppConst.SESSION_XBOOK_QUESTION;
//    }
//
//    public static ErrorQuestion getErrorQuestion( int session ){
//        AppSessionCache cache = AppSessionCache.getInstance();
//        if( cache == null ){
//            return null;
//        }
//        return cache.get( session );
//    }

    public static void removeErrorQuestion(int session ){
        AppSessionCache.getInstance().remove( session );
    }

    public static String getQuestionTypeName( String reason ){
        return questionHashMap.get( reason );
    }

    public static String getReasonName( String reason ){
        return questionHashMap.get( reason );
    }

    public static void previewImageActivity( Context context, String url ){
        UserDetailinfo studentInfo = AccountUtils.getUserdetailInfo();
        if(TextUtils.isEmpty(url) || context == null || studentInfo==null ){
            return;
        }

        AppLog.d("sdsasdffe url = " + url );

        ArrayList<String> mList = new ArrayList<>();
        mList.add( url );

        PicturePreviewActivity.startPicturePreviewActivity( context, mList, 0 );

//        PicturePreviewActivity.startPicturePreviewActivity( context, url, 0, "");
//        //区分是网络还是本地图片
//        Intent intent = new Intent( context, PicturePreviewActivity.class );
//
//        if( url.contains( AppConst.APP_NAME ) ){
//            //url = "file://"+url;
//            intent.putExtra( ImagePreviewActivity.IMAGE_TYPE, ImagePreviewActivity.IMAGE_LOCAL);
//            intent.putExtra( ImagePreviewActivity.IMAGE_URLS_DATA, url );
//        }else{
//            url = studentInfo.getFileAddress() + url;
//            try{
//                LoginInfo loginInfo = AccountUtils.getLoginUser();
//                if( loginInfo == null )
//                    return;
//                url += "?access_token=" + URLEncoder.encode( loginInfo.getAccessToken(), "utf-8");
//            }catch (Exception e){
//                AppLog.i(ErrTag.TAG_ENCODE, e.toString());
//            }
//            intent.putExtra( ImagePreviewActivity.IMAGE_TYPE, ImagePreviewActivity.IMAGE_URL);
//            intent.putExtra( ImagePreviewActivity.IMAGE_URLS_DATA, url );
//        }
//        context.startActivity(intent);
    }
}

package com.tsinghuabigdata.edu.ddmath.module.errorbook;

import android.content.Context;

import com.tsinghuabigdata.edu.ddmath.activity.CommonWebviewActivity;
import com.tsinghuabigdata.edu.ddmath.constant.AppRequestConst;

/**
 *
 */

public class HelpUtil {

    public static void showHelpActivity(Context context, String title, String model ){
        CommonWebviewActivity.startActivity( context,title, AppRequestConst.RESTFUL_ADDRESS + AppRequestConst.FUCKYION_USE_DESCRIP+"/"+model);
    }
}

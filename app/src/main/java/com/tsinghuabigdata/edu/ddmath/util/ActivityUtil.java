package com.tsinghuabigdata.edu.ddmath.util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


/**
 * Created by 28205 on 2016/12/21.
 */
public class ActivityUtil {

    public static void goActivity(Context context, Class clz) {
        Intent intent = new Intent(context, clz);
        context.startActivity(intent);
    }

    public static void goActivity(Context context, Class clazz, Bundle bundle) {
        Intent intent = new Intent(context, clazz);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}

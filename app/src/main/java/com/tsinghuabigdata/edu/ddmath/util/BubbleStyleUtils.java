package com.tsinghuabigdata.edu.ddmath.util;

import android.content.Context;
import android.view.View;

import com.tsinghuabigdata.edu.commons.controlle.BadgeView;

/**
 * Created by 28205 on 2016/8/24.
 */
public class BubbleStyleUtils {
    public static void setBubble(Context context, View v, boolean isShow) {
        BadgeView badge = (BadgeView) v.getTag();
        if (badge == null) {
            badge = new BadgeView(context, v);
            badge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
            badge.setBadgeMargin(20);
            badge.toggle();
            v.setTag(badge);
        }
        if (isShow) {
            badge.show();
        } else {
            badge.hide();
        }
    }

    public static void hideBubble(Context context, View v) {
        BadgeView badge = (BadgeView) v.getTag();
        if (badge == null) {
            badge = new BadgeView(context, v);
            badge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
            badge.toggle();
            v.setTag(badge);
        }
        badge.hide();
    }


    public static void setBubble(Context context, View v, int badgeMargin, int badgeHeight, boolean isShow) {
        BadgeView badge = (BadgeView) v.getTag();
        if (badge == null) {
            badge = new BadgeView(context, v);
            badge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
            badge.setBadgeMargin(badgeMargin);
            badge.setHeight(badgeHeight);
//            badge.setTop(25);
            badge.setText("");
            badge.toggle();
            v.setTag(badge);
        }
        if (isShow) {
            badge.show();
        } else {
            badge.hide();
        }
    }
}

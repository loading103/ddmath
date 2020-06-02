package com.tsinghuabigdata.edu.ddmath.module.myscore;

import com.tsinghuabigdata.edu.ddmath.MVPModel.UserCenterModel;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;

/**
 * 用户积分
 */

public class UserScoreUtil {

    public static void addUserScore(String studentId, String eventId, String contentId){
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        new UserCenterModel().addUserScore( studentId, eventId, contentId ,null);
    }
    public static void addUserScore(String eventId){
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if( detailinfo != null ){
            new UserCenterModel().addUserScore( detailinfo.getStudentId(), eventId, "" ,null);
        }
    }
    public static void addUserScore( String eventId, String contentId){
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if( detailinfo != null ){
            new UserCenterModel().addUserScore( detailinfo.getStudentId(), eventId, contentId ,null);
        }
    }
}

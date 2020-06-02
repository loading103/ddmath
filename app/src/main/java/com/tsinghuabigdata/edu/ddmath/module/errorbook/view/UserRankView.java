package com.tsinghuabigdata.edu.ddmath.module.errorbook.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.bean.UserRankBean;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

import java.util.ArrayList;

/**
 * 用户等级View 周题练使用
 */
public class UserRankView extends RelativeLayout{

    //
    private SingleRankView firstUserView;
    private SingleRankView secondUserView;
    private SingleRankView thirdUserView;

    private View normalLineView;
    private View correctLineView;


    public UserRankView(Context context) {
        super(context);
        init(context);
    }

    public UserRankView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public UserRankView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init( context);
    }

    /**
     * 设置按钮背
     * */
    public void setData(ArrayList<UserRankBean> list, boolean correct ){

        //批阅状态显示 线
        normalLineView.setVisibility( !correct?VISIBLE:GONE );
        correctLineView.setVisibility( correct?VISIBLE:GONE );

        //人数显示
        if( list == null || list.size() == 0 ){     //没有人 都不显示
            firstUserView.setData( null, 0, false, correct );
            secondUserView.setData( null, 0, false, correct );
            thirdUserView.setData( null, 0, false, correct );
        }else if( list.size() == 1 ){               //只有一个人  一个班

            UserRankBean userBean = list.get(0);

            firstUserView.setData( null, 0, false, correct );
            secondUserView.setData( userBean.getHeadImage(), (int)(100*userBean.getScore()), userBean.isMe(), correct );
            thirdUserView.setData( null, 0, false, correct );
        }else if( list.size() == 2 ){               //只有2个人

            if( correct ){      //批阅后
                firstUserView.setData( null, 0, false, true );

                UserRankBean userBean = list.get(0);
                secondUserView.setData( userBean.getHeadImage(), (int)(100*userBean.getScore()), userBean.isMe(), true );
                userBean = list.get(1);
                thirdUserView.setData( userBean.getHeadImage(), (int)(100*userBean.getScore()), userBean.isMe(), true );
            }else{      //批阅前
                if( list.get(1).isMe() ){       //自己是第一名
                    UserRankBean userBean = list.get(0);
                    firstUserView.setData( userBean.getHeadImage(), (int)(100*userBean.getScore()), userBean.isMe(), false );
                    userBean = list.get(1);
                    secondUserView.setData( userBean.getHeadImage(), (int)(100*userBean.getScore()), userBean.isMe(), false );
                    thirdUserView.setFristData();       //勋章占据第一的位置
                }else{  //自己是第二名
                    firstUserView.setData( null, 0, false, true );
                    UserRankBean userBean = list.get(0);
                    secondUserView.setData( userBean.getHeadImage(), (int)(100*userBean.getScore()), userBean.isMe(), false );
                    userBean = list.get(1);
                    thirdUserView.setData( userBean.getHeadImage(), (int)(100*userBean.getScore()), userBean.isMe(), false );
                }
            }
        }else{
            if( correct || !list.get(2).isMe() ){//批阅后
                UserRankBean userBean = list.get(0);
                firstUserView.setData( userBean.getHeadImage(),  (int)(100*userBean.getScore()), userBean.isMe(), correct );
                userBean = list.get(1);
                secondUserView.setData( userBean.getHeadImage(),  (int)(100*userBean.getScore()), userBean.isMe(), correct );
                userBean = list.get(2);
                thirdUserView.setData( userBean.getHeadImage(),  (int)(100*userBean.getScore()), userBean.isMe(), correct );
            }else{      //批阅前,自己排在第一名
                UserRankBean userBean = list.get(1);
                firstUserView.setData( userBean.getHeadImage(),  (int)(100*userBean.getScore()), userBean.isMe(), false );
                userBean = list.get(2);
                secondUserView.setData( userBean.getHeadImage(),  (int)(100*userBean.getScore()), userBean.isMe(), false );
                thirdUserView.setFristData();       //勋章占据第一的位置
            }
        }
    }

    //-------------------------------------------------------------------------
    private void init(Context context){
        inflate( context, GlobalData.isPad()?R.layout.view_errbook_userbank :R.layout.view_errbook_userbank_phone, this );

        firstUserView = (SingleRankView) findViewById( R.id.userbank_firstuser ) ;
        secondUserView = (SingleRankView) findViewById( R.id.userbank_seconduser ) ;
        thirdUserView = (SingleRankView) findViewById( R.id.userbank_thirduser ) ;

        normalLineView  = findViewById( R.id.userbank_splitline_normal );
        correctLineView = findViewById( R.id.userbank_splitline_correct );
    }

}

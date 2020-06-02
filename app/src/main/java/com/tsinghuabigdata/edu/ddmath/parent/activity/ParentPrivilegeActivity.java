package com.tsinghuabigdata.edu.ddmath.parent.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.bean.MyTutorClassInfo;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.parent.view.ParentToolbar;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.BitmapUtils;
import com.tsinghuabigdata.edu.ddmath.util.PicassoUtil;
import com.tsinghuabigdata.edu.ddmath.view.CircleImageView;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

/**
 * 家长端--我的孩子信息 及 特权
 */

public class ParentPrivilegeActivity extends RoboActivity implements View.OnClickListener{

    @ViewInject(R.id.worktoolbar)
    private ParentToolbar workToolbar;

    @ViewInject(R.id.iv_children_headimage)
    private CircleImageView headImageView;

    @ViewInject(R.id.layout_info_detail)
    private LinearLayout mainLayout;
    @ViewInject(R.id.iv_children_nickname)
    private TextView nicknameView;
    @ViewInject(R.id.iv_children_realname )
    private TextView  realnameView;
    @ViewInject(R.id.iv_children_year)
    private TextView yearView;
    @ViewInject(R.id.iv_children_gradeinfo)
    private TextView gradeView;
    @ViewInject(R.id.tv_moreclass)
    private TextView moreClassView;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        transparent = true;
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_parent_privilege);
        x.view().inject( this );
        mContext = this;
        initView();
    }

    @Override
    public void onClick(View v) {
        if( R.id.tv_moreclass == v.getId() || R.id.layout_info_detail == v.getId() ){
            startActivity( new Intent( mContext, ParentChildrenActivity.class) );
        }
    }

    @Override
    public String getUmEventName(){
        return "parent_mycenter_privilege";
    }
    //----------------------------------------------------------------------------------------
    private void initView(){

        workToolbar.setTitle( "我的孩子" );
        workToolbar.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        UserDetailinfo detailinfo = AccountUtils.getParentUserDetailinfo();
        if( detailinfo==null )
            return;
        PicassoUtil.getPicasso(mContext).load(BitmapUtils.getUrlWithToken(detailinfo.getHeadImage())  ).error( R.drawable.doudou_portrait_default ).into( headImageView );
        nicknameView.setText( detailinfo.getNickName() );
        realnameView.setText( detailinfo.getReallyName() );

        yearView.setText( detailinfo.getSerial() );
        StringBuilder sb = new StringBuilder();
        ArrayList<MyTutorClassInfo> list = detailinfo.getClassInfos();
        if( list!=null && list.size() > 0 ){
            MyTutorClassInfo classInfo = list.get(0);
            sb.append( TextUtils.isEmpty(classInfo.getSchoolName())?"":classInfo.getSchoolName() ).append("\n");
            sb.append( TextUtils.isEmpty(classInfo.getClassName())?"":classInfo.getClassName() );
        }
        gradeView.setText( sb.toString() );
        moreClassView.setOnClickListener( this );
        mainLayout.setOnClickListener( this );
    }

}

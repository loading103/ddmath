package com.tsinghuabigdata.edu.ddmath.parent.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
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
 * 家长端--我的孩子信息
 */

public class ParentChildrenActivity extends RoboActivity{

    @ViewInject(R.id.worktoolbar)
    private ParentToolbar workToolbar;

    @ViewInject(R.id.iv_children_headimage)
    private CircleImageView headImageView;
    @ViewInject(R.id.iv_children_nickname)
    private TextView nicknameView;
    @ViewInject(R.id.iv_children_realname )
    private TextView  realnameView;
    @ViewInject(R.id.iv_children_sex)
    private TextView sexView;
    @ViewInject(R.id.iv_children_examnumber)
    private TextView examNumberView;
    @ViewInject(R.id.iv_children_year)
    private TextView yearView;
    @ViewInject(R.id.iv_children_gradeinfo)
    private TextView gradeView;
    @ViewInject(R.id.iv_children_address)
    private TextView addressView;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        transparent = true;
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_parent_childreninfo);
        x.view().inject( this );
        mContext = this;
        initView();
    }

    @Override
    public String getUmEventName(){
        return "parent_mycenter_children";
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
        String sex = detailinfo.getSex();
        if("male".equals( sex ) ){
            sex = "男";
        }else if("female".equals( sex ) ){
            sex = "女";
        }
        sexView.setText( sex );
        examNumberView.setText( detailinfo.getExamNumber() );
        yearView.setText( detailinfo.getSerial() );
        StringBuilder sb = new StringBuilder();
        ArrayList<MyTutorClassInfo> list = detailinfo.getClassInfos();
        if( list!=null ){
            boolean first = true;
            for( MyTutorClassInfo classInfo : list ){
                if( first ){
                    first = false;
                }else{
                    sb.append("\n\n");
                }
                sb.append( TextUtils.isEmpty(classInfo.getSchoolName())?"":classInfo.getSchoolName() ).append("\n");
                sb.append( TextUtils.isEmpty(classInfo.getClassName())?"":classInfo.getClassName() );
            }
        }
        gradeView.setText( sb.toString() );
        addressView.setText( detailinfo.getMailAddr() );
    }

}

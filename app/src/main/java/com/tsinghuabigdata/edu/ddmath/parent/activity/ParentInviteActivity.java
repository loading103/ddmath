package com.tsinghuabigdata.edu.ddmath.parent.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.dialog.ShareLinkDialog;
import com.tsinghuabigdata.edu.ddmath.parent.view.ParentToolbar;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * 家长端--邀请好友
 */

public class ParentInviteActivity extends RoboActivity implements View.OnClickListener{

    @ViewInject(R.id.worktoolbar)
    private ParentToolbar workToolbar;

    @ViewInject(R.id.btn_invite)
    private Button inviteBtn;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        transparent = true;
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_parent_invite);
        x.view().inject( this );
        mContext = this;
        initView();
    }

    @Override
    public String getUmEventName() {
        return "parent_mycenter_invitefriend";
    }
    //----------------------------------------------------------------------------------------
    private void initView(){

        workToolbar.setTitle( "邀请好友" );
        workToolbar.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        inviteBtn.setOnClickListener( this );
    }

    @Override
    public void onClick(View v) {
        switch ( v.getId() ){
            case R.id.btn_invite:
                ShareLinkDialog shareLinkDialog = new ShareLinkDialog(mContext);
                shareLinkDialog.show();
                break;
            default:
                break;
        }
    }
}

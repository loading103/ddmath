package com.tsinghuabigdata.edu.ddmath.parent.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Callback;
import com.tsinghuabigdata.edu.commons.controlle.BadgeView;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.fragment.MyBaseFragment;
import com.tsinghuabigdata.edu.ddmath.module.login.model.LoginModel;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.bean.UserPrivilegeBean;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.ProductBean;
import com.tsinghuabigdata.edu.ddmath.parent.MVPModel.ParentCenterModel;
import com.tsinghuabigdata.edu.ddmath.parent.activity.ParentAboutActivity;
import com.tsinghuabigdata.edu.ddmath.parent.activity.ParentFeedBackActivity;
import com.tsinghuabigdata.edu.ddmath.parent.activity.ParentInfoActivity;
import com.tsinghuabigdata.edu.ddmath.parent.activity.ParentInviteActivity;
import com.tsinghuabigdata.edu.ddmath.parent.activity.ParentMessageListActivity;
import com.tsinghuabigdata.edu.ddmath.parent.activity.ParentPrivilegeActivity;
import com.tsinghuabigdata.edu.ddmath.parent.activity.ParentProductActivity;
import com.tsinghuabigdata.edu.ddmath.parent.activity.ParentRechargeActivity;
import com.tsinghuabigdata.edu.ddmath.parent.bean.ParentInfo;
import com.tsinghuabigdata.edu.ddmath.parent.event.ParentCenterEvent;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.BitmapUtils;
import com.tsinghuabigdata.edu.ddmath.util.DensityUtils;
import com.tsinghuabigdata.edu.ddmath.util.PicassoUtil;
import com.tsinghuabigdata.edu.ddmath.view.CircleImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Locale;

/**
 * 家长——我模块
 * Created by Administrator on 2018/6/27.
 */
public class ParentCenterFragment extends MyBaseFragment implements View.OnClickListener {

    private BadgeView msgBadgeView;
    private TextView nameView;
    private CircleImageView headImageView;
    private LinearLayout priviledgeLayout;

    private RelativeLayout buyLayout;
//    private RelativeLayout rechargeLayout;

    private boolean bForceUpdate = false;
    private boolean bPause = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //x.view().inject( this, inflater, container );
        View root = inflater.inflate(R.layout.fragment_parent_center, container, false);
        initView( root );
        setPrepared();

        EventBus.getDefault().register(this);

        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        loadData();
        loadPrivilegeData();
    }

    @Override
    public void onResume() {
        super.onResume();
        bPause = false;
        if( bForceUpdate ){
            bForceUpdate = false;
            headImageView.setVisibility(View.INVISIBLE);
            headImageView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadData();
                }
            },300);
        }
    }
    @Override
    public void onPause(){
        super.onPause();
        bPause = true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_parent_name:{
                startActivity( new Intent( getContext(), ParentInfoActivity.class) );
                break;
            }
            case R.id.rl_parent_msg:{
                startActivity( new Intent( getContext(), ParentMessageListActivity.class) );
                break;
            }
            case R.id.rl_parent_children:{
                startActivity( new Intent( getContext(), ParentPrivilegeActivity.class) );
                break;
            }
            case R.id.rl_parent_friend:{
                startActivity( new Intent( getContext(), ParentInviteActivity.class) );
                break;
            }
            case R.id.rl_parent_feedback:{
                startActivity( new Intent( getContext(), ParentFeedBackActivity.class) );
                break;
            }
            case R.id.rl_parent_about:{
                startActivity( new Intent( getContext(), ParentAboutActivity.class) );
                break;
            }
            case R.id.rl_parent_buy:{
                startActivity( new Intent( getContext(), ParentProductActivity.class) );
                break;
            }
            case R.id.rl_parent_recharge:{
                startActivity( new Intent( getContext(), ParentRechargeActivity.class));
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receive(ParentCenterEvent event) {
        // 收到本地消息后，更新用户信息
        if( event.getType() == ParentCenterEvent.TYPE_UPDATE_INFO ) {
            if (!bPause) {
                loadData();
            } else {
                bForceUpdate = true;
            }
        }else if( event.getType() == ParentCenterEvent.TYPE_MSG_COUNT ){
            updateUnreadMsgCount( event.getUnReadCount() );
        }else if( event.getType() == ParentCenterEvent.TYPE_MSG_DEC ){
            decUnreadMsgCount();
        }else if( event.getType() == ParentCenterEvent.TYPE_MSG_ADD ){
            addUnreadMsgCount();
        }
    }
    @Override
    public String getUmEventName(){
        return "parent_main_center";
    }
    //---------------------------------------------------------------------------------------------
    private void initView(View root) {

        nameView        = root.findViewById( R.id.tv_parent_name );
        headImageView   =  root.findViewById( R.id.civ_head );
        //headImageView.setBgColor(Color.WHITE);

        msgBadgeView = root.findViewById( R.id.parent_mymsg_count );
        msgBadgeView.setVisibility( View.GONE );


        LinearLayout parentLayout = root.findViewById( R.id.layout_parent_name );
        parentLayout.setOnClickListener( this );

        RelativeLayout myMsgLayout     = root.findViewById( R.id.rl_parent_msg );
        RelativeLayout myChildLayout   = root.findViewById( R.id.rl_parent_children );
        RelativeLayout friendLayout    = root.findViewById( R.id.rl_parent_friend );
        RelativeLayout fendbackLayout  = root.findViewById( R.id.rl_parent_feedback );
        RelativeLayout aboutLayout     = root.findViewById( R.id.rl_parent_about );
        buyLayout       = root.findViewById( R.id.rl_parent_buy );
//        rechargeLayout  = root.findViewById( R.id.rl_parent_recharge );

        myMsgLayout.setOnClickListener( this );
        myChildLayout.setOnClickListener( this );
        friendLayout.setOnClickListener( this );
        fendbackLayout.setOnClickListener( this );
        aboutLayout.setOnClickListener( this );
        buyLayout.setOnClickListener( this );
//        rechargeLayout.setOnClickListener( this );

        priviledgeLayout = root.findViewById( R.id.layout_user_priviledge );

        updateUI( AccountUtils.getParentInfo() );
    }

    //更新家长相关信息
    private void loadData(){
        ParentInfo parentInfo = AccountUtils.getParentInfo();
        if( parentInfo==null ) return;

        new ParentCenterModel().queryParentInfo( parentInfo.getParentId(), new RequestListener<ParentInfo>() {
            @Override
            public void onSuccess(ParentInfo parentInfo) {
                //先缓存
                AccountUtils.setParentInfo( parentInfo );
                //更新UI界面
                updateUI( parentInfo  );
            }

            @Override
            public void onFail(HttpResponse<ParentInfo> response, Exception ex) {
            }
        });
    }

    private void updateUI( ParentInfo parentInfo ){
        if( parentInfo==null )return;

        String name = "";
        if( parentInfo.getStudentInfos()!=null && parentInfo.getStudentInfos().size()>0) {
            name = parentInfo.getStudentInfos().get(0).getReallyName();
            if (TextUtils.isEmpty(name)) name = parentInfo.getStudentInfos().get(0).getNickName();
            if (TextUtils.isEmpty(name)) name = parentInfo.getStudentInfos().get(0).getPhone();
        }
        if( TextUtils.isEmpty(name) ) name = "";
        nameView.setText( String.format(Locale.getDefault(),"%s家长",name) );

        headImageView.setVisibility(View.VISIBLE);
        PicassoUtil.getPicasso(getContext()).load( BitmapUtils.getUrlWithToken(parentInfo.getHeadImage()) ).error(R.drawable.aboutme_parenttouxiang).into(headImageView);

        buyLayout.setVisibility(View.VISIBLE);
//        rechargeLayout.setVisibility(View.VISIBLE);
        UserDetailinfo detailinfo = AccountUtils.getParentUserDetailinfo();
        if( detailinfo!=null && !detailinfo.enableBuySuite() ) {     //不允许买
            buyLayout.setVisibility(View.GONE);
//            rechargeLayout.setVisibility(View.GONE);
        }
    }

    private void updateUnreadMsgCount( int count ){
        msgBadgeView.setVisibility( View.GONE );
        if( count > 0 ){
            msgBadgeView.setVisibility( View.VISIBLE );
            msgBadgeView.setText( String.valueOf(count) );
        }
    }
    private void decUnreadMsgCount(){
        String data = msgBadgeView.getText().toString();
        if( TextUtils.isEmpty(data) ) return;
        int count = Integer.parseInt( data );
        count--;
        msgBadgeView.setVisibility( View.GONE );
        if( count > 0 ){
            msgBadgeView.setVisibility( View.VISIBLE );
            msgBadgeView.setText( String.valueOf(count) );
        }
    }
    private void addUnreadMsgCount(){
        String data = msgBadgeView.getText().toString();
        int count = 0;
        if( !TextUtils.isEmpty(data) ){
            count = Integer.parseInt( data );
        }
        count++;
        msgBadgeView.setVisibility( View.GONE );
        if( count > 0 ){
            msgBadgeView.setVisibility( View.VISIBLE );
            msgBadgeView.setText( String.valueOf(count) );
        }
    }

    //加载用户权限列表
    private void loadPrivilegeData(){
        UserDetailinfo detailinfo = AccountUtils.getParentUserDetailinfo();
        final LoginInfo loginInfo = AccountUtils.getLoginParent();
        if (detailinfo == null || loginInfo == null ) {
            return;
        }
        String studentInfos = AccountUtils.getShoolClassInfos();
        new LoginModel().getUsePriviledgeList( studentInfos, new RequestListener<UserPrivilegeBean>() {

            @Override
            public void onSuccess(UserPrivilegeBean result) {
                if( isDetached() ) return;
                if( result == null || result.getList()==null || result.getList().size()==0 ){
                    return;
                }

                priviledgeLayout.setVisibility(View.VISIBLE);
                UserDetailinfo detailinfo = AccountUtils.getParentUserDetailinfo();
                if( detailinfo!=null && !detailinfo.enableBuySuite() ) {     //不允许买
                    priviledgeLayout.setVisibility(View.GONE);
                    return;
                }

                int count = result.getList().size();
                if(count > 3) count = 3;

                for( int i=0; i<count; i++ ){
                    final ImageView imageView = new ImageView(getContext());
                    priviledgeLayout.addView( imageView, i);
                    imageView.setScaleType( ImageView.ScaleType.FIT_XY );
                    ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
                    AppLog.d("dfdsfds layoutParams = " + layoutParams );
                    if( layoutParams instanceof ViewGroup.MarginLayoutParams ){
                        ViewGroup.MarginLayoutParams mLayoutParams = (ViewGroup.MarginLayoutParams)layoutParams;
                        mLayoutParams.setMargins( DensityUtils.dp2px(getContext(),2) , 0, 0, 0);
                        imageView.requestLayout();
                    }
                    int wh = DensityUtils.dp2px(getContext(),30);
                    layoutParams.width = wh;
                    layoutParams.height = wh;
                    imageView.setLayoutParams( layoutParams );

                    ProductBean taskBean = result.getList().get(i);

                    final String comUrl = BitmapUtils.getUrlWithToken(taskBean.getImagePath());
                    if( taskBean.getHasUseRight() != 0 ){
                        PicassoUtil.getPicasso( getContext() ).load( comUrl ).placeholder( R.drawable.ic_huizhang_zhanweitu ).error( R.drawable.ic_shibai ).into( imageView );
                    }else{
                        PicassoUtil.getPicasso( getContext() ).load(comUrl).error(R.drawable.ic_shibai).placeholder(R.drawable.ic_huizhang_zhanweitu ).into(imageView,new Callback() {
                            @Override
                            public void onSuccess() {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Bitmap bitmap = PicassoUtil.getPicasso(getContext()).load( comUrl ).get();
                                            final Bitmap newbitmap = BitmapUtils.toGrayscale( bitmap );
                                            //更新到
                                            priviledgeLayout.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    imageView.setImageBitmap( newbitmap );
                                                }
                                            });
                                        }catch (Exception e){
                                            AppLog.i("",e );
                                        }
                                    }
                                }).start();
                            }
                            @Override
                            public void onError() {
                                AppLog.d("dfdfdfssss  get error url=" + comUrl );
                            }
                        });
                    }
                }
            }

            @Override
            public void onFail(HttpResponse<UserPrivilegeBean> response, Exception ex) {}
        });
    }

}

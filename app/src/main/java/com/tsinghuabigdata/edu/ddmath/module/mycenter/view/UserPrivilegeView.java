package com.tsinghuabigdata.edu.ddmath.module.mycenter.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Callback;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.bean.MyTutorClassInfo;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.module.login.model.LoginModel;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.bean.UserPrivilegeBean;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.ProductBean;
import com.tsinghuabigdata.edu.ddmath.parent.activity.ParentProductActivity;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.BitmapUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.PicassoUtil;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.edu.ddmath.view.AutoGridView;
import com.tsinghuabigdata.edu.ddmath.view.LoadingPager;
import com.tsinghuabigdata.edu.ddmath.view.NoClassView;

import java.util.ArrayList;
import java.util.Locale;

/**
 * 展示用户拥有的特权功能
 */

public class UserPrivilegeView extends LinearLayout {

    private LoadingPager mLoadingPager;

    private LinearLayout mainLayout;
    private TextView priviledgeView;
    private NoClassView noClassView;

    private TextView buytipsView;
    private RelativeLayout exclusiveTipsLayout;

    private AutoGridView mGridView;

    private PrivilegeAdapter mAdapter;
    private boolean fromParent = false;
    private boolean fromSuite = false;

    public UserPrivilegeView(Context context) {
        super(context);
        init( null );
    }

    public UserPrivilegeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init( attrs );
    }

    public UserPrivilegeView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if(!fromSuite) loadData();
    }

    public void setSuiteData( int vipLevel, boolean exclusive, ArrayList<ProductBean> list){
        mLoadingPager.showTarget();
        if( !fromSuite ) return;

        priviledgeView.setText( String.format(Locale.getDefault(),"(%d)", list.size() ));

        exclusiveTipsLayout.setVisibility( exclusive?VISIBLE:GONE);
        if(AppConst.MEMBER_SVIP == vipLevel){
            buytipsView.setVisibility(VISIBLE);
            buytipsView.setText("购买后升级为超级VIP会员");
        }else if(AppConst.MEMBER_VIP == vipLevel){
            buytipsView.setVisibility(VISIBLE);
            buytipsView.setText("购买后升级为VIP会员");
        }else{
            buytipsView.setVisibility(GONE);
        }

        mGridView.setExtendAllItem( true );
        //
        mAdapter.clear();
        mAdapter.addAll( list );
        mAdapter.notifyDataSetChanged();
    }

    //----------------------------------------------------------------------------------
    private void init(AttributeSet attrs){
        if( attrs!=null ){
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.UserPrivilegeView);
            fromParent = a.getBoolean(R.styleable.UserPrivilegeView_fromParent,false);
            fromSuite  = a.getBoolean(R.styleable.UserPrivilegeView_fromSuite,false);
            a.recycle();
        }
        if( fromParent ){
            inflate( getContext(), R.layout.view_user_privilege_parent, this );
        }else{
            inflate( getContext(), GlobalData.isPad()?R.layout.view_user_privilege :R.layout.view_user_privilege_phone, this );
        }

        mLoadingPager = findViewById( R.id.loadingPager );

        mainLayout = findViewById( R.id.main_layout );
        noClassView = findViewById( R.id.noClassView );
        priviledgeView = findViewById( R.id.tv_has_privileageCount );
        mGridView = findViewById( R.id.grid_priviledge );
        mAdapter = new PrivilegeAdapter(getContext());
        mGridView.setAdapter( mAdapter );

        mLoadingPager.setTargetView(mGridView);
        mLoadingPager.setListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!fromSuite)loadData();
            }
        });

        TextView titleView = findViewById( R.id.tv_title_priviledge );
        buytipsView   = findViewById( R.id.tv_buy_tips );
        exclusiveTipsLayout = findViewById( R.id.layout_exclusive_tips );

        if( fromSuite ){
            titleView.setText( getContext().getString(R.string.user_priviledge) );
            buytipsView.setVisibility( VISIBLE );
        }
    }
    //-------------------------------------------------------------------------------------------------------

    public void loadData(){

        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if (detailinfo == null) {
            detailinfo = AccountUtils.getParentUserDetailinfo();
            if( detailinfo == null ) return;
        }

        noClassView.setVisibility(GONE);
        mainLayout.setVisibility(VISIBLE);

        MyTutorClassInfo classInfo;
        if( fromParent ){
            classInfo = AccountUtils.getCurrentClassInfoForParent();
            if( classInfo==null ){
                mLoadingPager.showEmpty("您的孩子暂时还没有加入班级，去提醒她加入班级吧。");
                return;
            }
        }else{
            classInfo = AccountUtils.getCurrentClassInfo();
            if( classInfo==null ){
                //mLoadingPager.showEmpty("你暂时还没有加入班级，去加入班级吧。");
                noClassView.setVisibility(VISIBLE);
                mainLayout.setVisibility(GONE);
                return;
            }
        }

        mLoadingPager.showLoading();
        new LoginModel().getUsePriviledgeList( AccountUtils.getShoolClassInfos(), new RequestListener<UserPrivilegeBean>() {

            @Override
            public void onSuccess(UserPrivilegeBean result) {
                if( !isAttachedToWindow() ) return;
                if( result == null || result.getList()==null || result.getList().size()==0 ){
                    mLoadingPager.showEmpty( fromParent?"您的孩子暂时还没有学习特权":"你暂时还没有学习特权。");
                    return;
                }
                priviledgeView.setText( String.format(Locale.getDefault(),"%d/%d", result.getUseCount(), result.getAllCount() ));

                mLoadingPager.showTarget();

                //相同商品要进行合并处理 处理规则见原型
                
                mAdapter.clear();
                mAdapter.addAll( result.getList() );
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFail(HttpResponse<UserPrivilegeBean> response, Exception ex) {
                if( !isAttachedToWindow() ) return;
                mLoadingPager.showFault(ex);
            }
        });
    }

    //--------------------------------------------------------------------------------------------------

    private class PrivilegeAdapter extends ArrayAdapter<ProductBean> {

        private Context mContext;

        /*public*/ PrivilegeAdapter(Context context ) {
            super(context, 0);
            mContext = context;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(GlobalData.isPad()?R.layout.item_user_privilege : R.layout.item_user_privilege_phone, parent, false);
                convertView.setTag(new ViewHolder(convertView));
            }
            ViewHolder viewHolder = ((ViewHolder) convertView.getTag());
            viewHolder.bindView( position );
            return convertView;
        }

        //------------------------------------------------------------------------------------------
        class ViewHolder implements View.OnClickListener{

            //private LinearLayout mainLayout;
            private int position;

            private LinearLayout mainLayout;
            private ImageView imageView;
            private TextView nameView;
            private TextView leaveTimeView;
            private TextView useTimeView;

            private RelativeLayout leaveTimeLayout;
            private TextView leaveTimeTipsView;

            private ViewHolder(View convertView) {

                convertView.setOnClickListener( this );

                mainLayout = convertView.findViewById( R.id.main_layout );
                mainLayout.setOnClickListener( this );
                imageView =  convertView.findViewById( R.id.item_product_image );
                nameView = convertView.findViewById( R.id.item_product_name );
                leaveTimeView = convertView.findViewById( R.id.item_product_leavetime );
                useTimeView = convertView.findViewById( R.id.item_product_usetime );

                leaveTimeLayout = convertView.findViewById( R.id.layout_leavetime );
                leaveTimeTipsView = convertView.findViewById( R.id.tv_leavetime );
            }

            @Override
            public void onClick(View v) {
                //不跳转
                if( fromSuite ) return;

                ProductBean bean = getItem(position);
                if( bean == null || bean.getHasUseRight()==1 ) return;

                if( fromParent ){
                    UserDetailinfo detailinfo = AccountUtils.getParentUserDetailinfo();
                    if( detailinfo!=null && !detailinfo.enableBuySuite() ) {     //不允许买
                        ToastUtils.show( mContext, mContext.getResources().getString(R.string.tj_nobuy_tips) );
                    }else{  //跳转到购买界面
                        Intent intent = new Intent( getContext(), ParentProductActivity.class);
                        intent.putExtra("privilegeId", bean.getPrivilegeId());
                        getContext().startActivity( intent );
                    }
                }else{
                    UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
                    if( detailinfo!=null && !detailinfo.enableBuySuite() ){     //不允许买
                        ToastUtils.show( mContext, mContext.getResources().getString(R.string.tj_nobuy_tips) );
                    }else{  //跳转到购买界面
                        //BuySuiteActivity.startBuySuiteActivity(mContext, BuySuiteActivity.WORK, bean.getPrivilegeId());
                        ToastUtils.showToastCenter( mContext, "请到“我要购买”中购买含此特权的套餐，来获得此特权。若套餐中没有包含此特权，请到该特权所在板块中直接购买。");
                    }
                }
            }
            void bindView( int position ){
                this.position = position;

                ProductBean taskBean = getItem(position);
                if( taskBean == null ) return;

                nameView.setText( taskBean.getName());

                if( taskBean.getSurplusUsageTimes() == 0 ){
                    taskBean.setHasUseRight( 0 );
                }

//                String text = "";
                leaveTimeLayout.setVisibility(INVISIBLE);
                if(  taskBean.getSurplusUsageTimes() > 0 && taskBean.getSurplusUsageTimes() <= 3 ){
                    leaveTimeLayout.setVisibility(VISIBLE);
                    leaveTimeTipsView.setText( String.format(Locale.getDefault(),"仅剩%d次",taskBean.getSurplusUsageTimes()));
                }

//                if( taskBean.getHasUseRight() == 0 ){
//                    leaveTimeView.setVisibility( View.INVISIBLE );
//                }else if( taskBean.getSurplusUsageTimes() < 0 ){
//                    leaveTimeView.setVisibility( View.VISIBLE );
//                    text = "无限次";
//                }else {
//                    leaveTimeView.setVisibility( View.VISIBLE );
//                    text = String.format(Locale.getDefault(),"剩余%d次", taskBean.getSurplusUsageTimes() );
//                }
                leaveTimeView.setVisibility( GONE );
//                leaveTimeView.setText( text );

                useTimeView.setVisibility( View.GONE );
                if( taskBean.getHasUsageTimes()>0 || taskBean.getHasUseRight()==1 ){
                    //useTimeView.setVisibility( View.VISIBLE );
                    useTimeView.setText( String.format(Locale.getDefault(),"已使用%d次", taskBean.getHasUsageTimes()));
                }

                if( fromSuite ){
                    leaveTimeLayout.setVisibility(GONE);

                    //s使用次数
                    useTimeView.setVisibility( VISIBLE );
                    String times = "不限次";
                    if (taskBean.getUseTimes() > 0) {
                        times = taskBean.getUseTimes() + "次";
                    }
                    useTimeView.setText( times );
                }

                final String comUrl = BitmapUtils.getUrlWithToken(taskBean.getImagePath());

                if( taskBean.getHasUseRight() != 0 || fromSuite ){
                    PicassoUtil.getPicasso( getContext() ).load( comUrl ).placeholder( R.drawable.ic_huizhang_zhanweitu).error( R.drawable.ic_shibai ).into( imageView );
                }else{
                    PicassoUtil.getPicasso( getContext() ).load(comUrl).error(R.drawable.ic_shibai).placeholder(R.drawable.ic_huizhang_zhanweitu).into(imageView,new Callback() {
                        @Override
                        public void onSuccess() {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Bitmap bitmap = PicassoUtil.getPicasso(getContext()).load( comUrl ).get();
                                        final Bitmap newbitmap = BitmapUtils.toGrayscale( bitmap );
                                        //更新到
                                        post(new Runnable() {
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
    }
}

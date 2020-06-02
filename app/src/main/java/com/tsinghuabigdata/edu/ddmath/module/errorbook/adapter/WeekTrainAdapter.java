package com.tsinghuabigdata.edu.ddmath.module.errorbook.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.bean.MyTutorClassInfo;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.event.RefreshWeektrainEvent;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.DDUploadActivity;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.bean.WeekTrainBean;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.dialog.KnowledgePointDialog;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.view.OvalTextView;
import com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.activity.KnowledgeActivity;
import com.tsinghuabigdata.edu.ddmath.module.product.ProductUtil;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.DateUtils;
import com.tsinghuabigdata.edu.ddmath.util.EventBusUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 错题精炼
 */

public class WeekTrainAdapter extends ArrayAdapter<WeekTrainBean> {

    private Context mContext;
//    private WeekTrainResult weekTrainResult;

    public WeekTrainAdapter(Context context ) {
        super(context, 0);
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(GlobalData.isPad()?R.layout.item_ebook_weektrain : R.layout.item_ebook_weektrain_phone, parent, false);
            convertView.setTag(new ViewHolder(convertView));
        }
        ViewHolder viewHolder = ((ViewHolder) convertView.getTag());
        viewHolder.bindView( position );
        return convertView;
    }

//    public void setWeekTrainResult(WeekTrainResult result) {
//        weekTrainResult = result;
//    }

    //------------------------------------------------------------------------------------------
    class ViewHolder implements View.OnClickListener{

        private RelativeLayout mainLayout;
        private LinearLayout tipsLayout;
        private OvalTextView guideTextView;

        private int position;

        private TextView nameView;

        private TextView kwpointView;       //知识点
        private TextView masterView;        //知识点掌握度

        private TextView moreView;          //更多知识点
        //private UserRankView userRankView;

        private RelativeLayout detailLayout;
        private ImageView detailView;
        private RelativeLayout shareView;

        private TextView priceView;
        private ImageView buyStatusView;

        private ImageView praticeType;
        private TextView commandView;
        private RelativeLayout gerenatLayout;

        private TextView submitDate;            //提交截止时间

        //分享下载只能点击一次
        private AtomicBoolean btnShare = new AtomicBoolean( false );

        private ViewHolder(View convertView) {

            mainLayout = convertView.findViewById(R.id.main_layout);
            tipsLayout = convertView.findViewById(R.id.layout_float_tips);
            guideTextView = convertView.findViewById(R.id.tv_guide_tips);

            nameView        = convertView.findViewById( R.id.item_weektrain_examname );
            kwpointView     = convertView.findViewById( R.id.item_weektrain_kwpoints );
            masterView      = convertView.findViewById( R.id.item_weektrain_pointrate );

            //userRankView  = (UserRankView)convertView.findViewById( R.id.item_weektrain_userrank );
            moreView       = convertView.findViewById(R.id.item_weektrain_morebtn);

            detailLayout  =  convertView.findViewById( R.id.item_weektrain_detaillayout );
            detailView    =  convertView.findViewById( R.id.item_weektrain_detailtext );

            shareView     = convertView.findViewById( R.id.item_weektrain_sharelayout );
            priceView     = convertView.findViewById( R.id.item_weektrain_price );

            buyStatusView = convertView.findViewById( R.id.item_weektrain_buystatus );

            praticeType = convertView.findViewById(R.id.iv_praticetype);
            commandView = convertView.findViewById(R.id.tv_refine_command);

            gerenatLayout = convertView.findViewById(R.id.rl_pratice_generating);
            submitDate = convertView.findViewById( R.id.item_weektrain_submittime );

            detailLayout.setOnClickListener( this );
            shareView.setOnClickListener( this );
            moreView.setOnClickListener( this );
            guideTextView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch( v.getId() ){
                case R.id.tv_guide_tips:{
                    Intent intent = new Intent( getContext(), KnowledgeActivity.class);
                    intent.putExtra( KnowledgeActivity.PARAM_FROM_TYPE, AppConst.FROM_REFINE);
                    getContext().startActivity(intent);
                    break;
                }
                case R.id.item_weektrain_morebtn:{
                    WeekTrainBean bean = getItem( position );
                    if( bean == null || bean.getKnowlegeVos()==null || bean.getKnowlegeVos().size()==0 ) return;
                    KnowledgePointDialog dialog = new KnowledgePointDialog(mContext,  R.style.FullTransparentDialog );
                    dialog.setData( bean.getTitle(), bean.getQuestionCount(), bean.getKnowledgeId(), bean.getKnowlegeVos() );
                    dialog.show();
                    break;
                }
                case R.id.item_weektrain_detaillayout:{
                    clickJudgeUserPriviledge();
                    break;
                }
//                case R.id.item_weektrain_sharelayout:
//                    break;
                default:break;
            }
        }

        //点击按钮先判断用户是否有权限进入详情界面
        private void clickJudgeUserPriviledge(){

            final WeekTrainBean bean = getItem( position );
            final LoginInfo loginInfo = AccountUtils.getLoginUser();
            final UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
            final MyTutorClassInfo classInfo= AccountUtils.getCurrentClassInfo();
            if( bean == null || detailinfo == null || loginInfo == null || classInfo == null ) return;

            //final int price = bean.getChargeDdAmt();

            if( btnShare.get() ){
                ToastUtils.show( mContext,"请不要重复点击");
                return;
            }
            btnShare.set( true );

            //先判断是否已购买
            if( !bean.hasPrivilege() ){     //没有购买

                ProductUtil.productCheckPermissionAndExchange( mContext, "", bean.getPrivilegeId(), bean.getRecordId(), "错题再练本", btnShare, ProductUtil.FROM_NORMAL, new ProductUtil.ProductCallBack(){
                    @Override
                    public void onSuccess() {
                        //兑换成功 处理   状态改变
                        bean.setHasPrivilege( true );
                        notifyDataSetChanged();

                        //如果消耗的是免费使用权，则更新界面
                        EventBusUtils.postDelay(new RefreshWeektrainEvent(), new Handler());

                        //兑换成功提示
                        gotoDetailActivity();
                    }
                } );
            }
            //已购买,直接分享下载
            else{
                gotoDetailActivity();//shareUrl( loginInfo, detailinfo, bean );
                btnShare.set( false );
            }
        }

        //打开题目详情界面
        private void gotoDetailActivity(){

            WeekTrainBean bean = getItem( position );
            LoginInfo loginInfo = AccountUtils.getLoginUser();
            UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
            if( bean == null || detailinfo == null || loginInfo == null ) return;
            Intent intent = new Intent(mContext, DDUploadActivity.class);
            intent.putExtra(DDUploadActivity.PARAM_DDWORKID, bean.getExamId());
            intent.putExtra(DDUploadActivity.PARAM_RECORDID, bean.getRecordId());
            intent.putExtra(DDUploadActivity.PARAM_TITLE, bean.getTitle()/*+String.valueOf( bean.getSerial() )*/  );
            intent.putExtra(DDUploadActivity.PARAM_SHAREURL, bean.getTitle() );
            intent.putExtra(DDUploadActivity.PARAM_HASBUY, bean.hasPrivilege() );
            intent.putExtra(DDUploadActivity.PARAM_PRIVILEDGEID, bean.getPrivilegeId() );

            mContext.startActivity(intent);
        }

        void bindView( int position ){
            this.position = position;

            Resources res = mContext.getResources();

            WeekTrainBean taskBean = getItem(position);
            if( taskBean == null ) return;

            if( taskBean.getShowtype() != 0 ){
                mainLayout.setVisibility(View.GONE);
                tipsLayout.setVisibility( View.GONE );
                guideTextView.setVisibility(View.VISIBLE);

                String text = "平台每周会自动根据你的最薄弱知识点，从未订正正确的错题中，智能挖掘出“精选推荐”的专属错题再练本；如需练习全部未订正正确的错题，请去    ";
                String key  = "创建自定义错题再练本";
                guideTextView.setText( text, key, guideTextView.getTextSize() );
                return;
            }
            mainLayout.setVisibility(View.VISIBLE);
            tipsLayout.setVisibility(View.VISIBLE);
            guideTextView.setVisibility(View.GONE);

            //套题名称+数量
            String data = taskBean.getTitle() + /*String.valueOf( taskBean.getSerial() ) +*/ String.format( res.getString(R.string.ebook_stage_allcount), taskBean.getQuestionCount() );
            nameView.setText( data );

            String know = taskBean.getKnowledgeName();
            if( TextUtils.isEmpty(know) && taskBean.getKnowlegeVos()!=null && taskBean.getKnowlegeVos().size()>0 ){
                know = taskBean.getKnowlegeVos().get(0).getKnowledgeName();
            }
            if( !TextUtils.isEmpty(know)){
                kwpointView.setText( know );
            }else if(AppConst.PRIVILEGE_WEEKLEAKFILLING.equals(taskBean.getPrivilegeId()) ){
                kwpointView.setText( "每周培优" );
            }else if(AppConst.PRIVILEGE_EXAM_LEAKFILLING.equals(taskBean.getPrivilegeId()) ){
                kwpointView.setText( "考前培优" );
            }else if(AppConst.PRIVILEGE_EXAM_RETRAINING.equals(taskBean.getPrivilegeId()) ){
                kwpointView.setText( "考前错题再练" );
            }

            //掌握度
            data = String.format( Locale.getDefault (), "正确率：%d", (int)(100*taskBean.getAccuracy()))+"%";
            masterView.setText( data );

            //用户排队
            //userRankView.setData( taskBean.getStudentInfo(), taskBean.getStatus() == WeekTrainBean.ST_CORRECTED );
            moreView.setVisibility( View.INVISIBLE );
            if( taskBean.getKnowlegeVos()!=null&&taskBean.getKnowlegeVos().size()>0 ){
                moreView.setVisibility( View.VISIBLE );
            }

            //题目类型显示
            praticeType.setVisibility( View.GONE );
            commandView.setVisibility( View.GONE );
            if( AppConst.PAPER_TYPE_WEEK == taskBean.getPaperType() ){
                praticeType.setVisibility( View.VISIBLE );
                commandView.setVisibility( View.VISIBLE );
                praticeType.setImageResource( R.drawable.icon_week );
            }else if( AppConst.PAPER_TYPE_CUSTOM == taskBean.getPaperType() ){
                praticeType.setVisibility( View.VISIBLE );
                commandView.setVisibility( View.GONE );
                praticeType.setImageResource( R.drawable.icon_custom );
            }else if( AppConst.PAPER_TYPE_MONTH == taskBean.getPaperType() ) {
                praticeType.setVisibility( View.VISIBLE );
                commandView.setVisibility( View.VISIBLE );
                praticeType.setImageResource( R.drawable.icon_month );
            }else if( AppConst.PAPER_TYPE_EXAM == taskBean.getPaperType() ) {
                praticeType.setVisibility( View.VISIBLE );
                commandView.setVisibility( View.VISIBLE );
                praticeType.setImageResource( R.drawable.icon_exam );
            }

            //没有PDF生成中
            if( AppConst.PAPER_TYPE_CUSTOM == taskBean.getPaperType() && TextUtils.isEmpty(taskBean.getPdfUrl()) ){
                gerenatLayout.setVisibility( View.VISIBLE );
                detailLayout.setVisibility( View.GONE );
            //    shareView.setVisibility( View.GONE );
            }else{
                gerenatLayout.setVisibility( View.GONE );
                detailLayout.setVisibility( View.VISIBLE );
            //    shareView.setVisibility( View.VISIBLE );
            }

            submitDate.setVisibility(View.INVISIBLE);
            //按钮状态
            switch ( taskBean.getStatus() ){
                case WeekTrainBean.ST_DETAIL:{
                    detailView.setImageResource( R.drawable.sel_btn_waitcamera );
                    break;
                }
                case WeekTrainBean.ST_WAITCAMERA:{
                    detailView.setImageResource( R.drawable.sel_btn_waitcamera );
                    submitDate.setVisibility(View.VISIBLE);
                    submitDate.setText(DateUtils.getEndDate( taskBean.getLimitTime() ) );
                    break;
                }
                case WeekTrainBean.ST_CORRECTING:{
                    detailView.setImageResource( R.drawable.sel_btn_correcting );
                    break;
                }
                case WeekTrainBean.ST_CORRECTED:{
                    detailView.setImageResource( R.drawable.sel_btn_corrected );
                    break;
                }
                default:
                    break;
            }

            //已兑换的，提示，不显示价格
            buyStatusView.setVisibility( View.GONE );
            if( taskBean.hasPrivilege() ) {
                buyStatusView.setVisibility(View.VISIBLE);
                buyStatusView.setImageResource( taskBean.getStatus()>WeekTrainBean.ST_WAITCAMERA? R.drawable.yishiyongliitle : R.drawable.useable_liitle );
            }

            //是否显示价格
            //priceView.setText(String.format(Locale.getDefault(), "%d学豆", taskBean.getChargeDdAmt() ));

            //无限次使用权时不显示学豆 或者 已购买
            //boolean hidden = weekTrainResult.getFreeUseTimes()<0 || (taskBean.hasPrivilege()||!TextUtils.isEmpty(taskBean.getExamId()));
            //priceView.setVisibility( hidden? View.INVISIBLE:View.VISIBLE );
            priceView.setVisibility(View.INVISIBLE);        //不在显示价格
        }
    }
}

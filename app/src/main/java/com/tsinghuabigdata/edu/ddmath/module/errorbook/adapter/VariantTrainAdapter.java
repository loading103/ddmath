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
import com.tsinghuabigdata.edu.ddmath.event.RefreshVariantEvent;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.DDUploadActivity;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.KnowledgePiontBean;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.bean.WeekTrainBean;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.dialog.KnowledgePointDialog;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.view.OvalTextView;
import com.tsinghuabigdata.edu.ddmath.module.exclusivepractice.bean.PracticeBean;
import com.tsinghuabigdata.edu.ddmath.module.exclusivepractice.bean.PracticeProductBean;
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
 * 变式训练套题列表适配器
 */
public class VariantTrainAdapter extends ArrayAdapter<PracticeBean> {

    private Context mContext;

    public VariantTrainAdapter(Context context ) {
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
        //private RelativeLayout shareView;

        private TextView priceView;
        private ImageView buyStatusView;

        private ImageView praticeType;
        private TextView commandView;
        private RelativeLayout gerenatLayout;

        private TextView submitDate;            //提交截止时间

        private ViewHolder(View convertView) {

            mainLayout = convertView.findViewById(R.id.main_layout);
            tipsLayout = convertView.findViewById(R.id.layout_float_tips);
            guideTextView = convertView.findViewById(R.id.tv_guide_tips);

            //convertView.setOnClickListener( this );

            nameView        = convertView.findViewById( R.id.item_weektrain_examname );
            kwpointView     = convertView.findViewById( R.id.item_weektrain_kwpoints );
            masterView      = convertView.findViewById( R.id.item_weektrain_pointrate );

            //userRankView  = convertView.findViewById( R.id.item_weektrain_userrank );
            moreView       = convertView.findViewById(R.id.item_weektrain_morebtn);

            detailLayout  =  convertView.findViewById( R.id.item_weektrain_detaillayout );
            detailView    =  convertView.findViewById( R.id.item_weektrain_detailtext );

            //shareView     = convertView.findViewById( R.id.item_weektrain_sharelayout );
            priceView     = convertView.findViewById( R.id.item_weektrain_price );

            buyStatusView = convertView.findViewById( R.id.item_weektrain_buystatus );

            praticeType = convertView.findViewById(R.id.iv_praticetype);
            commandView = convertView.findViewById(R.id.tv_refine_command);

            gerenatLayout = convertView.findViewById(R.id.rl_pratice_generating);
            submitDate = convertView.findViewById( R.id.item_weektrain_submittime );

            detailLayout.setOnClickListener( this );
            //shareView.setOnClickListener( this );
            moreView.setOnClickListener( this );
            guideTextView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch( v.getId() ){
                case R.id.tv_guide_tips:{
                    Intent intent = new Intent( getContext(), KnowledgeActivity.class);
                    intent.putExtra( KnowledgeActivity.PARAM_FROM_TYPE, AppConst.FROM_VARIANT);
                    getContext().startActivity(intent);
                    break;
                }
                case R.id.tv_refine_command:{
                    Intent intent = new Intent( getContext(), KnowledgeActivity.class);
                    intent.putExtra( KnowledgeActivity.PARAM_FROM_TYPE, AppConst.FROM_VARIANT);
                    getContext().startActivity(intent);
                    break;
                }
                case R.id.item_weektrain_morebtn:{
                    PracticeBean bean = getItem( position );
                    if( bean == null || bean.getKnowledgePoints()==null || bean.getKnowledgePoints().size()==0 ) return;
                    KnowledgePointDialog dialog = new KnowledgePointDialog(mContext,  R.style.FullTransparentDialog );
                    dialog.setData( bean.getTitle(), bean.getQuestionCount(), null, bean.getKnowledgePoints() );
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
        private void clickJudgeUserPriviledge() {

            final PracticeBean bean = getItem( position );
            final LoginInfo loginInfo = AccountUtils.getLoginUser();
            final MyTutorClassInfo classInfo = AccountUtils.getCurrentClassInfo();
            final UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
            if( bean == null || classInfo == null || loginInfo == null || detailinfo == null ) return;

            if( btnShare.get() ){
                ToastUtils.show( mContext,"请不要重复点击");
                return;
            }
            btnShare.set( true );

            //先判断是否已购买
            if( bean.getStatus() == PracticeBean.ST_NOBUY ){

                final PracticeProductBean mPracticeProductBean = bean.getProductBean();
                ProductUtil.productCheckPermissionAndExchange( mContext, mPracticeProductBean.getProductId(), mPracticeProductBean.getPrivilegeId(), bean.getExcluId(), "变式训练本", btnShare, ProductUtil.FROM_NORMAL, new ProductUtil.ProductCallBack(){
                    @Override
                    public void onSuccess() {
                        //兑换成功 处理   状态改变
                        final PracticeBean bean = getItem( position );
                        if( bean == null ) return;

                        PracticeProductBean mPracticeProductBean = bean.getProductBean();

                        bean.setStatus( PracticeBean.ST_BUYED );

                        //刷新界面
                        notifyDataSetChanged();

                        //如果消耗的是免费使用权，则更新界面
                        int freeCount = mPracticeProductBean.getUseTimes()==null?0: mPracticeProductBean.getUseTimes().getTotalTimes();
                        if( freeCount > 0 ){
                            EventBusUtils.postDelay( new RefreshVariantEvent(), new Handler() );
                        }

                        //兑换成功提示
                        gotoDetailActivity(bean);
                    }
                } );

            }
            //已购买,直接打开
            else{
                gotoDetailActivity( bean );//shareUrl( classInfo, loginInfo, detailinfo, bean );
                btnShare.set( false );
            }
        }
        //打开题目详情界面
        private void gotoDetailActivity(PracticeBean bean){

            Intent intent = new Intent(mContext, DDUploadActivity.class);
            intent.putExtra(DDUploadActivity.PARAM_DDWORKID, bean.getExamId());
            intent.putExtra(DDUploadActivity.PARAM_RECORDID, bean.getExcluId());
            intent.putExtra(DDUploadActivity.PARAM_TITLE, getPracticeName(bean) );
            intent.putExtra(DDUploadActivity.PARAM_SHAREURL, getPracticeName(bean)  );
            intent.putExtra(DDUploadActivity.PARAM_HASBUY, bean.getStatus()==PracticeBean.ST_BUYED  );
            intent.putExtra(DDUploadActivity.PARAM_PRIVILEDGEID, bean.getProductBean().getPrivilegeId());
            intent.putExtra(DDUploadActivity.PARAM_PRODUCTID, bean.getProductBean().getProductId());
            mContext.startActivity(intent);
        }

        //分享下载只能点击一次
        private AtomicBoolean btnShare = new AtomicBoolean( false );

        //得到套题名称
        private String getPracticeName( PracticeBean bean ){
            PracticeProductBean mPracticeProductBean = bean.getProductBean();
            String name = bean.getTitle();
            if( TextUtils.isEmpty(name) || name.contains("查漏补缺") || name.contains("考前培优") ||  name.contains("错题再练") ) {
                name = mPracticeProductBean.getSubName() + mPracticeProductBean.getName();
            }
            return name + getSuffixName( bean );
        }
        private String getSuffixName( PracticeBean bean ){
            String name = "";
            if( bean!=null && bean.getDifficult() >= PracticeBean.Q_DIFFICULT_EASY && bean.getDifficult() <= PracticeBean.Q_DIFFICULT_B ){
                String diff[] = {"(易)","(中)","(难)","(A卷)","(B卷)"};
                name = diff[ bean.getDifficult()-1 ];
            }
            return name;
        }

        void bindView( int position ){
            this.position = position;

            Resources res = mContext.getResources();

            PracticeBean taskBean = getItem(position);
            if( taskBean == null ) return;

            if( taskBean.getShowType() != 0 ){
                mainLayout.setVisibility(View.GONE);
                tipsLayout.setVisibility( View.GONE );
                guideTextView.setVisibility(View.VISIBLE);

                String text = "平台每周会自动根据你的最薄弱知识点，智能推荐出专属变式训练本；如需让平台根据自定近期时间段的最薄弱知识点推荐，请去    ";
                String key  = "创建自定义变式训练本";
                guideTextView.setText( text, key, guideTextView.getTextSize() );
                return;
            }
            mainLayout.setVisibility(View.VISIBLE);
            tipsLayout.setVisibility(View.VISIBLE);
            guideTextView.setVisibility(View.GONE);

            //变式训练
            //套题名称+数量
            String data = getPracticeName(taskBean) + /*String.valueOf( taskBean.getSerial() ) +*/ String.format( res.getString(R.string.ebook_stage_allcount), taskBean.getQuestionCount() );
            nameView.setText( data );

            //知识点
            KnowledgePiontBean knowBean = null;
            if( taskBean.getKnowledgePoints()!=null && taskBean.getKnowledgePoints().size()>0 ){
                knowBean = taskBean.getKnowledgePoints().get(0);
            }
            if( knowBean!=null ){
                kwpointView.setText( knowBean.getKnowledgeName() );//掌握度

                data = String.format( Locale.getDefault (), "正确率：%d", Math.round(100*knowBean.getAccuracy()))+"%";
                masterView.setText( data );
            }else if(AppConst.PRIVILEGE_WEEKLEAKFILLING.equals(taskBean.getProductBean().getPrivilegeId()) ){
                kwpointView.setText( "每周培优" );
            }else if(AppConst.PRIVILEGE_EXAM_LEAKFILLING.equals(taskBean.getProductBean().getPrivilegeId()) ){
                kwpointView.setText( "考前培优" );
            }else if(AppConst.PRIVILEGE_EXAM_RETRAINING.equals(taskBean.getProductBean().getPrivilegeId()) ){
                kwpointView.setText( "考前错题再练" );
            }else{
                kwpointView.setText( "" );
                masterView.setText( "" );
            }

            moreView.setVisibility( View.VISIBLE );
            if( taskBean.getKnowledgePoints()!=null&&taskBean.getKnowledgePoints().size()>1 ){
                moreView.setVisibility( View.VISIBLE );
            }

            //题目类型显示
            praticeType.setVisibility( View.GONE );
            commandView.setVisibility( View.GONE );
            if( AppConst.PAPER_TYPE_WEEK == taskBean.getPaperType() ){
                praticeType.setVisibility( View.VISIBLE );
                //commandView.setVisibility( View.VISIBLE );
                praticeType.setImageResource( R.drawable.icon_week );
            }else if( AppConst.PAPER_TYPE_CUSTOM == taskBean.getPaperType() ){
                praticeType.setVisibility( View.VISIBLE );
                //commandView.setVisibility( View.GONE );
                praticeType.setImageResource( R.drawable.icon_custom );
            }else if( AppConst.PAPER_TYPE_MONTH == taskBean.getPaperType() ) {
                praticeType.setVisibility( View.VISIBLE );
                //commandView.setVisibility( View.VISIBLE );
                praticeType.setImageResource( R.drawable.icon_month );
            }else if( AppConst.PAPER_TYPE_EXAM == taskBean.getPaperType() ) {
                praticeType.setVisibility( View.VISIBLE );
                //commandView.setVisibility( View.VISIBLE );
                praticeType.setImageResource( R.drawable.icon_exam );
            }

            //没有PDF生成中
            if( AppConst.PAPER_TYPE_CUSTOM == taskBean.getPaperType() && !taskBean.isCreated() ){
                gerenatLayout.setVisibility( View.VISIBLE );
                detailLayout.setVisibility( View.GONE );
                //shareView.setVisibility( View.GONE );
            }else{
                gerenatLayout.setVisibility( View.GONE );
                detailLayout.setVisibility( View.VISIBLE );
                //shareView.setVisibility( View.VISIBLE );
            }

            submitDate.setVisibility(View.INVISIBLE);
            //按钮状态
            switch ( taskBean.getExerStatus() ){
                case PracticeBean.ST_DETAIL:{
                    detailView.setImageResource( R.drawable.sel_btn_waitcamera );
                    break;
                }
                case PracticeBean.ST_WAITCAMERA:{
                    if( TextUtils.isEmpty(taskBean.getExamId()) ){
                        detailView.setImageResource( R.drawable.sel_btn_waitcamera );       //还没有分享下载
                    }else{
                        detailView.setImageResource( R.drawable.sel_btn_waitcamera );
                        submitDate.setVisibility(View.VISIBLE);
                        submitDate.setText(DateUtils.getEndDate( taskBean.getSubmitTime() ) );
                    }
                    break;
                }
                case PracticeBean.ST_CORRECTING:
                case PracticeBean.ST_WAITCORRECT:{
                    detailView.setImageResource( R.drawable.sel_btn_correcting );
                    break;
                }
                case PracticeBean.ST_STATED:
                case PracticeBean.ST_CORRECTED:{
                    detailView.setImageResource( R.drawable.sel_btn_corrected );
                    break;
                }
                default:
                    break;
            }

            //已兑换的，提示，不显示价格
            buyStatusView.setVisibility( View.GONE );
            if( taskBean.getStatus() == PracticeBean.ST_BUYED  ) {
                buyStatusView.setVisibility(View.VISIBLE);
                buyStatusView.setImageResource( taskBean.getExerStatus()> WeekTrainBean.ST_WAITCAMERA? R.drawable.yishiyongliitle : R.drawable.useable_liitle );
                priceView.setVisibility(View.INVISIBLE);
            }

            //是否显示价格
            priceView.setText(String.format(Locale.getDefault(), "%d学豆", taskBean.getXuedou() ));

            //无限次使用权时不显示学豆 或者 已购买
            //PracticeProductBean mPracticeProductBean = taskBean.getProductBean();
            //boolean hidden = mPracticeProductBean.getUseTimes().getTotalTimes()<0 || (taskBean.getStatus() == PracticeBean.ST_BUYED  );
            //priceView.setVisibility( hidden? View.INVISIBLE:View.VISIBLE );

            priceView.setVisibility(View.INVISIBLE );
        }
    }
}

package com.tsinghuabigdata.edu.ddmath.module.errorbook.adapter;

import android.content.Context;
import android.content.Intent;
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

import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.bean.MyTutorClassInfo;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.fragment.BasePayFragment;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.DDUploadActivity;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.NumberUtil;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.bean.WeekTrainBean;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.dialog.KnowledgePointDialog;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.view.OvalTextView;
import com.tsinghuabigdata.edu.ddmath.module.exclusivepractice.bean.PracticeBean;
import com.tsinghuabigdata.edu.ddmath.module.exclusivepractice.bean.PracticeProductBean;
import com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.activity.KnowledgeActivity;
import com.tsinghuabigdata.edu.ddmath.module.product.ProductUtil;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.ProductUseTimesBean;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.DateUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 精品套题列表适配器
 */
public class ClassicPracticeAdapter extends ArrayAdapter<PracticeBean> {

    private Context mContext;
    private BasePayFragment mPayFragment;
    public ClassicPracticeAdapter(Context context, BasePayFragment fragment ) {
        super(context, 0);
        mContext = context;
        mPayFragment = fragment;
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
    //打开题目详情界面
    public void gotoDetailActivity(PracticeBean bean){
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
//                case R.id.tv_refine_command:{
//                    Intent intent = new Intent( getContext(), KnowledgeActivity.class);
//                    intent.putExtra( KnowledgeActivity.PARAM_FROM_TYPE, AppConst.FROM_VARIANT);
//                    getContext().startActivity(intent);
//                    break;
//                }
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

            final PracticeProductBean productBean = bean.getProductBean();
            if( productBean == null ) return;

            if( btnShare.get() ){
                ToastUtils.show( mContext,"请不要重复点击");
                return;
            }
            btnShare.set( true );

            //先判断是否已购买
            if( bean.getStatus() == PracticeBean.ST_NOBUY ){
                ProductUtil.checkProductUseTimes(productBean.getProductId(), productBean.getPrivilegeId(), new RequestListener<List<ProductUseTimesBean>>() {
                    @Override
                    public void onSuccess(final List<ProductUseTimesBean> list) {
                        if (list == null || list.size() == 0 || list.get(0) == null || list.get(0).getUseTimes() == 0) {
                            mPayFragment.toPay( bean );
                            btnShare.set( false );
                        }else{
                            ProductUtil.exchangePracticeProduct(mContext, productBean.getProductId(), productBean.getPrivilegeId(), bean.getExcluId(), btnShare, new ProductUtil.ProductCallBack() {
                                @Override
                                public void onSuccess() {
                                    btnShare.set( false );
                                    int times = list.get(0).getUseTimes();
                                    if( times>0 ){
                                        String data = String.format(Locale.getDefault(),"已使用1次精品套题的学习特权,还剩%d次", times-1 );
                                        //提示 用户免费次数兑换一次 机会
                                        ToastUtils.showToastCenter( mContext, data );
                                    }
                                    //刷新列表
                                    bean.setStatus(PracticeBean.ST_BUYED);
                                    notifyDataSetChanged();
                                    //跳转到下一页
                                    gotoDetailActivity( bean );
                                }
                            });
                        }
                    }

                    @Override
                    public void onFail(HttpResponse<List<ProductUseTimesBean>> response, Exception ex) {
                        AlertManager.showErrorInfo( mContext, ex);
                        btnShare.set( false );
                    }
                });
            }
            //已购买,直接打开
            else{
                gotoDetailActivity( bean );//shareUrl( classInfo, loginInfo, detailinfo, bean );
                btnShare.set( false );
            }
        }

        //分享下载只能点击一次
        private AtomicBoolean btnShare = new AtomicBoolean( false );

        void bindView( int position ){
            this.position = position;

            //Resources res = mContext.getResources();

            PracticeBean taskBean = getItem(position);
            if( taskBean == null ) return;

            if( taskBean.getShowType() != 0 ){
                mainLayout.setVisibility(View.GONE);
                tipsLayout.setVisibility( View.GONE );
                guideTextView.setVisibility(View.VISIBLE);

//                String text = "平台每周会自动根据你的最薄弱知识点，智能推荐出专属变式训练本；如需让平台根据自定近期时间段的最薄弱知识点推荐，请去    ";
//                String key  = "创建自定义变式训练本";
//                guideTextView.setText( text, key, guideTextView.getTextSize() );
                return;
            }
            mainLayout.setVisibility(View.VISIBLE);
            tipsLayout.setVisibility(View.VISIBLE);
            guideTextView.setVisibility(View.GONE);

            //商品名称
            String data = taskBean.getProductBean().getName();
            kwpointView.setText( data );

            //套题名称
            nameView.setText( taskBean.getTitle() );

            //题目与批阅结果
            StringBuilder sb = new StringBuilder();
            sb.append( "共").append( taskBean.getQuestionCount() ).append("题");
            if( taskBean.getExerStatus() >= PracticeBean.ST_CORRECTED ){
                int totalScore = taskBean.getTotalScore();
                if( totalScore > taskBean.getQuestionCount() ){
                    sb.append( String.format( Locale.getDefault(), "（得%d分，共%d分）", taskBean.getScore(), totalScore ));
                }else{
                    sb.append( String.format( Locale.getDefault(), "（正确%d题，错误%d题）", taskBean.getRightCount(), taskBean.getWrongCount() ));
                }
            }
            submitDate.setVisibility(View.VISIBLE);
            submitDate.setText( sb.toString() );

            //不显示
            moreView.setVisibility( View.GONE );

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

            masterView.setVisibility(View.INVISIBLE);
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
                        masterView.setVisibility(View.VISIBLE);
                        masterView.setText(DateUtils.getEndDate( taskBean.getSubmitTime() ) );
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
            if( taskBean.getStatus() == PracticeBean.ST_BUYED || !TextUtils.isEmpty(taskBean.getExamId()) ) {
                buyStatusView.setVisibility(View.VISIBLE);
                buyStatusView.setImageResource( taskBean.getExerStatus()> WeekTrainBean.ST_WAITCAMERA? R.drawable.yishiyongliitle : R.drawable.useable_liitle );
                priceView.setVisibility(View.INVISIBLE);
            }

            //是否显示价格
            priceView.setText( "¥" + Float.valueOf( NumberUtil.double2floatFormat( taskBean.getXuedou()/10.0, 1 )).floatValue() );

            //无限次使用权时不显示学豆 或者 已购买
            //PracticeProductBean mPracticeProductBean = taskBean.getProductBean();
            boolean hidden =/* mPracticeProductBean.getUseTimes().getTotalTimes()<0 ||*/ (taskBean.getStatus() == PracticeBean.ST_BUYED  || !TextUtils.isEmpty(taskBean.getExamId()));
            priceView.setVisibility( hidden? View.INVISIBLE:View.VISIBLE );

        }
    }
}

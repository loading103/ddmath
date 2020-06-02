package com.tsinghuabigdata.edu.ddmath.module.errorbook.adapter;

import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.tsinghuabigdata.edu.ddmath.MVPModel.ProductModel;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.bean.MyTutorClassInfo;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.event.RefreshStageviewEvent;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.KnowledgePiontBean;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.PdfViewActivity;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.bean.KnowPointCount;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.bean.StageReviewBean;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.bean.StageReviewResult;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.dialog.KnowledgePointDialog;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.view.OvalTextView;
import com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.activity.KnowledgeActivity;
import com.tsinghuabigdata.edu.ddmath.module.product.ProductUtil;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.EventBusUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 日日清作业列表适配器
 */

public class StageReviewAdapter extends ArrayAdapter<StageReviewBean> {

    private Context mContext;
    private StageReviewResult stageReviewResult;

    public StageReviewAdapter(Context context ) {
        super(context, 0);
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(GlobalData.isPad()?R.layout.item_ebook_stagereview : R.layout.item_ebook_stagereview_phone, parent, false);
            convertView.setTag(new ViewHolder(convertView));
        }
        ViewHolder viewHolder = ((ViewHolder) convertView.getTag());
        viewHolder.bindView( position );
        return convertView;
    }

    public void setStageReviewResult( StageReviewResult reviewResult){
        stageReviewResult = reviewResult;
    }
    //------------------------------------------------------------------------------------------
    class ViewHolder implements View.OnClickListener{

        private RelativeLayout mainLayout;
        private LinearLayout tipsLayout;
        private OvalTextView guideTextView;

        private int position;

        private TextView kwpointView;
        private TextView errorCountView;        //错误次数
        private TextView moreView;                      //更多知识点

        private TextView qinfoView;
        private TextView nameView;

        private RelativeLayout detailLayout;
        private ImageView detailView;

        private TextView priceView;
        private ImageView buyStatusView;

        private ImageView praticeType;                  //套题类型
        private TextView commandView;
        private RelativeLayout gerenatLayout;          //生成中

        //分享下载只能点击一次
        private AtomicBoolean btnShare = new AtomicBoolean( false );

        private ViewHolder(View convertView) {

            mainLayout = convertView.findViewById(R.id.main_layout);
            tipsLayout = convertView.findViewById(R.id.layout_float_tips);
            guideTextView = convertView.findViewById(R.id.tv_guide_tips);

            kwpointView    = convertView.findViewById( R.id.item_stageview_kwpoints );
            errorCountView = convertView.findViewById( R.id.item_stageview_errcount );
            moreView        = convertView.findViewById( R.id.item_stageview_morebtn );

            qinfoView       = convertView.findViewById( R.id.item_stageview_masterinfo );
            nameView        = convertView.findViewById( R.id.item_stageview_examname );

            detailLayout    = convertView.findViewById( R.id.item_stageview_detaillayout );
            detailView    =  convertView.findViewById( R.id.item_stageview_detailtext );

            priceView       = convertView.findViewById( R.id.item_stageview_price );
            buyStatusView   =  convertView.findViewById( R.id.item_stageview_buystatus );

            praticeType     = convertView.findViewById( R.id.iv_praticetype );                  //套题类型
            commandView = convertView.findViewById(R.id.tv_refine_command);

            gerenatLayout   = convertView.findViewById( R.id.rl_pratice_generating );          //生成中

            detailLayout.setOnClickListener( this );
            //shareLayout.setOnClickListener( this );
            moreView.setOnClickListener( this );
            guideTextView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            switch( v.getId() ){
                case R.id.tv_guide_tips:{
                    Intent intent = new Intent( getContext(), KnowledgeActivity.class);
                    intent.putExtra( KnowledgeActivity.PARAM_FROM_TYPE, AppConst.FROM_BROWER);
                    getContext().startActivity(intent);
                    break;
                }
                case R.id.item_stageview_morebtn:{
                    StageReviewBean bean = getItem( position );
                    if( bean == null || bean.getErrorKnowledgeInfoList()==null || bean.getErrorKnowledgeInfoList().size()==0 ) return;

                    String firstId = bean.getErrorKnowledgeInfoList().get(0).getKnowledge().getKnowledgeId();
                    ArrayList<KnowledgePiontBean> list = new ArrayList<>();
                    for( KnowPointCount knowPointCount : bean.getErrorKnowledgeInfoList() ){
                        KnowledgePiontBean knowledgePiontBean = knowPointCount.getKnowledge();
                        list.add( knowledgePiontBean );
                        knowledgePiontBean.setErrorTimes( knowPointCount.getErrorCount() );
                    }

                    KnowledgePointDialog dialog = new KnowledgePointDialog(mContext,  R.style.FullTransparentDialog );
                    dialog.setData( bean.getTitle(), bean.getQuestionNum(), firstId, list );
                    dialog.show();
                    break;
                }
                case R.id.item_stageview_detaillayout:{
                    clickJudgeUserPriviledge();
                    break;
                }
//                case R.id.item_stageview_sharelayout:{
//                }
                default:
                    break;
            }
        }

        private void clickJudgeUserPriviledge(){
            final StageReviewBean bean = getItem( position );
            if( bean == null ) return;

            final UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
            final LoginInfo loginInfo = AccountUtils.getLoginUser();
            final MyTutorClassInfo classInfo = AccountUtils.getCurrentClassInfo();
            if( detailinfo==null || loginInfo == null || classInfo == null ){
                ToastUtils.show( mContext, "请登录", Toast.LENGTH_SHORT );
                return;
            }

            if( btnShare.get() ){
                ToastUtils.show( mContext,"请稍候...");
                return;
            }
            btnShare.set( true );

            //final int price = bean.getChargeDdAmt() *( bean.getRightCount()+bean.getWrongCount());
            //先判断是否已购买
            if( !bean.isHasPrivilege() && bean.getDownloadStatus()==0 ){
                ProductUtil.productCheckPermissionAndExchange( mContext, "", bean.getPrivilegeId(), bean.getQuestionsId(), "错题浏览本", btnShare, ProductUtil.FROM_NORMAL, new ProductUtil.ProductCallBack(){
                    @Override
                    public void onSuccess() {
                        //兑换成功 处理   状态改变
                        bean.setHasPrivilege( true );
                        notifyDataSetChanged();

                        //如果消耗的是免费使用权，则更新界面
                        int freeCount = stageReviewResult.getFreeUseTimes();
                        if( freeCount > 0 ){
                            EventBusUtils.postDelay(new RefreshStageviewEvent(), new Handler());
                        }
                        //兑换成功提示
                        gotoDetailActivity();
                    }
                } );
            }else{
                gotoDetailActivity();
                btnShare.set( false );
            }
        }
        //打开题目详情界面
        private void gotoDetailActivity(){
            StageReviewBean bean = getItem( position );
            if( bean == null ) return;
            PdfViewActivity.startPreviewPdfViewActivity( mContext, bean );
        }
        void bindView( int position ){
            this.position = position;

            Resources resources = mContext.getResources();

            StageReviewBean taskBean = getItem(position);
            if( taskBean == null ) return;
            if( taskBean.getShowType() != 0 ){
                mainLayout.setVisibility(View.GONE);
                tipsLayout.setVisibility( View.GONE );
                guideTextView.setVisibility(View.VISIBLE);
                String text = "平台每周会把本周的全部错题，智能生成错题浏览本；如需让平台生成自定义近期时间段的全部错题，请去 ";
                String key  = "创建自定义错题浏览本";
                guideTextView.setText( text, key, guideTextView.getTextSize() );
                return;
            }
            mainLayout.setVisibility(View.VISIBLE);
            tipsLayout.setVisibility(View.VISIBLE);
            guideTextView.setVisibility(View.GONE);

            ArrayList<KnowPointCount> list = taskBean.getErrorKnowledgeInfoList();
            kwpointView.setVisibility( View.GONE );
            errorCountView.setVisibility( View.GONE );
            if( list!=null && list.size()>=1){
                KnowPointCount knowPointCount = list.get(0);
                kwpointView.setVisibility( View.VISIBLE );
                kwpointView.setText( knowPointCount.getKnowledge().getKnowledgeName() );

                errorCountView.setVisibility( View.VISIBLE );
                errorCountView.setText( String.format(Locale.getDefault(),"错误：%d次", knowPointCount.getErrorCount() ) );
            }

            String data = String.format( resources.getString( R.string.ebook_stage_master ), taskBean.getRightCount() ) + String.format(resources.getString( R.string.ebook_stage_unmaster ), taskBean.getWrongCount() );
            qinfoView.setText( data );

            String title = taskBean.getTitle()+"("+(taskBean.getQuestionNum())+"题)";
            nameView.setText( title );

            //已兑换的，提示，不显示价格
            buyStatusView.setVisibility( View.GONE );
            if( stageReviewResult!=null && (taskBean.isHasPrivilege()|| taskBean.getDownloadStatus()==1) ) {
                buyStatusView.setVisibility(View.VISIBLE);
                priceView.setVisibility(View.GONE);
            }

            //是否显示价格
            priceView.setText(String.format(Locale.getDefault(), "%d学豆", taskBean.getChargeDdAmt() *( taskBean.getRightCount()+taskBean.getWrongCount())) );

            //无限次使用权时不显示学豆 或者 已购买
            //boolean hidden = stageReviewResult.getFreeUseTimes()<0 || (taskBean.isHasPrivilege()|| taskBean.getDownloadStatus()==1);
            //priceView.setVisibility( hidden? View.GONE:View.VISIBLE );
            priceView.setVisibility(View.GONE);

            //题目类型显示
            commandView.setVisibility( View.GONE );
            if( AppConst.PAPER_TYPE_CUSTOM == taskBean.getPaperType() ){
                praticeType.setImageResource( R.drawable.icon_custom );
            }else{
                praticeType.setImageResource( R.drawable.icon_week );
                //commandView.setVisibility( View.VISIBLE );
            }

            //没有PDF生成中
            detailView.setImageResource( R.drawable.sel_btn_detail );
            if( AppConst.PAPER_TYPE_CUSTOM == taskBean.getPaperType() && TextUtils.isEmpty(taskBean.getPdfUrl()) ){
                gerenatLayout.setVisibility( View.VISIBLE );
                detailLayout.setVisibility( View.GONE );
                //shareLayout.setVisibility( View.GONE );
            }else{
                gerenatLayout.setVisibility( View.GONE );
                detailLayout.setVisibility( View.VISIBLE );
                //shareLayout.setVisibility( View.VISIBLE );
            }
        }

//        private String getKnowPointData( int index, KnowPointCount knowPointCount ){
//            KnowledgePiontBean knowledge = knowPointCount.getKnowledge();
//            if( knowledge == null ) return "";
//            return String.valueOf(index)+"."+knowledge.getKnowledgeName()+"   错误"+knowPointCount.getErrorCount()+"次";
//        }

        //兑换套题商品,不区分用免费使用权 还是 学豆，服务器后台处理
        private void exchangePracticeProduct( /*final LoginInfo loginInfo,*/ final UserDetailinfo detailinfo, final MyTutorClassInfo classInfo, String privilegeId, final StageReviewBean bean ){
            //开始兑换
            new ProductModel().exchangePracticeProduct( detailinfo.getStudentId(), classInfo.getClassId(), "", privilegeId, bean.getQuestionsId(), new RequestListener() {
                @Override
                public void onSuccess(Object res) {


                }

                @Override
                public void onFail(HttpResponse response, Exception ex) {
                    String data = response.getInform();
                    if( TextUtils.isEmpty(data) )
                        data = "抱歉，本次操作失败，需要重新兑换。";

                    if( "您已经兑换过该套题".equals( data ) ){
                        bean.setHasPrivilege( true );
                        notifyDataSetChanged();
                        AlertManager.showCustomImageBtnDialog(getContext(), data, "返回", null, null );
                    }else{
                        data = "兑换失败，再来一次吧！";
                        AlertManager.showCustomImageBtnDialog(getContext(), data, "再次发起兑换", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //再次购买
                                exchangePracticeProduct( /*loginInfo,*/ detailinfo, classInfo, bean.getPrivilegeId(), bean );
                            }
                        }, null );
                    }
                }
            });
        }

//        //分享
//        private void shareUrl( final LoginInfo loginInfo, final UserDetailinfo detailinfo, final StageReviewBean bean ){
//
//            ErrorBookModel bookModel = new ErrorBookModel();
//            bookModel.queryStageReviewShare( detailinfo.getStudentId(), bean.getQuestionsId(), new RequestListener<String>() {
//                @Override
//                public void onSuccess(String path) {
//                    //
//                    if( TextUtils.isEmpty(path) ){
//                        ToastUtils.show( getContext(), R.string.nofind_practice );
//                    }
//                    //没有购买，不能分享 抛出异常处理
////                    else if( shareBean.getBuy()==0 ) {
////                        ToastUtils.show( getContext(), R.string.share_after_buy );
////                    }
//                    //开始分享
//                    else{
//                        try {
//                            String turl = loginInfo.getFileServer() + path + "?filename=" + URLEncoder.encode( detailinfo.getReallyName()+"_"+bean.getTitle()+".pdf","utf-8");
//                            //ShareUtils.shareUrl( mContext, turl );
//                            ShareDialog.showShaerDialog(mContext, bean.getTitle(), turl, bean.getCreateTime() )
//                                    .setScoreEventId(ScoreEventID.EVENT_DOWNLOAD_WRONG_BOOK)
//                                    .setContentId( bean.getQuestionsId() );
//                        }catch (Exception e){
//                            AppLog.i( e.toString(), e);
//                        }
//                    }
//                    btnShare.set(false);
//                }
//
//                @Override
//                public void onFail(HttpResponse response, Exception ex) {
//                    btnShare.set(false);
//                    if( 20236 == response.getCode() ){
//                        ToastUtils.show( getContext(), R.string.share_after_buy );
//                    }else{
//                        ToastUtils.show( getContext(), R.string.server_error );
//                    }
//                }
//            });
//        }
    }
}

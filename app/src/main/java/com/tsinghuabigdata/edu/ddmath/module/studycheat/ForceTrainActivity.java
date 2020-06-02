package com.tsinghuabigdata.edu.ddmath.module.studycheat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ScrollView;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.AppAsyncTask;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.QuestionTextView;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.WorkToolbar;
import com.tsinghuabigdata.edu.ddmath.module.studycheat.bean.AbilityQueryBean;
import com.tsinghuabigdata.edu.ddmath.module.studycheat.bean.FocreTrainQuestionInfo;
import com.tsinghuabigdata.edu.ddmath.module.studycheat.dailog.QuestionReviewDialog;
import com.tsinghuabigdata.edu.ddmath.requestHandler.StudyCheatService;
import com.tsinghuabigdata.edu.ddmath.requestHandler.requestImpl.StudyCheatServiceImpl;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.view.LoadingPager;
import com.tsinghuabigdata.edu.ddmath.view.MultListView;
import com.umeng.analytics.MobclickAgent;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

/**
 * 强化练习
 */
public class ForceTrainActivity extends RoboActivity {

    public static final String RESULT_PARAM = "reward";


    @ViewInject(R.id.work_toolbar)
    private WorkToolbar workToolbar;

    @ViewInject( R.id.progress_animationbar )
    private LoadingPager mLoadingPager;
    @ViewInject(R.id.forcetrain_mainScrollView)
    private ScrollView mainScrollView;

    @ViewInject( R.id.forcetrain_stemview )
    private QuestionTextView stemTextView;

    @ViewInject( R.id.forcetrain_listview)
    private MultListView mListView;

    private StudyCheatService studyCheatService;

    private Context mContext;

    private boolean isNextQuestion = false;            //再来一题的标志

    private ArrayList<AnswerOptionItem> optionList;
    private AnswerOptionAdapter answerOptionAdapter;

    private GetQuestionDetailTask mGetQuestionDetailTask;
    private SubmitAnswerTask mSubmitAnswerTask;

    private FocreTrainQuestionInfo mQuestionInfo;
    private long startTime = 0, endtime = 0;
    private ProgressDialog mProgressDialog;

    public void onCreate(Bundle savedInstanceState){
        transparent = true;
        super.onCreate(savedInstanceState);

        //设置全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView( GlobalData.isPad()?R.layout.activity_forcetrain:R.layout.activity_forcetrain_phone );
        mContext = this;

        x.view().inject( this );

        workToolbar.setTitle( "强化训练" );
        workToolbar.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLeftClick();
            }
        }, null);

        studyCheatService = new StudyCheatServiceImpl();
        initView();

        loadQuestion();
        MobclickAgent.onEvent( this, "cheat_frocetrain" );
    }

    public void onLeftClick() {
        quit( false, 0 );
    }

    @Override
    public void onBackPressed(){
        quit( false, 0 );
    }

    //-----------------------------------------------------------------

    private void initView(){

        optionList = new ArrayList<>();

        answerOptionAdapter = new AnswerOptionAdapter(mContext);
        mListView.setAdapter( answerOptionAdapter );

        //异常重试
        mLoadingPager.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadQuestion();
            }
        });

        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);
    }

    private void quit( boolean hasReward, float vaule ){

        if( hasReward ){
            Intent intent = new Intent();
            intent.putExtra( RESULT_PARAM, vaule );
            setResult( RESULT_OK, intent );
            finish();
        }else {
            if( mQuestionInfo==null ) {
                finish();
                return;
            }
            QuestionReviewDialog dialog = new QuestionReviewDialog(mContext, R.style.FullTransparentDialog );
            dialog.setData( "放弃本次强化训练吗？到手的奖励就溜走了哦～", "放弃", "取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent();
                    //intent.putExtra( RESULT_PARAM, vaule );
                    setResult( RESULT_OK, intent );
                    finish();
                }
            }, null);
            dialog.show();
        }
    }
    /**
     * 加载题目
     */
    private void loadQuestion() {
        if (mGetQuestionDetailTask == null || mGetQuestionDetailTask.isComplete() || mGetQuestionDetailTask.isCancelled()) {
            mQuestionInfo = null;
            mLoadingPager.showLoading();
            mainScrollView.setVisibility(View.INVISIBLE);
            mGetQuestionDetailTask = new GetQuestionDetailTask();
            mGetQuestionDetailTask.execute();
        }
    }

    /**
     * 答完一题处理
     */
    private void finishOneAnswer( String answer ){

        if( mQuestionInfo == null ) return;
        if( endtime == 0 ) endtime = System.currentTimeMillis();

        long time = (endtime - startTime )/1000;

        if( mSubmitAnswerTask==null || mSubmitAnswerTask.isComplete() || mSubmitAnswerTask.isCancelled() ){
            mProgressDialog.setMessage( getResources().getString(R.string.wait_submit) );
            mProgressDialog.show();

            mSubmitAnswerTask = new SubmitAnswerTask( answer, time );
            mSubmitAnswerTask.execute();
        }
    }

    //--------------------------------------------------------------
    //答案选项
    private class AnswerOptionItem{

        AnswerOptionItem( String index, String content ){
            this.indexStr= index+"";
            this.content = content;
        }
        String indexStr;
        String content;
    }

    private class AnswerOptionAdapter extends BaseAdapter{

        private Context mContext;
        private LayoutInflater mInflater;

        AnswerOptionAdapter(Context context) {
            this.mContext = context;
            mInflater=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public int getCount() {
            if( optionList == null ) return 0;
            return optionList.size();
        }
        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;
            if(convertView==null){
                convertView = mInflater.inflate( GlobalData.isPad()?R.layout.listview_answeroption_item:R.layout.listview_answeroption_item_phone, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag( holder );
            }else{
                holder=(ViewHolder)convertView.getTag();
            }

            //--------------------------------------------------------------------------------
            AnswerOptionItem item = optionList.get( position );
            holder.position = position;
            holder.indexTextView.setText( item.indexStr );
            //holder.indexTextView.setVisibility( View.GONE );
            holder.contentTextView.setQuestionContent( item.content );
            //holder.contentTextView.setSubStem();
            return convertView;
        }

        private class ViewHolder implements View.OnClickListener{

            private int position;
            private TextView indexTextView ;       //答案序号
            private QuestionTextView contentTextView;        //内容

            public ViewHolder(View root ){

                indexTextView          = (TextView) root.findViewById( R.id.answeroption_indexView );
                contentTextView        = (QuestionTextView)root.findViewById( R.id.answeroption_contentView );

                indexTextView.setOnClickListener( this );
            }

            @Override
            public void onClick(View v) {
                AnswerOptionItem item = optionList.get( position );
                finishOneAnswer( item.indexStr );
            }
        }
    }

    //根据questionid 得到题目详情
    private class GetQuestionDetailTask extends AppAsyncTask<String, Void, FocreTrainQuestionInfo> {

        private ArrayList<AnswerOptionItem> tmplist;

        @Override
        protected FocreTrainQuestionInfo doExecute(String... params) throws Exception {
            tmplist = null;
            LoginInfo loginInfo = AccountUtils.getLoginUser();
            UserDetailinfo userDetailinfo = AccountUtils.getUserdetailInfo();

            if( loginInfo == null || userDetailinfo==null ) throw new Exception("请登录");

            FocreTrainQuestionInfo questionInfo = studyCheatService.getForceTrainQuestion( userDetailinfo.getStudentId(), loginInfo.getAccessToken(), isNextQuestion?1:0 );

            //对答案选项进行分析处理
            tmplist = new ArrayList<>();
            if( questionInfo!=null && !TextUtils.isEmpty(questionInfo.getSubStem())){
                String subStem = QuestionTextView.replaceImageLatexTags( questionInfo.getSubStem(), questionInfo.getSubStemGraph(), questionInfo.getSubStemLatexGraph(), loginInfo );
                String answerSelects[] = subStem.split("#%#");
                int index = 0;
                for( String select : answerSelects ){
                    String indexStr = Character.toString( (char)('A'+index++) );
                    tmplist.add( new AnswerOptionItem( indexStr, select.replace( indexStr+".", "" )) );
                }
            }
            return questionInfo;
        }

        @Override
        protected void onResult(FocreTrainQuestionInfo question) {
            mLoadingPager.hideall();
            mainScrollView.setVisibility(View.VISIBLE);
            mQuestionInfo = question;
            //
            if( question==null ){
                AlertManager.toast( mContext, getResources().getString(R.string.nodata_froce_train));
                finish();
            }else{

                startTime = System.currentTimeMillis();
                endtime   = 0;            //每次加载

                stemTextView.setQuestion( question, false );    //显示主题干

                if( tmplist!=null ){
                    optionList.clear();
                    optionList.addAll( tmplist );
                    answerOptionAdapter.notifyDataSetChanged();
                }
            }
        }

        @Override
        protected void onFailure(HttpResponse<FocreTrainQuestionInfo> response, Exception ex) {
            mLoadingPager.showFault( ex );
            AlertManager.showErrorInfo( mContext, ex);
        }

    }

    //提交答案
    private class SubmitAnswerTask extends AppAsyncTask<String, Void, AbilityQueryBean> {

        private String answer;
        private long time;

        SubmitAnswerTask( String answer, long time ){
            this.answer = answer;
            this.time  = time;
        }
        @Override
        protected AbilityQueryBean doExecute(String... params) throws Exception {
            LoginInfo loginInfo = AccountUtils.getLoginUser();
            UserDetailinfo userDetailinfo = AccountUtils.getUserdetailInfo();

            if( loginInfo == null || userDetailinfo==null ) throw new Exception("请登录");
            return studyCheatService.submitForceTrainAnswer( loginInfo.getAccessToken(), userDetailinfo.getStudentId(),  mQuestionInfo.getQuestionId(), isNextQuestion?1:0, answer, time, mQuestionInfo.getRequestId() );
        }

        @Override
        protected void onResult(final AbilityQueryBean abilityBean) {
            mProgressDialog.dismiss();
            //
            if( abilityBean==null ){
                AlertManager.toast( mContext, "提交答案失败");
                return;
            }

            //显示结果
            QuestionReviewDialog dialog = new QuestionReviewDialog(mContext, R.style.FullTransparentDialog );

            final boolean hasQuestion = abilityBean.getSurplus()!=0;
            final boolean hasChance = abilityBean.getRemainChance()!=0;

            if( abilityBean.isCorrect() ){  //答对
                dialog.setFinishReviewData( abilityBean, hasQuestion?"再来一题":"真棒，全部完成了！", "休息一下",
                        new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if( hasQuestion ){
                                    isNextQuestion = true;
                                    loadQuestion();
                                }else{
                                    quit( true, abilityBean.getIncrease() );
                                }
                            }
                        },new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                quit( true, abilityBean.getIncrease() );
                            }
                        }   );
            }else{      //答错

                //还有机会
                if( hasChance ){
                    dialog.setErrorData( getResources().getString(R.string.forcetrain_answer_error), abilityBean.getRemainChance(), abilityBean.getTotalChance(), "重新挑战", new DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //直接返回
                        }
                    } );
                }else{          //机会已经用完
                    String tips;
                    if( abilityBean.getErrorRate() >= 0.4f ){
                        tips = getResources().getString(R.string.forcetrain_answer_errpart);
                        String rate = String.valueOf( (int)(abilityBean.getErrorRate()*100) );
                        tips = tips.replace("x", rate);
                    }else{
                        tips = getResources().getString(R.string.forcetrain_answer_errall);
                    }

                    dialog.setErrorData( tips, abilityBean.getRemainChance(), abilityBean.getTotalChance(), "休息一下", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            quit( true, abilityBean.getIncrease() );
                        }
                    } );
                }
            }
            dialog.show();
        }

        @Override
        protected void onFailure(HttpResponse<AbilityQueryBean> response, Exception ex) {
            mProgressDialog.dismiss();
            //AlertManager.showErrorInfo( mContext, ex);
            AlertManager.toast( mContext, "提交答案失败");
        }
    }

}

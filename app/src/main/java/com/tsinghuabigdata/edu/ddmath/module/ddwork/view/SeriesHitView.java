package com.tsinghuabigdata.edu.ddmath.module.ddwork.view;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.drawerlayout.widget.DrawerLayout;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.DDWorkDetail;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.LocalQuestionInfo;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.ShareBean;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.dialog.ShareWorkDialog;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.PreferencesUtils;

import java.util.ArrayList;

/**
 * 连击View
 */
public class SeriesHitView extends LinearLayout implements View.OnClickListener, ShareWorkDialog.ShareListener{

    //两层界面
    private MyRelativeLayout maintoolLayout;
    //private RelativeLayout mainshareLayout;

    //tool层
    private LinearLayout hitLayout;
    private TextView hitCountView;
//    private ImageView hitShareImage;

    private RelativeLayout hitShareLayout;
    private TextView hitCountView1;

    //
    private ShareWorkDialog shareDialog;
    private DrawerLayout mDrawerLayout;

    DDWorkDetail workDetail;

    public SeriesHitView(Context context) {
        super(context);
        init();
    }

    public SeriesHitView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SeriesHitView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_closehitview:{
                setVisibility(GONE);
                if( workDetail!=null )
                PreferencesUtils.putBoolean( getContext(), "SeriesHitView_"+workDetail.getWorkId(), true);
                break;
            }
            case R.id.view_shareseries_closehitlayout:
                hitLayout.setVisibility( View.GONE );
                hitShareLayout.setVisibility( View.VISIBLE );
                maintoolLayout.setInterceptEvent( false );
                if( mDrawerLayout!=null )mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.END);
                break;
            case R.id.view_shareseries_hitshare:
                hitLayout.setVisibility( View.GONE );
                hitShareLayout.setVisibility( View.VISIBLE );
                maintoolLayout.setInterceptEvent( false );
                if( mDrawerLayout!=null )mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.END);
                changeShowShareView();
                break;
            case R.id.view_shareseries_sharebtnlayout:
                changeShowShareView();
                break;
            default:
                break;
        }
    }

    public void setData(DDWorkDetail detail, DrawerLayout drawerLayout){
        workDetail = detail;
        mDrawerLayout = drawerLayout;
        //是否批阅完成
        if( detail.getExerStatus() < DDWorkDetail.WORK_CORRECTED ){
            setVisibility( GONE );
            if( mDrawerLayout!=null )mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.END);
            return;
        }

        //第一次查看
        if( isFirstLook( detail.getWorkId() ) ){
            hitLayout.setVisibility( View.VISIBLE );
            hitShareLayout.setVisibility( View.GONE );
            maintoolLayout.setInterceptEvent( true );
        }
        //
        else {
            hitLayout.setVisibility( View.GONE );
            hitShareLayout.setVisibility( View.VISIBLE );
            if( mDrawerLayout!=null )mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.END);
        }

        //统计连击数量
        ShareBean shareBean = getSeriesHitData(detail);
        hitCountView.setText( String.valueOf(shareBean.getValue()) );
        hitCountView1.setText( String.valueOf(shareBean.getValue()) );

        ArrayList<ShareBean> list = getShareData( detail );
        shareDialog.setData( detail.getShareUrl(), list );
        shareDialog.setConetntId( detail.getWorkId() );

        //list.size>0 && 连击数》=3
        if(list.size() != 0 && shareBean.getValue()>=3){
            setVisibility( View.VISIBLE );
        }else{
            setVisibility( View.GONE );
            if( mDrawerLayout!=null )mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.END);
        }

        if( PreferencesUtils.getBoolean( getContext(), "SeriesHitView_"+workDetail.getWorkId(), false) ){
            setVisibility( GONE );
        }
    }

    @Override
    public void dealShare() {
        maintoolLayout.setVisibility( View.VISIBLE );
        //mainshareLayout.setVisibility( View.GONE );
    }
    //-----------------------------------------------------------------------------
    private void init() {

        inflate( getContext(), GlobalData.isPad()?R.layout.view_ddwork_serieshit:R.layout.view_ddwork_serieshit_phone, this );

        maintoolLayout  = findViewById( R.id.view_shareseries_toollayout );
        shareDialog = new ShareWorkDialog( getContext(), R.style.FullTransparentDialog );
        shareDialog.setShareListener( this );

        RelativeLayout closeLayout = findViewById( R.id.view_shareseries_closehitlayout );
        closeLayout.setOnClickListener( this );
        hitLayout       = findViewById( R.id.view_shareseries_hitlayout );
        hitCountView    = findViewById( R.id.view_shareseries_hitcount );
        ImageView hitShareImage   = findViewById( R.id.view_shareseries_hitshare );
        hitShareImage.setOnClickListener( this );

        hitShareLayout  = findViewById( R.id.view_shareseries_sharebtnlayout );
        hitShareLayout.setOnClickListener( this );
        hitCountView1   = findViewById( R.id.view_shareseries_hitcount1 );

        closeLayout = findViewById(R.id.layout_closehitview);
        closeLayout.setOnClickListener(this);

        //
        //shareView       = (ShareView)findViewById( R.id.view_shareseries_shareview );
        //shareView.setShareListener( this );
    }

    private boolean isFirstLook(String examId){
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("WorkSeriesHit", Activity.MODE_PRIVATE);
        boolean show = sharedPreferences.getBoolean( examId, false );
        if( !show ){
            sharedPreferences.edit().putBoolean( examId, true ).apply();
        }
        return !show;
    }

    private void changeShowShareView(){
        maintoolLayout.setVisibility( View.GONE );
        //mainshareLayout.setVisibility( View.VISIBLE );
        shareDialog.show();
    }
    //
    private int getRightCount( DDWorkDetail detail ){
        int count = 0;
        for(LocalQuestionInfo questionInfo : detail.getPageInfo().get(0).getQuestions() ){
            if( questionInfo.isCorrect() ){
                count++;
            }
        }
        return count;
    }

    //获得连击的数据
    private ShareBean getSeriesHitData( DDWorkDetail detail ){

        ShareBean shareBean = new ShareBean( ShareBean.TYPE_SERIESHIT );

        int times    = 0;   //出现次数
        int hitCount = 0;   //连击次数
        int maxHitCount= 0; //最大连击次数
        int maxTimes   = 0; //
        for(LocalQuestionInfo questionInfo : detail.getPageInfo().get(0).getQuestions() ){
            if( questionInfo.isCorrect() ){
                hitCount++;
                if( times == 0 ) times++;
            }
            //错误时，一个代表统计结束
            else{
                //开头就是错题，则下一个
                if( hitCount==0 ) {
                    continue;
                }

                //一次统计完成，如果大于max，更新max值
                if( hitCount>maxHitCount ){
                    maxHitCount = hitCount;
                    maxTimes    = times;
                }else if( hitCount == maxHitCount ){    //
                    maxTimes++;
                }
                //else 不处理

                //准备下一个
                hitCount = 0;
                times    = 0;
            }
        }

        //整个结束
        if( hitCount > 0 ){
            if( hitCount>maxHitCount ){
                maxHitCount = hitCount;
                maxTimes    = times;
            }else if( hitCount == maxHitCount ){    //
                maxTimes++;
            }
        }

        shareBean.setValue( maxHitCount );
        shareBean.setTimes( maxTimes );
        return shareBean;
    }

    //分析数据，确定优先级
    private ArrayList<ShareBean> getShareData( DDWorkDetail detail ){

        //优先级列表
        ArrayList<ShareBean> firstList = new ArrayList<>();
        ArrayList<ShareBean> secondList = new ArrayList<>();

        ShareBean shareBean;
        //正确题数
        int right = getRightCount( detail );
        if( right >= 5 ){
            shareBean = new ShareBean( ShareBean.TYPE_RIGHTALL );
            shareBean.setValue( right );
            firstList.add( shareBean );
        }

        //连击
        shareBean = getSeriesHitData(detail);
        if( shareBean.getValue() >= 5 ){
            firstList.add( shareBean );
        }else  if( shareBean.getValue() >= 3 ){
            secondList.add( shareBean );
        }

        //提交排名
        if( detail.getSubmitRank() > 0 ){
            shareBean = new ShareBean( ShareBean.TYPE_SUBMITWORK );
            shareBean.setValue( detail.getSubmitRank() );
            if( detail.getSubmitRank() <= 5 ){
                firstList.add( shareBean );
            }else if( detail.getSubmitRank() <= 5 ){
                secondList.add( shareBean );
            }
        }

        //准确率
        int accuracy = (int)(detail.getAccuracy()*100+0.5);
        shareBean = new ShareBean( ShareBean.TYPE_RIGHTRATE );
        shareBean.setValue( accuracy );
        shareBean.setName( detail.getQuestionScore() > 0?"得分率":"正确率" );
        if( accuracy >= 90){
            firstList.add( shareBean );
        }else if( accuracy >= 80 ){
            secondList.add( shareBean );
        }

        //班级排名
        if( detail.getClassRank() > 0 ){
            shareBean = new ShareBean( ShareBean.TYPE_CLASSRANK );
            shareBean.setValue( detail.getClassRank() );
            if( detail.getClassRank() <= 5 ){
                firstList.add( shareBean );
            }else if( detail.getClassRank() <= 10 ){
                secondList.add( shareBean );
            }
        }

        //获得要显示的数据
        ArrayList<ShareBean> list = new ArrayList<>();
        //先first
        if( firstList.size() < 3 ){
            list.addAll( firstList );
            int len = 3 - firstList.size();
            len = len<secondList.size()?len:secondList.size();
            list.addAll( secondList.subList(0,len) );
        }else{
            list.addAll( firstList.subList(0,3) );
        }
        return list;
    }
}

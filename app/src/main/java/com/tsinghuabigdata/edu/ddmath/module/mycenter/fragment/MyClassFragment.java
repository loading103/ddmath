package com.tsinghuabigdata.edu.ddmath.module.mycenter.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.bean.MyTutorClassInfo;
import com.tsinghuabigdata.edu.ddmath.bean.QueryTutorClassInfo;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.dialog.CustomDialogNew;
import com.tsinghuabigdata.edu.ddmath.event.ChangeClassEvent;
import com.tsinghuabigdata.edu.ddmath.event.UpdateClassEvent;
import com.tsinghuabigdata.edu.ddmath.fragment.MyBaseFragment;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.ClassCodeJoinClassActivity;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.adapter.TutorClassAdapter;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.DensityUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.edu.ddmath.view.NoClassView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 我的班级
 * Created by Administrator on 2017/9/4.
 */

public class MyClassFragment extends MyBaseFragment {

    private NoClassView mNoClassView;

    private LinearLayout    mainLayout;

    private TextView        joinClassView;

    private ListView        mLvClass;

    //private ChangeClassDialog mChangeClassDialog;

    private TutorClassAdapter mAdapter;
    private List<MyTutorClassInfo>        mClassInfoList = new ArrayList<>();
    //private Map<String, MyTutorClassInfo> classFlagMap   = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root;
        if (GlobalData.isPad()) {
            root = inflater.inflate(R.layout.fragment_my_class, container, false);
        } else {
            root = inflater.inflate(R.layout.fragment_my_class_phone, container, false);
        }
        initView(root);
        setPrepared();
        initData();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    public String getUmEventName() {
        return "mycenter_myclass";
    }
    private void initView(View root) {
//        mLlNoClassPic = (LinearLayout) root.findViewById(R.id.ll_no_class_pic);
        mNoClassView=  root.findViewById(R.id.noClassView);

        mainLayout  = root.findViewById( R.id.ll_main_layout );

        joinClassView = root.findViewById( R.id.tv_join_classes );
        Button joinClassBtn = root.findViewById( R.id.iv_join_class );
        joinClassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ClassCodeJoinClassActivity.class);
                startActivity(intent);
            }
        });

        mLvClass =  root.findViewById(R.id.lv_class);
        mLvClass.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final MyTutorClassInfo classInfo = mClassInfoList.get(position);
                if (MyTutorClassInfo.TYPE_UNJOIN.equals(classInfo.getType())) {
                    ToastUtils.showShort(getContext(), "请选择正式班级");
                } else if (!AccountUtils.getCurrentClassId().equals(classInfo.getClassId())) {

                    CustomDialogNew dialog = AlertManager.showCustomImageBtnDialog( getContext(), "", "确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            changeClass(classInfo.getClassId());
                        }
                    }, null);
                    String changeClass  = "确定切换班级:                                         \n";
                    String schoolName   = classInfo.getSchoolName()+"\n";
                    String className    = classInfo.getClassName();
                    Spannable span = new SpannableString( changeClass+schoolName+className );
                    int start = 0, end = changeClass.length();
                    span.setSpan( new AbsoluteSizeSpan(DensityUtils.sp2px( getContext(), GlobalData.isPad()?24:16 )), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    span.setSpan( new ForegroundColorSpan(getResources().getColor(R.color.color_666666)), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    start   = end;
                    end     = start + schoolName.length();
                    span.setSpan( new AbsoluteSizeSpan(DensityUtils.sp2px( getContext(), GlobalData.isPad()?24:16 )), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    span.setSpan( new ForegroundColorSpan(getResources().getColor(R.color.color_333333)), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    start   = end;
                    end     = start + className.length();
                    span.setSpan( new AbsoluteSizeSpan(DensityUtils.sp2px( getContext(), GlobalData.isPad()?24:16 )), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    span.setSpan( new ForegroundColorSpan(getResources().getColor(R.color.color_333333)), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

                    //整体粗体
                    //span.setSpan( new StyleSpan(Typeface.BOLD), 0, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

                    dialog.setTextView( span );
                    dialog.show();
                }
            }
        });
    }

    private void initData() {
        showClassInfo();
        EventBus.getDefault().register(this);
    }

    private void changeClass(String id) {
        GlobalData.setClassId(id);
        changeCurrClassToFirst();
        mAdapter.notifyDataSetChanged();
        EventBus.getDefault().post(new ChangeClassEvent());
    }

    private void showClassInfo() {
        showQualityAnalyze();
        QueryTutorClassInfo res = null;
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if ( detailinfo!= null) {
            res = detailinfo.getQueryTutorClassInfo();
        }
        if (res == null || res.getClassInfos() == null || res.getClassInfos().size() == 0) {
            showClassLayout(false);
            return;
        }
        showClassLayout(true);
        joinClassView.setText( String.format(Locale.getDefault(),"已加入%d个班级", res.getClassInfos().size() ));
        mClassInfoList.clear();
        mClassInfoList.addAll(res.getClassInfos());
        changeCurrClassToFirst();
        if (mAdapter == null) {
            mAdapter = new TutorClassAdapter(getContext(), mClassInfoList);
            mLvClass.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    //调整当前班级信息到顶部
    private void changeCurrClassToFirst(){
        if( mClassInfoList.size() < 1 ) return;

        //
        String currClassId = GlobalData.getCurrClassId();
        if(TextUtils.isEmpty( currClassId ) ){
            currClassId = mClassInfoList.get(0).getClassId();
            GlobalData.setClassId(currClassId);
        }
        MyTutorClassInfo currClassInfo = null;

        for( int i=1; i<mClassInfoList.size(); i++ ){
            MyTutorClassInfo classInfo = mClassInfoList.get(i);
            if( classInfo.getClassId().equals( currClassId) ){
                currClassInfo = classInfo;
                mClassInfoList.remove( classInfo );
                break;
            }
        }
        if( currClassInfo!=null ){
            mClassInfoList.add( 0, currClassInfo );
        }
    }

    private void showClassLayout(boolean hasClass) {
        if (hasClass) {
            mainLayout.setVisibility(View.VISIBLE);
            mNoClassView.setVisibility(View.GONE);
        } else {
            mainLayout.setVisibility(View.GONE);
            mNoClassView.setVisibility(View.VISIBLE);
        }
    }

    private void showQualityAnalyze() {
//        classFlagMap.clear();
//        MyTutorClassInfo unjoinClassInfo = AccountUtils.getFirstClassInfo(MyTutorClassInfo.TYPE_UNJOIN);
//        if (unjoinClassInfo != null) {
//            classFlagMap.put(unjoinClassInfo.getType(), unjoinClassInfo);
//        }
//        MyTutorClassInfo joinedClassInfo = AccountUtils.getFirstClassInfo(MyTutorClassInfo.TYPE_JOINED);
//        if (joinedClassInfo != null) {
//            classFlagMap.put(joinedClassInfo.getType(), joinedClassInfo);
//        }

//        if (!classFlagMap.containsKey(MyTutorClassInfo.TYPE_UNJOIN) && classFlagMap.containsKey(MyTutorClassInfo.TYPE_JOINED)) {
//            mLlEvaluateEnter.setVisibility(View.VISIBLE);
//        } else {
//            mLlEvaluateEnter.setVisibility(View.GONE);
//        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receive(UpdateClassEvent event) {
        AppLog.d("event = " + event );
        showClassInfo();
    }

}

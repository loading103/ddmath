package com.tsinghuabigdata.edu.ddmath.module.mycenter.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.adapter.CommonAdapter;
import com.tsinghuabigdata.edu.ddmath.adapter.ViewHolder;
import com.tsinghuabigdata.edu.ddmath.bean.MyTutorClassInfo;
import com.tsinghuabigdata.edu.ddmath.bean.TeacherBean;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.view.TeacherInfoView;

import java.util.List;
import java.util.Locale;

/**
 * 班级信息适配器
 * Created by Administrator on 2017/9/5.
 */

public class TutorClassAdapter extends CommonAdapter<MyTutorClassInfo> {

    public TutorClassAdapter(Context context, List<MyTutorClassInfo> mDatas) {
        super(context, mDatas);
    }

    @Override
    protected int getLayoutId() {
        if (GlobalData.isPad()) {
            return R.layout.item_tutorclass;
        } else {
            return R.layout.item_tutorclass_mobile;
        }
    }

    @Override
    protected void convert(ViewHolder helper, int position, MyTutorClassInfo item) {

        RelativeLayout classInfo    = helper.getView( R.id.rl_classinfo_layout );
        TextView schoolName          = helper.getView( R.id.tv_school_name );
        TextView className           = helper.getView( R.id.tv_class_name );
        TextView classCode           = helper.getView( R.id.tv_class_code );

        LinearLayout teacherLayout  = helper.getView( R.id.ll_teacher_container );
        RelativeLayout noteacherLayout= helper.getView( R.id.rl_noteacher_container );
        ImageView tagView = helper.getView(R.id.tag_view);

        //学校和班级信息
        schoolName.setText( item.getSchoolName() );
        className.setText( item.getClassName() );
        classCode.setText( String.format(Locale.getDefault(), "班级码：%s",item.getClassCode()) );

        //ImageView ivSelect = helper.getView(R.id.iv_select);
        //标签
        if (item.getType().equals(MyTutorClassInfo.TYPE_UNJOIN)) {
            tagView.setVisibility(View.VISIBLE);
            tagView.setImageResource( R.drawable.wait_class );
//            tagView.setColor1(mContext.getResources().getColor(R.color.tag_class_unjoin_color3));
//            tagView.setColor2(mContext.getResources().getColor(R.color.tag_class_unjoin_color1));
//            tagView.setColor3(mContext.getResources().getColor(R.color.tag_class_unjoin_color2));
//            tagView.setContent("待入学班",Color.rgb(0x99,0xAD,0xFA) );
//            tagView.updatePaint();
            classInfo.setBackground( mContext.getResources().getDrawable( R.drawable.bg_item_tutorclass_normal ) );
            schoolName.setTextColor( mContext.getResources().getColor( R.color.color_666666) );
            className.setTextColor( mContext.getResources().getColor( R.color.color_666666) );
            classCode.setTextColor( mContext.getResources().getColor( R.color.color_999999) );
        } else{
            boolean currClass = AccountUtils.getCurrentClassId().equals(item.getClassId());
            classInfo.setBackground( mContext.getResources().getDrawable( currClass?R.drawable.bg_item_tutorclass_current:R.drawable.bg_item_tutorclass_normal) );
            schoolName.setTextColor( mContext.getResources().getColor( currClass?R.color.white:R.color.color_666666) );
            className.setTextColor( mContext.getResources().getColor( currClass?R.color.white:R.color.color_666666) );
            classCode.setTextColor( mContext.getResources().getColor( currClass?R.color.zx80_white:R.color.color_999999) );
            tagView.setVisibility( currClass?View.VISIBLE:View.INVISIBLE );
            if( currClass ){
                tagView.setImageResource( R.drawable.current_class );
            }
        }

        //添加老师显示组件
        List<TeacherBean> teachers = item.getTeachers();
        if (teachers == null || teachers.size() == 0) {
            teacherLayout.setVisibility( View.GONE );
            noteacherLayout.setVisibility( View.VISIBLE );
        }else{
            teacherLayout.setVisibility( View.VISIBLE );
            noteacherLayout.setVisibility( View.GONE );

            teacherLayout.removeAllViews();
            //only show two teachers
            for (int i = 0; i < teachers.size() && i <= 1; i++) {
                TeacherBean teacherBean = teachers.get(i);
                teacherLayout.addView(new TeacherInfoView(mContext).setImg(teacherBean.getHeadImage())
                        .setText(teacherBean.getTeacherName()));
            }
        }

    }
}

package com.tsinghuabigdata.edu.ddmath.module.mycenter.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.bean.ClassBean;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.bean.ReturnClassBean;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Administrator on 2018/1/24.
 * 班级选择（单选模式）
 */

public class ClassSelectView extends RecyclerView {

    private List<SelectClassBean> classBeanList = new ArrayList<>();

    private SelClassAdapter adapter;

    class SelectClassBean {
        ClassBean classBean;
        boolean selected;

        /*public*/ SelectClassBean(ClassBean classBean, boolean selected) {
            this.classBean = classBean;
            this.selected = selected;
        }
    }

    public ClassSelectView(Context context) {
        super(context);
        init();
    }

    public ClassSelectView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ClassSelectView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        adapter = new SelClassAdapter();
        setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        setAdapter(adapter);
    }

    public void setClassDataList(List<ReturnClassBean> classBeans) {
        classBeanList.clear();
        List<ClassBean> beanList = classBeans.get(0).getClassVoList();
        for (int i = 0; i < beanList.size(); i++) {
            classBeanList.add(new SelectClassBean(beanList.get(i), false));
        }
        adapter.notifyDataSetChanged();
    }

    public ClassBean getSelectClassBean() {
        // 返回选中的班级
        ClassBean reBean = null;
        for (SelectClassBean bean : classBeanList) {
            if (bean.selected) {
                reBean = bean.classBean;
            }
        }
        return reBean;
    }

    private class SelClassAdapter extends RecyclerView.Adapter<SelClassAdapter.ViewHolder> {

        class ViewHolder extends RecyclerView.ViewHolder {
            RelativeLayout mLinearLayout;
            TextView className;
            TextView joinNum;
            TextView teacherName;

            public ViewHolder(View itemView) {
                super(itemView);
                mLinearLayout =  itemView.findViewById(R.id.ll_linearlayout);
                className =  itemView.findViewById(R.id.tv_class);
                joinNum =  itemView.findViewById(R.id.tv_join_total);
                teacherName =  itemView.findViewById(R.id.tv_teacher);
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    GlobalData.isPad() ? R.layout.item_sel_class : R.layout.item_sel_class_phone, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
            final SelectClassBean classBean = classBeanList.get(holder.getAdapterPosition());

            holder.className.setText(classBean.classBean.getClassName());

            if (classBean.classBean.getStudentNum() != 0) {
                holder.joinNum.setText( String.format(Locale.getDefault(),"已加入%d人", classBean.classBean.getStudentNum() ));
            }else{
                holder.joinNum.setText("");
            }

            if (classBean.classBean.getTeaNameList() != null && classBean.classBean.getTeaNameList().size() != 0) {
                StringBuffer sb = new StringBuffer();
                sb.append("老师：");
                sb.append(classBean.classBean.getTeaNameList().get(0));
//                for (int i = 0; i < classBean.classBean.getTeaNameList().size(); i++) {
//                    if (i == classBean.classBean.getTeaNameList().size() - 1) {
//                        sb.append(classBean.classBean.getTeaNameList().get(i));
//                    } else {
//                        sb.append(classBean.classBean.getTeaNameList().get(i)).append("、");
//                    }
//                }
                holder.teacherName.setText(sb);
            }else{
                holder.teacherName.setText("");
            }

            if (classBean.selected) {
                holder.mLinearLayout.setBackgroundResource(R.drawable.bg_selected_class);
                holder.className.setTextColor(Color.WHITE);
                holder.joinNum.setTextColor(Color.WHITE);
                holder.teacherName.setTextColor(Color.WHITE);
            } else {
                holder.mLinearLayout.setBackgroundResource(R.drawable.bg_sel_class);
                holder.className.setTextColor(getResources().getColor( R.color.color_666666 ));
                holder.joinNum.setTextColor(getResources().getColor( R.color.color_666666 ));
                holder.teacherName.setTextColor(getResources().getColor( R.color.color_666666 ));
            }
            holder.mLinearLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    singleSelectMode( position );
                    adapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return classBeanList.size();
        }

        private void singleSelectMode(int position){
            for( int i = 0; i < classBeanList.size(); i++ ){
                SelectClassBean bean = classBeanList.get(i);
                bean.selected = ( i == position ) && !bean.selected;
            }
        }

    }

}

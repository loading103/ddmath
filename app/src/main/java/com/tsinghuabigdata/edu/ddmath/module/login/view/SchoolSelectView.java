package com.tsinghuabigdata.edu.ddmath.module.login.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.adapter.SchoolAdapter;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.bean.SchoolBean;

import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * 选择学校（单选模式）
 */

public class SchoolSelectView extends LinearLayout implements View.OnClickListener{

    // 学校数据
    private List<SchoolBean> schoolBeanList = new ArrayList<>();

    @ViewInject(R.id.et_school)
    private EditText etSchool;

    @ViewInject(R.id.iv_search)
    private ImageView imgSearch;

    @ViewInject(R.id.iv_delete)
    private ImageView imgDelete;

    @ViewInject(R.id.listview_school)
    private ListView schoolListView;

    private SelectSchoolListener mListener;

    public SchoolSelectView(Context context) {
        super(context);
        init();
    }

    public SchoolSelectView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SchoolSelectView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_delete:
                etSchool.setText(null);
                showSchoolList(schoolBeanList);
                break;
            default:
                break;
        }
    }

    public void setSchoolBeanList(List<SchoolBean> list) {
        schoolBeanList = list;

        etSchool.setText("");
        showSchoolList( list );
    }

    public void setSchoolSelectListener( SelectSchoolListener listener ){
        mListener = listener;
    }

    //----------------------------------------------------------------------------------------------
    private void init() {

        inflate( getContext(), R.layout.view_school_select_phone, this );

        etSchool = findViewById(R.id.et_school);
        imgSearch = findViewById(R.id.iv_search);
        imgDelete = findViewById(R.id.iv_delete);
        schoolListView = findViewById(R.id.listview_school);

        imgDelete.setOnClickListener(this);
        // 学校数据还没时不能搜索
        etSchool.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s.toString()) && etSchool.isFocused()) {
                    showSchoolList(searchSchool(s));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString()) && etSchool.isFocused()) {
                    imgDelete.setVisibility(View.VISIBLE);
                    imgSearch.setVisibility(View.INVISIBLE);
                    showSchoolList(searchSchool(s));
                }else {
                    imgDelete.setVisibility(View.INVISIBLE);
                    imgSearch.setVisibility(View.VISIBLE);
                    showSchoolList(schoolBeanList);
                }
            }
        });
        etSchool.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String str = etSchool.getText().toString();
                if (hasFocus && !TextUtils.isEmpty(str)){
                    imgDelete.setVisibility(View.VISIBLE);
                    imgSearch.setVisibility(View.INVISIBLE);
                }else {
                    imgDelete.setVisibility(View.INVISIBLE);
                    imgSearch.setVisibility(View.VISIBLE);
                }
            }
        });
        etSchool.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_UNSPECIFIED
                        || actionId == EditorInfo.IME_ACTION_DONE) {
                    String s = etSchool.getText().toString();
                    showSchoolList(searchSchool(s));
                }
                return true;
            }
        });

//        adapter = new SelClassAdapter();
//        setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
//        setAdapter(adapter);
    }

    // 返回搜索的学校
    private List<SchoolBean> searchSchool(CharSequence s) {
        if( TextUtils.isEmpty(s) ){
            return schoolBeanList;
        }
        List<SchoolBean> beanList = new ArrayList<>();
        for (int i = 0; i < schoolBeanList.size(); i++) {
            if (schoolBeanList.get(i).getSchoolName().contains(s)) {
                SchoolBean bean = new SchoolBean();
                bean.setSchoolId(schoolBeanList.get(i).getSchoolId());
                bean.setSchoolName(schoolBeanList.get(i).getSchoolName());
                bean.setLearnPeriod(schoolBeanList.get(i).getLearnPeriod());
                beanList.add(bean);
            }
        }
        return beanList;
    }

    private void showSchoolList(final List<SchoolBean> res) {
        if( res == null ) return;
        SchoolAdapter adapter = new SchoolAdapter(getContext(), res);
        adapter.setKeyword( etSchool.getText().toString().trim() );
        schoolListView.setAdapter(adapter);
        schoolListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if( mListener!=null ) mListener.onSelectSchool( res.get(position) );
            }
        });
    }

    //----------------------------------------------------------------------------------------------
    public interface SelectSchoolListener{
        void onSelectSchool( SchoolBean bean);
    }
}

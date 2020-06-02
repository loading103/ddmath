package com.tsinghuabigdata.edu.ddmath.module.mycenter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationListener;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.WorkToolbar;
import com.tsinghuabigdata.edu.ddmath.module.login.model.LoginModel;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.adapter.SchoolAdapter;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.bean.AreaBean;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.bean.CityBean;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.bean.SchoolBean;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.dialog.ProCityDialog;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.view.MyProgressDialog;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/24.
 * 选择学校
 */
@Deprecated
public class SchoolSelectActivity extends RoboActivity implements View.OnClickListener{

    public final static String PARAM_ACTION_TYPE = "action";
    public final static String PARAM_SCHOOL_TYPE = "schoolType";

    public final static String TYPE_SCHOOLINFO = "school";
    public final static String TYPE_ADDCLASS = "class";


    @ViewInject(R.id.toolbar)
    private WorkToolbar toolbar;

    @ViewInject(R.id.ll_pro_city)
    private LinearLayout ll_Pro_City;

    @ViewInject(R.id.et_school)
    private EditText etSchool;

    @ViewInject(R.id.iv_search)
    private ImageView imgSearch;

    @ViewInject(R.id.iv_delete)
    private ImageView imgDelete;

    @ViewInject(R.id.tv_pro_city)
    private TextView tv_Pro_City;

    @ViewInject(R.id.listview_school)
    private ListView schoolListView;

    @ViewInject(R.id.ll_list_content)
    private LinearLayout ll_list_content;

    @ViewInject( R.id.ll_periodSchool )
    private LinearLayout periodSchoolLayout;

    @ViewInject( R.id.ll_primary )
    private ImageView primaryView;

    @ViewInject( R.id.ll_middle )
    private ImageView middleView;

    // 定位的省名
    private String locationPro = "";
    // 定位的城市名
    private String locationCity = "";
    // 选中的省
    private AreaBean selProvince = null;
    // 选中的市
    private CityBean selCity = null;

    private MyProgressDialog progressDialog;

    private Context context;

    private ProCityDialog proCityDialog;

    // 省市数据
    private List<AreaBean> areaBeanList = null;

    // 学校数据
    private List<SchoolBean> schoolBeanList = null;

    // 学校类型
    private int schoolType;
    private String actionType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        transparent = true;
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(GlobalData.isPad() ? R.layout.activity_school_select : R.layout.activity_school_select_phone);
        x.view().inject(this);

        parseIntent();
        initView();
    }

    private void parseIntent() {
        schoolType = getIntent().getIntExtra( PARAM_SCHOOL_TYPE, 0);
        actionType = getIntent().getStringExtra( PARAM_ACTION_TYPE );
        if( TextUtils.isEmpty(actionType) )
            actionType = TYPE_SCHOOLINFO;
    }

    private void initView() {
        progressDialog = new MyProgressDialog(this);
        // 拉取省市数据
        queryProCity();
        // 定位
        location();
        context = this;
        toolbar.setTitle("选择所在学校");
        toolbar.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, null);
        ll_Pro_City.setOnClickListener(this);
        imgDelete.setOnClickListener(this);
        // 学校数据还没时不能搜索
        etSchool.setEnabled(false);
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

        //没有学段类型时，默认小学，可选
        if( schoolType == 0 ){
            schoolType = 2;
            periodSchoolLayout.setVisibility( View.VISIBLE );
            primaryView.setOnClickListener( this );
            middleView.setOnClickListener( this );
            primaryView.setSelected(false);
            middleView.setSelected(true);
        }
    }

    private void queryProCity() {
        progressDialog.setMessage("省市数据加载中...");
        progressDialog.show();
        new LoginModel().queryProCity(new RequestListener<List<AreaBean>>() {
            @Override
            public void onSuccess(List<AreaBean> res) {
                AppLog.i("queryAreaBean success");
                progressDialog.dismiss();
                areaBeanList = res;
            }

            @Override
            public void onFail(HttpResponse<List<AreaBean>> response, Exception ex) {
                AppLog.i("queryAreaBean failed" + ex.getMessage());
                progressDialog.dismiss();
                AlertManager.toast(context, "省市数据加载失败");
            }
        });
    }

    private void location() {
        final AMapLocationClient client = new AMapLocationClient(this);
        client.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null) {
                    if (aMapLocation.getErrorCode() == 0) {
                        //解析定位结果
                        locationPro = aMapLocation.getProvince();
                        locationCity = aMapLocation.getCity();
                        tv_Pro_City.setText(locationPro + locationCity);
                        // 得到省Id
                        for (int i = 0; i < areaBeanList.size(); i++) {
                            if (areaBeanList.get(i).getProvinceName().equals(locationPro)) {
                                selProvince = areaBeanList.get(i);
                                break;
                            }
                        }
                        // 得到市Id
                        for (int i  = 0; i < selProvince.getCityVoList().size(); i++) {
                            if (selProvince.getCityVoList().get(i).getCityName().equals(locationCity)) {
                                selCity = selProvince.getCityVoList().get(i);
                                break;

                            }
                        }
                        selSchool();
                        client.stopLocation();
                    }
                }
            }
        });
        client.startLocation();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_pro_city:
                selProCity();
                break;
            case R.id.iv_delete:
                etSchool.setText(null);
                showSchoolList(schoolBeanList);
                break;
            case R.id.ll_primary:
                if(AppConst.PRIMARY == schoolType)
                    return;
                primaryView.setSelected(true);
                middleView.setSelected(false);
                schoolType = AppConst.PRIMARY;
                selSchool();
                break;
            case R.id.ll_middle:
                if(AppConst.MIDDLE == schoolType)
                    return;
                primaryView.setSelected(false);
                middleView.setSelected(true);
                schoolType = AppConst.MIDDLE;
                selSchool();
                break;
            default:
                break;
        }
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

    private void selSchool() {
        progressDialog.setMessage("学校数据加载中...");
        progressDialog.show();
        String schoolName = "";
        String provinceId = "";
        if (selProvince != null) {
            provinceId = selProvince.getProvinceCode();
        }
        String cityId = "";
        if (selCity != null) {
            cityId = selCity.getCityCode();
        }
        new LoginModel().querySchool(schoolName, provinceId, cityId, String.valueOf(schoolType), new RequestListener<List<SchoolBean>>() {
            @Override
            public void onSuccess(List<SchoolBean> res) {
                if( isDestroyed() ) return;
                progressDialog.dismiss();
                AppLog.i("querySchoolList success");
                if (res == null || res.size() == 0) {
                    AlertManager.toast(context, "没有数据");
                    res = new ArrayList<>();
                    showSchoolList(res);
                    return;
                }
                int count = res.size();
                for( int i=count-1; i>=0; i--){
                    SchoolBean schoolBean = res.get(i);
                    if( schoolBean.getSchoolName().startsWith("ZXNC") ){
                        res.remove( schoolBean );
                        AppLog.d("schoolName = " +schoolBean.getSchoolName() );
                    }
                }

                schoolBeanList = res;
                etSchool.setEnabled(true);
                showSchoolList( searchSchool(etSchool.getText()) );
            }

            @Override
            public void onFail(HttpResponse<List<SchoolBean>> response, Exception ex) {
                AppLog.i("querySchoolList failed" + ex.getMessage());
                if( isDestroyed() ) return;
                progressDialog.dismiss();
                AlertManager.toast(context, ex.getMessage());
            }
        });
    }

    private void showSchoolList(final List<SchoolBean> res) {
        if( res == null ) return;
        SchoolAdapter adapter = new SchoolAdapter(context, res);
        adapter.setKeyword( etSchool.getText().toString().trim() );
        schoolListView.setAdapter(adapter);
        ll_list_content.setVisibility(View.VISIBLE);
        schoolListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String schoolId = res.get(position).getSchoolId();
                String schoolName= res.get(position).getSchoolName();

                if( TYPE_ADDCLASS.equals( actionType ) ){
                    //选择学校后直接添加班级
                    Intent intent = new Intent(context, JoinClassActivity.class);
                    intent.putExtra("schoolId", schoolId);
                    intent.putExtra("schoolName", schoolName);
                    intent.putExtra("learnPeriod", schoolType);
                    //intent.putExtra("enrolYear", enrolYear);
                    startActivity(intent);

                }else{
                    Intent intent = new Intent();
                    intent.putExtra("schoolId", schoolId );
                    intent.putExtra("schoolName", schoolName );
                    setResult(2, intent);
                    finish();
                }
            }
        });
    }

    private void selProCity() {
        if (areaBeanList == null) {
            queryProCity();
        } else {
            showProCityDialog();
        }
    }

    private void showProCityDialog() {
        if (proCityDialog == null) {
            proCityDialog = new ProCityDialog(this, R.style.dialog);
            proCityDialog.setData(areaBeanList);
            proCityDialog.setClickListener(new ProCityDialog.OnSureButtonClickListener() {
                @Override
                public void onSelect(AreaBean areaBean, CityBean cityBean) {
                    // 若重新选省市，则学校列表置空
                    if ((selProvince != null && !selProvince.getProvinceName().equals(areaBean.getProvinceName()))
                            || (selCity != null && !selCity.getCityName().equals(cityBean.getCityName()))) {
                        ll_list_content.setVisibility(View.INVISIBLE);
                        etSchool.setText(null);
                        etSchool.setEnabled(false);
                    }
                    tv_Pro_City.setText(areaBean.getProvinceName() + cityBean.getCityName());
                    selProvince = areaBean;
                    selCity = cityBean;
                    proCityDialog.dismiss();
                    selSchool();
                }
            });
            Window window = proCityDialog.getWindow();
            window.setGravity(Gravity.BOTTOM);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = getResources().getDisplayMetrics().widthPixels; // 宽度
            //lp.height= getResources().getDisplayMetrics().heightPixels *3/4;
            window.setAttributes(lp);
        }
        // 每次弹出定位到第一条
        proCityDialog.setCurrentItem(0);
        proCityDialog.show();
    }

}

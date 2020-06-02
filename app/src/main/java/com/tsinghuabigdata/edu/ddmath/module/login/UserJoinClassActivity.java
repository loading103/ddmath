package com.tsinghuabigdata.edu.ddmath.module.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationListener;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.WorkToolbar;
import com.tsinghuabigdata.edu.ddmath.module.login.model.LoginModel;
import com.tsinghuabigdata.edu.ddmath.module.login.view.SchoolSelectView;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.bean.AreaBean;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.bean.CityBean;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.bean.ClassBean;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.bean.ReturnClassBean;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.bean.SchoolBean;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.dialog.ProCityDialog;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.dialog.SchoolSelDialog;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.view.ClassSelectView;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.edu.ddmath.view.MyProgressDialog;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * 用户自己加入班级界面
 */

public class UserJoinClassActivity extends RoboActivity implements View.OnClickListener, SchoolSelectView.SelectSchoolListener {


    @ViewInject(R.id.toolbar)
    private WorkToolbar toolbar;

 //学段
    @ViewInject(R.id.ll_primary)
    private LinearLayout llPrimary;

    @ViewInject(R.id.ll_middle)
    private LinearLayout llMiddle;
    //
    @ViewInject(R.id.img_primary)
    private ImageView imgPrimary;

    @ViewInject(R.id.img_middle)
    private ImageView imgMiddle;

    //入学年份
    @ViewInject(R.id.et_encrollyears)
    private TextView etEncrollyears;

    //城市
    @ViewInject(R.id.et_city)
    private TextView etCity;

    //学校
    @ViewInject(R.id.tv_search_school)
    private TextView tvSchoolView;

    @ViewInject(R.id.school_select_view)
    private SchoolSelectView schoolSelectView;

    //-------------------------------------------------
    @ViewInject(R.id.layout_nodata)
    private RelativeLayout nodataLayout;
    @ViewInject(R.id.layout_class_list)
    private LinearLayout classListLayout;
    @ViewInject(R.id.classSelectView)
    private ClassSelectView classSelectView;
    @ViewInject(R.id.bt_join_class)
    private Button joinClassBtn;

    //@ViewInject(R.id.loading_pager)
    //private LoadingPager loadingPager;


    private List<AreaBean> areaBeanList = new ArrayList<>();

    //小学
    private List<SchoolBean> primarySchoolList = null;
    //中学
    private List<SchoolBean> middleSchoolList = null;

    private MyProgressDialog progressDialog;

    // 选择的入学年份
    private int enrolYear = 0;

    // 学校类型 小学1 初中2
    private int mSchoolType;

    //定位信息
    private AreaBean currProvinceBean = null;
    private CityBean currCityBean = null;

    private SchoolBean mSchoolBean;
    private ClassBean mClassBean;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        transparent = true;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(GlobalData.isPad() ? R.layout.activity_user_joinclass : R.layout.activity_user_joinclass_phone);
        x.view().inject(this);
        mContext = this;
        initView();
        loadData();
    }

    @Override
    public void onBackPressed() {
        if( schoolSelectView.isShown() ){
            schoolSelectView.setVisibility(View.GONE);
        }else
            super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.et_encrollyears:
                SchoolSelDialog.selectEnrolYear( mContext, mSchoolType, new SchoolSelDialog.OnSureButtonClickListener(){
                    @Override
                    public void onSelect(int year) {
                        if( year != enrolYear ){
                            enrolYear = year;
                            etEncrollyears.setText( String.format(Locale.getDefault(), "%d年", enrolYear));
                            tvSchoolView.setText(null);

                            nodataLayout.setVisibility(View.VISIBLE);
                            classListLayout.setVisibility(View.GONE);
                        }
                    }
                } );
                break;

            case R.id.ll_primary:
            case R.id.img_primary:
                imgPrimary.setSelected(true);
                imgMiddle.setSelected(false);
                if( mSchoolType != AppConst.PRIMARY ){
                    enrolYear = 0;
                    etEncrollyears.setText(null);
                    tvSchoolView.setText(null);

                    mSchoolType = AppConst.PRIMARY;
                    nodataLayout.setVisibility(View.VISIBLE);
                    classListLayout.setVisibility(View.GONE);
                }
                break;
            case R.id.ll_middle:
            case R.id.img_middle:
                imgPrimary.setSelected(false);
                imgMiddle.setSelected(true);
                if (mSchoolType != AppConst.MIDDLE) {
                    enrolYear = 0;
                    etEncrollyears.setText(null);
                    tvSchoolView.setText(null);

                    mSchoolType = AppConst.MIDDLE;
                    nodataLayout.setVisibility(View.VISIBLE);
                    classListLayout.setVisibility(View.GONE);
                }
                break;
            case R.id.tv_search_school:{
                if( enrolYear == 0 ){
                    ToastUtils.show( mContext, "请先选择入学年份");
                    break;
                }
                if( AppConst.PRIMARY == mSchoolType ){
                    if( primarySchoolList !=null ){
                        schoolSelectView.setSchoolBeanList( primarySchoolList );
                        schoolSelectView.setVisibility( View.VISIBLE );
                    }else{
                        if( currCityBean!=null && currProvinceBean!=null ){
                            querySchool( currProvinceBean, currCityBean );
                        }else{
                            ToastUtils.show( mContext, "城市信息错误，没有找到相关的学校信息");
                        }
                    }
                }else{
                    if( middleSchoolList !=null ){
                        schoolSelectView.setSchoolBeanList( middleSchoolList );
                        schoolSelectView.setVisibility( View.VISIBLE );
                    }else{
                        if( currCityBean!=null && currProvinceBean!=null ){
                            querySchool( currProvinceBean, currCityBean );
                        }else{
                            ToastUtils.show( mContext, "城市信息错误，没有找到相关的学校信息");
                        }
                    }
                }
                break;
            }
            case R.id.et_city:{
                showProCityDialog();
                break;
            }
            case R.id.bt_join_class:{
                ensureJoinClass();
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void onSelectSchool(SchoolBean bean) {
        schoolSelectView.setVisibility( View.GONE );
        tvSchoolView.setText( bean.getSchoolName() );
        mSchoolBean = bean;
        queryClassList();
    }
    //-----------------------------------------------------------------------------------------------

    private void initView() {
        toolbar.setTitle("没有班级码 手动加班");
        toolbar.setBackText( "取消", false );
        toolbar.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        }, null);

        etEncrollyears.setOnClickListener(this);

        llPrimary.setOnClickListener(this);
        llMiddle.setOnClickListener(this);
        imgPrimary.setOnClickListener(this);
        imgMiddle.setOnClickListener(this);

        etCity.setOnClickListener(this);
        tvSchoolView.setOnClickListener(this);

        joinClassBtn.setOnClickListener( this );

        schoolSelectView.setSchoolSelectListener( this );

        // 默认选中小学
        imgPrimary.setSelected(true);
        mSchoolType = AppConst.PRIMARY;

        progressDialog = new MyProgressDialog( mContext );
    }

    private void loadData(){
        // 拉取省市数据
        queryProCity();
    }

    // 拉取省市数据
    private void queryProCity() {
        progressDialog.setMessage("加载省市数据中...");
        progressDialog.show();
        new LoginModel().queryProCity(new RequestListener<List<AreaBean>>() {
            @Override
            public void onSuccess(List<AreaBean> res) {
                //progressDialog.dismiss();
                areaBeanList.clear();
                areaBeanList.addAll(res);
                // 定位
                location();
            }

            @Override
            public void onFail(HttpResponse<List<AreaBean>> response, Exception ex) {
                AppLog.i("queryAreaBean failed" + ex.getMessage());
                progressDialog.dismiss();
                AlertManager.toast( mContext, "省市数据加载失败");
                finish();
            }
        });
    }

    //定位信息
    private int tryTime = 0;
    private void location() {

        progressDialog.setMessage("正在定位当前城市...");
        progressDialog.show();
        final AMapLocationClient client = new AMapLocationClient(this);
        client.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
                    progressDialog.dismiss();

                    //解析定位结果
                    String locationPro = aMapLocation.getProvince();
                    String locationCity = aMapLocation.getCity();
                    etCity.setText(locationPro);
                    etCity.append(locationCity);

                    // 得到省Id
                    for (int i = 0; i < areaBeanList.size(); i++) {
                        if (areaBeanList.get(i).getProvinceName().equals(locationPro)) {
                            currProvinceBean = areaBeanList.get(i);
                            break;
                        }
                    }

                    if( currProvinceBean == null ){
                        ToastUtils.show( mContext, "没有找到当前的省份信息");
                        finish();
                        return;
                    }

                    // 得到市Id
                    for (int i  = 0; i < currProvinceBean.getCityVoList().size(); i++) {
                        if (currProvinceBean.getCityVoList().get(i).getCityName().equals(locationCity)) {
                            currCityBean = currProvinceBean.getCityVoList().get(i);
                            break;
                        }
                    }
                    if( currCityBean == null ){
                        ToastUtils.show( mContext, "没有找到当前的城市信息");
                        finish();
                        return;
                    }
                    client.stopLocation();
                }else{
                    //定位失败 重新定位
                    progressDialog.setMessage("重新定位当前城市...");
                    tryTime++;
                    if( tryTime > 3 ){
                        ToastUtils.show( mContext, "没有找到当前的城市信息");
                        finish();
                    }else{
                        client.startLocation();
                    }
                }
            }
        });
        client.startLocation();
    }

    //加载当前城市的学校信息
    private void querySchool(AreaBean currProvince, CityBean currCity ) {

        progressDialog.setMessage("学校数据加载中...");
        progressDialog.show();
        String schoolName = "";
        String provinceId = "";
        if (currProvince != null) {
            provinceId = currProvince.getProvinceCode();
        }
        String cityId = "";
        if (currCity != null) {
            cityId = currCity.getCityCode();
        }
        new LoginModel().querySchool(schoolName, provinceId, cityId, String.valueOf(mSchoolType), new RequestListener<List<SchoolBean>>() {
            @Override
            public void onSuccess(List<SchoolBean> res) {
                if( isDestroyed() ) return;
                progressDialog.dismiss();
                AppLog.i("querySchoolList success");
                if (res == null || res.size() == 0) {
                    AlertManager.toast( mContext, "没有学校数据");
                    return;
                }

                //过滤测试学校
                int count = res.size();
                for( int i=count-1; i>=0; i--){
                    SchoolBean schoolBean = res.get(i);
                    if( schoolBean.getSchoolName().startsWith("ZXNC") ){
                        res.remove( schoolBean );
                        AppLog.d("schoolName = " +schoolBean.getSchoolName() );
                    }
                }

                if( AppConst.PRIMARY == mSchoolType )
                    primarySchoolList = res;
                else
                    middleSchoolList = res;

                //显示学校界面
                schoolSelectView.setSchoolBeanList( res );
                schoolSelectView.setVisibility( View.VISIBLE );
            }

            @Override
            public void onFail(HttpResponse<List<SchoolBean>> response, Exception ex) {
                if( isDestroyed() ) return;
                progressDialog.dismiss();
                AlertManager.toast( mContext, ex.getMessage());
            }
        });
    }

    //查询班级列表
    private void queryClassList() {

        progressDialog.setMessage("班级数据加载中...");
        progressDialog.show();
        new LoginModel().queryClassList( mSchoolBean.getSchoolId(), String.valueOf(enrolYear), new RequestListener<List<ReturnClassBean>>() {
            @Override
            public void onSuccess(List<ReturnClassBean> res) {
                progressDialog.dismiss();

                nodataLayout.setVisibility(View.GONE);
                classListLayout.setVisibility(View.VISIBLE);


                if (res == null || res.size() == 0) {
                    classSelectView.setClassDataList( new ArrayList<ReturnClassBean>() );
                    return;
                }
                classSelectView.setClassDataList(res);
            }

            @Override
            public void onFail(HttpResponse<List<ReturnClassBean>> response, Exception ex) {
                progressDialog.dismiss();
                classSelectView.setClassDataList( new ArrayList<ReturnClassBean>() );
            }
        });
    }

    //加入班级
    private void ensureJoinClass() {

        // 确定加入班级
        final ClassBean classBean = classSelectView.getSelectClassBean();
        if (classBean == null) {
            ToastUtils.show( mContext, "请选择班级");
        } else {
            if (classBean.getAllowAddStudent() == 1) {
                AlertManager.toast(mContext, "此班级不允许加入，请重新选择!");
                return;
            }

            //判断是否已经在此班级
            if (AccountUtils.checkInClass(classBean.getClassId())) {
                AlertManager.toast(mContext, "你已经在此班级了，不能重复加入!");
                return;
            }

//            if (classBean.getStudentNum() >= 160) {
//                AlertManager.toast(context, "此班级人数已满，请重新选择");
//                return;
//            }

            final UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
            if( detailinfo == null ){
                ToastUtils.show( mContext, "请重新登录");
                return;
            }

//            //加入班级提醒
//            AlertManager.showCustomImageBtnDialog(mContext, "加入班级成功后，不可以退班哦！", "确定加入", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//
//                    progressDialog.setMessage("加入班级中...");
//                    progressDialog.show();
//                    String studentId = detailinfo.getStudentId();
//                    String studentName = detailinfo.getReallyName();
                    final String serial = String .valueOf(enrolYear);
//
//                    new LoginModel().joinClass(studentId, studentName, classBean.getClassId(),
//                            classBean.getClassName(), mSchoolBean.getSchoolId(), serial, new RequestListener() {
//                                @Override
//                                public void onSuccess(Object res) {
//                                    progressDialog.dismiss();

                                    mClassBean = classBean;

                                    //AlertManager.toast(mContext, "加入班级成功");
//                                    EventBus.getDefault().post(new JoinClassEvent());
//
//                                    //更新班级信息
//                                    Intent intent = new Intent(mContext, MainActivity.class);
//                                    intent.putExtra("joinclass", true);
//                                    mContext.startActivity(intent);

                                    mClassBean.setEnrollmentYear( serial );
                                    mClassBean.setSchoolId( mSchoolBean.getSchoolId() );
                                    mClassBean.setSchoolName( mSchoolBean.getSchoolName() );
                                    //
                                    Intent intent = new Intent();
                                    intent.putExtra( "school", mSchoolBean );
                                    intent.putExtra( "class", mClassBean );
                                    setResult( RESULT_OK, intent);
                                    finish();
//                                }
//
//                                @Override
//                                public void onFail(HttpResponse response, Exception ex) {
//                                    progressDialog.dismiss();
//                                    AppLog.i("joinClass failed" + ex.getMessage());
//                                    ToastUtils.show(mContext, "加入失败，请重试");
//                                }
//                            });
//                }
//            }, null);

        }
    }

    //手动选择城市
    private void showProCityDialog() {
        final ProCityDialog proCityDialog = new ProCityDialog(this, R.style.dialog);
        proCityDialog.setData(areaBeanList);
        proCityDialog.setClickListener(new ProCityDialog.OnSureButtonClickListener() {
            @Override
            public void onSelect(AreaBean areaBean, CityBean cityBean) {

                etCity.setText(areaBean.getProvinceName());
                etCity.append(cityBean.getCityName());

                if( !currCityBean.equals(cityBean) || !currProvinceBean.equals(areaBean) ){
                    nodataLayout.setVisibility(View.VISIBLE);
                    classListLayout.setVisibility(View.GONE);
                    tvSchoolView.setText(null);

                    currProvinceBean = areaBean;
                    currCityBean = cityBean;
                }
                proCityDialog.dismiss();

                //初始化信息
                primarySchoolList = null;
                middleSchoolList = null;
            }
        });
        Window window = proCityDialog.getWindow();
        if( window == null ) return;
        window.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = getResources().getDisplayMetrics().widthPixels; // 宽度
        //lp.height= getResources().getDisplayMetrics().heightPixels *3/4;
        window.setAttributes(lp);
        proCityDialog.show();
    }
}

package com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.tsinghuabigdata.edu.ddmath.MVPModel.KnowledgeDiagnoseModel;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.MainActivity;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.event.JumpEvent;
import com.tsinghuabigdata.edu.ddmath.fragment.MyReportFragment;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.HelpUtil;
import com.tsinghuabigdata.edu.ddmath.module.neteaseim.base.util.TimeUtil;
import com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.KnowledgeUtils;
import com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.MyValueFormatter;
import com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.adapter.AllKnowledgeAdapter;
import com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.adapter.KnowledgeSelectAdapter;
import com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.bean.KnowlegeDiagnoseBean;
import com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.bean.LevelKnowledgesBean;
import com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.bean.SubKnowledgesBean;
import com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.dialog.CityGloryDialog;
import com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.dialog.ClassGloryDialog;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.DateUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.edu.ddmath.view.LoadingPager;
import com.tsinghuabigdata.edu.ddmath.view.TimeSpinnerView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;


/**
 * 知识诊断
 * Created by Administrator on 2018/7/31.
 */

public class KnowledgeActivity extends RoboActivity implements View.OnClickListener, OnChartValueSelectedListener {

    public static final String PARAM_ALLACCURACY = "AllAccuracy";
    public static final String PARAM_FROM_TYPE = "from";

    private LinearLayout    mLlLefttitle;
    private TextView        mTvClass;
    private TextView        mTvGrade;
    private LoadingPager    mLoadingPager;
    private TextView        mTvAllAccuracy;
    private LinearLayout    mLlContent;
    private Button          mBtnLookGraph;
    private TimeSpinnerView mTimeSpinnerView;
    private LinearLayout    mLlChart;
    private TextView        mTvAvgAccuracy;
    private TextView        mTvLearnedKnowledges;
    private TextView        mTvKnowledgeLevel;
    private TextView        mTvQuestionCount;
    private TextView        mTvErrorCount;
    private LinearLayout    mLlEmpty;
    private ListView        mLvAllKnowledge;
    private ImageView       mIvCreateArrow;
    private TextView        mTvKnowldegTitle;
    private ListView        mLvSingleKnowledge;
    private CheckBox        mCbAllSelect;
    private TextView        mTvAllSelect;
    private CheckBox        mAllKnowledgeCheckBox;
    private TextView        mAllKnowledgeTextView;
    private TextView        mAllCreateTextView;
    private Button          mAllCteateBtn;
    private TextView        mTvSelectKnowledgeCount;
    private Button          mBtnCreateEnalbe;

    private ImageView        mIvHelp;

    private RelativeLayout noSelectKnowledgeLayout;    //没有选择知识点
    private LinearLayout    mLlSingleKnowledge;         //单一等级知识点
    private LinearLayout    mLlAllKnowledge;            //全部等级知识点
    private TextView        mTvNoDisplayKnowledges;     //没有数据



    private KnowlegeDiagnoseBean mDiagnoseBean;
    private LevelKnowledgesBean  mAllLevelBean;
    private List<LevelKnowledgesBean> mLevelList = new ArrayList<>();

    private KnowledgeSelectAdapter mAdapter;
    private List<SubKnowledgesBean> mList = new ArrayList<>();
    private long mCustomStartTime;
    private int  timeIndex = 1;
    private String mStudentId = "";
    private float holeRadius;
    private float centerY;
    private float centerX;

    private String fromType;

    private Context  mContext;
    private PieChart mChart;

    private   String[] mTitles         = new String[]{"等级优（90%～100%）", "等级良（80%～89%）", "等级中（60%～79%）", "等级差（0%～59%）"};
    protected String[] mParties        = new String[]{"Party A", "Party B", "Party C", "Party D"};
    protected String[] mLevelTitles    = new String[]{"优", "良", "中", "差"};
    protected int[]    mLevelDrawables = new int[]{R.drawable.bg_knowledge_level_you, R.drawable.bg_knowledge_level_liang
            , R.drawable.bg_knowledge_level_zhong, R.drawable.bg_knowledge_level_cha};

    protected int[] mColors = new int[]{Color.parseColor("#7ED321"), Color.parseColor("#FACE10"), Color.parseColor("#FA9614"), Color.parseColor("#F96417")};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        transparent = true;
        super.onCreate(savedInstanceState);
        //设置全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (GlobalData.isPad()) {
            setContentView(R.layout.activity_knowledge);
        } else {
            setContentView(R.layout.activity_knowledge_phone);
        }
        initView();
        initData();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_lefttitle:
                finish();
                break;
            case R.id.tv_class:
                ClassGloryDialog classGloryDialog = new ClassGloryDialog(mContext, R.style.dialog);
                classGloryDialog.show();
                break;
            case R.id.tv_grade:
                CityGloryDialog cityGloryDialog = new CityGloryDialog(mContext, R.style.dialog);
                cityGloryDialog.show();
                break;
            case R.id.btn_look_graph:
                EventBus.getDefault().post(new JumpEvent(MainActivity.FRAGMENT_STUDY_CONDITION, MyReportFragment.KNOWLEDGE_I));
                finish();
                break;
            case R.id.btn_create_enalbe:
                goToCreatPromote();
                break;
            case R.id.cb_all_select:
                clickCb();
                break;
            case R.id.tv_all_select:
                clickTv();
                break;
            case R.id.cb_allknowledge_select:
                if(mAllLevelBean!=null)clickAllSelect(true);
                break;
            case R.id.tv_allknowledge_select:
                if(mAllLevelBean!=null)clickAllSelect(false);
                break;
            case R.id.btn_createall:{
                goToCreatAllPromote();
            }
            break;
            case R.id.iv_help:
                HelpUtil.showHelpActivity( mContext, "知识诊断使用说明", "Q009");
                break;
            default:
                break;
        }
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        if (e instanceof PieEntry) {
            String label = ((PieEntry) e).getLabel();
            int index = getIndex(label);
            if (inHole(h)) {
                if( !mAllKnowledgeCheckBox.isChecked() ) showNoSelectKnowledge();
            } else {
                showSingleKnowledge( index );
            }
        }
    }

    @Override
    public void onNothingSelected() {
        if( !mAllKnowledgeCheckBox.isChecked() ) showNoSelectKnowledge();
    }

    //------------------------------------------------------------------------------------------------------
    private void initView() {
        mContext = this;
        mLlLefttitle =  findViewById(R.id.ll_lefttitle);
        mTvClass =  findViewById(R.id.tv_class);
        mTvGrade =  findViewById(R.id.tv_grade);
        mLoadingPager =  findViewById(R.id.loadingPager);
        mTvAllAccuracy =  findViewById(R.id.tv_all_accuracy);
        mLlContent =  findViewById(R.id.ll_content);
        mBtnLookGraph =  findViewById(R.id.btn_look_graph);
        mTimeSpinnerView =  findViewById(R.id.timeSpinnerView);
        mLlChart =  findViewById(R.id.ll_chart);
        mChart =  findViewById(R.id.chart1);
        mTvAvgAccuracy =  findViewById(R.id.tv_avg_accuracy);
        mTvLearnedKnowledges =  findViewById(R.id.tv_learned_knowledges);
        mTvKnowledgeLevel =  findViewById(R.id.tv_knowledge_level);
        mTvQuestionCount =  findViewById(R.id.tv_question_count);
        mTvErrorCount =  findViewById(R.id.tv_error_count);
        mLlEmpty =  findViewById(R.id.ll_empty);
        mLlAllKnowledge =  findViewById(R.id.ll_all_knowledge);
        mLvAllKnowledge =  findViewById(R.id.lv_all_knowledge);
        mIvCreateArrow =  findViewById(R.id.iv_create_arrow);
        mLlSingleKnowledge =  findViewById(R.id.ll_single_knowledge);
        mTvKnowldegTitle =  findViewById(R.id.tv_knowldeg_title);
        mLvSingleKnowledge =  findViewById(R.id.lv_single_knowledge);
        mCbAllSelect =  findViewById(R.id.cb_all_select);
        mTvAllSelect =  findViewById(R.id.tv_all_select);
        mTvSelectKnowledgeCount =  findViewById(R.id.tv_select_knowledge_count);
        mBtnCreateEnalbe =  findViewById(R.id.btn_create_enalbe);
        mTvNoDisplayKnowledges =  findViewById(R.id.tv_no_display_knowledges);
        mAllKnowledgeCheckBox = findViewById(R.id.cb_allknowledge_select);
        mAllKnowledgeTextView = findViewById(R.id.tv_allknowledge_select);
        mAllCreateTextView = findViewById(R.id.tv_createall_tip);
        mAllCteateBtn = findViewById(R.id.btn_createall);
        mIvHelp = findViewById(R.id.iv_help);

        noSelectKnowledgeLayout = findViewById(R.id.ll_none_knowledge);

        mLoadingPager.setVisibility(View.VISIBLE);
        mLoadingPager.setTargetView(mLlContent);
        mLoadingPager.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadingPager.showLoading();
                queryKnowledges();
            }
        });
        mLlLefttitle.setOnClickListener(this);
        mTvClass.setOnClickListener(this);
        mTvGrade.setOnClickListener(this);
        mBtnLookGraph.setOnClickListener(this);
        mBtnCreateEnalbe.setOnClickListener(this);
        mCbAllSelect.setOnClickListener(this);
        mTvAllSelect.setOnClickListener(this);
        mAllKnowledgeCheckBox.setOnClickListener(this);
        mAllKnowledgeTextView.setOnClickListener(this);
        mAllCteateBtn.setOnClickListener(this);
        mIvHelp.setOnClickListener(this);

        initTimeSpinner();

        mChart.setUsePercentValues(true);
        mChart.getDescription().setEnabled(false);
        if (GlobalData.isPad()) {
            mChart.setExtraOffsets(30, 0, 30, 0);
        } else {
            mChart.setExtraOffsets(20, 0, 20, 0);
        }

        mChart.setDragDecelerationFrictionCoef(0.95f);

        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColor(Color.WHITE);

        mChart.setTransparentCircleColor(Color.WHITE);
        mChart.setTransparentCircleAlpha(110);

        mChart.setHoleRadius(58f);
        mChart.setTransparentCircleRadius(61f);

        mChart.setDrawCenterText(true);

        mChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        mChart.setRotationEnabled(true);
        mChart.setHighlightPerTapEnabled(true);

        // add a selection listener
        mChart.setOnChartValueSelectedListener(this);

        //setData(4, 100);
        mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        // mChart.spin(2000, 0, 360);

        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setEnabled(false);
    }

    private void initTimeSpinner() {
        final List<String> timelist = Arrays.asList(getResources().getStringArray(R.array.knowledge_time_filter_array));
        mTimeSpinnerView.setText(timelist.get(timeIndex));
        mTimeSpinnerView.setData(timelist, timeIndex);
        mTimeSpinnerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == timelist.size() - 1) {
                    mTimeSpinnerView.showCustom(true);
                    mTimeSpinnerView.autoShowCustom();
                    mTimeSpinnerView.setTempPosition(position);
                } else if (position != mTimeSpinnerView.getSelectedPosition()) {
                    mTimeSpinnerView.showCustom(false);
                    mTimeSpinnerView.setSelectedPosition(position);
                    timeIndex = position;
                    String time = timelist.get(position);
                    mTimeSpinnerView.setText(time);
                    mLoadingPager.showLoading();
                    queryKnowledges();
                    mTimeSpinnerView.dismiss();
                } else {
                    mTimeSpinnerView.dismiss();
                }

            }
        });
        mTimeSpinnerView.setCustomSelect(new TimeSpinnerView.CustomSelect() {
            @Override
            public void selectTime(long startTime, long endTime) {
                mCustomStartTime = startTime;
                int lastPosition = timelist.size() - 1;
                if (timeIndex != lastPosition) {
                    timeIndex = lastPosition;
                    mTimeSpinnerView.setSelectedPosition(lastPosition);
                    String time = timelist.get(lastPosition);
                    mTimeSpinnerView.setText(time);
                }
                mLoadingPager.showLoading();
                queryKnowledges();
            }
        });

    }

    private void initData() {
        float glory = AccountUtils.getUserGlory();
        fromType = getIntent().getStringExtra( PARAM_FROM_TYPE );

        String accuracy = String.valueOf(Math.round(glory * 100));
        mTvAllAccuracy.setText( String.format(Locale.getDefault(),"已学全部知识点平均正确率为%s%%，详情查看",accuracy));
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if ( detailinfo!= null) {
            mStudentId = detailinfo.getStudentId();
        }
        mAdapter = new KnowledgeSelectAdapter(mContext, mList);
        mLvSingleKnowledge.setAdapter(mAdapter);
        mAdapter.setSelectKnowledgeListener(new KnowledgeSelectAdapter.SelectKnowledgeListener() {
            @Override
            public void select() {
                int knowCount = 0;
                for (int i = 0; i < mList.size(); i++) {
                    SubKnowledgesBean bean = mList.get(i);
                    if (bean.isSelect()) {
                        knowCount++;
                    }
                }
                showKnowledgeCount(knowCount);
            }
        });
        queryKnowledges();
    }

    private void queryKnowledges() {
        long startTime = TimeUtil.getStartTime( timeIndex+1, mCustomStartTime );
        long endTime = DateUtils.getYesterdayLast();
        //SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        //String str = format.format(startTime);
        //String str2 = format.format(endTime);

        HashMap<String, String> params = new LinkedHashMap<>();
        params.put("studentId", mStudentId);
        params.put("startTime", startTime + "");
        params.put("endTime", endTime + "");
        new KnowledgeDiagnoseModel().getKnowledgeDiagnose(params, new RequestListener<KnowlegeDiagnoseBean>() {

            @Override
            public void onSuccess(KnowlegeDiagnoseBean res) {
                if (res == null || res.getKnowledges() == null || res.getTotalSize() <= 0) {
                    //全部等级
                    mAllKnowledgeCheckBox.setVisibility(View.GONE);
                    mAllKnowledgeTextView.setVisibility(View.GONE);
                    showEmpty();
                    return;
                }
                //全部等级
                mAllKnowledgeCheckBox.setVisibility(View.VISIBLE);
                mAllKnowledgeTextView.setVisibility(View.VISIBLE);

                mDiagnoseBean = res;
                mAllLevelBean = res.getKnowledges();
                mLevelList.clear();
                mLevelList.add(mDiagnoseBean.getFirstSubKnowledges());
                mLevelList.add(mDiagnoseBean.getSecondSubKnowledges());
                mLevelList.add(mDiagnoseBean.getThirdSubKnowledges());
                mLevelList.add(mDiagnoseBean.getFourthSubKnowledges());

                //默认显示全部等级
                showChart();
                setData();
                showAllKnowledge();
            }

            @Override
            public void onFail(HttpResponse<KnowlegeDiagnoseBean> response, Exception ex) {
                mLoadingPager.showFault(ex);
            }
        });
    }

    //没有知识点
    private void showEmpty() {
        mLlChart.setVisibility(View.INVISIBLE);
        mLlEmpty.setVisibility(View.VISIBLE);

        mLlAllKnowledge.setVisibility(View.INVISIBLE);
        mLlSingleKnowledge.setVisibility(View.INVISIBLE);
        noSelectKnowledgeLayout.setVisibility(View.INVISIBLE);
        mTvNoDisplayKnowledges.setVisibility(View.VISIBLE);
        mLoadingPager.showTarget();
    }

    //没有选择等级
    private void showNoSelectKnowledge(){
        mLlChart.setVisibility(View.VISIBLE);
        mLlEmpty.setVisibility(View.INVISIBLE);

        mLlAllKnowledge.setVisibility(View.INVISIBLE);
        mLlSingleKnowledge.setVisibility(View.INVISIBLE);
        mTvNoDisplayKnowledges.setVisibility(View.INVISIBLE);
        noSelectKnowledgeLayout.setVisibility(View.VISIBLE);
        mLoadingPager.showTarget();

        float glory = AccountUtils.getUserGlory();
        String accuracy = String.valueOf(Math.round(glory * 100));
        mTvLearnedKnowledges.setText(String.format(Locale.getDefault(),"知识点平均正确率 %s%%", accuracy ));
        mTvKnowledgeLevel.setVisibility(View.INVISIBLE);

        mTvQuestionCount.setText( String.format(Locale.getDefault(),"%d题",mAllLevelBean.getTotalQuestionCount()));
        mTvErrorCount.setText(String.format(Locale.getDefault(),"%d题",mAllLevelBean.getTotalWrongCount()));
    }

    //显示全部等级
    private void showAllKnowledge(){
        mLlChart.setVisibility(View.VISIBLE);
        mLlEmpty.setVisibility(View.INVISIBLE);

        mLlAllKnowledge.setVisibility(View.VISIBLE);
        mLlSingleKnowledge.setVisibility(View.INVISIBLE);
        mTvNoDisplayKnowledges.setVisibility(View.INVISIBLE);
        noSelectKnowledgeLayout.setVisibility(View.INVISIBLE);
        mLoadingPager.showTarget();

        //
        selectAllLevel();
    }

    //显示单一等级
    private void showSingleKnowledge(int index){
        mLlChart.setVisibility(View.VISIBLE);
        mLlEmpty.setVisibility(View.INVISIBLE);

        mLlAllKnowledge.setVisibility(View.INVISIBLE);
        mLlSingleKnowledge.setVisibility(View.VISIBLE);
        mTvNoDisplayKnowledges.setVisibility(View.INVISIBLE);
        noSelectKnowledgeLayout.setVisibility(View.INVISIBLE);
        mLoadingPager.showTarget();

        selectlevel(index);
    }

    //
    private void setData() {
        mTvAvgAccuracy.setText(String.format(Locale.getDefault(),"平均正确率 %d%%", Math.round(mAllLevelBean.getAvgAccuracy() * 100) ));
        //showAllErrorCount();

        AllKnowledgeAdapter allKnowledgeAdapter = new AllKnowledgeAdapter(mContext, mAllLevelBean.getKnowledges());
        mLvAllKnowledge.setAdapter(allKnowledgeAdapter);
        String count = mAllLevelBean.getTotalKnowledgeCount() + "个";
        SpannableString s = new SpannableString(count + "\n已学知识点");
        float size1 = GlobalData.isPad() ? 2.5f : 1.4f;
        float size2 = GlobalData.isPad() ? 2.3f : 1.3f;
        s.setSpan(new RelativeSizeSpan(size1), 0, count.length(), 0);
        s.setSpan(new RelativeSizeSpan(size2), count.length(), s.length(), 0);
        s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.color_333333)), 0, s.length(), 0);
        mChart.setCenterText(s);
    }

    private void showAllKnowledgeCount() {
        showKnowledgeCount(mList.size());
    }

    private void showKnowledgeCount(int knowCount) {
        boolean all = knowCount == mList.size();
        mCbAllSelect.setChecked(all);
        mTvAllSelect.setActivated(all);
        SpannableString knowStr = new SpannableString(knowCount + "");
        knowStr.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.color_48B8FF)), 0, knowStr.length(), 0);
        mTvSelectKnowledgeCount.setText(knowStr);
        mTvSelectKnowledgeCount.append("个知识点");
    }

    private void showChart() {
        int size = mDiagnoseBean.getTotalSize();
        float first = (float) mDiagnoseBean.getFirstSize() / size;
        float second = (float) mDiagnoseBean.getSecondSSize() / size;
        float third = (float) mDiagnoseBean.getThirdSize() / size;
        float fourth = (float) mDiagnoseBean.getFourthSize() / size;
        float[] arrays = {first, second, third, fourth};
        setChartData(arrays);
    }

    private void setChartData(float[] arrays) {

        ArrayList<PieEntry> entries = new ArrayList<>();
        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        ArrayList<Integer> colors = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            float rate = arrays[i] * 100;
            if (rate >= 0.5f) {
                //1%在饼状图上展示空白，所以限定最少1.5%
                if (rate < 1.5f) {
                    rate = 1.5f;
                }
                PieEntry pieEntry = new PieEntry(rate, mParties[i % mParties.length]);
                entries.add(pieEntry);
                colors.add(mColors[i]);
            }
        }

        PieDataSet dataSet = new PieDataSet(entries, "Election Results");
        dataSet.setSliceSpace(1f);
        dataSet.setSelectionShift(GlobalData.isPad() ? 15f : 10f);
        // add a lot of colors
        colors.add(ColorTemplate.getHoloBlue());
        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);


        dataSet.setValueLinePart1OffsetPercentage(90.f);
        dataSet.setValueLinePart1Length(0.3f);
        dataSet.setValueLinePart2Length(0.4f);
        dataSet.setValueLineColor(Color.YELLOW);//设置连接线的颜色
        //dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueFormatter(new MyValueFormatter());//自己修改的方法
        data.setValueTextSize(GlobalData.isPad() ? 20f : 11f);
        //                data.setValueTextColor(Color.BLACK); //设置文字的颜色（每个区块）
        //        data.setValueTypeface(tf);
        mChart.setData(data);
        mChart.setDrawEntryLabels(false); //设置每部分的标签显示/隐藏

        // undo all highlights
        mChart.highlightValues(null);

        mChart.invalidate();
        initRaidus();
    }

    private void initRaidus() {
        float radius = mChart.getRadius();
        holeRadius = radius * (mChart.getHoleRadius() / 100);
        MPPointF center = mChart.getCenterCircleBox();
        centerX = center.x;
        centerY = center.y;
    }

    private void setAllList(boolean checked) {
        for (int i = 0; i < mList.size(); i++) {
            mList.get(i).setSelect(checked);
        }
        mAdapter.notifyDataSetChanged();
    }

    //全部等级 点击处理
    private void clickAllSelect( boolean checkbox ) {
        mChart.highlightValues(null);
        boolean checked = mAllKnowledgeCheckBox.isChecked();
        if( !checkbox ){
            checked = !checked;
            mAllKnowledgeCheckBox.setChecked( checked );
        }
        mAllKnowledgeTextView.setActivated( checked );
        if (checked) {
            showAllKnowledge();
        } else {
            showNoSelectKnowledge();
        }
    }
    private void clickCb() {
        boolean checked = mCbAllSelect.isChecked();
        setAllList(checked);
        mTvAllSelect.setActivated(checked);
        if (checked) {
            showAllKnowledgeCount();
        } else {
            showKnowledgeCount(0);
        }
    }

    private void clickTv() {
        boolean checked = !mCbAllSelect.isChecked();
        setAllList(checked);
        mCbAllSelect.setChecked(checked);
        mTvAllSelect.setActivated(checked);
        if (checked) {
            showAllKnowledgeCount();
        } else {
            showKnowledgeCount(0);
        }
    }

    private void goToCreatPromote() {
        List<SubKnowledgesBean> selectedlist = KnowledgeUtils.getSelectedlist(mList);
        if (selectedlist.size() == 0) {
            ToastUtils.showShort(mContext, "你还没有选择知识点哦");
        } else if (!KnowledgeUtils.hasWrong(selectedlist)) {
            ToastUtils.showShort(mContext, "请选择有错题的知识点");
        } else {
            ArrayList<String> knowledges = KnowledgeUtils.getKnowledges(selectedlist);
            Intent intent = new Intent(mContext, CreatPromoteActivity.class);
            intent.putStringArrayListExtra(CreatPromoteActivity.PARAM_KNOWLEDGES, knowledges);
            intent.putExtra(CreatPromoteActivity.PARAM_STARTTIME, TimeUtil.getStartTime(timeIndex+1,mCustomStartTime));
            intent.putExtra(CreatPromoteActivity.PARAM_ENDTIME, DateUtils.getYesterdayLast());
            intent.putExtra(CreatPromoteActivity.PARAM_FROM ,fromType);
            startActivity(intent);
        }
    }

    private void goToCreatAllPromote() {
        ArrayList<String> knowledges = KnowledgeUtils.getKnowledges(mList);
        Intent intent = new Intent(mContext, CreatPromoteActivity.class);
        intent.putStringArrayListExtra(CreatPromoteActivity.PARAM_KNOWLEDGES, knowledges);
        intent.putExtra(CreatPromoteActivity.PARAM_STARTTIME, TimeUtil.getStartTime(timeIndex+1,mCustomStartTime));
        intent.putExtra(CreatPromoteActivity.PARAM_ENDTIME, DateUtils.getYesterdayLast());
        intent.putExtra(CreatPromoteActivity.PARAM_FROM ,fromType);
        startActivity(intent);
    }

    private boolean inHole(Highlight h) {
        float dis1 = centerX - h.getXPx();
        float dis2 = centerY - h.getYPx();
        float sqrt = (float) Math.sqrt(dis1 * dis1 + dis2 * dis2);
        return sqrt < holeRadius;
    }

//    private void showAllErrorCount() {
//
//        mAllKnowledgeCheckBox.setChecked(false);
//        mAllKnowledgeTextView.setActivated(false);
//
//        mTvAvgAccuracy.setVisibility(View.VISIBLE);
//        mTvLearnedKnowledges.setVisibility(View.INVISIBLE);
//        mTvKnowledgeLevel.setVisibility(View.INVISIBLE);
//        mLlAllKnowledge.setVisibility(View.VISIBLE);
//        mLlSingleKnowledge.setVisibility(View.INVISIBLE);
//
//        mTvQuestionCount.setText( String.format(Locale.getDefault(),"%d题",mAllLevelBean.getTotalQuestionCount()));
//        mTvErrorCount.setText(String.format(Locale.getDefault(),"%d题",mAllLevelBean.getTotalWrongCount()));
//    }

    private void selectlevel(int index) {

        mAllKnowledgeCheckBox.setChecked(false);
        mAllKnowledgeTextView.setActivated(false);

        mTvAvgAccuracy.setVisibility(View.INVISIBLE);
        mTvLearnedKnowledges.setVisibility(View.VISIBLE);
        mTvKnowledgeLevel.setVisibility(View.VISIBLE);
        mTvKnowledgeLevel.setBackgroundResource(mLevelDrawables[index]);
        mTvKnowledgeLevel.setText(mLevelTitles[index]);
        LevelKnowledgesBean levelKnowledgesBean = mLevelList.get(index);
        showLevelErrorCout(levelKnowledgesBean);

        mTvKnowldegTitle.setText(String.format(Locale.getDefault(),"%s的已学知识点显示如下：",mTitles[index]) );
        mList.clear();
        addToList(KnowledgeUtils.getSubKnowledges(levelKnowledgesBean));
        mAdapter.notifyDataSetChanged();
        mCbAllSelect.setChecked(false);
        mTvAllSelect.setActivated(false);
        setAllList(true);
        showAllKnowledgeCount();
    }

    private void selectAllLevel() {

        mAllKnowledgeCheckBox.setChecked(true);
        mAllKnowledgeTextView.setActivated(true);

        mTvAvgAccuracy.setVisibility(View.INVISIBLE);
        mTvLearnedKnowledges.setVisibility(View.VISIBLE);

        mTvKnowledgeLevel.setVisibility(View.INVISIBLE);
        //mTvKnowledgeLevel.setBackgroundResource(mLevelDrawables[index]);
        //mTvKnowledgeLevel.setText(mLevelTitles[index]);

        //LevelKnowledgesBean levelKnowledgesBean = mLevelList.get(index);
        //showLevelErrorCout(levelKnowledgesBean);
//        int count = 0;
//        for( LevelKnowledgesBean levelKnowledgesBean : mLevelList )
//            count += levelKnowledgesBean.getTotalKnowledgeCount();

        float glory = AccountUtils.getUserGlory();
        String accuracy = String.valueOf(Math.round(glory * 100));
        mTvLearnedKnowledges.setText(String.format(Locale.getDefault(),"知识点平均正确率 %s%%", accuracy ));
        mTvQuestionCount.setText( String.format(Locale.getDefault(),"%d题",mAllLevelBean.getTotalQuestionCount()));
        mTvErrorCount.setText(String.format(Locale.getDefault(),"%d题",mAllLevelBean.getTotalWrongCount()));

        mTvKnowldegTitle.setText( "全部等级的知识点显示如下： " );

        mList.clear();
        for( LevelKnowledgesBean levelKnowledgesBean : mLevelList )
            addToList(KnowledgeUtils.getSubKnowledges(levelKnowledgesBean));
        mAdapter.notifyDataSetChanged();

        //setAllList(false);
        showAllKnowledgeCount();
    }

    private void showLevelErrorCout(LevelKnowledgesBean bean) {
        mTvLearnedKnowledges.setText(String.format(Locale.getDefault(),"已学知识点 %d个", bean.getTotalKnowledgeCount() ));
        mTvQuestionCount.setText(String.format(Locale.getDefault(),"%d题",bean.getTotalQuestionCount()));
        mTvErrorCount.setText(String.format(Locale.getDefault(),"%d题",bean.getTotalWrongCount() ));
    }

    private void addToList(List<SubKnowledgesBean> list) {
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setSelect(false);
        }
        mList.addAll(list);
    }

    private int getIndex(String label) {
        for (int i = 0; i < mParties.length; i++) {
            if (TextUtils.equals(label, mParties[i])) {
                return i;
            }
        }
        return 0;
    }

}

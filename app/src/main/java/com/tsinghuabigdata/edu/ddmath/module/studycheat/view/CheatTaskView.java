package com.tsinghuabigdata.edu.ddmath.module.studycheat.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;


/**
 * 提分秘籍任务View
 */
public class CheatTaskView extends LinearLayout implements View.OnClickListener {

    //支持两种任务模式
    public static final int TYPE_QUESTION_REVIEW = 11;
    public static final int TYPE_FORCE_TRAIN = 12;

    //private int mType = TYPE_FORCE_TRAIN;
    //
//    private RelativeLayout titleLayout;     //标题
//    private ImageView nameImageView;        //类型图
//    private TextView  nameTextView;         //类型名称
//    private TextView  abilityTextView;      //学力
    private TextView  countTextView;        //学力值
//    private TextView  timeLabelView;         //预计时间
    private TimeView  timeView;
    private ImageView  startView;           //开始btn
//
//    private View      coverView;            //已完成的蒙层View
//    private boolean bCoverSet = false;




    private View.OnClickListener startClickListener;

    public CheatTaskView(Context context) {
        super(context);
        init( context,null );
    }

    public CheatTaskView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init( context,attrs );
    }

    public CheatTaskView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init( context,attrs );
    }

//    @TargetApi(Build.VERSION_CODES.LOLLIPOP )
//    public CheatTaskView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//        init( context,attrs );
//    }

    @Override
    public void onClick(View view) {
        if( view.getId() == R.id.cheattask_startbtn ){
            if( startClickListener!=null ) startClickListener.onClick( view );
        }
    }

    public void setStartOnClickListener( View.OnClickListener listener ){
        startClickListener = listener;
    }

    public void setData( int value, int time ){
        //mType = type;

        String data = "+ " + String.valueOf(value);
        countTextView.setText( data );
        timeView.setText( time, 0, 0 );

//        if( TYPE_FORCE_TRAIN == type ){     //强化训练
//            titleLayout.setBackgroundResource( R.drawable.bg_cheattask_forcetitle );
//            nameImageView.setImageResource( R.drawable.ic_forcetrain );
//            nameTextView.setText( "强化训练" );
//            int color = getContext().getResources().getColor( R.color.color_919A7F);
//            int color2 = getContext().getResources().getColor( R.color.color_A6D24C);
//            abilityTextView.setTextColor( color );
//            timeLabelView.setTextColor( color );
//            timeView.setText( time, color, color2 );
//            setBackgroundResource( R.drawable.bg_rect_cheat_train );
//            startTextView.setBackgroundResource( R.drawable.bg_rect_cheatbtn_train);
//        }else if( TYPE_QUESTION_REVIEW == type ){             //错题回顾
//            titleLayout.setBackgroundResource( R.drawable.bg_cheattask_reviewtitle );
//            nameImageView.setImageResource( R.drawable.ic_wrong_collect );
//            nameTextView.setText( "错题回顾" );
//            int color = getContext().getResources().getColor( R.color.color_A18EB6);
//            int color2 = getContext().getResources().getColor( R.color.color_B082E1);
//            abilityTextView.setTextColor( color );
//            countTextView.setText( "+ " + String.valueOf(value) );
//            timeLabelView.setTextColor( color );
//            timeView.setText( time, color, color2 );
//            setBackgroundResource( R.drawable.bg_rect_cheat_err );
//            startTextView.setBackgroundResource( R.drawable.bg_rect_cheatbtn_err);
//        }
        startView.setVisibility(View.VISIBLE);

//        startTextView.setText("开始");
//        startTextView.setEnabled( true );
//        bCoverSet = false;
//        coverView.setVisibility(GONE);
    }

    public void finishCheatTask(){
        //startTextView.setText("已完成");
        startView.setVisibility( View.GONE );
    }

//    @Override
//    protected void onLayout(boolean changed, final int left, final int top, final int right,
//                            final int bottom) {
//        super.onLayout(changed, left, top, right, bottom);
//
//        if( !startTextView.isEnabled() && !bCoverSet )
//        // 初始化可视范围及框体大小
//        postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                bCoverSet = true;
//                ViewGroup.LayoutParams layoutParams = coverView.getLayoutParams();
//                layoutParams.height = bottom - top;
//                layoutParams.width  = right - left;
//                coverView.setLayoutParams( layoutParams );
//                coverView.setVisibility(VISIBLE);
//            }
//        },100);
//    }

    //-----------------------------------------------------------------------------------
    private void init(Context context, AttributeSet attrs){
        int type = 0;
        if( attrs != null ){
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CheatTaskView);
            type = a.getInt(R.styleable.CheatTaskView_cheattype, 0);
            a.recycle();
        }

        if( 0 == type ){
            inflate( context, GlobalData.isPad()?R.layout.view_cheat_task_errreview:R.layout.view_cheat_task_errreview_phone, this );
        }else{
            inflate( context, GlobalData.isPad()?R.layout.view_cheat_task_forcetrain:R.layout.view_cheat_task_forcetrain_phone, this );
        }

//        titleLayout   =  (RelativeLayout)findViewById( R.id.cheattask_title_layout );
//        nameImageView = (ImageView)findViewById( R.id.cheattask_name_imageview );
//        nameTextView  = (TextView)findViewById( R.id.cheattask_name_textview );
//        abilityTextView = (TextView)findViewById( R.id.cheattask_abilityname_textview );
        countTextView = (TextView)findViewById( R.id.cheattask_abilityvalue_textview );
//        timeLabelView  = (TextView)findViewById( R.id.cheattask_time_textview );
        timeView       = (TimeView)findViewById( R.id.cheattask_time_timeview );
        startView = (ImageView) findViewById( R.id.cheattask_startbtn );
        startView.setOnClickListener( this );
//        coverView     = findViewById( R.id.cheattask_coverview );
    }

//    private String getEstimateTime( int time ){
//        return "预计时间"+TimeUtil.getElapseTimeForShow( time*1000 );
//    }
}

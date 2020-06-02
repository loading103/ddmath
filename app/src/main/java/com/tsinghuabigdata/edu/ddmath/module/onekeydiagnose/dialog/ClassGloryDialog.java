package com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.view.ClassGloryView;
import com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.view.GradeGloryView;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

/**
 * 年级榜 && 班级榜
 */

public class ClassGloryDialog extends Dialog implements View.OnClickListener {

    private RelativeLayout           mRlClose;

    private TextView classNameView;
    private View selectClassView;
    private ClassGloryView classGloryView;

    private TextView gradeNameView;
    private View selectGradeView;
    private GradeGloryView gradeGloryView;

    public ClassGloryDialog(@NonNull Context context) {
        this(context, R.style.dialog);
    }

    public ClassGloryDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        initView(context);
    }

    private void initView(Context context) {
        if (GlobalData.isPad()) {
            setContentView(R.layout.dialog_class_rank);
        } else {
            setContentView(R.layout.dialog_class_rank_phone);
        }
        setCancelable(true);
        setCanceledOnTouchOutside(false);

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.dimAmount = 0.3f;
        dialogWindow.setAttributes(lp);
        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        //        mIvClose = (ImageView) findViewById(R.id.iv_close);
        mRlClose =  findViewById(R.id.rl_close);
        classNameView = findViewById( R.id.tv_rank_class );
        selectClassView = findViewById( R.id.view_select_class );
        classGloryView = findViewById( R.id.class_rank_view );

        gradeNameView = findViewById( R.id.tv_rank_grade );
        selectGradeView = findViewById( R.id.view_select_grade );
        gradeGloryView = findViewById( R.id.grade_rank_view );

        mRlClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        classNameView.setOnClickListener( this );
        gradeNameView.setOnClickListener( this );
    }


    @Override
    public void onClick(View v) {
        if( v.getId() == R.id.tv_rank_class ){
            classNameView .setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            selectClassView.setVisibility(View.VISIBLE);
            classGloryView.setVisibility(View.VISIBLE);

            gradeNameView.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            selectGradeView.setVisibility(View.GONE);
            gradeGloryView.setVisibility(View.GONE);

        }else if( v.getId() == R.id.tv_rank_grade ){
            classNameView.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            selectClassView.setVisibility(View.GONE);
            classGloryView.setVisibility(View.GONE);

            gradeNameView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            selectGradeView.setVisibility(View.VISIBLE);
            gradeGloryView.setVisibility(View.VISIBLE);
        }
    }

}

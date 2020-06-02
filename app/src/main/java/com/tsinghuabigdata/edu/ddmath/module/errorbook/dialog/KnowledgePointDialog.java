package com.tsinghuabigdata.edu.ddmath.module.errorbook.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.KnowledgePiontBean;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.adapter.KnowledgeMasteryAdapter;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

import java.util.ArrayList;
import java.util.Locale;


/**
 * 错误知识点Dialog
 */
public class KnowledgePointDialog extends Dialog {
    //
//    private FrameLayout mainLayout;

    private TextView titleView;
    private TextView qustionCountView;
    private TextView knowledgeCountView;
    private ListView knowledgeListView;

//    public CustomDialog(Context context) {
//        super(context);
//        initData();
//    }

    public KnowledgePointDialog(Context context, int theme) {
        super(context, theme);
        initData();
    }

    public void setData(String title, int qustionCount, String knowId, ArrayList<KnowledgePiontBean> list){
        titleView.setText( title );
        qustionCountView.setText( String.format(Locale.getDefault(),"共%d题", qustionCount ));
        knowledgeCountView.setText(String.format(Locale.getDefault(),"错误知识点（共%d个）", list.size() ));

        //设置主要知识点
        if(TextUtils.isEmpty(knowId)){      //默认第一个
            list.get(0).setMastery( true );
        }else {
            for( KnowledgePiontBean bean : list ){
                if( bean.getKnowledgeId().equals(knowId) ){
                    bean.setMastery( true );
                }
            }
        }

        //
        KnowledgeMasteryAdapter adapter = new KnowledgeMasteryAdapter( getContext() );
        adapter.addAll( list );
        knowledgeListView.setAdapter( adapter );
    }
    //--------------------------------------------------------------------------
    private void initData() {

        setContentView(GlobalData.isPad()?R.layout.dialog_knowledgepoint_layout:R.layout.dialog_knowledgepoint_layout_phone);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            if( window!=null ){
                window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        }

        ImageView closeView = (ImageView) findViewById(R.id.iv_close_btn);
        closeView.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        titleView = (TextView)findViewById( R.id.tv_title );
        qustionCountView = (TextView)findViewById( R.id.tv_question_count );
        knowledgeCountView = (TextView)findViewById( R.id.tv_know_count );
        knowledgeListView = (ListView)findViewById(R.id.lv_know_list);
    }

}

package com.tsinghuabigdata.edu.ddmath.module.ddwork.view;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.commons.TextHtml.LinkMovementMethodExt;
import com.tsinghuabigdata.edu.ddmath.commons.TextHtml.MessageSpan;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.DDWorkDetail;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.EKPiontBean;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.LocalQuestionInfo;
import com.tsinghuabigdata.edu.ddmath.module.famousteacher.FamousTeacherActivity;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.utils.ArrayUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 名师精讲视频引导
 */
public class GuideVideoView extends LinearLayout implements View.OnClickListener{

    private QuestionTextView knowledgesView;

    //private String knowIds;
    private MyHandler mHandler = new MyHandler();

    public GuideVideoView(Context context) {
        super(context);
        init();
    }

    public GuideVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GuideVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
//            case R.id.view_videoimage:
//                Intent intent = new Intent(getContext(), FamousTeacherActivity.class);
//                intent.putExtra(FamousTeacherActivity.KNOWLEDGEID, knowIds);
//                getContext().startActivity(intent);
//                break;
            default:
                break;
        }
    }

    public void setData(DDWorkDetail detail){
        ArrayList<EKPiontBean> list = detail.getTopKnowledgeList();
        if( list == null || list.size() == 0 ){
            setVisibility( GONE );
            return;
        }
        setVisibility( VISIBLE );

        List<String> idlist = new ArrayList<>();

        //String html = "<div style=\"display: flex;align-items: center;\">本次作业<span style=\"color:#CD6D41\">%s</span>还需提高，快去观看 <img src =\"file:///android_asset/btn_teachervideo.png\"></img> ，快速提高吧！</div>";
        String html = "本次作业%s还需提高，快去观看 <img>，快速提高吧！";

        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for( EKPiontBean point : list ){
            if( !first )
                sb.append( "、" );
            else
                first = false;
            sb.append( point.getKnowledgeName() );

            idlist.add( point.getKnowledgeId() );
        }
        //sb.append("<img src =\"file:///android_asset/btn_teachervideo.png\"></img>");
        mHandler.setKnowIds( ArrayUtil.join( idlist, "," ) );// idlist.toString();

        String keywords = sb.toString();
        html = String.format( html, keywords );

        //knowledgesView.setText( Html.fromHtml( html, new MImageGetter( knowledgesView, getContext().getApplicationContext()), null));

        LocalQuestionInfo questionInfo = new LocalQuestionInfo();
        questionInfo.setStem( html );
        questionInfo.setStemGraph( "file:///android_asset/btn_teachervideo.png" );
        knowledgesView.setQuestion( questionInfo, false );
        knowledgesView.setKeyWords( keywords, getResources().getColor(R.color.color_CB5F22) );
    }

    //-----------------------------------------------------------------------------
    private void init() {

        inflate( getContext(), GlobalData.isPad()?R.layout.view_ddwork_guidevideo:R.layout.view_ddwork_guidevideo_phone, this );
        knowledgesView = findViewById( R.id.view_knowledges );

//        ImageView iv = (ImageView) findViewById( R.id.view_videoimage );
//        iv.setOnClickListener( this );
        knowledgesView.setMovementMethod(LinkMovementMethodExt.getInstance(mHandler, ImageSpan.class));

        //默认不显示
        setVisibility( GONE );
    }

    class MyHandler extends Handler {
        private String mKnowIds;

        private void setKnowIds( String ids ){
            mKnowIds = ids;
        }
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            if (what == 200) {
                MessageSpan ms = (MessageSpan) msg.obj;
                Object[] spans = (Object[]) ms.getObj();
                //final ArrayList<String> list = new ArrayList<>();
                for (Object span : spans) {
                    if (span instanceof ImageSpan) {
                        Intent intent = new Intent(getContext(), FamousTeacherActivity.class);
                        intent.putExtra(FamousTeacherActivity.KNOWLEDGEID, mKnowIds);
                        getContext().startActivity(intent);
                    }
                }
            }
        }
    }

}

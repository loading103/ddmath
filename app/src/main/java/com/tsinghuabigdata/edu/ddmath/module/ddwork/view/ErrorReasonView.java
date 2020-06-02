package com.tsinghuabigdata.edu.ddmath.module.ddwork.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.module.xbook.XBookUtils;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.view.MultiGridView;

import java.util.ArrayList;

/**
 * 错误原因
 */
public class ErrorReasonView extends MultiGridView implements AdapterView.OnItemClickListener{

    private ArrayList<ReasonItem> list = new ArrayList<>();
    private ReasonAdapter reasonAdapter;

    public ErrorReasonView(Context context) {
        super(context);
        init();
    }

    public ErrorReasonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ErrorReasonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    public ReasonView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//        initialize();
//    }

//    public void setReasons( String reasons ){
//        if( TextUtils.isEmpty(reasons) ) return;
//        String reasonArray[] = reasons.split(",");
//        for( String reason : reasonArray ){
//            String reason_cn = XBookUtils.getQuestionTypeName(reason);
//            for( ReasonItem item : list ){
//                if(item.reason.equals( reason_cn) ){
//                    item.selected = true;
//                    break;
//                }
//            }
//        }
//        reasonAdapter.notifyDataSetChanged();
//    }

    public void setReasons( int resid ){
        String [] reasonArray = getResources().getStringArray( resid );
        for( String reason : reasonArray ){
            list.add( new ReasonItem( reason, false  ) );
        }

        reasonAdapter.clear();
        reasonAdapter.addAll( list );
        reasonAdapter.notifyDataSetChanged();

        AppLog.d("fdfdsfdsfsdfds   len = "+ reasonArray.length );
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if( position>=0 && position<list.size() ){

            ReasonItem item = list.get(position);
            item.selected = !item.selected;
            reasonAdapter.notifyDataSetChanged();

//            if( mFilterChangeListener!=null )
//                mFilterChangeListener.filterChange();
        }
    }

    //多个用","分割
    public String getSelectedData(){
        boolean first = true;
        StringBuilder sb = new StringBuilder();
        sb.append("");
        for( ReasonItem item : list ){
            if( item.selected ){
                if( !first ) sb.append(",");
                first = false;
                sb.append( XBookUtils.getQuestionTypeName(item.reason) );
            }
        }
        return sb.toString();
    }

    //-----------------------------------------------------------------------------
    private void init() {

        reasonAdapter = new ReasonAdapter( getContext(), 0);
        setAdapter( reasonAdapter );

        setOnItemClickListener( this );
    }

    //-----------------------------------------------------------------------------------------
    //默认怎么处理 是否选中
    class ReasonItem{

        String reason;
        boolean selected;

        ReasonItem( String reason, boolean selected){
            this.reason = reason;
            this.selected = selected;
        }
    }

    private class ReasonAdapter extends ArrayAdapter<ReasonItem> {

        ReasonAdapter(@NonNull Context context, @LayoutRes int resource) {
            super(context, resource);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(GlobalData.isPad()?R.layout.gridview_revise_reason:R.layout.gridview_revise_reason_phone, parent, false);
                convertView.setTag(new ViewHolder(convertView) );
            }
            ViewHolder viewHolder = ((ViewHolder) convertView.getTag());
            viewHolder.bindView( getItem(position) );
            return convertView;
        }

        class ViewHolder {
            RelativeLayout layout;
            TextView textView;

            private ViewHolder(View convertView) {
                layout = (RelativeLayout)convertView.findViewById( R.id.xbook_list_reason_layout );
                textView = (TextView) convertView.findViewById(R.id.xbook_list_reason_text);
            }

            void bindView( ReasonItem item ){

                textView.setText( item.reason );
                if( item.selected ){
                    layout.setSelected( true );
                    textView.setTextColor( Color.WHITE );
                }else{
                    layout.setSelected( false );
                    textView.setTextColor( getResources().getColor( R.color.color_666666 ));
                }

//                if(GlobalData.isPad()){
//                    int tb = WindowUtils.dpToPixels(getContext(), 12);
//                    viewHolder.textView.setPadding( 0, tb, 0, tb );
//                }
            }
        }
    }
}

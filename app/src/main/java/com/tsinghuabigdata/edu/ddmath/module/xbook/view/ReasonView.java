package com.tsinghuabigdata.edu.ddmath.module.xbook.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.module.xbook.QuestionFilterListener;
import com.tsinghuabigdata.edu.ddmath.module.xbook.XBookUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.WindowUtils;
import com.tsinghuabigdata.edu.ddmath.view.MultiGridView;

import java.util.ArrayList;

/**
 * 错误原因
 */
public class ReasonView extends MultiGridView implements AdapterView.OnItemClickListener{

    private ArrayList<ReasonItem> list = new ArrayList<>();
    private ReasonAdapter reasonAdapter;

    private boolean singleMode = false;     //单选模式，可以没有选中项
    private boolean mustMode   = false;     //单选模式，必须有选中项，即选中项是自己是不取消选中
    private boolean selectMode = false;     //选中模式，不能操作
    private boolean filterMode = false;     //筛选界面使用

    public ReasonView(Context context) {
        super(context);
        initialize();
    }

    public ReasonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public ReasonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    public ReasonView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//        initialize();
//    }
    public void setSelectedReasons( String reasons ){
        if( TextUtils.isEmpty(reasons) ) return;
        String reasonArray[] = reasons.split(",");
        for( String reason : reasonArray ){
            String reason_cn = XBookUtils.getQuestionTypeName(reason);
            for( ReasonItem item : list ){
                if(item.reason.equals( reason_cn) ){
                    item.selected = true;
                    break;
                }
            }
        }
        reasonAdapter.notifyDataSetChanged();
    }

    public void setReasons( int resid ){
        String [] reasonArray = getResources().getStringArray( resid );
        for( String reason : reasonArray ){
            list.add( new ReasonItem( reason, false  ) );
        }
        reasonAdapter.notifyDataSetChanged();
    }

    public void clear(){
        list.clear();
        reasonAdapter.notifyDataSetChanged();
    }

    public void setReason( String type ){
        list.add( new ReasonItem( XBookUtils.getQuestionTypeName(type), false  ) );
        reasonAdapter.notifyDataSetChanged();
    }

    public void setReasons( String reasons[] ){
        for( String reason : reasons ){
            list.add( new ReasonItem( XBookUtils.getQuestionTypeName(reason), false  ) );
        }
        reasonAdapter.notifyDataSetChanged();
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if( position>=0 && position<list.size() ){

            if( mustMode ){
                for( int i=0; i<list.size(); i++ ){
                    list.get(i).selected = false;
                }
            }else if( singleMode ){
                for( int i=0; i<list.size(); i++ ){
                    if( i!=position )
                     list.get(i).selected = false;
                }
            }

            ReasonItem item = list.get(position);
            item.selected = !item.selected;
            reasonAdapter.notifyDataSetChanged();

            if( mFilterChangeListener!=null )
                mFilterChangeListener.filterChange();
        }
    }

    public void setSingleMode( boolean single ){
        singleMode = single;
    }
    public void setSelectMode( boolean select ){
        selectMode = select;
    }

    public void setMustMode(boolean mustMode) {
        this.mustMode = mustMode;
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

    private QuestionFilterListener mFilterChangeListener;
    public void setFilterListener( QuestionFilterListener listener ){
        mFilterChangeListener = listener;
    }

    public void setFilterMode(boolean filterMode) {
        this.filterMode = filterMode;
    }

    //-----------------------------------------------------------------------------
    private void initialize() {
        //setEnabled( false );
//        setOnTouchListener(new OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if( event.getAction() == MotionEvent.ACTION_MOVE )
//                    return true;
//                return false;
//            }
//        });
        reasonAdapter = new ReasonAdapter();
        setAdapter( reasonAdapter );

        setOnItemClickListener( this );
    }

    //-----------------------------------------------------------------------------------------
    //默认怎么处理 是否选中
    class ReasonItem{

        String reason;
        boolean selected;

        public ReasonItem( String reason, boolean selected){
            this.reason = reason;
            this.selected = selected;
        }
    }

    class ReasonAdapter extends BaseAdapter {

        class ViewHolder {
            RelativeLayout layout;
            TextView textView;
            ImageView imageView;

            private ViewHolder(View convertView) {
                layout = (RelativeLayout)convertView.findViewById( R.id.xbook_list_reason_layout );
                this.textView = (TextView) convertView.findViewById(R.id.xbook_list_reason_text);
                this.imageView = (ImageView) convertView.findViewById(R.id.xbook_list_reason_image);
                if(filterMode){
                    layout.setBackgroundResource( R.drawable.xbook_reasonfilter_item);
                    imageView.setImageResource( R.drawable.ic_choosed_orange );
                }
            }
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public ReasonItem getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(GlobalData.isPad()?R.layout.gridview_xbook_reason:R.layout.gridview_xbook_reason_phone, parent, false);
                convertView.setTag(new ViewHolder(convertView) );
            }

            ViewHolder viewHolder = (ViewHolder) convertView.getTag();
            ReasonItem item = getItem(position);
            viewHolder.textView.setText( item.reason );
            if( selectMode ){
                viewHolder.imageView.setVisibility( GONE);
                viewHolder.layout.setSelected( true );

                viewHolder.textView.setTextColor( getResources().getColor( filterMode?R.color.color_FFB000:R.color.color_6472BE ));
            }else if( item.selected ){
                viewHolder.imageView.setVisibility( VISIBLE);
                viewHolder.layout.setSelected( true );
                viewHolder.textView.setTextColor( getResources().getColor( filterMode?R.color.color_FFB000:R.color.color_6472BE ));
            }else{
                viewHolder.imageView.setVisibility( GONE);
                viewHolder.layout.setSelected( false );
                viewHolder.textView.setTextColor( getResources().getColor( filterMode?R.color.color_666666:R.color.color_6472BE ));
            }

            if(GlobalData.isPad()){
                int tb = WindowUtils.dpToPixels(getContext(), 12);
                viewHolder.textView.setPadding( 0, tb, 0, tb );
            }
            return convertView;
        }

    }
}

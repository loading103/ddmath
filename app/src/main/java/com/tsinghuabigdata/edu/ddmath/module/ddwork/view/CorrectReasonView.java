package com.tsinghuabigdata.edu.ddmath.module.ddwork.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/11/22.
 */

public class CorrectReasonView extends RecyclerView {

    private ArrayList<ReasonItem> list = new ArrayList<>();
    private ReasonAdapter adapter;

    public CorrectReasonView(Context context) {
        super(context);
        init();
    }

    public CorrectReasonView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CorrectReasonView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private class ReasonItem {
        String reason;
        boolean selected;

        ReasonItem(String reason, boolean selected) {
            this.reason = reason;
            this.selected = selected;
        }
    }

    public void setReasons( int resid ){
        String [] reasonArray = getResources().getStringArray( resid );
        boolean first = true;
        for( String reason : reasonArray ){
            list.add( new ReasonItem( reason, first  ) );
            first = false;
        }

        adapter.notifyDataSetChanged();
        AppLog.d("fdfdsfdsfsdfds   len = "+ reasonArray.length );
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
                sb.append(item.reason);
            }
        }
        return sb.toString();
    }

    private void init() {
        adapter = new ReasonAdapter();
        setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL));
        setAdapter(adapter);
    }

    private class ReasonAdapter extends RecyclerView.Adapter<ReasonAdapter.ViewHolder> {

        class ViewHolder extends RecyclerView.ViewHolder {

            RelativeLayout layout;
            TextView textView;

            public ViewHolder(View itemView) {
                super(itemView);
                this.layout = (RelativeLayout) itemView.findViewById(R.id.correct_reason_layout);
                this.textView = (TextView) itemView.findViewById(R.id.correct_reason_text);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(GlobalData.isPad()?R.layout.item_view_correct_reason:R.layout.item_view_correct_reason_phone, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            final ReasonItem item = list.get(position);
            holder.textView.setText( item.reason );
            if( item.selected ){
                holder.layout.setSelected( true );
                holder.textView.setTextColor( Color.WHITE );
            }else{
                holder.layout.setSelected( false );
                holder.textView.setTextColor( getResources().getColor( R.color.color_666666 ));
            }
            holder.layout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    singleSelectMode( position );
                    adapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        private void singleSelectMode(int position){
            for( int i=0; i<list.size(); i++ ){
                ReasonItem item = list.get(i);
                item.selected = (i==position)&&!item.selected;
            }
        }
    }
}

package com.tsinghuabigdata.edu.ddmath.module.message.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.module.badge.BadgeManager;
import com.tsinghuabigdata.edu.ddmath.module.message.bean.MessageInfo;
import com.tsinghuabigdata.edu.ddmath.util.DateUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

/**
 * 消息 适配器
 */
public class MessageAdapter extends ArrayAdapter<MessageInfo> {

    private Context mContext;

    public MessageAdapter(Context context) {
        super(context, 0);
        mContext = context;
    }

    public boolean selectItem( int position ){

        int count = getCount();
        if( position >= count ){
            return false;
        }
        for( int i=0; i<count; i++ ){
            MessageInfo item = getItem( i );
            if( item==null ) continue;
            if( position == i ){

                //桌面消息数量减一
                if( MessageInfo.S_UNREAD.equals( item.getStatus() ) ) {
                    new BadgeManager(getContext()).removeBadge();
                }

                item.setStatus( MessageInfo.S_READ );
                if( GlobalData.isPad() )
                    item.setSelect( true );
            }
            else item.setSelect( false );
        }
        notifyDataSetChanged();
        return true;
    }

//    public int getSelectIndex(){
//        int count = getCount();
//        for( int i=0; i<count; i++ ){
//            LocalPageInfo item = getItem( i );
//            if( getItem(i).isSelected() ) return i;
//        }
//        return -1;
//    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate( GlobalData.isPad()?R.layout.item_message_msgitem:R.layout.item_message_msgitem_mobile, parent, false);
            convertView.setTag(new ViewHolder(convertView));
        }
        ViewHolder viewHolder = ((ViewHolder) convertView.getTag());
        viewHolder.bindView( getItem(position), position);
        return convertView;
    }

    class ViewHolder{

        private int position;

        private LinearLayout mainLayout;

        private TextView nameTextView;
        private TextView dateTextView;
        private TextView contentTextView;

        private ImageView iconImageView;
        private ImageView redPointImageView;
        private ImageView keyTipsImageView;

        private ViewHolder(View convertView) {
            mainLayout       = (LinearLayout) convertView;
            nameTextView     = (TextView) convertView.findViewById( R.id.item_msg_name );
            dateTextView     = (TextView) convertView.findViewById( R.id.item_msg_date );
            contentTextView  = (TextView) convertView.findViewById( R.id.item_msg_content );

            iconImageView    = (ImageView) convertView.findViewById( R.id.item_msg_icon );
            redPointImageView    = (ImageView) convertView.findViewById( R.id.item_msg_redpoint );
            keyTipsImageView    = (ImageView) convertView.findViewById( R.id.item_msg_keytips );
        }

        public void bindView( MessageInfo msg, int position ){
            this.position = position;

            nameTextView.setText( msg.getMsgTitle() );
            dateTextView.setText( DateUtils.getCurrDateStr(msg.getSendTime()));//DateUtils.format(msg.getSendTime()) );
            String data = msg.getDescription();
            if(TextUtils.isEmpty(data)) data = "";
            contentTextView.setText(Html.fromHtml(data) );

            //选中状态
            mainLayout.setBackgroundColor( mContext.getResources().getColor( msg.isSelect()?R.color.color_D4EEFF:R.color.color_E9F7FF));

            //已读/未读状态
            redPointImageView.setVisibility( MessageInfo.S_UNREAD.equals(msg.getStatus())?View.VISIBLE:View.GONE);

            //是否重要提示
            keyTipsImageView.setVisibility( msg.isKeyTips() ? View.VISIBLE:View.GONE);

            //消息类型
            switch ( msg.getMsgDataType() ){
                case MessageInfo.MSG_TYPE_REPORT: iconImageView.setImageResource( R.drawable.ic_msg_report );break;
                case MessageInfo.MSG_TYPE_WORK: iconImageView.setImageResource( R.drawable.ic_homework_message );break;
                default:iconImageView.setImageResource( R.drawable.ic_news );break;
            }
        }
    }

}
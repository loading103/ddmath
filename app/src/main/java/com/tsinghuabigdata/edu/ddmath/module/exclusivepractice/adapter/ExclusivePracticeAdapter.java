package com.tsinghuabigdata.edu.ddmath.module.exclusivepractice.adapter;

import android.content.Context;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.module.exclusivepractice.bean.PracticeProductBean;
import com.tsinghuabigdata.edu.ddmath.module.exclusivepractice.dialog.FreeDroitDialog;
import com.tsinghuabigdata.edu.ddmath.module.exclusivepractice.view.FreeDroitView;
import com.tsinghuabigdata.edu.ddmath.module.product.ProductDetailActivity;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.view.MultiGridView;


/**
 * 专属套题列表适配器
 */
@Deprecated
public class ExclusivePracticeAdapter extends ArrayAdapter<PracticeProductBean> {

    private Context mContext;
    private int practiceType;

    public ExclusivePracticeAdapter(Context context ) {
        super(context, 0);
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(GlobalData.isPad()?R.layout.item_practice_exclusive : R.layout.item_practice_exclusive_phone, parent, false);
            convertView.setTag(new ViewHolder(convertView));
        }
        ViewHolder viewHolder = ((ViewHolder) convertView.getTag());
        viewHolder.bindView( position );
        return convertView;
    }

    public void setPracticeType( int type ){
        practiceType = type;
    }

    //------------------------------------------------------------------------------------------
    class ViewHolder implements View.OnClickListener{

        //private LinearLayout mainLayout;
        private int position;

        private TextView goodsNameView;
        private TextView assitNameView;

        private FreeDroitView freeDroitView;

        private MultiGridView multiGridView;

        //private ClassicPracticeAdapter practiceAdapter;
        private ViewHolder(View convertView) {

            goodsNameView   = (TextView)convertView.findViewById( R.id.item_practice_name );
            assitNameView   = (TextView)convertView.findViewById( R.id.item_practice_assitname );
            //assitNameView.setVisibility( View.GONE );

            TextView textView = (TextView)convertView.findViewById( R.id.item_practice_introduce );
            textView.setOnClickListener( this );
            textView.setVisibility( View.GONE );
            ImageView imageView = (ImageView)convertView.findViewById( R.id.item_practice_report );
            imageView.setOnClickListener( this );
            //imageView.setVisibility( View.GONE );
            imageView = (ImageView)convertView.findViewById( R.id.item_practice_help );
            imageView.setOnClickListener( this );
            //imageView.setVisibility( View.GONE );

            //
            freeDroitView   = (FreeDroitView) convertView.findViewById( R.id.item_practice_freedroitview );
            freeDroitView.setOnClickListener( this );

            multiGridView = (MultiGridView)convertView.findViewById( R.id.item_practice_gridview );

        }
        @Override
        public void onClick(View v) {
            switch( v.getId() ){
                case R.id.item_practice_report:{  //跳转到报告
                    //跳转到专属习题报告
                    //EventBus.getDefault().post(new JumpEvent(MainActivity.FRAGMENT_STUDY_CONDITION, practiceType==TYPE_CLASSIC?MyReportFragment.CLASSIC_I : MyReportFragment.EXCLUSIVE_EXERCISES_I));
                    break;
                }
                case R.id.item_practice_introduce:{
                    PracticeProductBean bean = getItem(position);
                    if( bean == null ) return;
                    ProductDetailActivity.gotoProductDetailByProductId( getContext(), bean.getName(), bean.getProductId() );
                    break;
                }
                case R.id.item_practice_help: {
                    PracticeProductBean bean = getItem(position);
                    if( bean == null ) return;
                    //ProductHelpActivity.gotoProductHelpActivityByProductId( getContext(), bean.getName(), bean.getProductId() );
                    break;
                }
                case R.id.item_practice_freedroitview:{
                    //弹出提示
                    PracticeProductBean bean = getItem(position);
                    if( bean == null || bean.getUseTimes()==null || bean.getUseTimes().getDetails() == null || bean.getUseTimes().getDetails().size() == 0 ) return;

                    Rect rect = new Rect();
                    freeDroitView.getGlobalVisibleRect( rect );

                    FreeDroitDialog dialog = new FreeDroitDialog( mContext );
                    dialog.setData( bean.getUseTimes().getDetails(), rect.left, rect.bottom, 0  );
                    dialog.show();
                    break;
                }
                default:
                    break;
            }
        }

        void bindView( int position ){
            this.position = position;

            PracticeProductBean bean = getItem(position);
            if( bean == null ) return;

            //
            goodsNameView.setText( bean.getName() );
            assitNameView.setText( bean.getSubName() );

            freeDroitView.setVisibility( View.GONE );
//            if( bean.getUseTimes()!=null && bean.getUseTimes().getTotalTimes() > 0 ){
//                freeDroitView.setVisibility( View.VISIBLE );
//                freeDroitView.setData( bean.getUseTimes().getTotalTimes() );
//            }

            //
//            ClassicPracticeAdapter practiceAdapter = new ClassicPracticeAdapter( getContext() );
//            multiGridView.setAdapter( practiceAdapter );
////            practiceAdapter.setPracticeGoodsBean( bean/*, position%2==0*/ );
////            practiceAdapter.setPracticeType(practiceType);
//            practiceAdapter.clear();
//            practiceAdapter.addAll( bean.getProductList() );
//            practiceAdapter.notifyDataSetChanged();
        }

    }
}

package administrator.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.kyleduo.switchbutton.SwitchButton;
import com.nex3z.flowlayout.FlowLayout;
import com.qrcodescan.R;

import java.util.List;

import administrator.base.DensityUtil;
import administrator.adapters.listener.SpaceCardCallbackListener;
import administrator.entity.SpaceWithAreas;

/**
 * Created by zhuang_ge on 2017/8/4.
 */

public class SpaceCardAdapter extends Adapter {

    //数据源
    private List<SpaceWithAreas> swaList;

    private Context mContext;

    private SpaceCardCallbackListener listener;

    public SpaceCardCallbackListener getListener() {
        return listener;
    }

    public void setListener(SpaceCardCallbackListener listener) {
        this.listener = listener;
    }

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context mContext) {
        this.mContext = mContext;
    }

    public List<SpaceWithAreas> getSwaList() {
        return swaList;
    }

    public void setSwaList(List<SpaceWithAreas> swaList) {
        this.swaList = swaList;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        //空间名称
        private TextView spaceName;
        //填装房间标签的布局
        private FlowLayout roomLabels;
        //离家、归家的选择开关
        private SwitchButton typeSwitch;
        //开关的文字说明
        private TextView type;
        //是否为默认空间
//        private Switch defaultSwitch;
        //整个背景
        private CardView back;
        public ViewHolder(View itemView) {
            super(itemView);

            spaceName = (TextView) itemView.findViewById(R.id.space_name);
            roomLabels = (FlowLayout) itemView.findViewById(R.id.room_label_flowLayout);
            typeSwitch = (SwitchButton) itemView.findViewById(R.id.type_switch);
//            defaultSwitch = (Switch) itemView.findViewById(R.id.default_switch);
            type = (TextView) itemView.findViewById(R.id.space_type);
            back = (CardView)itemView.findViewById(R.id.space_card_back);

        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.space_card_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ViewHolder viewHolder = (ViewHolder) holder;
        final SpaceWithAreas mSpace = swaList.get(position);
        viewHolder.spaceName.setText(mSpace.getName());
        viewHolder.typeSwitch.setChecked(mSpace.getModelType() == 1);
        viewHolder.typeSwitch.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                viewHolder.type.setText(b ? "离家" : "归家");
                listener.onClickSwitch(mSpace.getId(),viewHolder.typeSwitch,b);
            }
        });

//        viewHolder.defaultSwitch.setChecked(mSpace.getIsDefault() == 1);
        viewHolder.type.setText(viewHolder.typeSwitch.isChecked() ? "离家" : "归家");

        RecyclerView.LayoutParams lp =
                (RecyclerView.LayoutParams) viewHolder.back.getLayoutParams();
        //高亮（浮起）默认空间卡片
        if(mSpace.getIsDefault() == (short)1) {
            viewHolder.back.setCardElevation((float)30.0);
//            viewHolder.back.setCardBackgroundColor(
//                    mContext.getResources().getColor(R.color.main_color_line));
//            viewHolder.back.setBackground(getContext().getDrawable(R.color.rice));
            lp.setMargins(
                    DensityUtil.dip2px(mContext,(float) 4.0),
                    DensityUtil.dip2px(mContext,(float) 6.0),
                    DensityUtil.dip2px(mContext,(float) 4.0),
                    DensityUtil.dip2px(mContext,(float) 6.0));
            viewHolder.back.setLayoutParams(lp);
        } else {
            viewHolder.back.setCardElevation((float)6.0);
//            viewHolder.back.setCardBackgroundColor(
//                    mContext.getResources().getColor(R.color.device_card_back));
//            viewHolder.back.setBackground(getContext().getDrawable(R.color.rice));
            lp.setMargins(
                    DensityUtil.dip2px(mContext,(float) 4.0),
                    DensityUtil.dip2px(mContext,(float) 4.0),
                    DensityUtil.dip2px(mContext,(float) 4.0),
                    DensityUtil.dip2px(mContext,(float) 4.0));
            viewHolder.back.setLayoutParams(lp);
        }

        viewHolder.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onChooseSpace(mSpace.getId());
                //如果被选中的卡片 不是默认空间 则进行更改
                if(mSpace.getIsDefault() != 1) {
                    changeDefaultSpaceCard(position);
                }
            }
        });

        //生成房间标签流式布局
        AreaLabelAdapter adapter = new AreaLabelAdapter(
                mSpace.getAreaList(), viewHolder.roomLabels,mContext);
        adapter.clearLabels();
        adapter.addLabels();
    }
    private void changeDefaultSpaceCard(int position) {
        for(SpaceWithAreas swa : swaList) {
            if (swa.getIsDefault() == 1) {
                swa.setIsDefault((short)0);
                break;
            }
        }
        swaList.get(position).setIsDefault((short)1);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return swaList.size();
    }
}

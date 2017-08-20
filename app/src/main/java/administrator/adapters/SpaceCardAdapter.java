package administrator.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.nex3z.flowlayout.FlowLayout;
import com.qrcodescan.R;

import java.util.List;

import administrator.entity.Space;
import administrator.entity.SpaceWithAreas;

/**
 * Created by zhuang_ge on 2017/8/4.
 */

public class SpaceCardAdapter extends Adapter {

    //数据源
    private List<SpaceWithAreas> swaList;

    private Context mContext;

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
        private Switch typeSwitch;
        //开关的文字说明
        private TextView type;
        //是否为默认空间
        private Switch defaultSwitch;

        public ViewHolder(View itemView) {
            super(itemView);

            spaceName = (TextView) itemView.findViewById(R.id.space_name);
            roomLabels = (FlowLayout) itemView.findViewById(R.id.roomLabelFlowLayout);
            typeSwitch = (Switch) itemView.findViewById(R.id.type_switch);
            defaultSwitch = (Switch) itemView.findViewById(R.id.default_switch);
            type = (TextView) itemView.findViewById(R.id.space_type);

        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.space_card_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        SpaceWithAreas mSpace = swaList.get(position);
        viewHolder.spaceName.setText(mSpace.getName());
        viewHolder.typeSwitch.setChecked(mSpace.getStatus() == 1);
        viewHolder.defaultSwitch.setChecked(mSpace.getIsDefault() == 1);
        viewHolder.type.setText(viewHolder.typeSwitch.isChecked() ? "离家模式" : "归家模式");

        //生成房间标签流式布局
        AreaLabelAdapter adapter = new AreaLabelAdapter(
                mSpace.getAreaList(), viewHolder.roomLabels,mContext);
        adapter.addLabels();
    }

    @Override
    public int getItemCount() {
        return swaList.size();
    }
}

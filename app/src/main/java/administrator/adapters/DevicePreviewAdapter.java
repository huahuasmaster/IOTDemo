package administrator.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qrcodescan.R;

import java.util.List;

import administrator.entity.DeviceCurValue;
import administrator.enums.DataTypeEnum;
import administrator.ui.AreaDetailActivity;
import lecho.lib.hellocharts.model.Line;

/**
 * 房间卡片中 设备数据预览的适配器 需要设置context
 * Created by zhuang_ge on 2017/8/19.
 */

public class DevicePreviewAdapter extends RecyclerView.Adapter {

    private List<DeviceCurValue> dcvList;

    private long areaId;

    private Context context;

    public long getAreaId() {
        return areaId;
    }

    public void setAreaId(long areaId) {
        this.areaId = areaId;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<DeviceCurValue> getDcvList() {
        return dcvList;
    }

    public void setDcvList(List<DeviceCurValue> dcvList) {
        this.dcvList = dcvList;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView icon;
        TextView value;
        View wholeView;

        ViewHolder(View itemView) {
            super(itemView);

            wholeView = itemView.findViewById(R.id.device_preview_item);
            icon = (ImageView) itemView.findViewById(R.id.device_icon);
            value = (TextView) itemView.findViewById(R.id.device_preview_value);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.device_preview_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        final DeviceCurValue mDcv = dcvList.get(position);
        //分析数据类型 湿度直接显示、温度加上单位、门开关则以汉字显示
        DataTypeEnum dataType = DataTypeEnum.indexOf(mDcv.getType());
        if (dataType != null) {
            switch (dataType) {
                //摄氏度
                case TMP_CELSIUS:
                    viewHolder.value.setText("" + mDcv.getCurValue() + "℃");
                    viewHolder.icon.setImageResource(R.drawable.ic_thermometer);
                    break;
                //开氏温度
                case TMP_K:
                    viewHolder.value.setText("" + mDcv.getCurValue() + "K");
                    viewHolder.icon.setImageResource(R.drawable.ic_thermometer);
                    break;
                //湿度
                case HUMIDITY:
                    viewHolder.value.setText(mDcv.getCurValue());
                    viewHolder.icon.setImageResource(R.drawable.ic_humidity);
                    break;
                //门开关 value==1 表示开 0表示关
                case DOOR_OPEN_CLOSE:
                    viewHolder.value.setText(mDcv.getCurValue().equals("1") ? "开" : "关");
                    viewHolder.icon.setImageResource(mDcv.getCurValue().equals("1") ?
                            R.drawable.ic_door_open_preview : R.drawable.ic_door_close_preview);
                    break;
                //防盗装置,显示移动秒数
                case SEC:
                    viewHolder.value.setText("移动"+mDcv.getCurValue()+"S");
                    viewHolder.icon.setImageResource(R.drawable.ic_anti_thief);
                default:break;
            }
        }

        //跳转至房间详情页面 并传递被点击设备id 及数据类型
        viewHolder.wholeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AreaDetailActivity.class);
                intent.putExtra("device_id",mDcv.getDeviceId());
                intent.putExtra("data_type",mDcv.getType());
                intent.putExtra("position",position);
                intent.putExtra("area_id",areaId);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return dcvList.size();
    }
}

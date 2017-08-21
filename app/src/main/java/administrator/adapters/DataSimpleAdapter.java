package administrator.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qrcodescan.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import administrator.entity.DeviceData;
import administrator.entity.UniversalData;
import administrator.enums.DataTypeEnum;

/**
 * Created by zhuang_ge on 2017/8/6.
 * 数据单项适配器
 * 可根据数据类型的不同 显示不同格式 不同单位的数据
 */

public class DataSimpleAdapter extends RecyclerView.Adapter {


    private DataTypeEnum dataType;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private List<DeviceData> dataList;

    public DataTypeEnum getDataType() {
        return dataType;
    }

    public void setDataType(DataTypeEnum dataType) {
        this.dataType = dataType;
    }

    public List<DeviceData> getDataList() {
        return dataList;
    }

    public void setDataList(List<DeviceData> dataList) {
        this.dataList = dataList;
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView doorImg;
        TextView timeText;
        TextView dataText;

        public ViewHolder(View itemView) {
            super(itemView);
            doorImg = (ImageView) itemView.findViewById(R.id.data_icon);
            timeText = (TextView) itemView.findViewById(R.id.time);
            dataText = (TextView) itemView.findViewById(R.id.data);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(getResId(dataType), null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        DeviceData data = dataList.get(position);

        viewHolder.timeText.setText(data.getReceiveTime());
        //如果是门检测器 则以图标显示 否则直接显示数据
        if (dataType == DataTypeEnum.DOOR_OPEN_CLOSE) {
            boolean isOpen = data.getValue().equals("1");
            viewHolder.doorImg
                    .setImageResource(isOpen ?
                            R.drawable.ic_door_open : R.drawable.ic_door_close);
        } else {
            String dataForShow = data.getValue();
            switch (dataType) {
                case HUMIDITY:
                    dataForShow = data.getValue();
                    break;
                case TMP_K:
                    dataForShow = data.getValue() + "K";
                    break;
                case TMP_CELSIUS:
                    dataForShow = data.getValue() + "℃";
                    break;
                default:
                    break;
            }
            viewHolder.dataText.setText(dataForShow);
        }
    }

    @Override
    public int getItemCount() {
        if (dataList == null) {
            return 0;
        } else {
            return dataList.size();
        }
    }

    //根据数据类型选择对应的布局
    private int getResId(DataTypeEnum dataType) {
        int resID;
        switch (dataType) {
            case DOOR_OPEN_CLOSE:
                resID = R.layout.device_data_icon_item;
                break;
            default:
                resID = R.layout.device_data_item;
                break;
        }
        return resID;
    }
}

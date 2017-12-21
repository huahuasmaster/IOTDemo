package administrator.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.qrcodescan.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import administrator.adapters.listener.AreaCardCallbackListener;
import administrator.adapters.listener.DeviceCardCallbackListener;
import administrator.entity.AreaCurValue;
import administrator.entity.DataEntity;
import administrator.entity.DeviceCurValue;
import administrator.entity.DeviceData;
import administrator.entity.DeviceInArea;
import administrator.enums.DataTypeEnum;
import administrator.ui.AreaDetailActivity;
import administrator.view.CurersView;

import static android.view.View.GONE;

/**
 * Created by zhuang_ge on 2017/8/18.
 * 资源-预览页面 房间卡片的适配器
 */

public class AreaCardAdapter extends RecyclerView.Adapter {

    //房间卡片的viewtype
    private static final int TYPE_AREA_CARD = 1;

    //设备卡片的viewtype
    private static final int TYPE_DEVICE_CARD = 2;

    //储存正常数据
    private List<AreaCurValue> areaList;

    //储存未绑定房间的设备数据
    private List<DeviceInArea> diaList;

    //设备卡片监听回调函数
    private DeviceCardCallbackListener deviceCardCallbackListener;

    //房间卡片监听回调函数
    private AreaCardCallbackListener areaCardCallbackListener;

    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private Context context;

    /**
     * 用于储存卡片内预览值列表的适配器 方便调用、刷新预览数据
     * 对特定布局刷新 而不是整个列表刷新 可以有效减少程序开销
     */
    private List<DevicePreviewAdapter> previewAdapterList;

    public AreaCardAdapter() {
        super();
        previewAdapterList = new ArrayList<>();
    }

    public DeviceCardCallbackListener getDeviceCardCallbackListener() {
        return deviceCardCallbackListener;
    }

    public void setDeviceCardCallbackListener(DeviceCardCallbackListener deviceCardCallbackListener) {
        this.deviceCardCallbackListener = deviceCardCallbackListener;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<AreaCurValue> getAreaList() {
        return areaList;
    }

    public void setAreaList(List<AreaCurValue> areaList) {
        this.areaList = areaList;
    }

    public List<DeviceInArea> getDiaList() {
        return diaList;
    }

    public void setDiaList(List<DeviceInArea> diaList) {
        this.diaList = diaList;
    }

    public List<DevicePreviewAdapter> getPreviewAdapterList() {
        return previewAdapterList;
    }

    public void setPreviewAdapterList(List<DevicePreviewAdapter> previewAdapterList) {
        this.previewAdapterList = previewAdapterList;
    }

    public AreaCardCallbackListener getAreaCardCallbackListener() {
        return areaCardCallbackListener;
    }

    public void setAreaCardCallbackListener(AreaCardCallbackListener areaCardCallbackListener) {
        this.areaCardCallbackListener = areaCardCallbackListener;
    }

    //根据sn返回对应的preview适配器
    public DevicePreviewAdapter findPreviewAdapterBySn(String sn) {
        for (DevicePreviewAdapter adapter : previewAdapterList) {
            List<DeviceCurValue> list = adapter.getDcvList();
            for (DeviceCurValue dcv : list) {
                if (dcv.getSn().equals(sn)) {
                    return adapter;
                }
            }
        }
        return null;
    }

    //房间卡片的viewholder
    class AreaCardViewHolder extends RecyclerView.ViewHolder {
        //房间背景图片
        ImageView areaBack;
        //房间名称
        TextView areaName;
        //设备预览数据列表
        RecyclerView curValueRV;

        public AreaCardViewHolder(View itemView) {
            super(itemView);

            areaBack = (ImageView) itemView.findViewById(R.id.room_bg);
            areaName = (TextView) itemView.findViewById(R.id.area_card_area_name);
            curValueRV = (RecyclerView) itemView.findViewById(R.id.area_card_inner_device_rv);
        }
    }

    //单独设备的viewholder
    class DIACardViewHolder extends RecyclerView.ViewHolder {

        //设备icon
        ImageView headIcon;
        //设备真实名称
        TextView realName;
        //设备副标题
        TextView subhead;
        //开关
        Switch statusSwitch;
        //描述
        TextView dsc;
        //数据展示列表
        RecyclerView dataRV;
        //设置阈值按钮
        Button thresholdBtn;
        //查看详情按钮
        Button checkBtn;
        //折线图
        CurersView curersView;
        ConstraintLayout constraintLayout;

        public DIACardViewHolder(View itemView) {
            super(itemView);

            headIcon = (ImageView) itemView.findViewById(R.id.device_head_ic);
            realName = (TextView) itemView.findViewById(R.id.device_real_name);
            subhead = (TextView) itemView.findViewById(R.id.name_and_type_of_device);
            statusSwitch = (Switch) itemView.findViewById(R.id.status_switch);
            dsc = (TextView) itemView.findViewById(R.id.dsc);
            dataRV = (RecyclerView) itemView.findViewById(R.id.device_data_rv);
            thresholdBtn = (Button) itemView.findViewById(R.id.go_setting_threshold);
            checkBtn = (Button) itemView.findViewById(R.id.check_detail_btn);
            curersView = (CurersView) itemView.findViewById(R.id.curersview);
            constraintLayout = (ConstraintLayout) itemView.findViewById(R.id.device_card_preview_conslayout);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        int viewRes = 0;
        //根据viewtype的不同 加载不同的布局
        switch (viewType) {
            case TYPE_AREA_CARD:
                viewRes = R.layout.area_card_item;
                return new AreaCardViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(viewRes, parent, false));

            case TYPE_DEVICE_CARD:
                viewRes = R.layout.device_card_preview_item;
                return new DIACardViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(viewRes, parent, false));
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        //如果位置大于arealist的长度 说明已在加载设备卡片
        if (position >= areaList.size()) {
            return TYPE_DEVICE_CARD;
        } else {
            return TYPE_AREA_CARD;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof AreaCardViewHolder) {
            initViewsOfAreaCard((AreaCardViewHolder) holder, position);
        } else if (holder instanceof DIACardViewHolder) {
            initViewsOfDeviceCard((DIACardViewHolder) holder, position - areaList.size());
        }

    }

    //用于设备卡片布局赋值的方法
    @SuppressLint("SetTextI18n")
    private void initViewsOfDeviceCard(DIACardViewHolder holder, final int position) {
        final DeviceInArea mDia = diaList.get(position);
        DataSimpleAdapter adapter = new DataSimpleAdapter();
        DataTypeEnum mEnum = DataTypeEnum.indexOf(mDia.getType());
        List<DeviceData> mDeviceDataList = mDia.getDeviceDataList();
        //为按钮添加事件
        holder.checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deviceCardCallbackListener.onCheck(position);
            }
        });

        //如果不是温度或者湿度，则隐藏
        if (holder.thresholdBtn != null) {
            if (mDia.getType() > 3) {
                holder.thresholdBtn.setVisibility(GONE);
            } else {
                holder.thresholdBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deviceCardCallbackListener.onThreshold(mDia);
                    }
                });
            }
        }
        //赋值名称
        holder.realName.setText(mDia.getDeviceName());
        assert mEnum != null;
        holder.subhead.setText(mDia.getOtherName()
                + "-" + mEnum.getType());

        //赋值开关
        holder.statusSwitch.setChecked(mDia.getStatus() == 1);
        if (mEnum == DataTypeEnum.HUMIDITY || mEnum == DataTypeEnum.TMP_CELSIUS) {
            holder.curersView.setMode(mEnum);
            holder.curersView.setVisibility(View.VISIBLE);
            //对现有的数据进行处理
            List<DataEntity> dataEntities = new ArrayList<>();
            Log.i("deviceData2entity", "" + mDeviceDataList.size());
            for (DeviceData deviceData : mDeviceDataList) {
                DataEntity dataEntity = new DataEntity();
                try {
                    dataEntity.setTime(format.parse(deviceData.getReceiveTime()).getTime());
                    dataEntity.setFloat(Float.valueOf(deviceData.getValue().split("%")[0]));
                    dataEntities.add(dataEntity);
                    Log.i("deviceData2entity", deviceData.toString() + "|" + dataEntity.toString());
                } catch (ParseException e) {
                    break;
                }
            }
            if (mDia.getMaxValue() != 9999 || mDia.getMinValue() != -9999) {
                Float[] risk = {(float) mDia.getMinValue(), (float) mDia.getMaxValue()};
                holder.curersView.setRisk(risk);
            }
            holder.curersView.init();
            holder.curersView.setEntityList(dataEntities);

            //描述文字的位置需要更改
            holder.dsc.setVisibility(GONE);
        } else {
            //不支持图表的，使用基本数据列表
            //数据列表
            adapter.setDataType(mEnum);
            adapter.setDataList(mDeviceDataList);
            holder.dataRV.setLayoutManager(new LinearLayoutManager(context));
            holder.dataRV.setAdapter(adapter);
        }


    }

    //用于房间卡片布局赋值的方法
    private void initViewsOfAreaCard(AreaCardViewHolder holder, int position) {
        DevicePreviewAdapter adapter = new DevicePreviewAdapter();
        previewAdapterList.add(adapter);

        final AreaCurValue mAcv = areaList.get(position);
        adapter.setContext(context);
        adapter.setDcvList(mAcv.getDeviceCurValueList());
        adapter.setAreaId(mAcv.getAreaId());
        //填充房间名
        holder.areaName.setText(mAcv.getName());
        //添加点击事件
        holder.areaName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                areaCardCallbackListener.onAreaName(mAcv);
            }
        });
        holder.areaBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                areaCardCallbackListener.onAreaBack(mAcv);
            }
        });
        holder.areaBack.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                areaCardCallbackListener.onLongAreaBack(mAcv);
                return false;
            }
        });
        //展示设备预览数据
        holder.curValueRV.setAdapter(adapter);
        holder.curValueRV.setLayoutManager(
                new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
    }

    private void goNextActivity(long areaId) {

    }


    @Override
    public int getItemCount() {
        return areaList.size() + diaList.size();
    }
}

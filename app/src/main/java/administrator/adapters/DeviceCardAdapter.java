package administrator.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.qrcodescan.R;

import java.util.List;

import administrator.adapters.listener.DeviceCardCallbackListener;
import administrator.entity.DeviceInArea;
import administrator.enums.DataTypeEnum;

/**
 * Created by zhuang_ge on 2017/8/5.
 * 设备卡片适配器
 */

public class DeviceCardAdapter extends PagerAdapter{
    private Context context;
    private List<View> views;
    private List<DeviceInArea> deviceInAreaList;
    private final String TAG = "devicecard";
    private DeviceCardCallbackListener listener;

    public DeviceCardCallbackListener getListener() {
        return listener;
    }

    public void setListener(DeviceCardCallbackListener listener) {
        this.listener = listener;
    }

    //    private List<>
    //// TODO: 2017/8/5 此为测试方法，后续应删除
    public DeviceCardAdapter(Context context) {
        this.context = context;

    }

    public List<DeviceInArea> getDeviceInAreaList() {
        return deviceInAreaList;
    }

    public void setDeviceInAreaList(List<DeviceInArea> deviceInAreaList) {
        this.deviceInAreaList = deviceInAreaList;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<View> getViews() {
        return views;
    }

    public void setList(List<View> views) {
        this.views = views;
    }

    @Override public int getCount() {
        return views.size();
    }

    @Override public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    //在此处进行view的赋值,点击事件等
    @Override public Object instantiateItem(ViewGroup container, final int position) {
        View view = views.get(position);
        DeviceInArea deviceInArea = deviceInAreaList.get(position);
        //为3个按钮添加点击事件
        Button checkDetailBtn = (Button)view.findViewById(R.id.check_detail_btn);
        checkDetailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onCheck(position);
            }
        });

        Button goBackBtn = (Button)view.findViewById(R.id.go_back);
        goBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onBack();
            }
        });

        Button thresholdSetBtn = (Button)view.findViewById(R.id.go_setting_threshold);
        if(thresholdSetBtn != null) {
            thresholdSetBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onThreshold(position);
                }
            });
        }
        //赋值各种名称
        TextView realNameText = (TextView)view.findViewById(R.id.device_real_name);
        realNameText.setText(deviceInArea.getDeviceName());

        TextView areaAndOtherName = (TextView)view.findViewById(R.id.room_and_name_of_device);
        areaAndOtherName.setText(deviceInArea.getAreaName()
                +"-"+deviceInArea.getOtherName()
                +"-"+ DataTypeEnum.indexOf(deviceInArea.getType()).getType());

        //赋值swtich
        Switch status = (Switch)view.findViewById(R.id.status_switch);
        status.setChecked(deviceInArea.getStatus() == 1);

        RecyclerView rv = (RecyclerView)view.findViewById(R.id.device_data_rv);
        DataSimpleAdapter adapter = new DataSimpleAdapter();
        //告知数据类型
        adapter.setDataType(DataTypeEnum.indexOf(deviceInArea.getType()));
        adapter.setDataList(deviceInArea.getDeviceDataList());
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(context));

        container.addView(views.get(position));

        return views.get(position);
    }

    @Override public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(views.get(position));
    }
}

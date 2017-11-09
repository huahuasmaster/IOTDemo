package administrator.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.qrcodescan.R;

import java.util.List;

import administrator.adapters.listener.DeviceCardCallbackListener;
import administrator.entity.DeviceInArea;
import administrator.enums.DataTypeEnum;

import static android.view.View.GONE;

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
        SDKInitializer.initialize(context.getApplicationContext());
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
        final DeviceInArea deviceInArea = deviceInAreaList.get(position);
        boolean isAT = deviceInArea.getDeviceName().equals("防盗传感器");
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
        //如果不是支持阈值设置的数据，则隐藏按钮
        if(thresholdSetBtn != null) {
            if(deviceInArea.getType() > 4) {
                thresholdSetBtn.setVisibility(GONE);
            } else {
                thresholdSetBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onThreshold(deviceInArea);
                    }
                });
            }
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

        if(!isAT) {
            RecyclerView rv = (RecyclerView) view.findViewById(R.id.device_data_rv);
            DataSimpleAdapter adapter = new DataSimpleAdapter();
            //告知数据类型
            adapter.setDataType(DataTypeEnum.indexOf(deviceInArea.getType()));
            adapter.setDataList(deviceInArea.getDeviceDataList());
            rv.setAdapter(adapter);
            rv.setLayoutManager(new LinearLayoutManager(context));
        } else {
            //解析坐标
            String[] coor = deviceInArea.getDeviceDataList()
                    .get(1).getValue().split(",");

            double longitude = Double.parseDouble(coor[0]);
            double latitude = Double.parseDouble(coor[1]);
            Log.i(this.getClass().getName(),"longitude = "+longitude+", latitude = "+latitude);
            TextureMapView mapView = (TextureMapView)view.findViewById(R.id.mapview);
            BaiduMap baiduMap = mapView.getMap();
            //屏蔽掉各种手势，防止造成滑动冲突
            baiduMap.getUiSettings().setZoomGesturesEnabled(false);
            baiduMap.getUiSettings().setRotateGesturesEnabled(false);
            baiduMap.getUiSettings().setOverlookingGesturesEnabled(false);
            baiduMap.setMyLocationEnabled(true);
            LatLng point = new LatLng(latitude,longitude);
            //动画
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(point);
            baiduMap.animateMapStatus(update);
            update = MapStatusUpdateFactory.zoomTo(22f);
            baiduMap.animateMapStatus(update);
            //显示坐标点
            MyLocationData.Builder locationBuilder = new MyLocationData.
                    Builder();
            locationBuilder.latitude(latitude);
            locationBuilder.longitude(longitude);
            MyLocationData locationData = locationBuilder.build();
            baiduMap.setMyLocationData(locationData);
        }

        container.addView(view);

        return view;
    }

    @Override public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(views.get(position));
    }
}

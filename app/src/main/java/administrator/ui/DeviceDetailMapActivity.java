package administrator.ui;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.google.gson.Gson;
import com.lichfaker.log.Logger;
import com.qrcodescan.R;

import java.util.Date;

import administrator.base.http.HttpCallbackListener;
import administrator.base.http.HttpUtil;
import administrator.base.http.UrlHandler;
import administrator.entity.DeviceInArea;
import administrator.enums.DataTypeEnum;

public class DeviceDetailMapActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageView batteryImg;
    private ImageView wifiImg;
    private Switch statusSwitch;
    private ImageView goSettingImg;
    private ImageView goBackImg;
    private TextView title;
    private TextureMapView mapView;
    private BaiduMap baiduMap;
    private DeviceInArea deviceInArea;
    private long deviceId;
    private MaterialDialog waitDialog;
    private double FirstLongitude;
    private double FirstLatitude;
    private double LastLongitude;
    private double LastLatitude;
    private boolean isFirstLocate = true;
    View label1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_detail_map);
        deviceId = getIntent().getLongExtra("device_id", -1L);
        SDKInitializer.initialize(getApplicationContext());
        findViews();
        baiduMap = mapView.getMap();
        baiduMap.setMyLocationEnabled(true);
        initData();

    }
    private void findViews() {
        batteryImg = (ImageView) findViewById(R.id.bat_img);
        wifiImg = (ImageView) findViewById(R.id.wifi_img);
        statusSwitch = (Switch) findViewById(R.id.detail_status_switch);
        goSettingImg = (ImageView) findViewById(R.id.go_setting_img);
        goBackImg = (ImageView) findViewById(R.id.go_back);
        title = (TextView) findViewById(R.id.title_msg);
        mapView = (TextureMapView)findViewById(R.id.mapview);
        label1 = LayoutInflater.from(this).inflate(R.layout.initial_label,null);
        goBackImg.setOnClickListener(this);
        goSettingImg.setOnClickListener(this);
        waitDialog = new MaterialDialog.Builder(this)
                .title(getResources()
                        .getString(R.string.getting_data))
                .content(getResources()
                        .getString(R.string.plz_wait))
                .progress(true, 0)
                .autoDismiss(false)
                .progressIndeterminateStyle(false)
                .build();
    }
    private void initData() {

        HttpCallbackListener listener = new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                deviceInArea = new Gson().fromJson(response, DeviceInArea.class);
                String[] coor1 = deviceInArea.getDeviceDataList().get(0).getValue().split(",");
                String[] coor2 = deviceInArea.getDeviceDataList().get(1).getValue().split(",");
                FirstLatitude = Double.parseDouble(coor1[1]);
                FirstLongitude = Double.parseDouble(coor1[0]);
                LastLongitude = Double.parseDouble(coor2[0]);
                LastLatitude = Double.parseDouble(coor2[1]);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                   initViews();
                   updateMarker(LastLatitude,LastLongitude);
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(DeviceDetailMapActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
            }
        };
        waitDialog.show();
        String url = UrlHandler.getDeviceWithDataUrl(deviceId, DataTypeEnum.POS_GPS.getIndex(),10);
        HttpUtil.sendRequestWithCallback(url, listener);
    }
    private void initViews() {
        if (deviceInArea != null) {
            title.setText(deviceInArea.getDeviceName()
                    + "(" + deviceInArea.getOtherName() + ")");

            statusSwitch.setChecked(deviceInArea.getStatus() == 1);
        }
        if (waitDialog.isShowing()) {
            waitDialog.dismiss();
        }

    }
    private void updateMarker(double secondLatitude, double secondLongitude) {
        if(!isFirstLocate) {
            baiduMap.clear();
        }
        addFirstMarker();
        LatLng point2 = new LatLng(secondLatitude,secondLongitude);
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
        OverlayOptions option2 = new MarkerOptions().position(point2).icon(bitmap);
        Log.i("LALALA","开始标注当前位置"+point2.latitude+","+point2.longitude);
        baiduMap.addOverlay(option2);
        //动画
        MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(point2);
        Logger.i("执行了移动动画");
        baiduMap.animateMapStatus(update);
        if(isFirstLocate) {
            update = MapStatusUpdateFactory.zoomTo(19f);
            baiduMap.animateMapStatus(update);
            isFirstLocate = false;
        }
    }
    //标注初始位置，添加标签
    private void addFirstMarker() {
        LatLng point = new LatLng(FirstLatitude,FirstLongitude);
        InfoWindow iw1 = new InfoWindow(label1,point,-32);
        baiduMap.showInfoWindow(iw1);
        MyLocationData.Builder locationBuilder = new MyLocationData.
                Builder();
        locationBuilder.latitude(FirstLatitude);
        locationBuilder.longitude(FirstLongitude);
        MyLocationData locationData = locationBuilder.build();
        baiduMap.setMyLocationData(locationData);
    }
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mLocationClient.stop();
        mapView.onDestroy();
        baiduMap.setMyLocationEnabled(false);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.go_back:
                finish();
                break;
            case R.id.go_setting_img:
                Intent intent = new Intent(DeviceDetailMapActivity.this
                        , DeviceSettingActivity.class);
                intent.putExtra("device_in_area", deviceInArea);
                startActivity(intent);
                break;
        }
    }
}

package administrator.ui;

import android.app.Dialog;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jaygoo.widget.RangeSeekBar;
import com.lichfaker.log.Logger;
import com.qrcodescan.R;

import java.util.ArrayList;
import java.util.List;

import administrator.adapters.DeviceCardAdapter;
import administrator.adapters.listener.DeviceCardCallbackListener;
import administrator.base.http.HttpCallbackListener;
import administrator.base.http.HttpUtil;
import administrator.base.http.UrlHandler;
import administrator.entity.AlertConfigDto;
import administrator.entity.DeviceInArea;
import administrator.enums.DataTypeEnum;
import administrator.view.DialogUtil;
import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * 需要对防盗装置进行特殊处理（显示地图信息）
 */
public class AreaDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private ViewPager viewPager;
    private LayoutInflater inflater;
    private Button goCheckBtn;
    private MaterialDialog thresholdSetDialog;
    private MaterialDialog waitDialog;
    private DeviceCardAdapter adapter;
    private long deviceId;
    private int type;
    private int targetNth = 0;
    private long areaId;

    public static final int DEAULT_OFFSCEEN_LIMIT = 6;
    private List<View> viewList = new ArrayList<>();
    private List<DeviceInArea> deviceInAreaList = new ArrayList<>();
    private boolean isFirst = true;
    private boolean isAntiThief = false;
    private final String atName = "防盗传感器";

    private DeviceCardCallbackListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_detail);

        areaId = getIntent().getLongExtra("area_id", -1L);
        deviceId = getIntent().getLongExtra("device_id", -1L);
        type = getIntent().getIntExtra("data_type", -1);


        viewPager = (ViewPager) findViewById(R.id.device_pager);
        initViews();
        initData();
        adapter = new DeviceCardAdapter(this);
        adapter.setListener(listener);
        adapter.setList(viewList);
        viewPager.setAdapter(adapter);
        viewPager.setPageMargin(50);
        viewPager.setOffscreenPageLimit(DEAULT_OFFSCEEN_LIMIT);
    }

    //根据各项数据 提供不同布局 现有列表布局和地图布局
    private List<View> getPageViews(List<DeviceInArea> deviceInAreas) {
        inflater = LayoutInflater.from(this);
        List<View> views = new ArrayList<>();
        for (DeviceInArea deviceInArea : deviceInAreas) {
            if (deviceInArea.getDeviceName().equals(atName)) {
                views.add(inflater.inflate(R.layout.device_card_map_item, null));
            } else {
                views.add(inflater.inflate(R.layout.device_card_item, null));
            }
        }
        return views;
    }

    //根据上一个页面传过来的数据 确定显示第几个卡片
    private int getTargetNth() {
        //如果deviceid 或者 type有一个不确定 则直接显示第一个
        if (deviceId != -1 && type != -1) {
            for (int i = 0; i < adapter.getDeviceInAreaList().size(); i++) {
                DeviceInArea deviceInArea = adapter.getDeviceInAreaList().get(i);
                //寻找type和设备都对应的卡片
                if (deviceInArea.getId() == deviceId) {
                    if (deviceInArea.getType() == type ||
                            (deviceInArea.getType() == DataTypeEnum.POS_GPS.getIndex() && type == DataTypeEnum.SEC.getIndex())) {
                        Logger.i("", "跳转至第" + i + "个卡片");
                        return i;
                    }
                }
            }
        }

        return 0;
    }

    private void initViews() {

        listener = new DeviceCardCallbackListener() {
            @Override
            public void onBack() {
                finish();
            }

            @Override
            public void onThreshold(DeviceInArea dia) {
                if (dia.getType() == DataTypeEnum.POS_GPS.getIndex()) {
                    DialogUtil.showSingleThreshold(dia, AreaDetailActivity.this);
                } else if (dia.getType() == DataTypeEnum.DOOR_OPEN_CLOSE.getIndex()) {
                    DialogUtil.showCameraListDialog(dia, AreaDetailActivity.this);
                } else {
                    DialogUtil.showThresholdSetDialog(dia, AreaDetailActivity.this);
                }
            }

            @Override
            public void onCheck(int position) {
                //如果是防盗装置，则跳转至地图界面
                if (deviceInAreaList.get(position).getType() == DataTypeEnum.POS_GPS.getIndex()) {
                    Intent intent =
                            new Intent(AreaDetailActivity.this, DeviceDetailMapActivity.class);
                    intent.putExtra("device_id", deviceInAreaList.get(position).getId());
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(
                            AreaDetailActivity.this, DeviceDetailActivity.class);
                    intent.putExtra("device_id", deviceInAreaList.get(position).getId());
                    intent.putExtra("data_type", deviceInAreaList.get(position).getType());
                    startActivity(intent);
                }
            }
        };
    }

    //进行网络请求 获取必须数据
    private void initData() {
        String url = UrlHandler.getDeviceInAreaListByAreaId(areaId, 20);
        waitDialog = new MaterialDialog.Builder(this)
                .title(getResources()
                        .getString(R.string.getting_data))
                .content(getResources()
                        .getString(R.string.plz_wait))
                .progress(true, 0)
                .progressIndeterminateStyle(false)
                .build();
        //回调函数 在获取数据之后更新试图  如果是第一次 则帮用户跳转到对应卡片
        HttpCallbackListener listener = new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                deviceInAreaList = new Gson().fromJson(response,
                        new TypeToken<List<DeviceInArea>>() {
                        }.getType());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.setDeviceInAreaList(deviceInAreaList);
                        Logger.i("deviceInArea的个数为" + deviceInAreaList.size());
                        List<View> views = getPageViews(deviceInAreaList);
                        Logger.i("views的个数为" + views.size());
                        adapter.setList(views);

                        adapter.notifyDataSetChanged();
                        waitDialog.dismiss();
                        if (isFirst) {
                            viewPager.setCurrentItem(getTargetNth());
                            isFirst = false;
                        }
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        waitDialog.dismiss();
                    }
                });
            }
        };
        waitDialog.show();
        HttpUtil.sendRequestWithCallback(url, listener);

    }

    /**
     * 弹出阈值设置弹窗
     */

    @Override
    public void onClick(View view) {

    }
}

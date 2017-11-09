package administrator.ui;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lichfaker.log.Logger;
import com.qrcodescan.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import administrator.adapters.AlertSimpleAdapter;
import administrator.adapters.DataSimpleAdapter;
import administrator.adapters.listener.SwipeItemCallbackListener;
import administrator.base.http.HttpCallbackListener;
import administrator.base.http.HttpUtil;
import administrator.base.http.UrlHandler;
import administrator.entity.AlertDto;
import administrator.entity.DeviceInArea;
import administrator.entity.UniversalData;
import administrator.enums.DataTypeEnum;

// TODO: 2017/10/27 新增纯警报消息的展示
public class DeviceDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView batteryImg;
    private ImageView wifiImg;
    private RecyclerView rv;
    private Switch statusSwitch;
    private ImageView goSettingImg;
    private ImageView goBackImg;
    private SwipeItemCallbackListener onProcessListener;
    private long deviceId;
    private int type;
    private boolean fromAlert = false;
    private FloatingActionButton fab;
    private TextView title;
    private MaterialDialog waitDialog;//提示等待弹窗
    private DeviceInArea deviceInArea;
    private DataSimpleAdapter dataSimpleAdapter;
    private AlertSimpleAdapter alertSimpleAdapter;
    private List<AlertDto> alertDtos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_detail);

        deviceId = getIntent().getLongExtra("device_id", -1L);
        type = getIntent().getIntExtra("data_type", -1);
        fromAlert = getIntent().getBooleanExtra("from_alert", false);
        Logger.i("fromAlert = "+fromAlert);

        findViews();

        initData();

        goBackImg.setOnClickListener(this);
        goSettingImg.setOnClickListener(this);
//        Snackbar.make(rv,position == -1 ? "未知设备" : "这是第"+(position+1)+"个卡片中的设备"
//                ,Snackbar.LENGTH_SHORT)
//                .show();
//        title.setText("第"+(position+1)+"个设备");
    }

    private void findViews() {
        batteryImg = (ImageView) findViewById(R.id.bat_img);
        wifiImg = (ImageView) findViewById(R.id.wifi_img);
        rv = (RecyclerView) findViewById(R.id.all_data_rv);
        statusSwitch = (Switch) findViewById(R.id.detail_status_switch);
        goSettingImg = (ImageView) findViewById(R.id.go_setting_img);
        goBackImg = (ImageView) findViewById(R.id.go_back);
        title = (TextView) findViewById(R.id.title_msg);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        waitDialog = new MaterialDialog.Builder(this)
                .title(getResources()
                        .getString(R.string.getting_data))
                .content(getResources()
                        .getString(R.string.plz_wait))
                .progress(true, 0)
                .autoDismiss(false)
                .progressIndeterminateStyle(false)
                .build();
        if (!fromAlert) {
            Logger.i("fab已经隐藏");
            fab.setVisibility(View.GONE);
            fab.setEnabled(false);
        } else {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String url = UrlHandler
                            .processAlertByDeviceIdAndDataType(deviceId, type, new Date().getTime());
                    HttpCallbackListener listener = new HttpCallbackListener() {
                        @Override
                        public void onFinish(String response) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Snackbar.make(fab, "已消除警报", Toast.LENGTH_SHORT).show();
                                    fab.hide();
                                }
                            });
                        }

                        @Override
                        public void onError(Exception e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Snackbar.make(fab, "提交失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    };
                    HttpUtil.sendRequestWithCallback(url, listener);

                }
            });
        }
    }

    private void initViews() {
        if (deviceInArea != null) {
            title.setText(deviceInArea.getDeviceName()
                    + "(" + deviceInArea.getOtherName() + ")");

            statusSwitch.setChecked(deviceInArea.getStatus() == 1);
        }

        if (!fromAlert) {
            refreshDataSimple();
        } else {
            //如果是刷新alert信息，需要额外的网络请求
            initAlertData();
        }

    }

    private void refreshDataSimple() {

        if (dataSimpleAdapter == null) {
            dataSimpleAdapter = new DataSimpleAdapter();
            dataSimpleAdapter.setDataType(DataTypeEnum.indexOf(deviceInArea.getType()));
            dataSimpleAdapter.setDataList(deviceInArea.getDeviceDataList());
            rv.addItemDecoration(new DividerItemDecoration(
                    this, DividerItemDecoration.VERTICAL));
            rv.setAdapter(dataSimpleAdapter);
            rv.setLayoutManager(new LinearLayoutManager(this));
        } else {
            dataSimpleAdapter.setDataType(DataTypeEnum.indexOf(deviceInArea.getType()));
            dataSimpleAdapter.setDataList(deviceInArea.getDeviceDataList());
            dataSimpleAdapter.notifyDataSetChanged();
        }

        if (waitDialog.isShowing()) {
            waitDialog.dismiss();
        }
    }

    private void refreshAlertSimple() {
        if (onProcessListener == null) {
            onProcessListener = new SwipeItemCallbackListener() {
                @Override
                public void onDelete(int position) {
                    String url = UrlHandler.processAlert(alertDtos.get(position).getId()
                            , new Date().getTime());
                    HttpCallbackListener listener = new HttpCallbackListener() {
                        @Override
                        public void onFinish(String response) {
                            Logger.i("标注确认成功");
                        }

                        @Override
                        public void onError(Exception e) {
                            Logger.i("标注确认失败");
                        }
                    };
                    HttpUtil.sendRequestWithCallback(url, listener);
                }

                @Override
                public void onEdit(int position) {

                }

                @Override
                public void onMain(int position) {

                }
            };
        }
        if (alertSimpleAdapter == null) {
            alertSimpleAdapter = new AlertSimpleAdapter();
            alertSimpleAdapter.setListener(onProcessListener);
            alertSimpleAdapter.setAlertDtos(alertDtos);
            rv.addItemDecoration(new DividerItemDecoration(
                    this, DividerItemDecoration.VERTICAL));
            rv.setAdapter(alertSimpleAdapter);
            rv.setLayoutManager(new LinearLayoutManager(this));
        } else {
            alertSimpleAdapter.setAlertDtos(alertDtos);
            alertSimpleAdapter.notifyDataSetChanged();
        }
        if (waitDialog.isShowing()) {
            waitDialog.dismiss();
        }
    }

    private void initAlertData() {
        String url = UrlHandler.getAlertByDeviceId(deviceId, type, 20);
        HttpCallbackListener listener = new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                alertDtos = new Gson().fromJson(response
                        , new TypeToken<List<AlertDto>>() {
                        }.getType());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshAlertSimple();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                Logger.e(e.getMessage());
            }
        };
        HttpUtil.sendRequestWithCallback(url,listener);
    }

    private void initData() {


        HttpCallbackListener listener = new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                deviceInArea = new Gson().fromJson(response, DeviceInArea.class);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initViews();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(DeviceDetailActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
            }
        };

        waitDialog.show();

        String url = UrlHandler.getDeviceWithDataUrl(deviceId, type, 20);
        HttpUtil.sendRequestWithCallback(url, listener);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.go_back:
                finish();
                break;
            case R.id.go_setting_img:
                Intent intent = new Intent(DeviceDetailActivity.this
                        , DeviceSettingActivity.class);
                intent.putExtra("device_in_area", deviceInArea);
                startActivity(intent);
                break;
        }
    }
}
